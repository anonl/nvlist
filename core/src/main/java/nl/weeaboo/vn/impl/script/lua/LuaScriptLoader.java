package nl.weeaboo.vn.impl.script.lua;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FileCollectOptions;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemView;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.io.StreamUtil;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.lib.BaseLib;
import nl.weeaboo.lua2.lib.LuaResource;
import nl.weeaboo.lua2.lib.LuaResourceFinder;
import nl.weeaboo.lua2.lib.PackageLib;
import nl.weeaboo.lua2.vm.LuaError;
import nl.weeaboo.lua2.vm.LuaString;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ISeenLogHolder;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.impl.core.ResourceLoader;
import nl.weeaboo.vn.impl.script.lvn.ICompiledLvnFile;
import nl.weeaboo.vn.impl.script.lvn.ILvnParser;
import nl.weeaboo.vn.impl.script.lvn.LvnParseException;
import nl.weeaboo.vn.impl.script.lvn.LvnParserFactory;
import nl.weeaboo.vn.script.IScriptLoader;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

@CustomSerializable
public class LuaScriptLoader implements IScriptLoader, LuaResourceFinder {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;

    private static final LuaString PATH = LuaString.valueOf("path");

    private final ISeenLogHolder seenLog;
    private final LuaScriptResourceLoader resourceLoader;
    private final String engineTargetVersion;

    private transient ILvnParser lvnParser;

    private LuaScriptLoader(ISeenLogHolder seenLog, LuaScriptResourceLoader resourceLoader,
            String engineTargetVersion) {
        this.seenLog = Checks.checkNotNull(seenLog);
        this.resourceLoader = Checks.checkNotNull(resourceLoader);
        this.engineTargetVersion = Checks.checkNotNull(engineTargetVersion);

        initTransients();
    }

    /** Creates a new script loader */
    public static LuaScriptLoader newInstance(IEnvironment env) {
        LuaScriptLoader scriptLoader = new LuaScriptLoader(env.getSeenLog(), new LuaScriptResourceLoader(env),
                env.getPref(NovelPrefs.ENGINE_TARGET_VERSION));
        scriptLoader.initEnv();
        return scriptLoader;
    }

    private void initTransients() {
        lvnParser = LvnParserFactory.getParser(engineTargetVersion);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    void initEnv() {
        LuaRunState lrs = LuaRunState.getCurrent();

        PackageLib packageLib = lrs.getPackageLib();
        packageLib.setLuaPath("?.lvn;?.lua");

        lrs.setResourceFinder(this);
    }

    @Override
    public @Nullable LuaResource findResource(String filename) {
        ResourceId resourceId = resolveResource(FilePath.of(filename));
        if (resourceId == null) {
            return null;
        }

        try {
            return luaOpenScript(resourceId);
        } catch (LvnParseException e) {
            throw new LuaError(e);
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new LuaError(e);
        }
    }

    @Override
    public @Nullable ResourceId resolveResource(FilePath path) {
        if (getScriptExists(path)) {
            return new ResourceId(MediaType.SCRIPT, path);
        }

        // Use Lua PATH to find the file
        LuaRunState lrs = LuaRunState.getCurrent();
        PackageLib packageLib = lrs.getPackageLib();
        for (String pattern : packageLib.PACKAGE.get(PATH).tojstring().split(";")) {
            FilePath filename = FilePath.of(pattern.replaceFirst("\\?", path.toString()));
            if (getScriptExists(filename)) {
                return new ResourceId(MediaType.SCRIPT, filename);
            }
        }
        return null;
    }

    protected boolean getScriptExists(FilePath filename) {
        return resourceLoader.isValidFilename(filename);
    }

    @Override
    public InputStream openScript(FilePath filename) throws IOException {
        ResourceId resourceId = resolveResource(filename);
        if (resourceId == null) {
            throw new FileNotFoundException(filename.toString());
        }
        return resourceLoader.newInputStream(resourceId.getFilePath());
    }

    @Override
    public Collection<FilePath> getScriptFiles(FilePath folder) {
        return resourceLoader.getMediaFiles(folder);
    }

    ICompiledLvnFile compileScript(ResourceId resourceId, InputStream in)
            throws LvnParseException, IOException {

        ICompiledLvnFile file = lvnParser.parseFile(resourceId.getFilePath(), in);

        //TODO Re-enable analytics
        //        IAnalytics analytics = getAnalytics();
        //        if (analytics != null) {
        //            long modTime = getScriptModificationTime(normalizedFilename);
        //            analytics.logScriptCompile(filename, modificationTime);
        //        }

        seenLog.getScriptLog().registerScriptFile(resourceId, file.countTextLines(false));

        return file;
    }

    LuaResource luaOpenScript(ResourceId resourceId) throws LvnParseException, IOException {
        FilePath path = resourceId.getFilePath();
        String pathString = path.toString();

        final byte[] fileData;
        InputStream in = resourceLoader.newInputStream(path);
        try {
            if (!LuaScriptUtil.isLvnFile(pathString)) {
                fileData = StreamUtil.readBytes(in);
            } else {
                ICompiledLvnFile file = compileScript(resourceId, in);
                String contents = file.getCompiledContents();
                fileData = StringUtil.toUTF8(contents);
            }
        } finally {
            in.close();
        }

        return new LuaResource(pathString) {
            @Override
            public InputStream open() throws IOException {
                return new ByteArrayInputStream(fileData);
            }
        };
    }

    @Override
    public void loadScript(IScriptThread thread, FilePath filename) throws IOException, ScriptException {
        LuaScriptThread luaThread = (LuaScriptThread)thread;

        Varargs loadResult = BaseLib.loadFile(filename.toString());
        if (!loadResult.arg1().isclosure()) {
            throw new ScriptException("Error loading script, " + filename + ": " + loadResult.arg(2));
        }

        luaThread.call(loadResult.checkclosure(1));
    }

    private static class LuaScriptResourceLoader extends ResourceLoader {

        private static final long serialVersionUID = 1L;

        private final IEnvironment env;

        private transient FileSystemView cachedFileSystemView;

        public LuaScriptResourceLoader(IEnvironment env) {
            super(MediaType.OTHER, env.getResourceLoadLog());

            this.env = env;
        }

        protected final FileSystemView getFileSystem() {
            if (cachedFileSystemView == null) {
                cachedFileSystemView = new FileSystemView(env.getFileSystem(), FilePath.of("script/"));
            }
            return cachedFileSystemView;
        }

        public static boolean isBuiltInScript(FilePath filename) {
            return filename.toString().startsWith("builtin/");
        }

        public static URL getClassResource(FilePath filename) {
            return LuaScriptLoader.class.getResource("/script/" + filename);
        }

        @Override
        protected boolean isValidFilename(FilePath filename) {
            if (isBuiltInScript(filename)) {
                return getClassResource(filename) != null;
            }
            return getFileSystem().getFileExists(filename);
        }

        public InputStream newInputStream(FilePath filename) throws IOException {
            if (isBuiltInScript(filename)) {
                URL url = LuaScriptResourceLoader.getClassResource(filename);
                if (url == null) {
                    throw new FileNotFoundException(filename.toString());
                }
                return url.openStream();
            }

            return getFileSystem().openInputStream(filename);
        }

        @Override
        protected List<FilePath> getFiles(FilePath folder) throws IOException {
            FileCollectOptions opts = FileCollectOptions.files(folder);
            return ImmutableList.copyOf(getFileSystem().getFiles(opts));
        }

    }

}
