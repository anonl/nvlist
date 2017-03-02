package nl.weeaboo.vn.impl.save;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.lua2.io.LuaSerializer;
import nl.weeaboo.lua2.io.ObjectDeserializer;
import nl.weeaboo.lua2.io.ObjectSerializer;
import nl.weeaboo.lua2.io.ObjectSerializer.ErrorLevel;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.IProgressListener;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.impl.image.EmptyScreenshot;
import nl.weeaboo.vn.impl.image.PixmapDecodingScreenshot;
import nl.weeaboo.vn.save.ISaveFile;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.ISaveParams;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.ThumbnailInfo;

@CustomSerializable
public class SaveModule implements ISaveModule {

    private static final long serialVersionUID = SaveImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SaveModule.class);
    private static final FilePath SHARED_GLOBALS_PATH = FilePath.of("save-shared.bin");
    private static final FilePath SEEN_LOG_PATH = FilePath.of("seen.bin");
    private static final int QUICK_SAVE_OFFSET = 800;
    private static final int NUM_QUICK_SAVE_SLOTS = 100;
    private static final int AUTO_SAVE_OFFSET = 900;
    private static final int NUM_AUTO_SAVE_SLOTS = 100;

    private final IEnvironment env;

    private transient IStorage sharedGlobals;
    private transient LuaSerializer luaSerializer;

    public SaveModule(IEnvironment env) {
        this.env = Checks.checkNotNull(env);

        initTransients();
    }

    private void initTransients() {
        sharedGlobals = new Storage();
        tryLoadSharedGlobals();

        luaSerializer = new LuaSerializer();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    @Override
    public void destroy() {
        savePersistent();
    }

    @Override
    public void update() {
    }

    protected final SecureFileWriter getSecureFileWriter() {
        return new SecureFileWriter(getFileSystem());
    }

    protected final IWritableFileSystem getFileSystem() {
        return env.getOutputFileSystem();
    }

    @Override
    public void loadPersistent() {
        tryLoadSharedGlobals();

        try {
            env.getPlayTimer().load(sharedGlobals);
        } catch (IOException e) {
            LOG.error("Unable to load play timer state from shared globals", e);
        }

        SecureFileWriter sfw = getSecureFileWriter();
        try {
            env.getSeenLog().load(sfw, SEEN_LOG_PATH);
        } catch (FileNotFoundException fnfe) {
            // Seen log doesn't exist yet, not an error
        } catch (IOException ioe) {
            LOG.error("Error loading seen log", ioe);
        }
    }

    private void tryLoadSharedGlobals() {
        try {
            // TODO LVN-017
            // if (isVNDS()) {
            //    sharedGlobals.set(VndsUtil.readVndsGlobalSav(fs));
            // }
            loadSharedGlobals();
        } catch (FileNotFoundException fnfe) {
            // Shared globals don't exist yet, not an error
        } catch (IOException ioe) {
            LOG.error("Error loading shared globals", ioe);
        }
    }

    private void loadSharedGlobals() throws IOException {
        SecureFileWriter sfw = getSecureFileWriter();
        IStorage read = StorageIO.read(sfw, SHARED_GLOBALS_PATH);

        sharedGlobals.clear();
        sharedGlobals.addAll(read);
    }

    @Override
    public void savePersistent() {
        SecureFileWriter sfw = getSecureFileWriter();

        try {
            env.getPlayTimer().save(sharedGlobals);
        } catch (IOException e) {
            LOG.error("Unable to save play timer state to shared globals", e);
        }

        try {
            StorageIO.write(sharedGlobals, sfw, SHARED_GLOBALS_PATH);
        } catch (IOException e) {
            LOG.error("Unable to save shared globals", e);
        }

        try {
            env.getSeenLog().save(sfw, SEEN_LOG_PATH);
        } catch (IOException e) {
            LOG.error("Unable to save seen log", e);
        }
        generatePreloaderData();
    }

    protected void generatePreloaderData() {
        // TODO LVN-011 Re-enable analytics
        //      IAnalytics an = novel.getAnalytics();
        //      if (an instanceof BaseLoggingAnalytics) {
        //          BaseLoggingAnalytics ba = (BaseLoggingAnalytics)an;
        //          try {
        //              ba.optimizeLog(true);
        //          } catch (IOException ioe) {
        //              GameLog.w("Error dumping analytics", ioe);
        //          }
        //      }
    }

    @Override
    public IStorage getSharedGlobals() {
        return sharedGlobals;
    }

    @Override
    public void delete(int slot) throws IOException {
        try {
            IWritableFileSystem fs = getFileSystem();
            fs.delete(getSavePath(slot));
        } catch (FileNotFoundException fnfe) {
            //Ignore
        }

        if (getSaveExists(slot)) {
            throw new IOException("Deletion of slot " + slot + " failed");
        }
    }

    @Override
    public void load(INovel novel, int slot, IProgressListener pl) throws SaveFormatException, IOException {
        IFileSystem arc = openSaveArchive(slot);
        try {
            readSaveData(arc, novel, pl);
        } finally {
            arc.close();
        }
    }

    private IFileSystem openSaveArchive(int slot) throws IOException {
        return SaveFileIO.openArchive(getFileSystem(), getSavePath(slot));
    }

    @Override
    public Collection<ISaveFile> getSaves(int fromSlot, int numSlots) {
        List<ISaveFile> result = Lists.newArrayList();
        for (int slot = fromSlot; slot < fromSlot + numSlots; slot++) {
            if (getSaveExists(slot)) {
                try {
                    result.add(readSave(slot));
                } catch (IOException ioe) {
                    LOG.warn("Error reading save slot: {}", slot, ioe);
                }
            }
        }
        return result;
    }

    @Override
    public ISaveFile readSave(int slot) throws SaveFormatException, IOException {
        IFileSystem arc = openSaveArchive(slot);
        try {
            SaveFileHeader header = readSaveHeader(arc);

            IScreenshot screenshot = EmptyScreenshot.getInstance();
            if (header.getThumbnail() != null) {
                screenshot = readSaveThumbnail(arc, header.getThumbnail());
            }

            return new SaveFile(slot, header, screenshot);
        } finally {
            arc.close();
        }
    }

    @Override
    public SaveFileHeader readSaveHeader(int slot) throws SaveFormatException, IOException {
        IFileSystem arc = openSaveArchive(slot);
        try {
            return readSaveHeader(arc);
        } finally {
            arc.close();
        }
    }

    private static SaveFileHeader readSaveHeader(IFileSystem arc) throws SaveFormatException, IOException {
        SaveFileHeaderJson headerJson = SaveFileIO.readJson(arc, SaveFileConstants.HEADER_PATH,
                SaveFileHeaderJson.class);
        return SaveFileHeaderJson.decode(headerJson);
    }

    private static IScreenshot readSaveThumbnail(IFileSystem arc, ThumbnailInfo thumbnail) throws IOException {
        byte[] bytes = SaveFileIO.readBytes(arc, thumbnail.getPath());
        return new PixmapDecodingScreenshot(bytes);
    }

    private void readSaveData(IFileSystem fs, INovel novel, IProgressListener pl) throws IOException {
        InputStream in = ProgressInputStream.wrap(fs.openInputStream(SaveFileConstants.SAVEDATA_PATH),
                fs.getFileSize(SaveFileConstants.SAVEDATA_PATH), pl);

        // Clean up resources for current environment before we start loading a new one
        novel.getEnv().destroy();

        try {
            ObjectDeserializer is = luaSerializer.openDeserializer(in);
            try {
                novel.readAttributes(is);
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            } finally {
                is.close();
            }
        } finally {
            in.close();
        }
    }

    @Override
    public void save(INovel novel, int slot, ISaveParams params, IProgressListener pl) throws IOException {
        IWritableFileSystem fs = getFileSystem();

        FilePath savePath = getSavePath(slot);
        ZipOutputStream zout = new ZipOutputStream(fs.openOutputStream(savePath, false));
        try {
            ThumbnailInfo thumbnailInfo = params.getThumbnailInfo();

            // Save header
            SaveFileHeader header = new SaveFileHeader(System.currentTimeMillis());
            header.setThumbnail(thumbnailInfo);
            header.setUserData(params.getUserData());
            SaveFileIO.writeJson(zout, SaveFileConstants.HEADER_PATH, SaveFileHeaderJson.encode(header));

            // Thumbnail
            if (thumbnailInfo != null) {
                SaveFileIO.writeBytes(zout, thumbnailInfo.getPath(), params.getThumbnailData());
            }

            // Save data
            writeSaveData(zout, novel, pl);
        } finally {
            zout.close();
        }

        LOG.info("Save written: {}", StringUtil.formatMemoryAmount(fs.getFileSize(savePath)));
    }

    private void writeSaveData(ZipOutputStream zout, INovel novel, IProgressListener pl) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectSerializer os = luaSerializer.openSerializer(ProgressOutputStream.wrap(bout, pl));
        try {
            os.setCollectStats(true);
            os.setPackageErrorLevel(ErrorLevel.NONE);
            novel.writeAttributes(os);
            os.flush();

            List<String> warnings = ImmutableList.copyOf(os.checkErrors());
            if (!warnings.isEmpty()) {
                handleSaveWarnings(novel, warnings);
            }
        } finally {
            os.close();
        }

        SaveFileIO.writeBytes(zout, SaveFileConstants.SAVEDATA_PATH, bout.toByteArray());
    }

    /**
     * @param novel The novel object currently being saved.
     * @param warnings A collection of warnings generated while saving.
     */
    protected void handleSaveWarnings(INovel novel, List<String> warnings) {
        for (String warning : warnings) {
            LOG.warn("Save warning: {}", warning);
        }
    }

    protected FilePath getSavePath(int slot) {
        return FilePath.of(StringUtil.formatRoot("save-%03d.sav", slot));
    }

    @Override
    public int getNextFreeSlot() {
        int slot = 1;
        while (getSaveExists(slot)) {
            slot++;
        }
        return slot;
    }

    @Override
    public boolean getSaveExists(int slot) {
        IFileSystem fs = getFileSystem();
        return fs.getFileExists(getSavePath(slot));
    }

    @Override
    public int getQuickSaveSlot(int slot) {
        int s = QUICK_SAVE_OFFSET + slot;
        if (!isQuickSaveSlot(s)) {
            throw new IllegalArgumentException("Slot outside valid range: " + slot);
        }
        return s;
    }

    static boolean isQuickSaveSlot(int slot) {
        return slot > QUICK_SAVE_OFFSET && slot < QUICK_SAVE_OFFSET + NUM_QUICK_SAVE_SLOTS;
    }

    @Override
    public int getAutoSaveSlot(int slot) {
        int s = AUTO_SAVE_OFFSET + slot;
        if (!isAutoSaveSlot(s)) {
            throw new IllegalArgumentException("Slot outside valid range: " + slot);
        }
        return s;
    }

    static boolean isAutoSaveSlot(int slot) {
        return slot > AUTO_SAVE_OFFSET && slot < AUTO_SAVE_OFFSET + NUM_AUTO_SAVE_SLOTS;
    }

}
