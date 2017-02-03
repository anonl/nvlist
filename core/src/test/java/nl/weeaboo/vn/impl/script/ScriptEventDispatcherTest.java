package nl.weeaboo.vn.impl.script;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.impl.script.lua.LuaScriptFunctionStub;
import nl.weeaboo.vn.script.IScriptFunction;

public class ScriptEventDispatcherTest {

    private ScriptEventDispatcher eventDispatcher;

    @Before
    public void before() {
        eventDispatcher = new ScriptEventDispatcher();
    }

    /** Add a few tasks, and check that the tasks are executed in priority order */
    @Test
    public void taskOrder() {
        LuaScriptFunctionStub alpha = new LuaScriptFunctionStub();
        LuaScriptFunctionStub beta = new LuaScriptFunctionStub();

        eventDispatcher.addTask(alpha, .9);
        eventDispatcher.addTask(beta, 13);

        // Work is returned sorted by (descending) priority
        assertWork(beta, alpha);

        // The same function can be added as a task multiple times
        eventDispatcher.addTask(alpha, 100);
        assertWork(alpha, beta, alpha);

        // Removing a function removes all tasks for that function
        eventDispatcher.removeTask(alpha);
        assertWork(beta);
    }

    /** Test basic behavior of events */
    @Test
    public void events() {
        final LuaScriptFunctionStub alpha = new LuaScriptFunctionStub();
        final LuaScriptFunctionStub beta = new LuaScriptFunctionStub();

        // Events are only executed once
        eventDispatcher.addEvent(alpha);
        assertWork(alpha);
        assertWork();

        // Multiple instances of the same function can be enqueued as events
        eventDispatcher.addEvent(alpha);
        eventDispatcher.addEvent(alpha);
        assertWork(alpha, alpha);

        // Events are executed in the order they were enqueued
        eventDispatcher.addEvent(beta);
        eventDispatcher.addEvent(alpha);
        assertWork(beta, alpha);
    }

    private void assertWork(IScriptFunction... expectedWork) {
        Assert.assertEquals(Arrays.asList(expectedWork),
                eventDispatcher.retrieveWork());
    }

}
