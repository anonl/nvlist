package nl.weeaboo.vn.impl.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.annotation.Nullable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.test.SerializeTester;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.impl.core.ResourceLoader.EFileExtCheckResult;
import nl.weeaboo.vn.stats.IResourceSeenLog;

public class FileResourceLoaderTest {

    private static final FilePath BASE_FOLDER = FilePath.of("base/");

    private TestEnvironment env;
    private TestResourceLoader resourceLoader;

    @Before
    public void before() throws IOException {
        env = TestEnvironment.newInstance();

        IWritableFileSystem outputFileSystem = env.getOutputFileSystem();
        writeTestFiles(outputFileSystem);

        resourceLoader = new TestResourceLoader(env);
    }

    private void writeTestFiles(IWritableFileSystem fs) throws IOException {
        writeString(fs, "base/valid.png");
        writeString(fs, "base/valid.jpg");
        writeString(fs, "base/subfolder/a.txt");
        writeString(fs, "base/subfolder/b.txt");
        writeString(fs, "invalid.jpg");
    }

    @Test
    public void isValidFilename() {
        // Resource loader paths are relative to the base folder
        assertValidFilename(true, "valid.jpg");
        assertValidFilename(true, "valid.png");
        assertValidFilename(true, "subfolder/a.txt");
        assertValidFilename(true, "subfolder/b.txt");
        assertValidFilename(false, "invalid.jpg");

        // Null filenames just return false and don't throw an exception
        assertValidFilename(false, null);
    }

    @Test
    public void normalizeFilename() {
        resourceLoader.setAutoFileExts("png", "jpg");
        assertNormalizedFilename("valid.png", "valid");
        assertNormalizedFilename("valid.jpg", "valid.jpg");
        assertNormalizedFilename(null, "subfolder/a");
        resourceLoader.setAutoFileExts("txt");
        assertNormalizedFilename("subfolder/a.txt", "subfolder/a");

        // Null filenames just return null and don't throw an exception
        assertNormalizedFilename(null, null);
    }

    /** Check behavior when changing the base resource folder at runtime */
    @Test
    public void changeResourceFolder() {
        Assert.assertEquals(BASE_FOLDER, resourceLoader.getResourceFolder());
        assertValidFilename(true, "valid.jpg");
        assertValidFilename(false, "a.txt");

        // Change resource folder
        FilePath subFolder = BASE_FOLDER.resolve("subfolder/");
        resourceLoader.setResourceFolder(subFolder);
        Assert.assertEquals(subFolder, resourceLoader.getResourceFolder());
        Assert.assertEquals(subFolder.resolve("test"), resourceLoader.getAbsolutePath(FilePath.of("test")));
        assertValidFilename(false, "valid.jpg");
        assertValidFilename(true, "a.txt");
        assertFiles(Arrays.asList("a.txt", "b.txt"));
    }

    /** Check that the preload method calls preloadNormalized for any valid filename */
    @Test
    public void testPreload() {
        resourceLoader.setAutoFileExts("png");

        assertPreload("valid.jpg", "valid.jpg");
        assertPreload("valid.png", "valid.png");
        // Incorrect file ext is automatically fixed
        assertPreload("valid.png", "valid.txt");
        assertPreload("valid.png", "valid");

        assertPreload(null, "invalid");
        // Further attempts to load an invalid resource still fail, but follow a slightly different code path
        assertPreload(null, "invalid");

        // Set a preload handler, check that it's called
        PreloadHandlerStub preloadHandler = new PreloadHandlerStub();
        resourceLoader.setPreloadHandler(preloadHandler);
        assertPreload("valid.png", "valid");
        preloadHandler.consumePreloaded(new ResourceId(MediaType.OTHER, FilePath.of("valid.png")));
    }

    @Test
    public void testSerialize() {
        FileResourceLoader reserialized = SerializeTester.reserialize(resourceLoader);

        Assert.assertEquals(true, reserialized.isValidFilename(FilePath.of("valid.jpg")));
    }

    @Test
    public void testCheckRedundantFileExt() {
        resourceLoader.setCheckFileExts(false);
        checkFileExt("valid.jpg", EFileExtCheckResult.DISABLED);

        resourceLoader.setAutoFileExts("jpg");
        resourceLoader.setCheckFileExts(true);
        checkFileExt("valid.txt", EFileExtCheckResult.INVALID);
        checkFileExt("valid.jpg", EFileExtCheckResult.REDUNDANT);
        // As an optimization, each file path is only checked once
        checkFileExt("valid.jpg", EFileExtCheckResult.ALREADY_CHECKED);
        checkFileExt("valid", EFileExtCheckResult.OK);
    }

    @Test
    public void testLogLoad() {
        // Log a resource load
        FilePath filePath = FilePath.of("valid.jpg");
        ResourceId resourceId = resourceLoader.resolveResource(filePath);
        resourceLoader.logLoad(resourceId, new ResourceLoadInfo(MediaType.OTHER, filePath));

        // The resource load event is passed along to the stats module
        IResourceSeenLog resourceLog = env.getStatsModule().getSeenLog().getResourceLog();
        Assert.assertEquals(true, resourceLog.hasSeen(resourceId));
    }

    private void checkFileExt(@Nullable String inputFilename, EFileExtCheckResult expectedResult) {
        Assert.assertEquals(expectedResult, resourceLoader.checkRedundantFileExt(toPath(inputFilename)));
    }

    private void assertPreload(String expectedNormalized, String inputFilename) {
        resourceLoader.lastPreload = null;
        resourceLoader.preload(FilePath.of(inputFilename));

        String actual = null;
        ResourceId lastPreload = resourceLoader.lastPreload;
        if (lastPreload != null) {
            actual = lastPreload.getFilePath().toString();
        }
        Assert.assertEquals(expectedNormalized, actual);
    }

    private void assertFiles(Collection<String> expected) {
        assertFiles(FilePath.empty(), expected);
    }

    private void assertFiles(FilePath folder, Collection<String> expected) {
        ImmutableSet.Builder<FilePath> expectedSet = ImmutableSet.builder();
        for (String exp : expected) {
            expectedSet.add(FilePath.of(exp));
        }

        Assert.assertEquals(expectedSet.build(), ImmutableSet.copyOf(resourceLoader.getMediaFiles(folder)));
    }

    private void assertNormalizedFilename(String expectedNormalized, String inputFilename) {
        ResourceId resourceId = resourceLoader.resolveResource(toPath(inputFilename));
        String actual = (resourceId != null ? resourceId.getFilePath().toString() : null);
        Assert.assertEquals(expectedNormalized, actual);
    }

    private @Nullable FilePath toPath(@Nullable String inputFilename) {
        return inputFilename != null ? FilePath.of(inputFilename) : null;
    }

    private void assertValidFilename(boolean expectedValid, String path) {
        Assert.assertEquals(expectedValid, resourceLoader.isValidFilename(
                toPath(path)));
    }

    private static void writeString(IWritableFileSystem fs, String path) throws IOException {
        FileSystemUtil.writeString(fs, FilePath.of(path), path);
    }

    private static final class TestResourceLoader extends FileResourceLoader {

        private static final long serialVersionUID = 1L;

        private @Nullable ResourceId lastPreload;

        public TestResourceLoader(IEnvironment env) {
            super(env, MediaType.OTHER, BASE_FOLDER);
        }

        @Override
        protected void preloadNormalized(ResourceId resourceId) {
            super.preloadNormalized(resourceId);

            lastPreload = resourceId;
        }

    }
}
