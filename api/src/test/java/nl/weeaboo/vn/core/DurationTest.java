package nl.weeaboo.vn.core;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.gdx.test.ExceptionTester;

public class DurationTest {

    private static final ImmutableSet<TimeUnit> ACCEPTABLE_UNITS = ImmutableSet.of(
            TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS, TimeUnit.MILLISECONDS
    );

    private ExceptionTester exTester;

    @Before
    public void before() {
        exTester = new ExceptionTester();
    }

    @Test
    public void zeroDuration() {
        // As a special optimization, a preallocated ZERO duration is returned
        Assert.assertSame(Duration.ZERO, Duration.fromSeconds(0));

        // The same optimization works for every time unit
        for (TimeUnit unit : ACCEPTABLE_UNITS) {
            Assert.assertSame(Duration.ZERO, Duration.fromDuration(0, unit));
        }
    }

    /** Time units with too much precision are rejected to avoid unexpected loss of precision */
    @Test
    public void unacceptableUnits() {
        exTester.expect(IllegalArgumentException.class,
                () -> Duration.fromDuration(1_000_000, TimeUnit.MICROSECONDS));

        exTester.expect(IllegalArgumentException.class,
                () -> Duration.fromDuration(1_000_000, TimeUnit.NANOSECONDS));
    }

    @Test
    public void testToSeconds() {
        assertSeconds(0, Duration.ZERO);
        assertSeconds(123, Duration.fromSeconds(123));
        assertSeconds(2 * 3600, Duration.fromDuration(2, TimeUnit.HOURS));
    }


    @Test
    public void testToString() {
        assertString("0:00", Duration.ZERO);

        // Short durations are printed as minutes:seconds
        assertString("2:03", Duration.fromSeconds(123));

        // Durations longer than 1 hour are printed as hours:minutes:seconds
        assertString("2:00:00", Duration.fromDuration(2, TimeUnit.HOURS));
        assertString("3:25:45", Duration.fromSeconds(12345));

        // We never change to days or weeks, hours is the largest unit printed
        assertString("120:00:00", Duration.fromDuration(5, TimeUnit.DAYS));
    }

    private void assertSeconds(long expected, Duration duration) {
        Assert.assertEquals(expected, duration.toSeconds());
    }

    private void assertString(String expected, Duration duration) {
        Assert.assertEquals(expected, duration.toString());
    }

}
