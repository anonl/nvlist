package nl.weeaboo.gdx.graphics;

import java.util.Deque;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;
import com.google.common.collect.Queues;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.math.Matrix;

public final class GLMatrixStack {

    private static final int MAX_POOL_SIZE = 8;

    private final Deque<Matrix4> matrixPool = Queues.newArrayDeque();

    private final Deque<Matrix4> matrixStack = Queues.newArrayDeque();
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
        Matrix4 copy = alloc(transform);
        matrixStack.push(copy);
    }

    // Take from pool, create a copy only if pool empty
    private Matrix4 alloc(Matrix4 val) {
        Matrix4 pooled = matrixPool.pollFirst();
        if (pooled != null) {
            pooled.set(val);
            return pooled;
        }
        return val.cpy();
    }

    public void popMatrix() {
        Matrix4 popped = matrixStack.pop();
        transform.set(popped);
        free(popped);

        onTransformChanged();
    }

    // Return to pool
    private void free(Matrix4 val) {
        if (matrixPool.size() >= MAX_POOL_SIZE) {
            return;
        }

        matrixPool.addFirst(val);
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
