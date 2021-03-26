package nl.weeaboo.vn.buildtools.file;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TempFileProviderTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private TempFileProvider tempFileProvider;

    @Before
    public void before() {
        tempFileProvider = new TempFileProvider(tempFolder.getRoot());
    }

    @Test
    public void testDeleteAll() throws IOException {
        File alpha = tempFileProvider.newTempFile();
        File beta = tempFileProvider.newTempFile();

        tempFileProvider.deleteAll();

        Assert.assertEquals(false, alpha.exists());
        Assert.assertEquals(false, beta.exists());
    }

}
