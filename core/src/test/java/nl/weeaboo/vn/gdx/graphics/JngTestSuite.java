package nl.weeaboo.vn.gdx.graphics;

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
import nl.weeaboo.vn.gdx.graphics.JngReader;

class JngTestSuite implements Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(JngTestSuite.class);

    private ZipFileArchive archive;

    private JngTestSuite(ZipFileArchive archive) {
        this.archive = archive;
    }

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

    public Pixmap loadImage(String path) throws IOException {
        InputStream in = archive.openInputStream(FilePath.of(path));
        try {
            LOG.info("Reading JNG file: {}", path);
            return JngReader.read(in);
        } finally {
            in.close();
        }
    }

    public void extract(String path, File outputFile) throws IOException {
        byte[] contents = FileSystemUtil.readBytes(archive, FilePath.of(path));

        Files.write(contents, outputFile);
    }

}
