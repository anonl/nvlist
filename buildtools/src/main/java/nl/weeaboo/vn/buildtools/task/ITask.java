package nl.weeaboo.vn.buildtools.task;

public interface ITask {

    void cancel();

    void addProgressListener(IProgressListener listener);

    void removeProgressListener(IProgressListener listener);

}
