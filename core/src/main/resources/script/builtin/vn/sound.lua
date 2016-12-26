
---Functions related to audio playback (music, sound effects, voice clips)
-- 
module("vn.sound", package.seeall)

-- Local functions shared between sections
--------------------------------------------------------------------------------------------------------------

local function tosound(soundOrChannel)
    if type(soundOrChannel) == "number" then
        return Sound.findByChannel(soundOrChannel)
    end
    return soundOrChannel
end

---Global declarations
-------------------------------------------------------------------------------------------------------------- @section globals


---Music functions
-------------------------------------------------------------------------------------------------------------- @section music

---Changes the background music.
-- @string filename Path to a valid audio file, relative to <code>/snd</code>.
-- @number[opt=1.0] volume Loudness of the music between <code>0.0</code> and <code>1.0</code>.
-- @see sound
function music(filename, volume)
    return sound(filename, -1, volume or 1.0, 9000, SoundType.MUSIC)
end

---Stops background music started with the <code>music</code> function.
-- @number fadeTimeFrames Optional argument specifying the duration (in frames) of a slow fade-out instead of
--         stopping playback immediately.
-- @see music
function musicStop(fadeTimeFrames)
    soundStop(0, fadeTimeFrames)
end

---Voice functions
-------------------------------------------------------------------------------------------------------------- @section voice

---Plays a voice clip.
-- @string filename Path to a valid audio file, relative to <code>/snd</code>.
-- @int[opt=9200] channel The audio channel to use. Each channel can only play one sound at a time.
-- @see sound
function voice(filename, channel)
    return sound(filename, 1, 1.0, channel or 9200, SoundType.VOICE)
end

---Sound functions
-------------------------------------------------------------------------------------------------------------- @section sound

---Starts playing a sound effect, voice clip or background music.
-- @string filename Path to a valid audio file, relative to <code>/snd</code>.
-- @int[opt=1] loops The number of times the sound should repeat. Default is <code>1</code> (play it only
--             once). Use <code>-1</code> for infinite looping.
-- @number[opt=1.0] volume Loudness of the sound between <code>0.0</code> and <code>1.0</code>.
-- @int[opt=1] channel The audio channel to use. Each channel can only play one sound at a time. Use
--             <code>-1</code> to use a random free channel.
-- @tparam[opt=SoundType.SOUND] SoundType type The sound type:
--         <code>SoundType.SOUND</code>, <code>SoundType.MUSIC</code> or
--         <code>SoundType.VOICE</code>.
-- @treturn ISound A sound object with which to control playback, or <code>nil</code> if for whatever reason
--          no sound could be started.
function sound(filename, loops, volume, channel, type)
    loops = loops or 1
    if loops == 0 then
        --Playing a sound zero times is easy, just don't do anything
        return
    end
    
    volume = volume or 1.0
    channel = channel or 9100
    type = type or SoundType.SOUND

    local s = Sound.create(filename, type)
    if s ~= nil then
        s:setPrivateVolume(volume)
        s:setPreferredChannel(channel)
        s:start(loops)
    end    
    return s
end

---Stops a playing sound.
-- @param sound The Sound object or audio channel to change the volume of.
-- @number fadeTimeFrames Optional argument specifying the duration (in frames)
--         of a slow fade-out instead of stopping playback immediately.
-- @see sound
function soundStop(sound, fadeTimeFrames)
    sound = tosound(sound)    
    if sound == nil then
        return
    end

    sound:stop(fadeTimeFrames)
end

---Changes the volume of playing sound/music/voice.
-- @param sound The Sound object or audio channel to change the volume of.
-- @number[opt=1.0] targetVolume The target volume for the Sound.
-- @number[opt=0] durationFrames If specified, the number of frames over which to gradually change the sound's
--                volume to the <code>targetVolume</code>.
function changeVolume(sound, targetVolume, durationFrames)
    sound = tosound(sound)    
    if sound == nil then
        return
    end

    targetVolume = targetVolume or 1
    durationFrames = durationFrames or 0
    
    local vol = sound:getPrivateVolume()
    if durationFrames > 0 then
        local delta = (targetVolume - vol) / durationFrames
        while math.abs(targetVolume - vol) > math.abs(delta * getEffectSpeed()) do
            vol = vol + delta * getEffectSpeed()
            sound:setPrivateVolume(vol)
            yield()
        end 
    end
    sound:setPrivateVolume(targetVolume)
end

-- -----------------------------------------------------------------------------------------------------------
-- -----------------------------------------------------------------------------------------------------------
-- -----------------------------------------------------------------------------------------------------------
