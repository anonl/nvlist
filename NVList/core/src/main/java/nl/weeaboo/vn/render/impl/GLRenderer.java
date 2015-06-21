package nl.weeaboo.vn.render.impl;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.gdx.gl.GLBlendMode;
import nl.weeaboo.gdx.gl.GLMatrixStack;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.impl.AlignUtil;
import nl.weeaboo.vn.image.impl.TextureAdapter;

public abstract class GLRenderer extends BaseRenderer {

    //--- Properties only valid between renderBegin() and renderEnd() beneath this line ---
    private int buffered;
    private final SpriteBatch spriteBatch = new SpriteBatch();
    protected GLMatrixStack matrixStack = new GLMatrixStack(spriteBatch);
    //-------------------------------------------------------------------------------------

    public GLRenderer(IRenderEnv env, RenderStats stats) {
        super(env, stats);
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
        TextureAdapter ta = (TextureAdapter)qrc.tex;
        TextureRegion tex = ta.getTextureRegion(qrc.uv);

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
            // Optimized path for scale+translate transforms
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
    public void renderBlendQuad(BlendQuadCommand bqc) {
        // TODO LVN-019 Support this properly
        Rect2D bounds0 = AlignUtil.getAlignedBounds(bqc.tex0, bqc.alignX0, bqc.alignY0);
        renderQuad(new QuadRenderCommand(bqc.z, bqc.clipEnabled, bqc.blendMode, bqc.argb,
                bqc.tex0, bqc.transform, bounds0.toArea2D(), bqc.uv));
    }

    @Override
    public void renderFadeQuad(FadeQuadCommand fqc) {
        // TODO LVN-019 Support this properly
        renderQuad(new QuadRenderCommand(fqc.z, fqc.clipEnabled, fqc.blendMode, fqc.argb,
                fqc.tex, fqc.transform, fqc.bounds, fqc.uv));
    }

    @Override
    public void renderDistortQuad(DistortQuadCommand dqc) {
        // TODO LVN-019 Support this properly
        renderQuad(new QuadRenderCommand(dqc.z, dqc.clipEnabled, dqc.blendMode, dqc.argb,
                dqc.tex, dqc.transform, dqc.bounds, dqc.uv));
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

        spriteBatch.flush();        
        renderStats.onRenderQuadBatch(buffered);
        buffered = 0;
    }

    @Override
    protected void setColor(int argb) {
        spriteBatch.setColor(argb);
    }

    @Override
    protected void setBlendMode(BlendMode bm) {
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

}
