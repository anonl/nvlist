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

## Save/load

@@@ registerSaveScreen
@@@ registerLoadScreen

## Choice

@@@ registerChoiceScreen

## Text log

@@@ registerTextLogScreen

## Exit handler

@@@ Override the global onExit() method to run code when the user tries to quit (for example by clicking on the close button of the application window)
