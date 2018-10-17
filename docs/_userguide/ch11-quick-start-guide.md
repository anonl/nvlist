---
title: Quick-start guide
---

## Download and install NVList

1. Download NVList from <http://nvlist.weeaboo.nl/>
2. Extract the downloaded archive file (.zip/.7z) to a new folder.<br/>The result should look something like this:<br/>

![NVList install folder]({{site.baseurl}}{% link assets/userguide/ch11-nvlist-install-folder.png %})

3. Start the build tool:<br/>
   - Windows: `nvlist-build.exe`
   - Other: `java -jar nvlist-build.jar`

![NVList build user interface screenshot]({{site.baseurl}}{% link assets/userguide/ch11-build-gui.png %})

## Creating a new visual novel project

The following section is an abridged version of the chapter ["Setting up a new project"]({{site.baseurl}}{% link _userguide/ch30-setting-up-a-new-project.md %})

@@@ Point the user to the right folders using the build GUI. It's easier if the template project already includes a usable sprite, so we can just show that instead of requiring a new image to be added.

@@@ Text by named character. Define character, then refer to it with $.

{% include sourcecode.html id="text" content="
@registerSpeaker(\"bob\", \"Man named Bob\")
$bob Hi, my name is Bob.
" %}

@@@ Now that we've edited the script, time to test. Save changes in whatever text editor you're using then go back to build tool. Press the big fat Run game button. First run can take quite a while, some parts still need to be downloaded.

@@@ Not much of a visual novel without visuals. First add a background, then show bob in front of it.

{% include sourcecode.html id="images" content="
@bgf(\"bg/example\")
@imgf(\"sprite/bob\")
" %}

## Distribution

The following section is an abridged version of the chapter ["Distribution"]({{site.baseurl}}{% link _userguide/ch32-distribution.md %})

@@@ distribute (condensed version) -> arrow next to 'Run game' -> Create release


  