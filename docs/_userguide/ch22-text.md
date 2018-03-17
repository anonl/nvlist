---
title: Text
---

- Text
- Terminology

## Speakers

@@@ Shorthand for name in text box.
@@@ Example syntax
@@@ short-name (no spaces), full name

## Stringifiers

- Stringifiers

## Embedded commands

It's also possible to embed code in text lines. The embedded code will be executed when its surrounding text becomes visible.

{% include sourcecode.html id="embeddedInText" content="
I dropped [sound(\"sfx/shatter\")] the cup from my hands.
" %}

The main use for this is syncing up special effects with a specific word in the line of text. In the above example, a 'shatter' sound effect is triggered right after the word 'dropped' for dramatic effect.

## Text tags

@@@ Standard text tags (see: advanced text chapter for custom
