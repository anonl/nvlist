package nl.weeaboo.vn.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.TimeUtils;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IUpdateable;

/** Updates a fixed-rate simulation based on fixed-rate rendering */
public final class SimulationRateLimiter {

    private static final Logger LOG = LoggerFactory.getLogger(SimulationRateLimiter.class);

    private IUpdateable simulation = IUpdateable.EMPTY;
    private int simulationStepMs = 1_000 / 60;

    private long accumMs;

    // Stats
    private long statsPeriodStart;
    private int statsCount;
    private double averageSimUpdateRate;

    public void onRender(int deltaMs) {
        accumMs += Math.min(1_000, deltaMs);

        LOG.trace("On render (accum: {}ms)", accumMs);

        while (accumMs >= simulationStepMs) {
            accumMs -= simulationStepMs;

            simulation.update();
            statsCount++;
        }

        long now = TimeUtils.nanoTime();
        if (now > statsPeriodStart + 1_000_000_000L) {
            double timeDiffS = (now - statsPeriodStart) / 1_000_000_000.0;
            averageSimUpdateRate = statsCount / timeDiffS;

            statsPeriodStart = now;
            statsCount = 0;
        }
    }

    public void setSimulation(IUpdateable simulation, int simulationFps) {
        Checks.checkArgument(simulationFps > 0, "simulationFps must be > 0");

        this.simulation = Checks.checkNotNull(simulation);
        this.simulationStepMs = 1_000 / simulationFps;
    }

    /**
     * @return The average simulation update rate over the recent past.
     */
    public double getSimulationUpdateRate() {
        return averageSimUpdateRate;
    }

}
