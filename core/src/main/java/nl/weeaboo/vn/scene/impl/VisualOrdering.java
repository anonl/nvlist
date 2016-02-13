package nl.weeaboo.vn.scene.impl;

import java.io.Serializable;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Shorts;

import nl.weeaboo.vn.scene.IVisualElement;

/** Sorts visual elements from front to back */
public final class VisualOrdering extends Ordering<IVisualElement> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final Ordering<IVisualElement> FRONT_TO_BACK = new VisualOrdering();
    public static final Ordering<IVisualElement> BACK_TO_FRONT = FRONT_TO_BACK.reverse();
    
    @Override
    public int compare(IVisualElement left, IVisualElement right) {
        return Shorts.compare(left.getZ(), right.getZ());
    }

}
