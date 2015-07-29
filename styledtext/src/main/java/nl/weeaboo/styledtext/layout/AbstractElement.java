package nl.weeaboo.styledtext.layout;

abstract class AbstractElement implements ILayoutElement {

    private float x;
    private float y;
    private float layoutWidth;
    private float layoutHeight;

    public AbstractElement() {
        this(0, 0);
    }

    public AbstractElement(float w, float h) {
        layoutWidth = w;
        layoutHeight = h;
    }

    @Override
    public float getLayoutWidth() {
        return layoutWidth;
    }

    public void setLayoutWidth(float layoutWidth) {
        this.layoutWidth = layoutWidth;
    }

    @Override
    public float getLayoutHeight() {
        return layoutHeight;
    }

    public void setLayoutHeight(float layoutHeight) {
        this.layoutHeight = layoutHeight;
    }

    @Override
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

}
