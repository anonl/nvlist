---
title: Replacing built-in screens
---

## Text box

You can register a custom text box using the `registerTextBox` function:

{% include sourcecode.html id="registertextbox" content="
@@
local CustomTextBox = {}

function CustomTextBox.new(self)
    self = AdvTextBox.new(extend(CustomTextBox, self))

    -- Change click indicator image to 'test.png'
    self.clickIndicator.drawable:setTexture(tex(&quot;test&quot;))
    -- Show click indicator in the bottom-right of the text box
    self.clickIndicator.pos = ClickIndicatorPos.RIGHT

    return self
end

-- Registers CustomTextBox as the ADV text box
registerTextBox(TextMode.ADV, CustomTextBox.new)
@@
" %}

For more details, please refer to the source code of [textbox.lua]({{site.baseurl}}{% link _lua/vn/textbox.md %})

## Choice

In many cases you don't even need to replace the default choice screen. The default implementation just shows a row of buttons with text on them. You can change the look of the buttons by altering `gui/button.png` located in the `res/img` folder.

If you want to make more significant changes, you can register a completely custom choice screen using the `registerChoiceScreen` function. For more details, please refer to the source code of [choice.lua]({{site.baseurl}}{% link _lua/vn/choice.md %}).

## Save/load

Like the choice screen, the easiest way to customize the built-in save-/load screens is to just alter the images it uses. In this case, the images are located in `gui/savescreen.png`.

To register custom save or load screens, you can use the `registerSaveScreen`/`registerLoadScreen` functions. For more details, please refer to the source code of [savescreen.lua]({{site.baseurl}}{% link _lua/vn/savescreen.md %})

## Text log

To override the standard text log screen, use the `registerTextLogScreen` function. For more details, please refer to the source code of [textlog.lua]({{site.baseurl}}{% link _lua/vn/textlog.md %})

## Exit handler

You can run custom code when the user tries to close the application window. The default behavior is to just immediately exit without saving, but you can override that to add an are-you-sure confirmation, auto-save, or just show a little shutdown animation.

To change the behavior, define a global function named `onExit` as such:
{% include sourcecode.html id="onexit" content="
@@
function onExit()
    --Add your custom pre-shutdown code here
    System.exit(true)
end
@@
" %}

On mobile platforms (Android, iOS) the `onExit` function is never called.
