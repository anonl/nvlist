---
title: Choices and branches
---

## Showing a choice

{% include sourcecode.html id="showChoice" content="
choice(\"I like apples\", \"I like pears\")
"%}

Use the `choice` function to display a choice between one or more options to the player. By default, choices are
displayed as a vertical list of buttons but you can change that if you want to. For more information on changing the
appearance, see ["Replacing built-in screens"]({{site.baseurl}}{% link _userguide/ch53-replacing-built-in-screens.md %})

## Reacting to a choice (branching)

{% include sourcecode.html id="choiceBranch" content="
Showing a choice to the reader: A|B|C
@local selected = choice(\"A\", \"B\", \"C\")
@if selected == 1 then
    Selected A
@elseif selected == 2 then
    Selected B
@else
    Selected C
@end
"%}

The `choice` function returns the number of the selected option (starting at `1`). By storing this number in a
variable (the variable is named `selected` in the example) you can react to the selection made by the player. I

## Persistent storage

Sometimes you want choices to affect not just the current playthrough, but also unlock things permanently for future
playthroughs. A typical use for this would be a "route cleared" flag, or a gallery of bonus content that unlocks after
finishing the game. To store/load such data, use the `setSharedGlobal`/`getSharedGlobal` functions, see
[save.lua]({{site.baseurl}}{% link _lua/vn/save.md %}) for details.

