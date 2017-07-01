package nl.weeaboo.vn.buildgui.task;

public interface ITask {

    void cancel();

    void addProgressListener(IProgressListener listener);
    void removeProgressListener(IProgressListener listener);

}
