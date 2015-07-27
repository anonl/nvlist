package nl.weeaboo.gdx.gl;

import java.util.ArrayDeque;
import java.util.Deque;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.math.Matrix;

public final class GLMatrixStack {

    private final Deque<Matrix4> matrixStack = new ArrayDeque<Matrix4>();
    private final Matrix4 transform = new Matrix4();
    private final Matrix4 projection = new Matrix4();
    private final Matrix4 combined = new Matrix4();
    private boolean combinedDirty = false;
    private final Batch batch;

    /**
     * @param batch The batch that should be updated whenever the transform changes.
     */
    public GLMatrixStack(Batch batch) {
        this.batch = Checks.checkNotNull(batch);
    }

    public void pushMatrix() {
        matrixStack.push(transform.cpy());
    }

    public void popMatrix() {
        transform.set(matrixStack.pop());
        onTransformChanged();
    }

    public void multiply(Matrix m) {
        Matrix4.mul(transform.val, m.toGLMatrix());
        onTransformChanged();
    }

    public void multiply(Matrix4 m) {
        transform.mul(m);
        onTransformChanged();
    }

    public void translate(double dx, double dy) {
        translate((float)dx, (float)dy);
    }
    public void translate(float dx, float dy) {
        transform.translate(dx, dy, 0);
        onTransformChanged();
    }

    public void scale(double dx, double dy) {
        scale((float)dx, (float)dy);
    }
    public void scale(float sx, float sy) {
        transform.scale(sx, sy, 1);
        onTransformChanged();
    }

    public Matrix4 getCombined() {
        if (combinedDirty) {
            combinedDirty = false;
            combined.set(projection).mul(transform);
        }
        return combined;
    }

    protected void onTransformChanged() {
        batch.setTransformMatrix(transform);
        combinedDirty = true;
    }

    public void setProjectionMatrix(Matrix4 m) {
        projection.set(m);
        onProjectionChanged();
    }

    protected void onProjectionChanged() {
        batch.setProjectionMatrix(projection);
        combinedDirty = true;
    }

}
