package nl.weeaboo.vn.image;

import java.io.Serializable;

import nl.weeaboo.common.Dim;

/**
 * Wrapper object for screenshot operations. Since screenshots can only be taken during rendering, it may take
 * several frames for a scheduled screenshot to become available.
 */
public interface IScreenshot extends Serializable {

	public void cancel();

	/**
	 * Marks this screenshot object as transient; its pixels won't be serialized.
	 */
	public void markTransient();

	@Deprecated
	public void makeTransient();

	/** @return {@code true} when the screenshot operation has completed successfully. */
	public boolean isAvailable();

	/**
     * A volatile screenshot only stores its pixels on the GPU. As a consequence, it may lose its pixels at
     * any time.
     */
	public boolean isVolatile();

	/** @see #markTransient() */
	public boolean isTransient();

	/** @see #cancel() */
	public boolean isCancelled();

	/**
	 * Warning: May return {@code null} if the screenshot is not yet available or volatile.
	 */
	public ITextureData getPixels();

	/**
	 * @return The screen size in pixels when this screenshot was taken.
	 */
	public Dim getScreenSize();

	public short getZ();

}
