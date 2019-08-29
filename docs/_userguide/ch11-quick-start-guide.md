---
title: Quick-start guide
---

## Download and install NVList

1. Download NVList from <http://nvlist.weeaboo.nl/> <br>
   Or from <https://github.com/anonl/nvlist/releases>
2. Extract the downloaded archive file (.zip/.7z) to a new folder.<br/>The result should look something like this:<br/>

![NVList install folder]({{site.baseurl}}{% link assets/userguide/ch11-nvlist-install-folder.png %})

3. Start the build tool:<br/>
   - Windows: `nvlist-build.exe`
   - Other: `java -jar nvlist-build.jar`

![NVList build user interface screenshot]({{site.baseurl}}{% link assets/userguide/ch11-build-gui.png %})

## Editing and running your own visual novel

The following section is an abridged version of the chapter ["Setting up a new project"]({{site.baseurl}}{% link _userguide/ch30-setting-up-a-new-project.md %})

NVList comes packaged with an example project which shows off some basic functionality. press the 'Run Game' button in the build tool to run the example project. The first time you run the project it can take a long time to start (some extra files will be downloaded), but subsequent runs should be much faster. After a while, a new test window will pop up showing the example script.

Now let's try editing the script. If you still have the test window open from the previous step, close it now. Press the `Open project folder` button in the build tool to open a file browser window showing the project files. We're going to edit a script file, which is located in `res` -> `script` -> `main.lvn`. Open the file in your favorite text editor (Notepad++ works).

Find the line that says `# Insert your code here` and add the following immediately below it:
{% include sourcecode.html id="text" content="
@registerSpeaker(\"aza\", \"Azathoth\")
$aza Hello there
" %}

Save the changes in your text editor and go back to the build tool. Press the `Run Game` button to test your changes. Once the test window pops up, you should see something like this:

![User script screenshot 1]({{site.baseurl}}{% link assets/userguide/ch11-user-script1.png %})

Now we know how to show text, but it's not much of a visual novel without visuals. Let's add a background image, and then a character image (sprite) on top of it. Open your text editor again and change your code to the following:

{% include sourcecode.html id="images" content="
@registerSpeaker(\"aza\", \"Azathoth\")
@bgf(\"bg/gate\")
@imgf(\"sprite/azathoth\", \"c\")
$aza Hello there
" %}

Running the game again shows the added images.

![User script screenshot 2]({{site.baseurl}}{% link assets/userguide/ch11-user-script2.jpg %})

## Distribution

The following section is an abridged version of the chapter ["Distribution"]({{site.baseurl}}{% link _userguide/ch32-distribution.md %})

When you want to share your creation with others, you can use the build tool to create a packaged release. The package contains your visual novel, together with all the files needed to run it (you don't need to install NVList to run it).

To create a packaged release with the build tool, press the arrow button next to `Run Game`, then select `Create release` from the drop-down menu. Building the release will take several minutes. Once it finishes, a file browser window showing the created files will automatically pop up.
