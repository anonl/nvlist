package nl.weeaboo.vn.impl.debug;

/**
 * Provides access to performance-related statistics and messages.
 */
public interface IPerformanceMetrics {

    /**
     * @return The relative CPU load between {@code [0.0, 1.0]}, or {@code -1} if not supported
     */
    public double getCpuLoad();

    /**
     * A multi-line human-readable string describing the performance of the system.
     */
    String getPerformanceSummary();

}
