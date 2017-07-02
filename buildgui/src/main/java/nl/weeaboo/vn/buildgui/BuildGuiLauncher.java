package nl.weeaboo.vn.buildgui;

import java.io.File;

import javax.swing.SwingUtilities;

import nl.weeaboo.vn.buildgui.gradle.GradleBuildController;
import nl.weeaboo.vn.buildgui.task.ITaskController;
import nl.weeaboo.vn.buildgui.task.TaskController;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.impl.InitConfig;

public final class BuildGuiLauncher {

    /**
     * Launches the NVList build user interface as a stand-alone window.
     */
    public static void main(String[] args) {
        InitConfig.init();
        SwingHelper.setDefaultLookAndFeel();

        ITaskController taskController = new TaskController();
        GradleBuildController buildController = new GradleBuildController(taskController);
        BuildGuiController controller = new BuildGuiController(buildController, taskController);

        SwingUtilities.invokeLater(() -> {
            BuildGui window = new BuildGui(controller);

            // TODO: Project should be (re)openened when based on the selected project/engine folders, not hardcoded
            ProjectFolderConfig folderConfig = new ProjectFolderConfig(new File("../template/"), new File(".."));
            controller.getModel().setProjectFolders(folderConfig);

            window.setVisible(true);
        });
    }

}
