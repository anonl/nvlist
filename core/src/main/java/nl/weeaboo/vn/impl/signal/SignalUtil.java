package nl.weeaboo.vn.impl.signal;

import java.util.Arrays;

import nl.weeaboo.vn.signal.ISignal;
import nl.weeaboo.vn.signal.ISignalHandler;

public final class SignalUtil {

    private SignalUtil() {
    }

    public static void forward(ISignal signal, ISignalHandler... handlers) {
        forward(signal, Arrays.asList(handlers));
    }

    public static void forward(ISignal signal, Iterable<? extends ISignalHandler> handlers) {
        for (ISignalHandler handler : handlers) {
            if (signal.isHandled()) {
                break;
            }
            handler.handleSignal(signal);
        }
    }
}
