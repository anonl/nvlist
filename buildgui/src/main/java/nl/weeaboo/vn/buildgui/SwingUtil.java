package nl.weeaboo.vn.buildgui;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatDarkLaf;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various functions for working with Java Swing.
 */
public final class SwingUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SwingUtil.class);

    private SwingUtil() {
    }

    /**
     * Changes the current Swing look-and-feel to the system default.
     */
    public static void setDefaultLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            LOG.warn("Unable to set Look and Feel", e);
        }
    }

    /**
     * Checks that the current thread is the Swing event dispatch thread.
     */
    public static void assertIsEdt() {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
    }

    /**
     * Creates a new image (ARGB) with the specified dimensions.
     */
    public static BufferedImage newBufferedImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Registers a method to be called when the component becomes visible or invisible.
     */
    public static void registerVisibilityChangeListener(JComponent component, Runnable listener) {
        component.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                listener.run();
            }
        });
    }
}
