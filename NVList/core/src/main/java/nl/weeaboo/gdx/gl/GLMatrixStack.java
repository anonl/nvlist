package nl.weeaboo.gdx.gl;

import java.util.ArrayDeque;
import java.util.Deque;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Matrix4;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.math.Matrix;

public final class GLMatrixStack {

    private final Deque<Matrix4> matrixStack = new ArrayDeque<Matrix4>();
    private final Matrix4 matrix = new Matrix4();
    private final Batch batch;
    
    /**
     * @param batch The batch that should be updated whenever the transform changes.
     */
    public GLMatrixStack(Batch batch) {
        this.batch = Checks.checkNotNull(batch);
    }
    
    public void pushMatrix() {
        matrixStack.push(matrix.cpy());
    }
    
    public void popMatrix() {
        matrix.set(matrixStack.pop());
        onTransformChanged();
    }
    
    public void multiply(Matrix m) {
        Matrix4.mul(matrix.val, m.toGLMatrix());
        onTransformChanged();
    }

    public void multiply(Matrix4 m) {
        matrix.mul(m);
        onTransformChanged();
    }
    
    public void translate(double dx, double dy) {
        translate((float)dx, (float)dy);
    }
    public void translate(float dx, float dy) {
        matrix.translate(dx, dy, 0);
        onTransformChanged();
    }
    
    public void scale(double dx, double dy) {
        scale((float)dx, (float)dy);
    }
    public void scale(float sx, float sy) {
        matrix.scale(sx, sy, 1);
        onTransformChanged();
    }

    protected void onTransformChanged() {
        batch.setTransformMatrix(matrix);
    }
    
}
