package nl.weeaboo.vn.gdx.graphics;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.JngTextureLoader;

public class JngTextureLoaderTest {

    private static final Logger LOG = LoggerFactory.getLogger(JngTextureLoaderTest.class);

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private JngTestSuite testSuite;
    private AssetManager assetManager;

    @Before
    public void before() throws IOException {
        HeadlessGdx.init();
        testSuite = JngTestSuite.open();

        assetManager = new AssetManager(new AbsoluteFileHandleResolver());
        JngTextureLoader.register(assetManager);
    }

    @After
    public void after() {
        testSuite.dispose();
        assetManager.dispose();
    }

    @Test
    public void loadJng() throws IOException {
        File tempFile = tempFolder.newFile("temp.jng");
        testSuite.extract("TGPN0S.jng", tempFile);

        String filename = tempFile.getAbsolutePath().replace("\\", "/");
        assetManager.load(filename, Texture.class);
        assetManager.finishLoading();
        Texture tex = assetManager.get(filename, Texture.class);
        LOG.info("Loaded texture: {}", tex);
        tex.dispose();
    }

}
