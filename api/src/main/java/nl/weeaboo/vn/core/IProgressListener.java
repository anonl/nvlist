package nl.weeaboo.vn.core;

public interface IProgressListener {

    /**
     * @param progress The relative progress in the range {@code (0.0, 1.0)}
     */
	public void onProgressChanged(float progress);

}
