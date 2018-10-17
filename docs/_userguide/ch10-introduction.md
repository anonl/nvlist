---
title: Introduction
---

NVList is a tool for creating visual novels.

## Supported platforms
NVList projects can be exported to run on the following platforms:
- Windows
- Mac OS X
- Linux
- Android
- iOS (experimental)

![Windows]({{site.baseurl}}{% link assets/userguide/ch10-win10.jpg %})
![Linux]({{site.baseurl}}{% link assets/userguide/ch10-ubuntu16.jpg %})
![Android]({{site.baseurl}}{% link assets/userguide/ch10-android.jpg %})

## Scripting 
Since visual novels consist mostly of text, the scripting language used by NVList was designed to make displaying text as convenient as possible. Take for example the following code:
{% include sourcecode.html content="
This is some example text.
" %}

![Screenshot of the example script]({{site.baseurl}}{% link assets/userguide/ch10-example-script.jpg %})

The above is actually valid NVList script to show "This is some example text." on the screen. For a full introduction to scripting, please refer to [Script Introduction]({{site.baseurl}}{% link _userguide/ch20-script-intro.md %}).

