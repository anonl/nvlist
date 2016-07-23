
---Functions related to the main text box.
-- 
module("vn.textbox", package.seeall)

function getText()
    return getTextState():getText()
end

function getMainTextDrawable()
    return getTextState():getTextDrawable()
end

function setMainTextDrawable(textDrawable)
    getTextState():setTextDrawable(textDrawable)
end
