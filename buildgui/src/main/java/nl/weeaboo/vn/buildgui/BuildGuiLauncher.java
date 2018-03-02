package nl.weeaboo.vn.buildgui;

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

        // TODO: Who save the preferences? And when?

        SwingUtilities.invokeLater(() -> {
            BuildGuiModel model = controller.getModel();
            model.setProjectFolders(prefs.getProjectFolderConfig());

            BuildGui window = new BuildGui(controller);
            window.setVisible(true);
        });
    }

}
