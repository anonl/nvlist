package nl.weeaboo.vn.render.impl;

import java.nio.FloatBuffer;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.google.common.base.Stopwatch;

import nl.weeaboo.common.Rect;
import nl.weeaboo.gdx.graphics.GLBlendMode;
import nl.weeaboo.gdx.graphics.GLMatrixStack;
import nl.weeaboo.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.gdx.graphics.PixmapUtil;
import nl.weeaboo.styledtext.gdx.GdxFontUtil;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.image.impl.PixelTextureData;

public class GLScreenRenderer extends BaseScreenRenderer {

    private boolean destroyed;

    // --- Properties only valid between renderBegin() and renderEnd() beneath this line ---
    private int buffered;
    private final SpriteBatch spriteBatch = new SpriteBatch();
    protected GLMatrixStack matrixStack = new GLMatrixStack(spriteBatch);
    private transient Mesh triangleMesh;
    // -------------------------------------------------------------------------------------

    public GLScreenRenderer(IRenderEnv env, RenderStats stats) {
        super(env, stats);
    }

    @Override
    public void destroy() {
        destroyed = true;
        spriteBatch.dispose();
        if (triangleMesh != null) {
            triangleMesh.dispose();
        }
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void renderBegin() {
        matrixStack.pushMatrix();
        buffered = 0;

        super.renderBegin();

        spriteBatch.begin();
    }

    @Override
    public void renderEnd() {
        spriteBatch.end();

        super.renderEnd();

        matrixStack.popMatrix();
    }

    @Override
    public void renderQuad(QuadRenderCommand qrc) {
        TextureRegion tex = GdxTextureUtil.getTextureRegion(qrc.tex, qrc.uv);

        double x = qrc.bounds.x;
        double y = qrc.bounds.y;
        double w = qrc.bounds.w;
        double h = qrc.bounds.h;

        if (qrc.transform.hasShear()) {
            // Slow path
            matrixStack.pushMatrix();
            matrixStack.multiply(qrc.transform);
            spriteBatch.draw(tex, (float)x, (float)y, (float)w, (float)h);
            matrixStack.popMatrix();
        } else {
            // Optimized path for simple transforms (doesn't trigger a SpriteBatch flush)
            double sx = qrc.transform.getScaleX();
            double sy = qrc.transform.getScaleY();
            x = x * sx + qrc.transform.getTranslationX();
            y = y * sy + qrc.transform.getTranslationY();
            w = w * sx;
            h = h * sy;

            spriteBatch.draw(tex, (float)x, (float)y, (float)w, (float)h);
        }
        buffered++;
    }

    @Override
    public void renderDistortQuad(DistortQuadCommand dqc) {
        // TODO LVN-019 Support this properly
        renderQuad(new QuadRenderCommand(dqc.z, dqc.clipEnabled, dqc.blendMode, dqc.argb, dqc.tex,
                dqc.transform, dqc.bounds, dqc.uv));
    }

    @Override
    public void renderText(TextRenderCommand trc) {
        flushQuadBatch();

        int dx = (int)Math.round(trc.dx);
        int dy = (int)Math.round(trc.dy);

        matrixStack.pushMatrix();
        matrixStack.multiply(trc.transform);
        GdxFontUtil.draw(spriteBatch, trc.textLayout, dx, dy, (float)trc.visibleGlyphs);
        matrixStack.popMatrix();
    }

    @Override
    public void renderCustom(CustomRenderCommand cmd) {
        flushQuadBatch();

        spriteBatch.end();

        matrixStack.pushMatrix();
        matrixStack.multiply(cmd.transform);

        cmd.renderLogic.render(this);

        matrixStack.popMatrix();

        spriteBatch.setShader(null);
        spriteBatch.begin();
    }

    @Override
    public void renderTriangleGrid(TriangleGrid grid, ShaderProgram shader) {
        flushQuadBatch();

        VertexAttributes attrs = grid.getVertexAttributes();

        final float packedColor = spriteBatch.getPackedColor();
        final int rows = grid.getRows();
        final int cols = grid.getCols();
        final int texCount = grid.getTextures();
        final int verticesPerRow = cols * 2;

        // x, y, color, u0, v0, u1, v1, ...
        final int floatsPerVertex = 3 + 2 * texCount;

        // Reuse a single buffer, garbage collecting them is very, very slow.
        int requiredFloats = floatsPerVertex * verticesPerRow;

        if (triangleMesh == null || triangleMesh.getMaxVertices() < cols * 4
                || !triangleMesh.getVertexAttributes().equals(attrs))
        {
            if (triangleMesh != null) {
                triangleMesh.dispose();
            }
            triangleMesh = new Mesh(false, false, cols * 4, cols * 6, attrs);
        }

        // Create an index buffer for a triangle strip
        short[] indices = new short[cols * 6];
        for (int col = 0, i = 0, v = 0; col < cols - 1; col++, i += 2) {
            indices[v++] = (short)(i + 0);
            indices[v++] = (short)(i + 1);
            indices[v++] = (short)(i + 2);
            indices[v++] = (short)(i + 2);
            indices[v++] = (short)(i + 1);
            indices[v++] = (short)(i + 3);
        }
        triangleMesh.setIndices(indices);

        shader.begin();
        shader.setUniformMatrix("u_projTrans", matrixStack.getCombined());
        for (int n = 0; n < texCount; n++) {
            int loc = shader.fetchUniformLocation("u_texture" + n, false);
            if (loc >= 0) {
                shader.setUniformi(loc, n);
            }
        }
        try {
            FloatBuffer buf = FloatBuffer.allocate(requiredFloats);
            for (int row = 0; row < rows; row++) {
                buf.position(0);
                grid.getVertices(row, buf, floatsPerVertex - 2);
                buf.position(2);
                for (int v = 0; v < verticesPerRow; v++) {
                    buf.put(buf.position() + floatsPerVertex * v, packedColor);
                }
                for (int t = 0; t < grid.getTextures(); t++) {
                    buf.position(3 + 2 * t);
                    grid.getTexCoords(t, row, buf, floatsPerVertex - 2);
                }
                buf.rewind();

                triangleMesh.setVertices(buf.array(), 0, requiredFloats);
                triangleMesh.render(shader, GL20.GL_TRIANGLES);
            }
        } finally {
            shader.end();
        }
    }

    @Override
    public void renderScreenshot(IWritableScreenshot ss, Rect glRect) {
        /*
         * TODO Support volatile screenshots (libGDX doesn't seem to have built-in support for GPU-only
         * texturedata objects) EDIT: Support added in 1.7.2 (GLOnlyTextureData)
         */

        Pixmap pixels = ScreenUtils.getFrameBufferPixmap(glRect.x, glRect.y, glRect.w, glRect.h);
        PixmapUtil.flipVertical(pixels);

        PixelTextureData texData = PixelTextureData.fromPixmap(pixels);
        ss.setPixels(texData, renderEnv.getScreenSize());
    }

    @Override
    protected boolean renderUnknownCommand(RenderCommand cmd) {
        return false;
    }

    @Override
    protected void flushQuadBatch() {
        if (buffered == 0) {
            return;
        }

        Stopwatch sw = Stopwatch.createStarted();

        spriteBatch.flush();
        renderStats.onRenderQuadBatch(buffered);
        buffered = 0;

        sw.stop();
        renderStats.logExtra(QuadRenderCommand.class, QuadRenderCommand.ID, sw.elapsed(TimeUnit.NANOSECONDS));
    }

    @Override
    protected void applyColor(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = (argb) & 0xFF;
        spriteBatch.setColor(Color.toFloatBits(r, g, b, a));
    }

    @Override
    protected void applyBlendMode(BlendMode bm) {
        switch (bm) {
        case DEFAULT:
            GLBlendMode.DEFAULT.apply(spriteBatch);
            break;
        case ADD:
            GLBlendMode.ADD.apply(spriteBatch);
            break;
        default:
            GLBlendMode.DISABLED.apply(spriteBatch);
            break;
        }
    }

    @Override
    protected void translate(double dx, double dy) {
        matrixStack.translate(dx, dy);
    }

    @Override
    protected void applyClip(boolean c) {
        if (c) {
            Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        } else {
            Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
        }
    }

    @Override
    protected void applyClipRect(Rect glRect) {
        Gdx.gl.glScissor(glRect.x, glRect.y, glRect.w, glRect.h);
    }

    public void setProjectionMatrix(Matrix4 projectionMatrix) {
        matrixStack.setProjectionMatrix(projectionMatrix);
    }

}
