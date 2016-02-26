package nl.weeaboo.vn.render.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import nl.weeaboo.common.StringUtil;

public class RenderStats {

    private static final Logger LOG = LoggerFactory.getLogger(RenderStats.class);

	private final CommandStats[] cmdStats;
	private final List<Integer> quadBatchSizes;
	private int framesRendered;

	public RenderStats() {
		cmdStats = new CommandStats[256]; //Command ID is a byte, so max 256 possibilities
		quadBatchSizes = new ArrayList<Integer>();
	}

	public void startRender() {
	}
	public void stopRender() {
		framesRendered++;
        if ((framesRendered & 0x3FF) == 0) {
            LOG.trace(toString());
		}

		Arrays.fill(cmdStats, null);
		quadBatchSizes.clear();
	}

	public void onRenderQuadBatch(int count) {
		quadBatchSizes.add(count);
	}

	public void logCommand(RenderCommand cmd, long durationNanos) {
		CommandStats stats = cmdStats[cmd.id & 0xFF];
		if (stats == null) {
            cmdStats[cmd.id & 0xFF] = stats = new CommandStats(cmd.getClass());
		}
        stats.addRun();
        stats.addTime(durationNanos);
	}

    public void logExtra(Class<? extends RenderCommand> cmdClass, int cmdId, long durationNanos) {
        CommandStats stats = cmdStats[cmdId];
        if (stats == null) {
            cmdStats[cmdId] = stats = new CommandStats(cmdClass);
        }
        stats.addTime(durationNanos);
    }

	@Override
	public String toString() {
        StringBuilder sb = new StringBuilder("[Render Stats]\n");
        Joiner.on('\n').skipNulls().appendTo(sb, cmdStats);

		sb.append("\nQuad Render Batches:");
		for (int i : quadBatchSizes) {
			sb.append(' ').append(i);
		}

		return sb.toString();
	}

	private static class CommandStats {

        private final String label;

		private int count;
		private long durationNanos;

        public CommandStats(Class<?> cmdClass) {
            label = cmdClass.getSimpleName();
		}

        public void addRun() {
			this.count++;
        }

        public void addTime(long durationNanos) {
			this.durationNanos += durationNanos;
		}

		@Override
		public String toString() {
            return String.format(Locale.ROOT, "%s[%03dx] %s", label, count,
                    StringUtil.formatTime(durationNanos, TimeUnit.NANOSECONDS));
		}
	}

}
