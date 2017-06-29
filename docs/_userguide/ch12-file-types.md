---
title: Supported file types
---

## Images

| Format | Description |
| --- | --- |
| .jpg   | For backgrounds and other images without transparent parts. |
| .png   | For sprites and other images with (semi-)transparent parts. |
| .jng   | Hybrid format combining the compression of .jpg with support for transparency. Very few image editors directly support this format, but NVList's image optimizer (work in progress) can generate these for you. |
| .ktx   | Uncompressed image data. Images of this type take a large amount of disk-space, but are very fast to load. KTX files are assumed to use [premultiplied alpha](https://en.wikipedia.org/wiki/Alpha_compositing#Other_transparency_methods). |

## Audio

| Format | Description |
| --- | --- |
| .ogg | Vorbis audio. Free to use and offers a good balance between audio quality and file size. |
| .mp3 | The popular lossy audio format. |

## Video

| Format | Description |
| --- | --- |
| .webm | VP8/VP9 video, with Vorbis audio. |
