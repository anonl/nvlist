package nl.weeaboo.vn.impl.save;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
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
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.impl.core.AbstractModule;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;
import nl.weeaboo.vn.impl.image.EmptyScreenshot;
import nl.weeaboo.vn.impl.image.PixmapDecodingScreenshot;
import nl.weeaboo.vn.save.ISaveFile;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.ISaveParams;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.ThumbnailInfo;
import nl.weeaboo.vn.stats.IStatsModule;

/**
 * Sub-module for saving/loading.
 */
@CustomSerializable
public class SaveModule extends AbstractModule implements ISaveModule {

    private static final long serialVersionUID = SaveImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(SaveModule.class);

    private static final int QUICK_SAVE_OFFSET = 800;
    private static final int NUM_QUICK_SAVE_SLOTS = 99;
    private static final int AUTO_SAVE_OFFSET = 900;
    private static final int NUM_AUTO_SAVE_SLOTS = 99;

    private final StaticRef<INovel> novelRef = StaticEnvironment.NOVEL;
    private final IEnvironment env;

    private transient IStorage sharedGlobals;
    private transient LuaSerializer luaSerializer;
    private transient Deque<LoadRequest> loadRequests;
    private transient Deque<SaveRequest> saveRequests;

    public SaveModule(IEnvironment env) {
        this.env = Checks.checkNotNull(env);

        initTransients();
    }

    private void initTransients() {
        sharedGlobals = new Storage();

        luaSerializer = new LuaSerializer();
        loadRequests = new ArrayDeque<>();
        saveRequests = new ArrayDeque<>();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    /** The list order determines the save order, the load order is the opposite */
    private List<IPersistentSavePlugin> getPersistentSavePlugins() {
        IStatsModule statsModule = env.getStatsModule();
        return ImmutableList.of(
                new PlayTimerSavePlugin(statsModule.getPlayTimer(), sharedGlobals),
                new SeenLogSavePlugin(statsModule.getSeenLog()),
                new AnalyticsSavePlugin(statsModule.getAnalytics()),

                // Save shared globals last (so other plugins can save their data in shared-globals)
                new SharedGlobalsSavePlugin(sharedGlobals)
        );
    }

    final SecureFileWriter getSecureFileWriter() {
        return new SecureFileWriter(getFileSystem());
    }

    final IWritableFileSystem getFileSystem() {
        return env.getOutputFileSystem();
    }

    @Override
    public void processSaveLoadRequests() {
        while (!saveRequests.isEmpty()) {
            try {
                doSave(novelRef.get(), saveRequests.removeFirst());
            } catch (IOException e) {
                LOG.warn("Save error", e);
            }
        }
        while (!loadRequests.isEmpty()) {
            try {
                doLoad(novelRef.get(), loadRequests.removeFirst());
            } catch (IOException e) {
                LOG.warn("Load error", e);
            }
        }
    }

    @Override
    public void loadPersistent() {
        SecureFileWriter writer = getSecureFileWriter();

        // Note: loads are in reverse order of saving
        for (IPersistentSavePlugin plugin : Lists.reverse(getPersistentSavePlugins())) {
            plugin.loadPersistent(writer);
        }
    }

    @Override
    public void savePersistent() {
        SecureFileWriter writer = getSecureFileWriter();
        for (IPersistentSavePlugin plugin : getPersistentSavePlugins()) {
            plugin.savePersistent(writer);
        }
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
    public void load(INovel novel, int slot) {
        loadRequests.add(new LoadRequest(slot));
        LOG.info("Load requested: {}", slot);
    }

    private void doLoad(INovel novel, LoadRequest lr) throws IOException {
        IFileSystem arc = openSaveArchive(lr.slot);
        try {
            readSaveData(arc, novel);
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

    private void readSaveData(IFileSystem fs, INovel novel) throws IOException {
        InputStream in = fs.openInputStream(SaveFileConstants.SAVEDATA_PATH);

        // Clean up resources for current environment before we start loading a new one
        savePersistent();
        novel.getEnv().destroy();

        try {
            ObjectDeserializer is = luaSerializer.openDeserializer(in);
            is.setCollectStats(false);
            is.setDepthWarnLimit(125);
            try {
                novel.readAttributes(is);

                /*
                 * Note: be very careful here. We've just recreated the env in novel, but we're still running
                 * in the SaveModule of the previous env.
                 */
                novel.getEnv().getSaveModule().loadPersistent();
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
    public void save(INovel novel, int slot, ISaveParams params) {
        saveRequests.add(new SaveRequest(slot, params));
        LOG.info("Save requested: {}", slot);
    }

    private void doSave(INovel novel, SaveRequest sr) throws IOException {
        // This seems to be a good time to flush other values to disk as well
        savePersistent();

        IWritableFileSystem fs = getFileSystem();

        FilePath savePath = getSavePath(sr.slot);
        ZipOutputStream zout = new ZipOutputStream(fs.openOutputStream(savePath, false));
        try {
            ThumbnailInfo thumbnailInfo = sr.saveParams.getThumbnailInfo();

            // Save header
            SaveFileHeader header = new SaveFileHeader(System.currentTimeMillis());
            header.setThumbnail(thumbnailInfo);
            header.setUserData(sr.saveParams.getUserData());
            SaveFileIO.writeJson(zout, SaveFileConstants.HEADER_PATH, SaveFileHeaderJson.encode(header));

            // Thumbnail
            if (thumbnailInfo != null) {
                SaveFileIO.writeBytes(zout, thumbnailInfo.getPath(), sr.saveParams.getThumbnailData());
            }

            // Save data
            writeSaveData(zout, novel);
        } finally {
            zout.close();
        }

        LOG.info("Save written: {}", StringUtil.formatMemoryAmount(fs.getFileSize(savePath)));
    }

    private void writeSaveData(ZipOutputStream zout, INovel novel) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectSerializer os = luaSerializer.openSerializer(bout);
        try {
            os.setCollectStats(false);
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
        return slot > QUICK_SAVE_OFFSET && slot <= QUICK_SAVE_OFFSET + NUM_QUICK_SAVE_SLOTS;
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
        return slot > AUTO_SAVE_OFFSET && slot <= AUTO_SAVE_OFFSET + NUM_AUTO_SAVE_SLOTS;
    }

    private static final class LoadRequest {

        final int slot;

        LoadRequest(int slot) {
            this.slot = slot;
        }

    }

    private static final class SaveRequest {

        final int slot;
        final ISaveParams saveParams;

        SaveRequest(int slot, ISaveParams saveParams) {
            this.slot = slot;
            this.saveParams = saveParams;
        }

    }

}
