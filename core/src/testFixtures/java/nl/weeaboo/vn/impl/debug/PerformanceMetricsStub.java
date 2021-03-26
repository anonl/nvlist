package nl.weeaboo.vn.impl.debug;

public final class PerformanceMetricsStub implements IPerformanceMetrics {

    @Override
    public double getCpuLoad() {
        return Double.NaN;
    }

    @Override
    public String getPerformanceSummary() {
        return "test";
    }

}
