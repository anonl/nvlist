package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.vn.layout.ILayoutGroup;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;

public abstract class VisualGroup extends VisualElement implements IVisualGroup {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final ChildCollection children;

    public VisualGroup() {
        children = new ChildCollection(this);
        addSignalHandler(0, children);
    }

    public VisualGroup(IVisualGroup parent) {
        this();

        this.parent = parent;
    }

    protected void add(IVisualElement child) {
        children.add(child);
    }

    protected void remove(IVisualElement child) {
        children.remove(child);
    }

    @Override
    public Iterable<? extends IVisualElement> getChildren() {
        return children.getSnapshot();
    }

    @Override
    public boolean contains(IVisualElement elem) {
        return children.contains(elem);
    }

    @Override
    protected abstract ILayoutGroup createLayoutAdapter();

}
