
#Resource management

@@@ Piggyback on libGDX. AssetManager.
@@@ Not manually disposed -> garbage collected.

#Images

@@@ Images go in /img

@@@ img.xml

@@@ Texture atlas
@@@ interpolation (nearest, bilinear)
@@@ Tiling (only PoT textures -- limitation of OpenGL ES 2.0)
@@@ Mipmaps (only PoT textures -- limitation of OpenGL ES 2.0)
@@@ TODO: Make an importer that converts whatever libGDX uses to my format. That would allow you to use the
          libGDX tools to create the atlas.

##Supported image formats
* PNG
* JPG
* KTX

@@@ Support JNG or WEBP
@@@ KTX is premultiplied, PNG isn't.

#Audio

@@@ Sound + music + voice go in /snd

@@@ snd.xml

##Supported audio formats
* OGG (Vorbis)

#Videos

@@@ Videos go in /video

##Supported video formats
* WEBM

#Fonts

@@@ How does a user declare a font

#Resource variants

@@@ Screen resolution, OS, etc.
