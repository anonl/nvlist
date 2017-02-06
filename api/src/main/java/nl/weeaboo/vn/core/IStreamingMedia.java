package nl.weeaboo.vn.core;

public interface IStreamingMedia {

    /**
     * Temporarily pauses playback. Use {@link #resume()} to resume playback.
     */
    void pause();

    /**
     * Resumes when paused. Behavior is unspecified when not paused.
     */
    void resume();

    /**
     * Stops playback.
     */
    void stop();

    /**
     * @return {@code true} if the stream is playing, and not paused.
     */
    boolean isPlaying();

    /**
     * @return {@code true} if the stream is paused. A stopped stream is never paused.
     */
    boolean isPaused();

    /**
     * @return {@code true} if the stream is stopped. This is the default state for a stream.
     */
    boolean isStopped();

}
