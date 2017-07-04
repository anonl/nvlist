package nl.weeaboo.vn.buildgui;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public final class SwingHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SwingHelper.class);

    private SwingHelper() {
    }

    public static void setDefaultLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {

            LOG.warn("Unable to set Look and Feel", e);
        }
    }

    public static void assertIsEdt() {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
    }

    public static BufferedImage newBufferedImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public static void registerVisibilityChangeListener(JComponent component, Runnable listener) {
        component.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                listener.run();
            }
        });
    }
}
