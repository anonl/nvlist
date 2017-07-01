package nl.weeaboo.vn.buildgui;

import nl.weeaboo.vn.buildgui.task.ITask;

/**
 * Interface for starting build tasks.
 */
public interface IBuildController {

    /**
     * Runs the current NVList project.
     */
    ITask startRunTask();

}
