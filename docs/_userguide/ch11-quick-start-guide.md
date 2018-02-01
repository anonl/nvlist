---
title: Quick-start guide
---

@@@ Does the default distribution include a JDK? It probably should so the user doesn't have to install any separate software.

## Download and install NVList

@@@ Project set-up (condensed version)
- Download and extract zip
- Navigate to extracted folder, should look something like this (add screenshot of NVList engine folder)
- Start the build tool:
  Windows: buildgui.exe
  @@@ Create some sort of visual marker for WIP sections of the manual
  Linux: buildgui
  MacOS: ???

## Creating a new visual novel project

  - Point to the full chapter (ch30)

@@@ edit the template script to show a line of dialogue, with a single sprite and background.
  - Point the user to the right folders. Its easier if the template project already includes a usable sprite, so we can just show that instead of requiring a new image to be added.

{% include sourcecode.html content="
@registerSpeaker(\"bob\", \"Man named Bob\")
$bob Hi, my name is Bob.
" %}
  
  
@@@ run your VN to test
  - For now: gradlew run
@@@ distribute (condensed version)
  - Point to the full chapter
  