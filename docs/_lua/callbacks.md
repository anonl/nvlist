---
id: callbacks.lua
title: callbacks.lua
---

<!--excerpt-->

{% include sourcecode.html id="textnotation" lang="lua" class="full-screen" content="
function main()
    return titlescreen()
end

function titlescreen()
end

function onExit()
    System.exit(true)
end

function onPrefsChanged()
end

function savepoint(filename)
end
" %}
                