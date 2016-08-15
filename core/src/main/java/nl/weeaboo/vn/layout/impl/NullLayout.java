package nl.weeaboo.vn.layout.impl;

public final class NullLayout extends LayoutGroup {

    private static final long serialVersionUID = 1L;

    public NullLayout(ILayoutElemPeer visualElem) {
        super(visualElem);
    }

    @Override
    protected void doLayout() {
        // Do nothing
    }

}
