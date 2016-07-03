package nl.weeaboo.vn.video.impl;

import java.io.IOException;

import com.badlogic.gdx.video.VideoPlayerInitException;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.video.IVideo;

public class Video implements IVideo {

    private static final long serialVersionUID = VideoImpl.serialVersionUID;

    private final String filename;
    private final IVideoAdapter videoAdapter;

    private double privateVolume = 1.0;
    private double masterVolume = 1.0;

    public Video(String filename, IVideoAdapter videoAdapter) {
        this.filename = Checks.checkNotNull(filename);
        this.videoAdapter = Checks.checkNotNull(videoAdapter);
    }

    @Override
    public void prepare() throws IOException {
        videoAdapter.prepare();
    }

    @Override
    public boolean isPrepared() {
        return videoAdapter.isPrepared();
    }

    @Override
    public void start() throws IOException {
        try {
            videoAdapter.play();
        } catch (VideoPlayerInitException e) {
            throw new IOException("Error initializing video: " + filename, e);
        }
    }

    @Override
    public void render() {
        videoAdapter.render();
    }

    @Override
    public void stop() {
        videoAdapter.stop();
    }

    @Override
    public void pause() {
        videoAdapter.pause();
    }

    @Override
    public void resume() {
        videoAdapter.resume();
    }

    @Override
    public boolean isPlaying() {
        return videoAdapter.isPlaying();
    }

    @Override
    public boolean isPaused() {
        return videoAdapter.isPaused();
    }

    protected void onVolumeChanged() {
        videoAdapter.setVolume(getVolume());
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public boolean isStopped() {
        return !isPlaying() && !isPaused();
    }

    @Override
    public double getVolume() {
        return getPrivateVolume() * getMasterVolume();
    }

    @Override
    public double getPrivateVolume() {
        return privateVolume;
    }

    @Override
    public double getMasterVolume() {
        return masterVolume;
    }

    @Override
    public void setPrivateVolume(double v) {
        if (privateVolume != v) {
            privateVolume = v;

            onVolumeChanged();
        }
    }

    @Override
    public void setMasterVolume(double v) {
        if (masterVolume != v) {
            masterVolume = v;

            onVolumeChanged();
        }
    }

    @Override
    public void setRenderEnv(IRenderEnv renderEnv) {
        videoAdapter.setRenderEnv(renderEnv);
    }

}
