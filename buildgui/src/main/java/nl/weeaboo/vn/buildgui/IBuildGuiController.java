package nl.weeaboo.vn.buildgui;

import nl.weeaboo.vn.buildgui.task.ITaskController;

public interface IBuildGuiController {

    /**
     * Returns the non-GUI build controller.
     */
    IBuildController getBuildController();

    /**
     * The task controller used to manipulate background tasks.
     */
    ITaskController getTaskController();

    /**
     * The model provides access to the underlying project.
     */
    BuildGuiModel getModel();

}
