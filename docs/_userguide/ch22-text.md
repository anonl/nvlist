---
title: Text
---

- Text

## NVL and ADV modes

@@@ Terminology
@@@ NVL -> full-screen text. More room for descriptive text. New lines are appended to the visible text until an explicit `clearText()` command is used.
@@@ ADV -> textbox at the bottom of the screen, usually 2-4 lines tall. Every new line clears the screen. Possible to append text, but requires a special command instead of being the default behavior.

## Speakers

@@@ Typing the same name over and over again isn't fun. Define a shorthand.

@@@ Shorthand for name in text box.
{% include sourcecode.html id="speakersyntax" content="
@registerSpeaker(\"bob\", \"Man named Bob\")
"%}
@@@ short-name (no spaces), full name

## Stringifiers

@@@ Dynamic text, replacing `$whatever` with the value of a script variable or function. Common term for this is "string interpolation".
@@@ Stringifier example, both simple variable replacement, but also a custom stringifier function.

@@@ Explain that variables are automatically replaced by their value, but that you can also register a function. Stringifiers run before the first character of the line becomes visible. Full line of text is needed to calculate the line-wrapping.

## Embedded commands

It's also possible to embed code in text lines. The embedded code will be executed when its surrounding text becomes visible.

{% include sourcecode.html id="embeddedInText" content="
I dropped [sound(\"sfx/shatter\")] the cup from my hands.
" %}

The main use for this is syncing up special effects with a specific word in the line of text. In the above example, a 'shatter' sound effect is triggered right after the word 'dropped' for dramatic effect.

## Text tags

@@@ Only standard text tags (see: advanced text chapter for custom tags)
@@@ HTML-like tags that allow you to change the appearance (color, size, etc.) of text.

{% include sourcecode.html id="basictexttags" content="
{b}bold{/b}
{i}italic{/i}
{u}underlined{/u}
{font myCustomFont.ttf}different font{/font}
{color FF0000}red text{/color}
{size 24}text size{/size}
{align right}right-aligned text{/align}
{center}center-aligned text{/center}
"%}

@@@ Add screenshot of result of this code example
