package nl.weeaboo.vn.save.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.filesystem.SecureFileWriter;
import nl.weeaboo.lua2.io.LuaSerializer;
import nl.weeaboo.lua2.io.ObjectDeserializer;
import nl.weeaboo.lua2.io.ObjectSerializer;
import nl.weeaboo.lua2.io.ObjectSerializer.PackageLimit;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.IProgressListener;
import nl.weeaboo.vn.core.impl.Storage;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.impl.PixmapDecodingScreenshot;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.ISaveParams;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.ThumbnailInfo;

public class SaveModule implements ISaveModule {

    private static final long serialVersionUID = SaveImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SaveModule.class);
    private static final String SHARED_GLOBALS_PATH = "save-shared.bin";
    private static final int QUICK_SAVE_OFFSET = 800;
    private static final int AUTO_SAVE_OFFSET = 900;

    private final IEnvironment env;

    private final LuaSerializer luaSerializer;
    private final IStorage globals;
    private final IStorage sharedGlobals;

    public SaveModule(IEnvironment env) {
        this.env = Checks.checkNotNull(env);

        luaSerializer = new LuaSerializer();
        globals = new Storage();
        sharedGlobals = new Storage();
    }

    protected SecureFileWriter getSecureFileWriter() {
        return new SecureFileWriter(getFileSystem());
    }

    protected IWritableFileSystem getFileSystem() {
        return (IWritableFileSystem)env.getFileSystem();
    }

    @Override
    public void loadPersistent() {
        try {
// TODO LVN-017
//            if (isVNDS()) {
//                sharedGlobals.set(VndsUtil.readVndsGlobalSav(fs));
//            }
            loadSharedGlobals();
        } catch (FileNotFoundException fnfe) {
            // Shared globals don't exist yet, not an error
        } catch (IOException ioe) {
            LOG.error("Error loading shared globals", ioe);
        }
    }

    protected void loadSharedGlobals() throws IOException {
        SecureFileWriter sfw = getSecureFileWriter();
        IStorage read = StorageIO.read(sfw, SHARED_GLOBALS_PATH);

        sharedGlobals.clear();
        sharedGlobals.addAll(read);
    }

    @Override
    public void savePersistent() {
        try {
            saveSharedGlobals();
        } catch (IOException e) {
            LOG.error("Unable to save shared globals", e);
        }
        generatePreloaderData();
    }

    protected void saveSharedGlobals() throws IOException {
        SecureFileWriter sfw = getSecureFileWriter();
        StorageIO.write(sharedGlobals, sfw, SHARED_GLOBALS_PATH);
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
    public IStorage getGlobals() {
        return globals;
    }

    @Override
    public IStorage getSharedGlobals() {
        return sharedGlobals;
    }

    @Override
    public void delete(int slot) throws IOException {
        try {
            IWritableFileSystem fs = getFileSystem();
            fs.delete(getSaveFilename(slot));
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
        return SaveFileIO.openArchive(getFileSystem(), getSaveFilename(slot));
    }

    private static SaveFileHeader readSaveHeader(IFileSystem arc) throws SaveFormatException, IOException {
        SaveFileHeaderJson headerJson = SaveFileIO.readJson(arc, SaveFileConstants.HEADER_PATH,
                SaveFileHeaderJson.class);
        return SaveFileHeaderJson.decode(headerJson);
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

    @Override
    public IScreenshot readSaveThumbnail(int slot) throws IOException {
        IFileSystem arc = openSaveArchive(slot);
        try {
            SaveFileHeader saveHeader = readSaveHeader(arc);
            ThumbnailInfo thumbnail = saveHeader.getThumbnail();
            if (thumbnail == null) {
                return null;
            }

            byte[] bytes = SaveFileIO.readBytes(arc, thumbnail.getPath());
            return new PixmapDecodingScreenshot(bytes);
        } finally {
            arc.close();
        }
    }

    private void readSaveData(IFileSystem fs, INovel novel, IProgressListener pl) throws IOException {

        InputStream in = ProgressInputStream.wrap(fs.newInputStream(SaveFileConstants.SAVEDATA_PATH),
                fs.getFileSize(SaveFileConstants.SAVEDATA_PATH), pl);

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

        ZipOutputStream zout = new ZipOutputStream(fs.newOutputStream(getSaveFilename(slot), false));
        try {
            ThumbnailInfo thumbnailInfo = params.getThumbnailInfo();

            // Save header
            SaveFileHeader header = new SaveFileHeader(System.currentTimeMillis());
            header.setThumbnail(thumbnailInfo);
            header.setUserData(params.getUserData());
            SaveFileIO.writeJson(zout, SaveFileConstants.HEADER_PATH, SaveFileHeaderJson.encode(header));

            // Thumbnail
            SaveFileIO.writeBytes(zout, thumbnailInfo.getPath(), params.getThumbnailData());

            // Save data
            writeSaveData(zout, novel, pl);
        } finally {
            try {
                zout.close();
            } catch (IOException ioe) {
                LOG.warn("Error closing save file: slot=" + slot, ioe);
            }
        }
    }

    private void writeSaveData(ZipOutputStream zout, INovel novel, IProgressListener pl) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectSerializer os = luaSerializer.openSerializer(ProgressOutputStream.wrap(bout, pl));
        try {
            os.setPackageLimit(PackageLimit.NONE);
            os.writeObject(novel);
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
    }

    protected String getSaveFilename(int slot) {
        return StringUtil.formatRoot("save-%03d.sav", slot);
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
        return fs.getFileExists(getSaveFilename(slot));
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
        return slot > QUICK_SAVE_OFFSET && slot < QUICK_SAVE_OFFSET + 100;
    }

    @Override
    public int getAutoSaveSlot(int slot) {
        int s = AUTO_SAVE_OFFSET + slot;
        if (!isAutoSaveSlot(s)) throw new IllegalArgumentException("Slot outside valid range: " + slot);
        return s;
    }

    static boolean isAutoSaveSlot(int slot) {
        return slot > AUTO_SAVE_OFFSET && slot < AUTO_SAVE_OFFSET + 100;
    }

}
