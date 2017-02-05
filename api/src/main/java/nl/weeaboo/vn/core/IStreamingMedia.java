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

    /** Stops playback. */
    void stop();

    boolean isPlaying();

    boolean isPaused();

    boolean isStopped();

}
