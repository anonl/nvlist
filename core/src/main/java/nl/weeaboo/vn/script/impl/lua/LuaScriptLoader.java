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
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.impl.ResourceLoader;
import nl.weeaboo.vn.script.IScriptLoader;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.impl.lvn.ICompiledLvnFile;
import nl.weeaboo.vn.script.impl.lvn.ILvnParser;
import nl.weeaboo.vn.script.impl.lvn.LvnParseException;
import nl.weeaboo.vn.script.impl.lvn.LvnParserFactory;

@CustomSerializable
public class LuaScriptLoader implements IScriptLoader {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;

    private static final LuaString PATH = LuaString.valueOf("path");

    private final LuaScriptResourceLoader resourceLoader;
    private final String engineTargetVersion;

    private transient ILvnParser lvnParser;

    private LuaScriptLoader(LuaScriptResourceLoader resourceLoader, String engineTargetVersion) {
        this.resourceLoader = Checks.checkNotNull(resourceLoader);
        this.engineTargetVersion = Checks.checkNotNull(engineTargetVersion);

        initTransients();
    }

    public static LuaScriptLoader newInstance(IEnvironment env) {
        LuaScriptLoader scriptLoader = new LuaScriptLoader(new LuaScriptResourceLoader(env),
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

        lrs.setResourceFinder(new LuaResourceFinder() {
            @Override
            public LuaResource findResource(String filename) {
                try {
                    return luaOpenScript(filename);
                } catch (LvnParseException e) {
                    throw new LuaError(e);
                } catch (FileNotFoundException e) {
                    return null;
                } catch (IOException e) {
                    throw new LuaError(e);
                }
            }
        });
    }

    @Override
    public String findScriptFile(String name) {
        LuaRunState lrs = LuaRunState.getCurrent();
        PackageLib packageLib = lrs.getPackageLib();
        LuaTable packageTable = packageLib.PACKAGE;
        String path = packageTable.get(PATH).tojstring();

        if (getScriptExists(name)) {
            return name;
        }

        for (String pattern : path.split(";")) {
            String filename = pattern.replaceFirst("\\?", name);
            if (getScriptExists(filename)) {
                return filename;
            }
        }
        return name;
    }

    protected boolean getScriptExists(String filename) {
        return resourceLoader.isValidFilename(filename);
    }

    public boolean isBuiltInScript(String filename) {
        return LuaScriptResourceLoader.isBuiltInScript(filename);
    }

    @Override
    public InputStream openScript(String filename) throws IOException {
        return resourceLoader.newInputStream(filename);
    }

    protected long getScriptModificationTime(String filename) throws IOException {
        return resourceLoader.getModifiedTime(filename);
    }

    @Override
    public Collection<String> getScriptFiles(String folder) {
        return resourceLoader.getMediaFiles(folder);
    }

    public ICompiledLvnFile compileScript(String normalizedFilename, InputStream in)
            throws LvnParseException, IOException {

        ICompiledLvnFile file = lvnParser.parseFile(normalizedFilename, in);

//TODO Re-enable analytics
//        IAnalytics analytics = getAnalytics();
//        if (analytics != null) {
//            long modTime = getScriptModificationTime(normalizedFilename);
//            analytics.logScriptCompile(filename, modificationTime);
//        }

//TODO Re-enable seen logging
//        ISeenLog seenLog = getSeenLog();
//        if (seenLog != null) {
//            seenLog.registerScriptFile(filename, file.countTextLines(false));
//        }

        return file;
    }

    LuaResource luaOpenScript(String filename) throws LvnParseException, IOException {
        filename = findScriptFile(filename);

        final byte[] fileData;
        InputStream in = openScript(filename);
        try {
            if (!LuaScriptUtil.isLvnFile(filename)) {
                fileData = StreamUtil.readBytes(in);
            } else {
                ICompiledLvnFile file = compileScript(filename, in);
                String contents = file.getCompiledContents();
                fileData = StringUtil.toUTF8(contents);
            }
        } finally {
            in.close();
        }

        return new LuaResource(filename) {
            @Override
            public InputStream open() throws IOException {
                return new ByteArrayInputStream(fileData);
            }
        };
    }

    @Override
    public void loadScript(IScriptThread thread, String filename) throws IOException, ScriptException {
        filename = findScriptFile(filename);

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
            super(env.getResourceLoadLog());

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

        public long getModifiedTime(String filename) throws IOException {
            if (isBuiltInScript(filename)) {
                return 0L;
            }
            return getFileSystem().getFileModifiedTime(filename);
        }

        @Override
        protected List<String> getFiles(String folder) throws IOException {
            List<String> out = new ArrayList<String>();
            getFileSystem().getFiles(out, folder, true);
            return out;
        }

    }

}
