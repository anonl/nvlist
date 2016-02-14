package nl.weeaboo.vn.scene.impl;

import java.io.Serializable;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Shorts;

import nl.weeaboo.vn.scene.IVisualElement;

/** Sorts visual elements from front to back */
public class VisualOrdering extends Ordering<IVisualElement> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final VisualOrdering FRONT_TO_BACK = new VisualOrdering(false);
    public static final VisualOrdering BACK_TO_FRONT = new VisualOrdering(true);

    protected final boolean backToFront;

    protected VisualOrdering(boolean backToFront) {
        this.backToFront = backToFront;
    }

    @Override
    public int compare(IVisualElement left, IVisualElement right) {
        int c = Shorts.compare(left.getZ(), right.getZ());
        return (backToFront ? -c : c);
    }

    public final boolean isBackToFront() {
        return backToFront;
    }

}
