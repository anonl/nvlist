---
title: Images
---

   - Introduction and terminology
   - Supported image formats, see: Introduction#supported_file_types

## Backgrounds

{% include sourcecode.html id="bgBasic" content="
Display/change background (instantly)
@bg(\"a\")
Remove background (instantly)
@rmbg()

Display/change background (gradual fade)
@bgf(\"b\")
Remove background (gradual fade)
@rmbgf()
"%}

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

## Positioning

@@@ positioning slots: l/lc/c/rc/r
@@@ X/Y positioning
@@@ Anchors & alignment

### Movement

@@@ translateTo/translateRelative

### Scaling

@@@ setScale, scale
@@@ setUnscaledSize
@@@ scaleToFit

### Rotation

@@@ setRotation

### Z

## Effects

### Color tinting

@@@ setColor

### Transparency

@@@ setAlpha

### Blending mode

@@@ setBlendMode
