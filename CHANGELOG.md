
# v4.9.3
- fix: If the click indicator was already visible, `waitClick` would accidentally hide it.

# v4.9.2
- fix: When calling `Save.load` the Lua thread continued executing for one frame before actually loading.
- fix: `imgtween`/`crossFadeTween` didn't animate properly when the end textures was `nil`.
- fix: Text drawables didn't take their color tint (including alpha) into account.

# v4.9.1
- fix: Enabling auto read didn't cancel the current `waitClick()` (if any).

# v4.9.0
- Added the ability to change the text speed using the built-in settings screen.
- When debug mode is enabled, `(debug)` is now added to the window's title. This makes it easier to spot if debug mode is enabled/disabled.
- Added auto read mode which causes text to advance automatically after a short delay. Use the `autoRead()` script function to enable.

# v4.8.1
- fix: Gradle plugin portal was missing from `build-tools/build.gradle` causing "Could not resolve all artifacts" errors.

# v4.8.0
- Restart hotkey changed to Ctrl+F5 to prevent accidental restarts when debugging (F5 is 'Resume' in the debugger).
- Improved preloader performance. It can now preload images loaded from a background thread.
- Added the space bar to the default 'textContinue' keys (see `input-config.json`).
- Rendering code now logs warnings when images unexpectedly can't be rendered. This makes it a bit easier to detect/investigate texture (un)loading bugs.
- NVList version number is now logged during startup.
- The OSD (activated by pressing F7 in debug mode) now shows an estimate of the native memory use. If this value becomes very large and keeps increasing, you probably have a memory leak somewhere.
- fix: In the default settings screen, the + button didn't disable when already as max volume. Same goes for the - button and min volume.
- fix: The default textlog didn't perform word-wrapping.
- fix: Memory leak in font loader. Unused fonts would never be unloaded.
- fix: Memory leak after using the `restart()` function. This could also cause the OSD text to become corrupted.

# v4.7.0
- Changed the default italic font to be less slanted (it was a little extreme).
- Preloader analytics gathered during development are now included in the distribution (`save/analytics.bin`). Without this file, the preloader does very little.
- fix: When idle for a long time, currently playing music could be unloaded (causing it to stop playing)

# v4.6.3
- fix: Off-by-one for animations with length 1 (broken since v4.6.2)
- fix: Toggling between fullscreen and windowed now remembers the window size.
- fix: Window decorations were missing when going to windowed mode after starting full-screen.
- fix: Reload (F5) now also clears cached img.json/snd.json files.

# v4.6.2
- fix: Animations with duration 0 should end immediately

# v4.6.1
- fix: Build error when using the build gui (broken since v4.6.0).
- fix: Off-by-one in behavior between fadeTo() and Anim. Now both treat duration==0.0 as instant transition and duration==1.0 as ending the next frame.
- fix: `ClickIndicatorPos.RIGHT` should be displayed to the right (outside) the text drawable, not inside its bounds.
- fix: `bitmapTweenOut` didn't work (changed size to 0x0 causing the image to immediately disappear).

# v4.6.0
- Implemented debug adapter protocol (in nvlist-desktop) and language server protocol (in nvlist-langserver).

# v4.5.2
- fix: Quick-saving from within a click handler resulted in a broken save file (more robust fix).

# v4.5.1
- fix: Fonts could be unloaded while still in use, causing text to be rendered as black squared.
- fix: Quick-saving from within a click handler resulted in a broken save file.
- You can now display choices without tracking which options were previously selected by the user. To to so, use `choice2(nil, "my option 1", "my option 2")` instead of `choice("my option 1", "my option 2")`.
- The click indicator position can now be modified with relative dx/dy offsets (i.e. `self.clickIndicator.dx = 50`). The default click indicator position changed slightly to make the position easier to customize.

# v4.5.0
- fix: Deactivating the current context from Lua didn't stop other threads and events from executing. Now, when the context becomes inactive, script execution within that context is immediately suspended.

# v4.4.2
- fix: Texture reloading sometimes resulted in black images

# v4.4.1
- fix: `attempt to call nil` at `builtin/vn/tween.lua:93` when calling `bitmapTween` 

# v4.4.0
- Build artifacts for NVList projects are now stored relative to the project folder (`vnRoot/build-out`), instead of inside the engine folder (`build-tools`).
- Added functions to change the master volume levels from Lua.
- Added functions to toggle between fullscreen/windowed mode from Lua.
- Added a warning to the log when a file couldn't be loaded because its file-extension uses the wrong case (e.g. .PNG instead of .png)
- fix: Internal error (`NullPointerException`) in nvlist-buildgui when attempting to open a project folder missing the file `build-res/build.properties`.
- fix: `bitmapTweenIn` reset any custom renderer properties other than texture. In particular, any scaling would be reset.
- fix: Improvements to bundled gallery script.

# v4.3.0
- Added a global preference (`fullscreen`) to choose whether to start in full-screen mode or windows mode. Default value is `true`, unless `debug` mode is enabled.
- Improvements to text rendering. In particular, glyphs are now rendered slightly thicker which really helps with thin lines in some fonts.
- New keyboard shortcut (`VKey.SHOW_TEXT_LOG`) for opening the text log screen. With the default key config, press the up arrow key to open the text log while  
- fix: Attempting to layout an empty `GridLayout` with non-zero row/col spacing resulted in an exception (layout size with negative dimensions).

# v4.2.2
- fix: t was possible to load an empty save slot using the default load screen. Doing so resulted in a crash.

# v4.2.1
- Changed RoboVM plugin to v2.3.9-snapshot (2.3.8 doesn't work anymore)
- fix: Revert change to blend mode used for text rendering (caused text to render as white rectangles on some systems, depending on the OS and/or video card)

# v4.2.0
- Style-specific font files (i.e. bold, italic) can now be named in the style `fontName-bold.ttf` to be more consistent with other, similar systems such as button images. Previously, the name had to be `fontName.bold.ttf`.
- When in debug mode (`-Pdebug=true`), you can now press `F12` to take a screenshot (it will be stored as a file in the `save` folder).
- New global Lua function: `callInContext`. This function can be used to implement menus/sub-modes, etc. It pauses execution of the current script and clears the screen, giving you a temporary blank space to work with.

# v4.1.1
- fix: Invisible text due to double-applied transform

# v4.1.0
- Added a setting to change the file name of generated releases (`appShortName` in `build-res/build.properties`)
- Decrease default music fade-out time from 5sec to 1sec (it was a bit slow)
- To avoid cross-platform issues, all resource loading now treats the file system as case-sensitive, regardless of operating system.
- fix: `FileNotFoundException` at `ResolutionFolderSelector.getOptions():82` when all resources are stored in a .nvl archive, and therefore no `res/img` folder exists.
- fix: Unable to load font files with uppercase characters from a .nvl file (font names in were automatically converted to lowercase)
- fix: Fuzzy text when using fractional x/y coordinates. An internal snap-to-grid function intended to prevent this didn't work due to a double-rounding bug in the implementation.

# v4.0.0
Release date: 2020-04-01
- First stable release of NVList 4
