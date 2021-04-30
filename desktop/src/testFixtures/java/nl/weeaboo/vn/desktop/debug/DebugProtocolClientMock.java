package nl.weeaboo.vn.desktop.debug;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.lsp4j.debug.ContinuedEventArguments;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.ThreadEventArguments;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;

final class DebugProtocolClientMock implements IDebugProtocolClient {

    private final List<StoppedEventArguments> stoppedEvents = new ArrayList<>();
    private final List<ContinuedEventArguments> continuedEvents = new ArrayList<>();
    private final List<ThreadEventArguments> threadEvents = new ArrayList<>();

    @Override
    public void stopped(StoppedEventArguments event) {
        stoppedEvents.add(event);
    }

    public @Nullable StoppedEventArguments consumeStopped() {
        return consume(stoppedEvents);
    }

    @Override
    public void continued(ContinuedEventArguments event) {
        continuedEvents.add(event);
    }

    public @Nullable ContinuedEventArguments consumeContinued() {
        return consume(continuedEvents);
    }

    @Override
    public void thread(ThreadEventArguments event) {
        threadEvents.add(event);
    }

    public @Nullable ThreadEventArguments consumeThread() {
        return consume(threadEvents);
    }

    private static <T> @Nullable T consume(List<T> events) {
        if (events.isEmpty()) {
            return null;
        }
        return events.remove(0);
    }

}
