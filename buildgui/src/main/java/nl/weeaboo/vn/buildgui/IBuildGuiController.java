package nl.weeaboo.vn.buildgui;

import nl.weeaboo.vn.buildgui.task.ITaskController;

public interface IBuildGuiController {

    IBuildController getBuildController();

    ITaskController getTaskController();

    BuildGuiModel getModel();

}
