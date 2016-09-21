package nl.weeaboo.vn.scene.impl;

import java.io.Serializable;
import java.util.Collection;

import nl.weeaboo.vn.core.impl.DestructibleElemList;
import nl.weeaboo.vn.core.impl.Indirect;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.scene.signal.VisualElementDestroySignal;
import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.signal.ISignalHandler;

public final class ChildCollection implements Serializable, ISignalHandler {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final Indirect<IVisualGroup> parentRef;
    private final DestructibleElemList<IVisualElement> children = new DestructibleElemList<IVisualElement>();

    public ChildCollection(IVisualGroup parent) {
        this.parentRef = Indirect.of(parent);
    }

    public void add(IVisualElement elem) {
        children.add(elem);
        elem.setParent(parentRef.get());
    }

    public void remove(IVisualElement elem) {
        children.remove(elem);
        if (elem.getParent() == parentRef.get()) {
            elem.setParent(null);
        }
    }

    public void destroyAll() {
        children.destroyAll();
    }

    @Override
    public void handleSignal(ISignal signal) {
        if (signal instanceof VisualElementDestroySignal) {
            IVisualElement elem = ((VisualElementDestroySignal)signal).getDestroyedElement();
            if (children.contains(elem)) {
                remove(elem);
            }
            if (elem == parentRef.get()) {
                destroyAll();
            }
        }
    }

    public boolean contains(IVisualElement elem) {
        return children.contains(elem);
    }

    public Collection<IVisualElement> getSnapshot() {
        return children.getSnapshot();
    }

}
