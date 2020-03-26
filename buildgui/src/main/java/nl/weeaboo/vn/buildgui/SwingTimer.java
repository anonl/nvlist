package nl.weeaboo.vn.buildgui;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.Timer;

import nl.weeaboo.common.Checks;

final class SwingTimer {

    private static final SwingTimer INSTANCE = new SwingTimer();

    private final Timer globaltimer;

    private final CopyOnWriteArrayList<Animation> runningAnimations = new CopyOnWriteArrayList<>();

    private SwingTimer() {
        globaltimer = new Timer(1_000, e -> tick());
        globaltimer.start();
    }

    public static void startAnimation(JComponent component, int millisPerFrame, Runnable onFrameCallback) {
        Animation animation = new Animation(millisPerFrame, onFrameCallback);
        if (component.isShowing()) {
            animation.start();
        }

        SwingUtil.registerVisibilityChangeListener(component, () -> {
            if (component.isShowing()) {
                animation.start();
            } else {
                animation.stop();
            }
        });
    }

    private void tick() {
        long nanoTime = System.nanoTime();
        for (Animation animation : runningAnimations) {
            animation.tick(nanoTime);
        }
    }

    private static final class Animation {

        private final int millisPerFrame;
        private final Runnable onFrameCallback;

        private long lastFrameNanoTime;

        public Animation(int millisPerFrame, Runnable onFrameCallback) {
            this.millisPerFrame = Checks.checkRange(millisPerFrame, "millisPerFrame", 1);
            this.onFrameCallback = Checks.checkNotNull(onFrameCallback);
        }

        public void start() {
            INSTANCE.runningAnimations.add(this);
        }

        public void stop() {
            INSTANCE.runningAnimations.remove(this);
        }

        public void tick(long nanoTime) {
            long nanosPerFrame = millisPerFrame * 1_000_000L;
            if (nanoTime - lastFrameNanoTime >= nanosPerFrame) {
                onFrameCallback.run();

                lastFrameNanoTime = nanoTime - nanoTime % nanosPerFrame;
            }
        }
    }
}
