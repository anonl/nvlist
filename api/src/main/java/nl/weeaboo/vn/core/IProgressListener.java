package nl.weeaboo.vn.core;

public interface IProgressListener {

    /**
     * @param progress The relative progress, in the range {@code [0.0f, 1.0f]}.
     */
    void onProgressChanged(float progress);

}
