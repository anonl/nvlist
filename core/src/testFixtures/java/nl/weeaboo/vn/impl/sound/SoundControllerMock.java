package nl.weeaboo.vn.impl.sound;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.junit.Assert;

import nl.weeaboo.collections.IntMap;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

class SoundControllerMock implements ISoundController {

    private static final long serialVersionUID = 1L;

    private final AtomicInteger updateCount = new AtomicInteger();
    private final AtomicInteger stopAllCount = new AtomicInteger();

    private final Map<SoundType, Double> masterVolume = new EnumMap<>(SoundType.class);
    private final IntMap<ISound> playing = new IntMap<>();

    @Override
    public void update() {
        updateCount.incrementAndGet();

        checkSounds();
    }

    @Override
    public void checkSounds() {
    }


    void consumeUpdateCount(int expected) {
        Assert.assertEquals(expected, updateCount.getAndSet(0));
    }

    @Override
    public void stopAll() {
        stopAllCount.incrementAndGet();
        playing.clear();
    }

    void consumeStopAllCount(int expected) {
        Assert.assertEquals(expected, stopAllCount.getAndSet(0));
    }

    @Override
    public void stop(int channel) {
        stop(channel, 0);
    }

    @Override
    public void stop(int channel, int fadeOutMillis) {
        playing.remove(channel);
    }

    @Override
    public @Nullable ISound get(int channel) {
        return playing.get(channel);
    }

    @Override
    public double getMasterVolume(SoundType type) {
        return masterVolume.getOrDefault(type, 1.0);
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public int getFreeChannel() {
        return 123;
    }

    @Override
    public void set(int channel, ISound sound) {
        playing.put(channel, sound);
    }

    @Override
    public void setMasterVolume(SoundType type, double volume) {
        masterVolume.put(type, volume);
    }

    @Override
    public void setPaused(boolean p) {
    }

}
