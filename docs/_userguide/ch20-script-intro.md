---
title: Script introduction
---

## Text

Visual novels are mostly text, so NVList tries to make displaying text as easy as possible. To show a line of text on the screen, just type what you want to appear:
{% include sourcecode.html id="textnotation" content="
A line of text.
Another line of text.
" %}

Any line that doesn't start with a special character (and isn't in a block delimited by special characters) is treated as text and displayed as-is.

## Embedded scripting

Lines starting with an `@`-character are code lines. NVList uses [Lua](https://www.lua.org/manual/5.1/) as its scripting language. Everything after the `@` is treated as Lua code. If you have multiple lines of Lue code in a row, you add `@@` above and below the code to create a block of Lua code.

{% include sourcecode.html id="codenotation" content="
@value = 2
Text
@@
-- The lines between the @@ characters are Lua code
if value > 1 then
    jump(\"badend7\")
end
@@
" %}

## Comments

It can be convenient to leave notes in your script. A popular use is to leave `TO DO` comments for yourself to mark places in the code that are unfinished or can be improved in some way. 

{% include sourcecode.html id="commentnotation" content="
&#35;Single line comment
Text
&#35;&#35;
This comment
spans multiple lines
&#35;&#35;
"%}
