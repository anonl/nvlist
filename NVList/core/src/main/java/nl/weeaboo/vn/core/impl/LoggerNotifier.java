package nl.weeaboo.vn.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.core.INotifier;

public final class LoggerNotifier implements INotifier {

    private final Logger LOG = LoggerFactory.getLogger(LoggerNotifier.class);

    @Override
    public void message(String message) {
        LOG.info(message);
    }

}
