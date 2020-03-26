package nl.weeaboo.vn.buildgui;

import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;

/**
 * Listener for changes to the currently selected project.
 */
public interface IProjectModelListener {

    /**
     * This method is called when the active project changes.
     */
    void onProjectChanged(NvlistProjectConnection projectModel);

}
