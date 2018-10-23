package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Disposable;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.ZipFileArchive;
import nl.weeaboo.io.RandomAccessUtil;

public final class JngTestSuite implements Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(JngTestSuite.class);

    private ZipFileArchive archive;

    private JngTestSuite(ZipFileArchive archive) {
        this.archive = archive;
    }

    /**
     * Opens the test suit archive. After opening, the test suite must be manually disposed by calling the
     * {@link #dispose()} method.
     *
     * @throws IOException If the JNG test suite can't be read.
     */
    public static JngTestSuite open() throws IOException {
        byte[] zipBytes = Resources.toByteArray(JngReaderTest.class.getResource("/jng/JNGsuite-20021214.zip"));

        ZipFileArchive archive = new ZipFileArchive();
        archive.open(RandomAccessUtil.wrap(zipBytes, 0, zipBytes.length));
        return new JngTestSuite(archive);
    }

    @Override
    public void dispose() {
        archive.close();
    }

    /**
     * Reads a single image from the test suite archive.
     *
     * @throws IOException If the requested file can't be found or decoded.
     */
    public Pixmap loadImage(String path) throws IOException {
        InputStream in = archive.openInputStream(FilePath.of(path));
        try {
            LOG.info("Reading JNG file: {}", path);
            return JngReader.read(in, new JngReaderOpts());
        } finally {
            in.close();
        }
    }

    /**
     * Extracts a single image from the test suite and writes it to a file.
     *
     * @throws IOException If the requested image can't be read from the test suite archive, or can't be
     *         written to the output file.
     */
    public void extract(String path, File outputFile) throws IOException {
        byte[] contents = FileSystemUtil.readBytes(archive, FilePath.of(path));

        Files.write(contents, outputFile);
    }

}
