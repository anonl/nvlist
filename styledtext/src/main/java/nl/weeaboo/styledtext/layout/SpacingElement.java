package nl.weeaboo.styledtext.layout;

public final class SpacingElement extends AbstractElement {

    public SpacingElement() {
        super();
    }

    public SpacingElement(float w, float h) {
        super(w, h);
    }

    @Override
    public boolean isWhitespace() {
        return true;
    }

}
