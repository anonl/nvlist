
# v4.1.0
- Added a setting to change the file name of generated releases (`appShortName` in `build-res/build.properties`)
- Decrease default music fade-out time from 5sec to 1sec (it was a bit slow)
- To avoid cross-platform issues, all resource loading now treats the file system as case-sensitive, regardless of operating system.
- fix: `FileNotFoundException` at `ResolutionFolderSelector.getOptions():82` when all resources are stored in a .nvl archive, and therefore no `res/img` folder exists.
- fix: Unable to load font files with uppercase characters from a .nvl file (font names in were automatically converted to lowercase)

# v4.0.0
Release date: 2020-04-01
- First stable release of NVList 4
