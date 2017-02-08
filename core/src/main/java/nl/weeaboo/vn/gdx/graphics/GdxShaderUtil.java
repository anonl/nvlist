package nl.weeaboo.vn.gdx.graphics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import nl.weeaboo.vn.image.ITexture;

public final class GdxShaderUtil {

    private static final Logger LOG = LoggerFactory.getLogger(GdxShaderUtil.class);

    private GdxShaderUtil() {
    }

    /**
     * Sets a texture uniform of the given shader.
     */
    public static void setTexture(ShaderProgram shader, int texUnit, ITexture tex) {
        setTexture(shader, texUnit, tex, "u_texture" + texUnit);
    }

    /**
     * Sets a texture uniform of the given shader.
     */
    public static void setTexture(ShaderProgram shader, int texUnit, ITexture tex, String uniformName) {
        GdxTextureUtil.bindTexture(texUnit, tex);

        int loc = shader.fetchUniformLocation(uniformName, false);
        if (loc >= 0) {
            LOG.trace("Set shader uniform: loc={}, name={}", loc, uniformName);
            shader.setUniformi(loc, texUnit);
        } else {
            LOG.warn("Invalid shader uniform: {}", uniformName);
        }
    }

}
