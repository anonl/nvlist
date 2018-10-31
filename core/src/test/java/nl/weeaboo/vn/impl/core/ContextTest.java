package nl.weeaboo.vn.impl.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.vn.core.IContext;

public class ContextTest  {

    private ContextManager contextManager;
    private Context alpha;
    private Context beta;

    @Before
    public void init() {
        TestEnvironment env = TestEnvironment.newInstance();
        TestContextFactory contextFactory = new TestContextFactory(env.getScriptEnv());
        contextManager = new ContextManager(contextFactory);

        alpha = contextManager.createContext();
        beta = contextManager.createContext();
    }

    @Test
    public void changeActiveContext() {
        assertActiveContexts();

        contextManager.setContextActive(alpha, true);
        assertActiveContexts(alpha);

        contextManager.setContextActive(alpha, false);
        assertActiveContexts();

        contextManager.setContextActive(alpha, true);
        contextManager.setContextActive(beta, true);
        assertActiveContexts(alpha, beta);
    }

    @Test
    public void testContextListeners() {
        ContextListenerStub ls = new ContextListenerStub();
        alpha.addContextListener(ls);

        alpha.setActive(true);
        ls.consumeActivatedCount(1);

        // An event is only triggered when the context becomes active, not when it's already active
        alpha.setActive(true);
        ls.consumeActivatedCount(0);

        alpha.setActive(false);
        ls.consumeDeactivatedCount(1);

        // Like with activation, an event is only generated when the context isn't already deactivated
        alpha.setActive(false);
        ls.consumeDeactivatedCount(0);

        // If the listener is removed, no further events are received
        alpha.removeContextListener(ls);
        alpha.setActive(true);

        // Re-add listener for the next check
        alpha.addContextListener(ls);

        // Destroying the context also fires an event
        alpha.destroy();
        ls.consumeDestroyedCount(1);

        // Double-destroy is a no-op
        alpha.destroy();
        ls.consumeDestroyedCount(0);
    }

    private void assertActiveContexts(IContext... actives) {
        Set<IContext> activesSet = new HashSet<>();
        Collections.addAll(activesSet, actives);

        for (IContext context : contextManager.getContexts()) {
            Assert.assertEquals(activesSet.contains(context), context.isActive());
        }

        // Ensure getActiveContexts() contains the expected elements and no others
        Assert.assertTrue(contextManager.getActiveContexts().containsAll(activesSet));
        Assert.assertTrue(activesSet.containsAll(contextManager.getActiveContexts()));
    }

}
