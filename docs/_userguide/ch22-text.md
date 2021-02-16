---
title: Text
---

## NVL and ADV modes

NVList has built-in support for two text display styles: ADV and NVL. To change between styles, use the `setTextModeADV()` or `setTextModeNVL()` functions.

{% include sourcecode.html id="change-textmode" content="
@bgf(\"bg/marsh\")
@setTextModeADV()
First line
Second line
" %}

### ADV
Text is displayed in a text box, typically at the bottom of the screen. One line of text is shown at a time.

![ADV mode]({{site.baseurl}}{% link assets/userguide/ch22-text.jpg %})

### NVL
Text is displayed over the entire screen. New lines are appended to the currently visible text until the screen is explicitly cleared by using the `clearText()` function.

![NVL mode]({{site.baseurl}}{% link assets/userguide/ch22-text-nvl.jpg %})

## Speakers

NVList has special support for displaying text spoken by a character.

{% include sourcecode.html id="speakersyntax" content="
#Register 'bob' as a speaker (you only need to do this once)
@registerSpeaker(\"bob\", \"Man named Bob\")

#Now that 'bob' is defined, we can make him say stuff by prefixing text lines with $bob
$bob My name is Bob
$bob I'm still talking
"%}

## Stringifiers

Stringifiers are a way to display dynamic text.

{% include sourcecode.html id="stringifiersyntax" content="
# Assign 'red' to a Lua variable
@local color = 'purple'

# Displays: My favorite color is purple 
My favorite color is $color
"%}

## Embedded commands

It's also possible to embed code in text lines. The embedded code will be executed when its surrounding text becomes visible.

{% include sourcecode.html id="embeddedInText" content="
I dropped [sound(\"sfx/shatter\")] the cup from my hands.
" %}

The main use for this is syncing up special effects with a specific word in the line of text. In the above example, a 'shatter' sound effect is triggered right after the word 'dropped' for dramatic effect.

## Text tags

Text tags can be used to apply styling to text. Simply surround the text you want to style with any of the following tags:

{% include sourcecode.html id="basictexttags" content="
{b}bold{/b}
{i}italic{/i}
{u}underlined{/u}
{font=\"custom-font.ttf\"}different font{/font}
{color=0xFFFF0000}red text{/color}
{size=24}text size{/size}
{align=\"right\"}right-aligned text{/align}
{center}center-aligned text{/center}
"%}

![Text tags]({{site.baseurl}}{% link assets/userguide/ch22-text-tags.jpg %})
