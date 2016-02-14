package nl.weeaboo.vn.core;

import static nl.weeaboo.settings.Preference.newConstPreference;
import static nl.weeaboo.settings.Preference.newPreference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.settings.AbstractPreferenceStore;
import nl.weeaboo.settings.Preference;
import nl.weeaboo.settings.PropertiesUtil;

public final class NovelPrefs extends AbstractPreferenceStore {

    public static final Preference<Integer> WIDTH = newConstPreference("width",
            "Width",
            1280,
            "Desired width for the main window. This is the width that will be passed to user code, the framework will take care of any scaling required.");

    public static final Preference<Integer> HEIGHT = newConstPreference("height",
            "Height",
            720,
            "Desired height for the main window. This is the height that will be passed to user code, the framework will take care of any scaling required.");

    public static final Preference<Boolean> SCRIPT_DEBUG = newConstPreference("vn.scriptDebug",
            "Script Debug",
            false,
            "Certain functions detect and warn about additional errors when script debug is turned on.");

    public static final Preference<String> ENGINE_MIN_VERSION = newConstPreference("vn.engineMinVersion",
            "Engine Minimum Version",
            "4.0",
            "The minimum allowable version of NVList that can be used to read your novel. Raises an error if the current version is less than the required version.");

    public static final Preference<String> ENGINE_TARGET_VERSION = newConstPreference("vn.engineTargetVersion",
            "Engine Target Version",
            "4.0",
            "The version of NVList this VN was created for.");

//	public static final Preference<Integer> SAVE_SCREENSHOT_WIDTH = newPreference("vn.saveScreenshotWidth", 224, "Save Screenshot Width", "Width (in pixels) to store the save slot screenshots at.");
//	public static final Preference<Integer> SAVE_SCREENSHOT_HEIGHT = newPreference("vn.saveScreenshotHeight", 126, "Save Screenshot Height", "Height (in pixels) to store the save slot screenshots at.");
//	public static final Preference<TextStyle> TEXT_STYLE = newPreference("vn.textStyle", new TextStyle(null, FontStyle.PLAIN, 30), "Default Text Style", "The default style to be used for rendered text.");
//	public static final Preference<TextStyle> TEXT_READ_STYLE = newPreference("vn.textReadStyle", TextStyle.defaultInstance(), "Read Text Style", "The text style to use for previously read text");
//	public static final Preference<TextStyle> TEXT_LOG_STYLE = newPreference("vn.textLogStyle", coloredTextStyle(0xFFFFFF80), "Text Log Style", "The text style to use for the text log.");
//	public static final Preference<TextStyle> CHOICE_STYLE = newPreference("vn.choiceStyle", TextStyle.defaultInstance(), "Choice Style", "Text style used for options of choices");
//	public static final Preference<TextStyle> SELECTED_CHOICE_STYLE = newPreference("vn.selectedChoiceStyle", coloredTextStyle(0xFF808080), "Selected Choice Style", "Text style used for previously chosen options of choices.");

    public static final Preference<Double> TEXT_SPEED = newPreference("vn.textSpeed",
            "Text Speed",
            0.5,
            "The text fade-in speed in characters per frame.");

    public static final Preference<Integer> TEXTLOG_PAGE_LIMIT = newPreference("vn.textLog.pageLimit",
            "Textlog Page Limit",
            50,
            "The number of pages the textlog keeps in memory.");

//	public static final Preference<Double>  EFFECT_SPEED = newPreference("vn.effectSpeed", 1.0, "Effect Speed", "Effect speed modifier. The base effect speed is multiplied by the specified amount.");
//	public static final Preference<Boolean> AUTO_READ = newPreference("vn.autoRead", false, "Auto Read", "Toggles auto read mode. In this mode, all wait-for-clicks are replaced by a timed wait.");
//	public static final Preference<Integer> AUTO_READ_WAIT = newPreference("vn.autoReadWait", 1000, "Auto Read Wait", "The wait time (in milliseconds) for the timed waits used by auto read mode.");
//	public static final Preference<Boolean> SKIP_UNREAD = newPreference("vn.skipUnread", true, "Skip Unread", "If set to false, skip mode will stop at unread text.");
//	public static final Preference<Integer> PRELOADER_LOOK_AHEAD = newPreference("vn.preloaderLookAhead", 30, "Preloader Lookahead", "The number of lines the preloader looks ahead to determine what to preload.");
//	public static final Preference<Integer> PRELOADER_MAX_PER_LINE = newPreference("vn.preloaderMaxPerLine", 3, "Preloader Max Per Line", "The maximum number of items the preloader is allowed to preload based on a single script line. This limit prevents the preloader from choking the system by preloading a very large number of images/sounds at once.");
//	public static final Preference<Double>  MUSIC_VOLUME = newPreference("vn.musicVolume", 0.7, "Music Volume", "Volume (between 0.0 and 1.0) of background music.");
//	public static final Preference<Double>  SOUND_VOLUME = newPreference("vn.soundVolume", 0.8, "Sound Volume", "Volume (between 0.0 and 1.0) of sound effects.");
//	public static final Preference<Double>  VOICE_VOLUME = newPreference("vn.voiceVolume", 1.0, "Voice Volume", "Volume (between 0.0 and 1.0) of voices.");
//	public static final Preference<Integer> TIMER_IDLE_TIMEOUT = newPreference("vn.timerIdleTimeout", 30, "Timer Idle Timeout", "The number of seconds of user inactivity that are tolerated before the playtime timer is stopped.");
//	public static final Preference<Boolean> ENABLE_PROOFREADER_TOOLS = newPreference("vn.enableProofreaderTools", false, "Enable Proofreader Tools", "Enables available bug reporting features for proofreaders/editors.");
//	public static final Preference<Boolean> RTL = newPreference("vn.rtl", false, "Right-to-Left Text", "Sets the default text direction to RTL (right to left).");

    private static final String CONSTANTS_FILENAME = "config.ini";
    private static final String DEFAULTS_FILENAME = "prefs.default.ini";
    private static final String VARIABLES_FILENAME = "prefs.ini";

    private final IWritableFileSystem fileSystem;

    public NovelPrefs(IWritableFileSystem fs) {
        this.fileSystem = Checks.checkNotNull(fs);
	}

    @Override
    public void loadVariables() throws IOException {
        initConsts(load(CONSTANTS_FILENAME));
        setAll(load(DEFAULTS_FILENAME));
        setAll(load(VARIABLES_FILENAME));
    }

    private Map<String, String> load(String filename) throws IOException {
        if (!fileSystem.getFileExists(filename)) {
            return Collections.emptyMap();
        }

        InputStream in = fileSystem.openInputStream(filename);
        try {
            return PropertiesUtil.load(in);
        } finally {
            in.close();
        }
    }

    @Override
    public void saveVariables() throws IOException {
        OutputStream out = fileSystem.openOutputStream(VARIABLES_FILENAME, false);
        try {
            PropertiesUtil.save(out, getVariables());
        } finally {
            out.close();
        }
    }

}
