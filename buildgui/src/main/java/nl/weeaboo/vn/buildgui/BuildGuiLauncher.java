package nl.weeaboo.vn.buildgui;

import java.io.File;

import javax.swing.SwingUtilities;

import nl.weeaboo.vn.buildgui.gradle.CheckedGradleException;
import nl.weeaboo.vn.buildgui.gradle.GradleBuildController;
import nl.weeaboo.vn.buildgui.gradle.GradleMonitor;
import nl.weeaboo.vn.buildgui.task.ITaskController;
import nl.weeaboo.vn.buildgui.task.TaskController;
import nl.weeaboo.vn.impl.InitConfig;

public final class BuildGuiLauncher {

    /**
     * Launches the NVList build user interface as a stand-alone window.
     */
    public static void main(String[] args) throws CheckedGradleException {
        InitConfig.init();
        SwingHelper.setDefaultLookAndFeel();

        GradleMonitor gradleMonitor = new GradleMonitor();
        // TODO: Project should be (re)openened when based on the selected project/engine folders, not hardcoded
        gradleMonitor.open(new File(".."));

        ITaskController taskController = new TaskController();
        GradleBuildController buildController = new GradleBuildController(gradleMonitor, taskController);
        BuildGuiController controller = new BuildGuiController(buildController, taskController);

        SwingUtilities.invokeLater(() -> {
            new BuildGui(controller).setVisible(true);
        });
    }

}
