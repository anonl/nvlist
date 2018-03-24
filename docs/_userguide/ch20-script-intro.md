---
title: Script introduction
---

@@@ VN is mostly text, so scripting language optimized for displaying text.

## Text

@@@ Just type what you want to appear. Every line is a separate sentence (starts a new paragraph in NVL mode, new window of text for ADV mode (<- too advanced?))

{% include sourcecode.html id="textnotation" content="
A line of text.
Another line of text.
" %}

## Embedded scripting

@@@ Lines starting with an `@`-character are code lines. NVList uses Lua (link!) as its scripting language. Easy to learn and VNs require very little programming.

{% include sourcecode.html id="codenotation" content="
@value = 2
Text
@@
if value > 1 then
    jump(\"badend7\")
end
@@
" %}

@@@ Doesn't really matter what it does, but for completeness sake: assign value of `2` to `value`, then jumps to another script file (`badend7`) if `value` is greater than `1`. 


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
