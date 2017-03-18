package nl.weeaboo.vn.scene;

public interface IScrollable {

    /**
     * The horizontal scroll offset. A higher x-scroll values moves the contents leftwards, relative to the viewport.
     */
    double getScrollX();

    /**
     * The vertical scroll offset. A higher y-scroll value moves the contents upwards, relative to the viewport.
     */
    double getScrollY();

    /**
     * Relative scroll method.
     * @see #getScrollX()
     * @see #getScrollY()
     * @see #setScroll(double, double)
     */
    void scroll(double dx, double dy);

    /**
     * Sets the absolute scroll position.
     * @see #scroll(double, double)
     */
    void setScroll(double x, double y);

}
