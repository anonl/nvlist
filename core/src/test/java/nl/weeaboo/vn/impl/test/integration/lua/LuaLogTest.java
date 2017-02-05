package nl.weeaboo.vn.impl.test.integration.lua;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import nl.weeaboo.logging.slf4j.InMemoryLogger;
import nl.weeaboo.logging.slf4j.InMemoryLogger.LogEntry;
import nl.weeaboo.logging.slf4j.InMemoryLogger.LogLevel;
import nl.weeaboo.vn.impl.script.lib.LogLib;
import nl.weeaboo.vn.script.ScriptException;

public class LuaLogTest extends LuaIntegrationTest {

    private InMemoryLogger logger;

    @Before
    public void before() throws ScriptException {
        logger = new InMemoryLogger();

        TestLogLib testLogLib = new TestLogLib();
        testLogLib.initEnv(env.getScriptEnv());
    }

    /** Log some messages at different log levels */
    @Test
    public void basicLogging() {
        loadScript("integration/log/basiclogging");

        List<LogEntry> entries = logger.getEntries();
        assertEntry(entries.get(0), LogLevel.TRACE, "trace");
        assertEntry(entries.get(1), LogLevel.DEBUG, "debug");
        assertEntry(entries.get(2), LogLevel.INFO, "info");
        assertEntry(entries.get(3), LogLevel.WARN, "warn");
        assertEntry(entries.get(4), LogLevel.ERROR, "error");
    }

    /** Check that using log format strings works */
    @Test
    public void logFormat() {
        loadScript("integration/log/logformat");

        List<LogEntry> entries = logger.getEntries();
        assertEntry(entries.get(0), LogLevel.TRACE, "trace abc 123");
        assertEntry(entries.get(1), LogLevel.DEBUG, "debug abc 123");
        assertEntry(entries.get(2), LogLevel.INFO, "info abc 123");
        assertEntry(entries.get(3), LogLevel.WARN, "warn abc 123");
        assertEntry(entries.get(4), LogLevel.ERROR, "error abc 123");
    }

    private void assertEntry(LogEntry entry, LogLevel level, String message) {
        assertEntry(entry, level, message, null);
    }

    private void assertEntry(LogEntry entry, LogLevel level, String message, Throwable exception) {
        Assert.assertEquals(level, entry.getLevel());
        Assert.assertEquals(message, entry.getMessage());
        Assert.assertEquals(exception, entry.getException());
    }

    @SuppressWarnings("serial")
    private class TestLogLib extends LogLib {

        @Override
        protected Logger getLogger(List<String> luaStackTrace) {
            return logger;
        }

    }

}
