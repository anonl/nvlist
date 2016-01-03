package nl.weeaboo.vn.scene.impl;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;
import nl.weeaboo.vn.scene.signal.DestroySignal;
import nl.weeaboo.vn.scene.signal.ISignal;

public class VisualGroup extends VisualElement implements IVisualGroup {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final List<IVisualElement> children = Lists.newArrayList();

    public VisualGroup() {
    }

    public VisualGroup(IVisualGroup parent) {
        super();

        this.parent = parent;
    }

    @Override
    public void handleSignal(ISignal signal) {
        if (signal instanceof DestroySignal) {
            IVisualElement elem = ((DestroySignal)signal).getDestroyedElement();
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

        // Destroy all children recursively
        for (IVisualElement child : ImmutableList.copyOf(getChildren())) {
            child.destroy();
        }
    }

    protected void onChildDestroyed(IVisualElement elem) {
        remove(elem);
    }

    @Override
    public Iterable<? extends IVisualElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

}
