package nl.weeaboo.vn.scene.signal;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.signal.AbstractSignal;

public final class VisualElementDestroySignal extends AbstractSignal {

    private final IVisualElement destroyedElement;

    public VisualElementDestroySignal(IVisualElement destroyedElement) {
        this.destroyedElement = Checks.checkNotNull(destroyedElement);
    }

    public IVisualElement getDestroyedElement() {
        return destroyedElement;
    }

}
