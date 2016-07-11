package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.vn.core.impl.DestructibleElemList;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.scene.signal.VisualElementDestroySignal;
import nl.weeaboo.vn.signal.ISignal;

public class VisualGroup extends VisualElement implements IVisualGroup {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final DestructibleElemList<IVisualElement> children = new DestructibleElemList<IVisualElement>();

    public VisualGroup() {
    }

    public VisualGroup(IVisualGroup parent) {
        super();

        this.parent = parent;
    }

    @Override
    public void handleSignal(ISignal signal) {
        if (signal instanceof VisualElementDestroySignal) {
            IVisualElement elem = ((VisualElementDestroySignal)signal).getDestroyedElement();
            if (children.contains(elem)) {
                onChildDestroyed(elem);
            }
        }

        super.handleSignal(signal);
    }

    protected void add(IVisualElement elem) {
        children.add(elem);
        elem.setParent(this);
    }

    protected void remove(IVisualElement elem) {
        children.remove(elem);
        if (elem.getParent() == this) {
            elem.setParent(null);
        }
    }

    @Override
    protected void onDestroyed() {
        super.onDestroyed();

        children.destroyAll();
    }

    protected void onChildDestroyed(IVisualElement elem) {
        remove(elem);
    }

    @Override
    public Iterable<? extends IVisualElement> getChildren() {
        return children;
    }

    @Override
    public boolean contains(IVisualElement elem) {
        return children.contains(elem);
    }

}
