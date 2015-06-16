package nl.weeaboo.vn.sound.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;

import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.ISoundPart;
import nl.weeaboo.vn.sound.SoundType;

public class SoundController implements ISoundController {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    private final Map<SoundType, Double> masterVolume;
    private Map<Integer, ISoundPart> sounds;
    private List<ISoundPart> pausedList;
    private boolean paused;

    protected SoundController() {
        masterVolume = new EnumMap<SoundType, Double>(SoundType.class);
        for (SoundType type : SoundType.values()) {
            masterVolume.put(type, 1.0);
        }

        sounds = new HashMap<Integer, ISoundPart>();
        pausedList = new ArrayList<ISoundPart>();
    }

    //Functions
    @Override
    public void update() {
        Iterator<Entry<Integer, ISoundPart>> itr = sounds.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<Integer, ISoundPart> entry = itr.next();
            ISoundPart s = entry.getValue();
            if (s.isStopped()) {
                pausedList.remove(s);
                itr.remove();
            }
        }
    }

    @Override
    public void stopAll() {
        Integer channels[] = sounds.keySet().toArray(new Integer[sounds.size()]);
        for (int channel : channels) {
            stop(channel);
        }

        ISoundPart ps[] = pausedList.toArray(new ISoundPart[pausedList.size()]);
        pausedList.clear();
        for (ISoundPart sound : ps) {
            sound.stop(0);
        }
    }

    @Override
    public void stop(int channel) {
        stop(channel, -1);
    }

    @Override
    public void stop(int channel, int fadeOutMillis) {
        ISoundPart sound = sounds.remove(channel);
        if (sound != null) {
            pausedList.remove(sound);
            sound.stop(fadeOutMillis);
        }
    }

    //Getters
    @Override
    public ISoundPart get(int channel) {
        return sounds.get(channel);
    }

    @Override
    public double getMasterVolume(SoundType type) {
        Preconditions.checkNotNull(type);
        return masterVolume.get(type);
    }

    protected Iterable<ISoundPart> getSounds(SoundType type) {
        Collection<ISoundPart> result = new ArrayList<ISoundPart>();
        for (ISoundPart sound : sounds.values()) {
            if (sound.getSoundType() == type) {
                result.add(sound);
            }
        }
        return result;
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    //Setters

    @Override
    public void set(int channel, ISoundPart sound) {
        stop(channel);

        if (sounds.containsKey(channel)) {
            throw new IllegalStateException("Attempt to overwrite an existing sound entry.");
        }

        double mvol = getMasterVolume(sound.getSoundType());
        sound.setMasterVolume(mvol);
        sounds.put(channel, sound);
    }

    @Override
    public void setMasterVolume(SoundType type, double vol) {
        masterVolume.put(type, vol);

        for (ISoundPart sound : getSounds(type)) {
            sound.setMasterVolume(vol);
        }
    }

    @Override
    public void setPaused(boolean p) {
        paused = p;

        if (paused) {
            for (ISoundPart sound : sounds.values()) {
                if (!sound.isPaused()) {
                    sound.pause();
                    pausedList.add(sound);
                }
            }
        } else {
            for (ISoundPart sound : pausedList) {
                sound.resume();
            }
            pausedList.clear();
        }
    }

}
