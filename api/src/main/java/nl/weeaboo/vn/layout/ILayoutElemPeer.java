package nl.weeaboo.vn.layout;

import java.io.Serializable;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IDestructible;

/**
 * Interface containing the methods that {@link ILayoutElem} needs to interact with its visual element peer.
 */
public interface ILayoutElemPeer extends Serializable, IDestructible {

    /**
     * @return {@code true} if this element is currently visible.
     *
     * @see ILayoutElem#isVisible
     */
    boolean isVisible();

    /**
     * Positions this element within its parent's layout. This method is intended to only be called by the
     * layout system.
     *
     * @see ILayoutElem#setLayoutBounds
     */
    void setLayoutBounds(Rect2D rect);

}
