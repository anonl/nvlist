package nl.weeaboo.vn.script.impl.lua;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
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
import nl.weeaboo.vn.core.ISeenLog;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.impl.ResourceLoader;
import nl.weeaboo.vn.script.IScriptLoader;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.impl.lvn.ICompiledLvnFile;
import nl.weeaboo.vn.script.impl.lvn.ILvnParser;
import nl.weeaboo.vn.script.impl.lvn.LvnParseException;
import nl.weeaboo.vn.script.impl.lvn.LvnParserFactory;

@CustomSerializable
public class LuaScriptLoader implements IScriptLoader, LuaResourceFinder {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;

    private static final LuaString PATH = LuaString.valueOf("path");

    private final ISeenLog seenLog;
    private final LuaScriptResourceLoader resourceLoader;
    private final String engineTargetVersion;

    private transient ILvnParser lvnParser;

    private LuaScriptLoader(ISeenLog seenLog, LuaScriptResourceLoader resourceLoader,
            String engineTargetVersion) {
        this.seenLog = Checks.checkNotNull(seenLog);
        this.resourceLoader = Checks.checkNotNull(resourceLoader);
        this.engineTargetVersion = Checks.checkNotNull(engineTargetVersion);

        initTransients();
    }

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
    public LuaResource findResource(String filename) {
        ResourceId resourceId = resolveResource(filename);
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
    public ResourceId resolveResource(String name) {
        if (getScriptExists(name)) {
            return new ResourceId(MediaType.SCRIPT, name);
        }

        // Use Lua PATH to find the file
        LuaRunState lrs = LuaRunState.getCurrent();
        PackageLib packageLib = lrs.getPackageLib();
        String path = packageLib.PACKAGE.get(PATH).tojstring();

        for (String pattern : path.split(";")) {
            String filename = pattern.replaceFirst("\\?", name);
            if (getScriptExists(filename)) {
                return new ResourceId(MediaType.SCRIPT, filename);
            }
        }

        return null;
    }

    protected boolean getScriptExists(String filename) {
        return resourceLoader.isValidFilename(filename);
    }

    public boolean isBuiltInScript(String filename) {
        return LuaScriptResourceLoader.isBuiltInScript(filename);
    }

    @Override
    public InputStream openScript(String filename) throws IOException {
        ResourceId resourceId = resolveResource(filename);
        if (resourceId == null) {
            throw new FileNotFoundException(filename);
        }
        return resourceLoader.newInputStream(resourceId.getCanonicalFilename());
    }

    @Override
    public Collection<String> getScriptFiles(String folder) {
        return resourceLoader.getMediaFiles(folder);
    }

    public ICompiledLvnFile compileScript(ResourceId resourceId, InputStream in)
            throws LvnParseException, IOException {

        ICompiledLvnFile file = lvnParser.parseFile(resourceId.getCanonicalFilename(), in);

//TODO Re-enable analytics
//        IAnalytics analytics = getAnalytics();
//        if (analytics != null) {
//            long modTime = getScriptModificationTime(normalizedFilename);
//            analytics.logScriptCompile(filename, modificationTime);
//        }

        seenLog.registerScriptFile(resourceId, file.countTextLines(false));

        return file;
    }

    LuaResource luaOpenScript(ResourceId resourceId) throws LvnParseException, IOException {
        final byte[] fileData;
        InputStream in = resourceLoader.newInputStream(resourceId.getCanonicalFilename());
        try {
            if (!LuaScriptUtil.isLvnFile(resourceId.getCanonicalFilename())) {
                fileData = StreamUtil.readBytes(in);
            } else {
                ICompiledLvnFile file = compileScript(resourceId, in);
                String contents = file.getCompiledContents();
                fileData = StringUtil.toUTF8(contents);
            }
        } finally {
            in.close();
        }

        return new LuaResource(resourceId.getCanonicalFilename()) {
            @Override
            public InputStream open() throws IOException {
                return new ByteArrayInputStream(fileData);
            }
        };
    }

    @Override
    public void loadScript(IScriptThread thread, String filename) throws IOException, ScriptException {
        LuaScriptThread luaThread = (LuaScriptThread)thread;

        Varargs loadResult = BaseLib.loadFile(filename);
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
                cachedFileSystemView = new FileSystemView(env.getFileSystem(), "script/");
            }
            return cachedFileSystemView;
        }

        public static boolean isBuiltInScript(String filename) {
            return filename.startsWith("builtin/");
        }

        public static URL getClassResource(String filename) {
            return LuaScriptLoader.class.getResource("/script/" + filename);
        }

        @Override
        protected boolean isValidFilename(String filename) {
            if (isBuiltInScript(filename)) {
                return getClassResource(filename) != null;
            }
            return getFileSystem().getFileExists(filename);
        }

        public InputStream newInputStream(String filename) throws IOException {
            if (isBuiltInScript(filename)) {
                URL url = LuaScriptResourceLoader.getClassResource(filename);
                if (url == null) {
                    throw new FileNotFoundException(filename);
                }
                return url.openStream();
            }

            return getFileSystem().openInputStream(filename);
        }

        @Override
        protected List<String> getFiles(String folder) throws IOException {
            List<String> out = new ArrayList<String>();
            getFileSystem().getFiles(out, folder, true);
            return out;
        }

    }

}
