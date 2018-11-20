package nl.weeaboo.vn.impl.stats;

import java.io.Serializable;

import nl.weeaboo.filesystem.FilePath;

interface IAnalyticsPreloader extends Serializable {

    void preloadImage(FilePath path);

}