---
title: Quick-start guide
---

@@@ This would be much nicer if the build-tool already existed, but alas.
@@@ For now, explain how to build using Gradle.
@@@ Does the default distribution include a JDK? It probably should so the user doesn't have to install any separate software.

@@@ Project set-up (condensed version)
  - Point to the full chapter
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
  