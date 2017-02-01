package nl.weeaboo.vn.impl.layout;

import nl.weeaboo.vn.layout.ILayoutElem;

public final class NullLayout extends LayoutGroup {

    private static final long serialVersionUID = 1L;

    public NullLayout(ILayoutElemPeer visualElem) {
        super(visualElem);
    }

    @Override
    public void remove(ILayoutElem elem) {
    }

    @Override
    protected void doLayout() {
        // Do nothing
    }

}
