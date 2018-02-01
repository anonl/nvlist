---
title: Script introduction
---

@@@ Introduction

## Text

   - Text only

## Embedded scripting

   - Commands (single- and multiline)

{% include sourcecode.html id="code" content="
@value = 2
Text
@@
if value > 1 then
    jump(\"badend7\")
end
@@
" %}

@@@ It's also possible to embed code in text lines. The code will be executed when the surrounding text becomes visible.

{% include sourcecode.html id="embeddedInText" content="
I dropped [sound(\"sfx/shatter\")] the cup from my hands.
" %}

## Comments

   - Comments (single- and multiline)

