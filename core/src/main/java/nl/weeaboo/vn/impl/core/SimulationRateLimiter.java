package nl.weeaboo.vn.impl.core;

import com.badlogic.gdx.utils.TimeUtils;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IUpdateable;

/** Updates a fixed-rate simulation based on fixed-rate rendering */
public final class SimulationRateLimiter {

    private static final double nanosToSeconds = 1e-9;

    private IUpdateable simulation = IUpdateable.EMPTY;
    private double simulationStepS = 1.0 / 60.0;

    private long lastRenderTime;
    private double accumS;

    // Stats
    private long statsPeriodStart;
    private int statsCount;
    private double averageSimUpdateRate;

    /**
     * Needs to be called every time the app renders to the screen.
     */
    public void onRender() {
        long now = timestampNanos();

        accumS += Math.min(1.0, (now - lastRenderTime) * nanosToSeconds);

        while (accumS >= simulationStepS) {
            accumS -= simulationStepS;

            simulation.update();
            statsCount++;
        }

        if (now >= statsPeriodStart + 1_000_000_000L) {
            double timeDiffS = (now - statsPeriodStart) / 1_000_000_000.0;
            averageSimUpdateRate = statsCount / timeDiffS;

            statsPeriodStart = now;
            statsCount = 0;
        }

        lastRenderTime = now;
    }

    protected long timestampNanos() {
        return TimeUtils.nanoTime();
    }

    /**
     * Sets the simulation that this rate-limiter should run.
     */
    public void setSimulation(IUpdateable simulation, int simulationFps) {
        Checks.checkArgument(simulationFps > 0, "simulationFps must be > 0");

        this.simulation = Checks.checkNotNull(simulation);
        this.simulationStepS = 1.0 / simulationFps;
    }

    /**
     * @return The average simulation update rate over the recent past.
     */
    public double getSimulationUpdateRate() {
        return averageSimUpdateRate;
    }

}
