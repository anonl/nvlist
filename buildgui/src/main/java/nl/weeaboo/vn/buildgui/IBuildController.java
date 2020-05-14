package nl.weeaboo.vn.buildgui;

import java.io.File;

import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;
import nl.weeaboo.vn.buildtools.task.ITask;

/**
 * Interface for starting build tasks.
 */
public interface IBuildController extends IProjectModelListener {

    /**
     * @see ProjectFolderConfig#getBuildToolsFolder()
     */
    @Deprecated
    default File getBuildToolsFolder() {
        return getFolderConfig().getBuildToolsFolder();
    }

    /**
     * @see NvlistProjectConnection#getFolderConfig()
     */
    ProjectFolderConfig getFolderConfig();

    /**
     * Adds a listener to track build progress.
     */
    void addLogListener(IBuildLogListener listener);

    /**
     * Removes a listener previously added through {@link #addLogListener(IBuildLogListener)}.
     */
    void removeLogListener(IBuildLogListener listener);

    /**
     * Creates an empty NVList project in the current project folder.
     */
    ITask startInitProject();

    /**
     * Runs the current NVList project.
     */
    ITask startRun();

    /**
     * Builds a distribution of the current NVList project.
     */
    ITask startCreateRelease();

    /**
     * Checks for updates to NVList.
     */
    ITask startCheckForUpdates();

    /**
     * Runs the resource optimizer task of the current NVList project.
     */
    ITask startResourceOptimizer();

}
