
# v4.3.0
- Added a global preference (`fullscreen`) to choose whether to start in full-screen mode or windows mode. Default value is `true`, unless `debug` mode is enabled.
- Improvements to text rendering. In particular, glyphs are now rendered slightly thicker which really helps with thin lines in some fonts.
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
