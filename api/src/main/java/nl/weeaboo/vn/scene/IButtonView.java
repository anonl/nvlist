package nl.weeaboo.vn.scene;

public interface IButtonView {

    /**
     * @see IDrawable#isVisible(double)
     */
    boolean isVisible(double minAlpha);

    /**
     * @return {@code true} if the specified point lies inside the clickable area of the button.
     */
    boolean contains(double cx, double cy);

}
