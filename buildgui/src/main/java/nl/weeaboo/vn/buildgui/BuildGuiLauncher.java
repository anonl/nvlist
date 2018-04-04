package nl.weeaboo.vn.buildgui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import nl.weeaboo.vn.buildgui.gradle.GradleBuildController;
import nl.weeaboo.vn.buildgui.task.ITaskController;
import nl.weeaboo.vn.buildgui.task.TaskController;
import nl.weeaboo.vn.buildtools.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.InitConfig;

public final class BuildGuiLauncher {

    /**
     * Launches the NVList build user interface as a stand-alone window.
     */
    public static void main(String[] argsArray) {
        // Use ["../template", ".."] to run from Eclipse

        InitConfig.init();
        HeadlessGdx.init();
        SwingHelper.setDefaultLookAndFeel();

        BuildGuiPrefs prefs = BuildGuiPrefs.load(argsArray);

        ITaskController taskController = new TaskController();
        GradleBuildController buildController = new GradleBuildController(taskController);
        BuildGuiController controller = new BuildGuiController(buildController, taskController);

        SwingUtilities.invokeLater(() -> {
            BuildGuiModel model = controller.getModel();
            model.setProjectFolders(prefs.getProjectFolderConfig());

            BuildGui window = new BuildGui(controller);
            window.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);

                    // Store (possible changed) user preferences
                    storePrefs(model, prefs);
                }
            });
            window.setVisible(true);
        });
    }

    private static void storePrefs(BuildGuiModel model, BuildGuiPrefs prefs) {
        model.getProject().ifPresent(project -> {
            prefs.setProjectFolderConfig(project.getFolderConfig());
        });
        prefs.save();
    }

}
