package nl.weeaboo.vn.buildgui;

import java.io.File;

import javax.swing.SwingUtilities;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import nl.weeaboo.vn.buildgui.gradle.GradleBuildController;
import nl.weeaboo.vn.buildgui.task.ITaskController;
import nl.weeaboo.vn.buildgui.task.TaskController;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.impl.InitConfig;

public final class BuildGuiLauncher {

    /**
     * Launches the NVList build user interface as a stand-alone window.
     */
    public static void main(String[] argsArray) {
        // Use ["../template", ".."] to run from Eclipse

        InitConfig.init();
        SwingHelper.setDefaultLookAndFeel();

        ImmutableList<String> args = ImmutableList.copyOf(argsArray);
        String projectPath = Iterables.get(args, 0, ".");
        String buildToolsPath = Iterables.get(args, 0, "build-tools");

        ITaskController taskController = new TaskController();
        GradleBuildController buildController = new GradleBuildController(taskController);
        BuildGuiController controller = new BuildGuiController(buildController, taskController);

        SwingUtilities.invokeLater(() -> {
            BuildGui window = new BuildGui(controller);

            ProjectFolderConfig folderConfig = new ProjectFolderConfig(new File(projectPath),
                    new File(buildToolsPath));
            controller.getModel().setProjectFolders(folderConfig);

            window.setVisible(true);
        });
    }

}
