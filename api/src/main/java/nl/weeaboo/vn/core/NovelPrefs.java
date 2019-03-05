package nl.weeaboo.vn.core;

import static nl.weeaboo.prefsstore.Preference.newConstPreference;
import static nl.weeaboo.prefsstore.Preference.newPreference;

import nl.weeaboo.prefsstore.Preference;

public final class NovelPrefs {

    public static final Preference<String> TITLE = newConstPreference("title",
            "Tile",
            "NVList",
            "Human-readable display name of your VN.");

    public static final Preference<Integer> WIDTH = newConstPreference("width",
            "Width",
            1280,
            "Desired width for the main window. This is the width that will be passed to user code, "
                    + "the framework will take care of any scaling required.");

    public static final Preference<Integer> HEIGHT = newConstPreference("height",
            "Height",
            720,
            "Desired height for the main window. This is the height that will be passed to user code, "
                    + "the framework will take care of any scaling required.");

    public static final Preference<Boolean> DEBUG = newPreference("debug",
            "Debug",
            false,
            "Enables developer mode, unlocking various debugging features.");

    public static final Preference<String> ENGINE_MIN_VERSION = newConstPreference("vn.engineMinVersion",
            "Engine Minimum Version",
            "4.0",
            "The minimum allowable version of NVList that can be used to read your novel. "
                    + "Raises an error if the current version is less than the required version.");

    public static final Preference<String> ENGINE_TARGET_VERSION = newConstPreference("vn.engineTargetVersion",
            "Engine Target Version",
            "4.0",
            "The version of NVList this VN was created for.");

    public static final Preference<Double> TEXT_SPEED = newPreference("vn.textSpeed",
            "Text Speed",
            0.5,
            "The text fade-in speed in characters per frame.");

    public static final Preference<Integer> TEXTLOG_PAGE_LIMIT = newPreference("vn.textLog.pageLimit",
            "Textlog Page Limit",
            50,
            "The number of pages the textlog keeps in memory.");

    public static final Preference<Double> MUSIC_VOLUME = newPreference("vn.sound.volume.music",
            "Music volume",
            1.0,
            "Master volume (between 0.0 and 1.0) for music.");

    public static final Preference<Double> SOUND_EFFECT_VOLUME = newPreference("vn.sound.volume.sound",
            "Sound effect volume",
            1.0,
            "Master volume (between 0.0 and 1.0) for sound effects.");

    public static final Preference<Double> VOICE_VOLUME = newPreference("vn.sound.volume.voice",
            "Voice volume",
            1.0,
            "Master volume (between 0.0 and 1.0) for voice audio.");

    public static final Preference<Integer> TEXTURE_CACHE_PAGES = newPreference("vn.texture.cacheSizePages",
            "Texture cache size (pages)",
            20,
            "Texture cache size in number of pages. A page is equal to the texture memory required for a "
            + "full screen, full color image.");

}
