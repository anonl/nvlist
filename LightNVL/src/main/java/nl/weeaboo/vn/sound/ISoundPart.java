package nl.weeaboo.vn.sound;

import java.io.IOException;

public interface ISoundPart {

	// === Functions ===========================================================
	/**
	 * Starts playing the sound.
	 *
	 * @param loops The number of times the sound should loop. Use
	 *        <code>-1</code> for infinite looping.
	 */
	public void start(int loops) throws IOException;

	/**
	 * @see #stop(int)
	 */
	public void stop();

	/**
	 * Stops playing the sound.
	 * @param fadeOutMillis Instead of stopping the sound immediately, fade it
	 *        out slowly over the course of <code>fadeOutMillis</code>.
	 */
	public void stop(int fadeOutMillis);

	/**
	 * Temporarily pauses playback. Use {@link #resume()} to resume playback.
	 */
	public void pause();

	/**
	 * Resumes a previously paused sound. Behavior is unspecified when the sound
	 * is not paused.
	 */
	public void resume();

	// === Getters =============================================================

	public String getFilename();
	public SoundType getSoundType();
	public boolean isPlaying();
	public boolean isPaused();
	public boolean isStopped();
	public boolean isDestroyed();
	public int getLoopsLeft();

	public double getVolume();
	public double getPrivateVolume();
	public double getMasterVolume();

	// === Setters =============================================================

	public void setPrivateVolume(double v);
	public void setMasterVolume(double v);

}
