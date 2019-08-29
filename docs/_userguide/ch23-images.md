---
title: Images
---

   - Introduction and terminology
   - Supported image formats, see: Introduction#supported_file_types

## Backgrounds
@@@ bgf
   
## Character images (sprites)

{% include sourcecode.html id="spriteBasic" content="
Creates a sprite in the center (c) position
@local aza = imgf(\"azathoth\", \"c\")
Change image (instantly)
@aza:setTexture(tex(\"azathoth-angry\"))
Change image (animated)
@imgtween(aza, \"azathoth-happy\")
Remove sprite
@rmf(aza)
"%}

@@@ img vs imgf
@@@ rm vs rmf
@@@

### Positioning

@@@ positioning slots: l/lc/c/rc/r

### Movement

@@@ translateTo/translateRelative

