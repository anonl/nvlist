package nl.weeaboo.vn.buildgui.gradle;

import org.gradle.tooling.ProgressEvent;
import org.gradle.tooling.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LoggingProgressListener implements ProgressListener {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingProgressListener.class);

    @Override
    public void statusChanged(ProgressEvent event) {
        LOG.debug("[gradle] {}", event.getDescription());
    }

}
