package nl.weeaboo.vn.impl.image;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.gdx.res.IResource;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.TestEnvironment;

public final class GdxTextureStoreTest {

    private TestEnvironment env;
    private GdxTextureStore texStore;

    @Before
    public void before() {
        env = TestEnvironment.newInstance();
        texStore = StaticEnvironment.TEXTURE_STORE.get();
    }

    @After
    public void after() {
        env.destroy();
    }

    /**
     * Check that the generated {@link TextureParameter} matches the corresponding {@link IImageDefinition}
     * settings for that texture.
     */
    @Test
    public void testLoadParams() {
        FilePath imagePath = FilePath.of("img/loadparams/loadparams.png");

        IResource<Texture> texResource = texStore.getResource(imagePath);
        Assert.assertNotNull(texResource);
        Texture tex = texResource.get();
        Assert.assertNotNull(tex);

        Assert.assertEquals(TextureFilter.MipMapLinearLinear, tex.getMinFilter());
        Assert.assertEquals(TextureFilter.Nearest, tex.getMagFilter());
        Assert.assertEquals(TextureWrap.ClampToEdge, tex.getUWrap());
        Assert.assertEquals(TextureWrap.Repeat, tex.getVWrap());
    }

}
