package nl.weeaboo.vn.video.impl;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerInitException;

import nl.weeaboo.filesystem.FilePath;

interface IGdxVideoPlayerFactory {

    VideoPlayer createVideoPlayer() throws VideoPlayerInitException;

    FileHandle resolveFileHandle(FilePath filePath);

}
