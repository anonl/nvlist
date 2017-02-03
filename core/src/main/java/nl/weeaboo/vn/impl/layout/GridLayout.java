package nl.weeaboo.vn.impl.layout;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.layout.GridCellConstraints;
import nl.weeaboo.vn.layout.IGridLayout;
import nl.weeaboo.vn.layout.ILayoutElem;
import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;

public class GridLayout extends LayoutGroup implements IGridLayout {

    private static final long serialVersionUID = LayoutImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(GridLayout.class);
    private static final double ADJUST_EPSILON = 0.001;

    private final List<GridRow> rows = Lists.newArrayList();

    public GridLayout(ILayoutElemPeer visualElem) {
        super(visualElem);
    }

    @Override
    public void add(ILayoutElem elem, GridCellConstraints constraints) {
        GridRow row = reserveRow();
        row.cells.add(new GridCell(elem, constraints));

        invalidateLayout();
    }

    @Override
    public void endRow() {
        addRow();
    }

    private GridRow reserveRow() {
        if (rows.isEmpty()) {
            addRow();
        }
        return rows.get(rows.size() - 1);
    }
    private void addRow() {
        rows.add(new GridRow());
    }

    @Override
    public void remove(ILayoutElem elem) {
        for (GridRow row : rows) {
            if (row.remove(elem)) {
                break;
            }
        }
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColCount() {
        int cols = 0;
        for (GridRow row : rows) {
            cols = Math.max(cols, row.getColCount());
        }
        return cols;
    }

    protected Iterable<GridCell> getRowCells(int rowIndex) {
        return rows.get(rowIndex).cells;
    }

    protected Iterable<GridCell> getColCells(int colIndex) {
        List<GridCell> result = Lists.newArrayList();
        for (GridRow row : rows) {
            GridCell cell = row.findColCell(colIndex);
            if (cell != null) {
                result.add(cell);
            }
        }
        return result;
    }

    @Override
    protected void doLayout() {
        TrackMetrics[] colSizes = initColumns();
        adjustColumnSizes(colSizes);

        TrackMetrics[] rowSizes = initRows(colSizes);
        adjustRowSizes(rowSizes);

        // Position cells based on calculated rows/columns
        Rect2D innerRect = getChildLayoutBounds();
        double y = innerRect.y;
        for (int r = 0; r < rowSizes.length; r++) {
            GridRow row = rows.get(r);

            double x = innerRect.x;
            double rowH = rowSizes[r].breadth;
            for (int c = 0; c < colSizes.length; c++) {
                double colW = colSizes[c].breadth;

                GridCell cell = row.findColCell(c);
                if (cell != null) {
                    LOG.debug("Set cell bounds: x={}, y={}, w={}, h={}", x, y, colW, rowH);
                    cell.setBounds(x, y, colW, rowH);
                }

                x += colW;
            }
            y += rowH;
        }
    }

    private void adjustColumnSizes(TrackMetrics[] colSizes) {
        double remainingSpace = getChildLayoutWidth() - getTotalBreadth(colSizes);
        adjustTrackSizes(colSizes, remainingSpace);
    }

    private void adjustRowSizes(TrackMetrics[] rowSizes) {
        double remainingSpace = getChildLayoutHeight() - getTotalBreadth(rowSizes);
        adjustTrackSizes(rowSizes, remainingSpace);
    }

    /**
     * @param remainingSpace Negative when shrinking
     */
    private void adjustTrackSizes(TrackMetrics[] tracks, double remainingSpace) {
        /*
         * While we have remaining space, divide space evenly between elements until they reach their
         * preferred size. If we have remaining space after that, start growing elements to their max sizes.
         */
        for (LayoutSizeType phase : new LayoutSizeType[] { LayoutSizeType.PREF, LayoutSizeType.MAX }) {
            Collection<TrackMetrics> unfrozen = Lists.newArrayList(tracks);
            while (!unfrozen.isEmpty()) {
                if (remainingSpace < 0) {
                    return; // Finished
                }

                // TODO: For better render results, we should try to avoid non-integral track breadths
                // Spread remaining space evenly amongst tracks
                final double spacePerTrack = remainingSpace / unfrozen.size();
                for (Iterator<TrackMetrics> itr = unfrozen.iterator(); itr.hasNext();) {
                    TrackMetrics track = itr.next();

                    double adjusted = track.grow(phase, spacePerTrack);
                    remainingSpace -= adjusted;
                    if (Math.abs(adjusted) <= ADJUST_EPSILON) {
                        itr.remove();
                    }
                }
            }
        }
    }

    /** Creates initial sizes for each column */
    private TrackMetrics[] initColumns() {
        TrackMetrics[] result = new TrackMetrics[getColCount()];
        for (int c = 0; c < result.length; c++) {
            TrackMetrics tm = new TrackMetrics();
            for (GridCell cell : getColCells(c)) {
                tm.updatePrefBreadth(cell.calculateLayoutWidth(LayoutSizeType.PREF, LayoutSize.UNKNOWN));
                tm.updateMinBreadth(cell.calculateLayoutWidth(LayoutSizeType.MIN, LayoutSize.UNKNOWN));
                tm.updateMaxBreadth(cell.calculateLayoutWidth(LayoutSizeType.MAX, LayoutSize.UNKNOWN));
            }
            tm.init();
            result[c] = tm;
        }
        return result;
    }

    /** Creates initial sizes for each row based on supplied column sizes */
    private TrackMetrics[] initRows(TrackMetrics[] colSizes) {
        TrackMetrics[] result = new TrackMetrics[getRowCount()];
        for (int r = 0; r < result.length; r++) {
            TrackMetrics tm = new TrackMetrics();
            int c = 0;
            for (GridCell cell : getRowCells(r)) {
                LayoutSize widthHint = LayoutSize.of(colSizes[c].breadth);
                tm.updatePrefBreadth(cell.calculateLayoutHeight(LayoutSizeType.PREF, widthHint));
                tm.updateMinBreadth(cell.calculateLayoutHeight(LayoutSizeType.MIN, widthHint));
                tm.updateMaxBreadth(cell.calculateLayoutHeight(LayoutSizeType.MAX, widthHint));
                c++;
            }
            tm.init();
            result[r] = tm;
        }
        return result;
    }

    private static double getTotalBreadth(TrackMetrics[] sizes) {
        double total = 0.0;
        for (TrackMetrics size : sizes) {
            total += size.breadth;
        }
        return total;
    }

    private static class GridRow implements Serializable {

        private static final long serialVersionUID = 1L;

        public final List<GridCell> cells = Lists.newArrayList();

        public boolean remove(ILayoutElem elem) {
            for (Iterator<GridCell> itr = cells.iterator(); itr.hasNext();) {
                GridCell cell = itr.next();
                if (cell.contains(elem)) {
                    itr.remove();
                    return true;
                }
            }
            return false;
        }

        /**
         * @return The cell at the requested column index, or {@code null} if that cell doesn't exist.
         */
        public GridCell findColCell(int colIndex) {
            // Rowspan/colspan not yet supported
            if (colIndex < 0 || colIndex >= cells.size()) {
                return null;
            }
            return cells.get(colIndex);
        }

        public int getColCount() {
            // Rowspan/colspan not yet supported
            return cells.size();
        }

    }

    private static class GridCell implements Serializable {

        private static final long serialVersionUID = 1L;

        final ILayoutElem contents;
        final GridCellConstraints constraints;

        public GridCell(ILayoutElem contents, GridCellConstraints constraints) {
            this.contents = Checks.checkNotNull(contents);
            this.constraints = Checks.checkNotNull(constraints);
        }

        public void setBounds(double x, double y, double w, double h) {
            // Limit to max cell size
            LayoutSize layoutW = LayoutSize.of(w);
            LayoutSize layoutH = LayoutSize.of(h);

            layoutW = LayoutSize.min(calculateLayoutWidth(LayoutSizeType.MAX, LayoutSize.UNKNOWN), layoutW);
            layoutH = LayoutSize.min(calculateLayoutHeight(LayoutSizeType.MAX, layoutW), layoutH);

            contents.setLayoutBounds(Rect2D.of(x, y, layoutW.value(w), layoutH.value(h)));
        }

        public boolean contains(ILayoutElem elem) {
            return contents.equals(elem);
        }

        public LayoutSize calculateLayoutWidth(LayoutSizeType type, LayoutSize heightHint) {
            if (type == LayoutSizeType.MAX && !constraints.growX) {
                type = LayoutSizeType.PREF;
            }
            return contents.calculateLayoutWidth(type, heightHint);
        }

        public LayoutSize calculateLayoutHeight(LayoutSizeType type, LayoutSize widthHint) {
            if (type == LayoutSizeType.MAX && !constraints.growY) {
                type = LayoutSizeType.PREF;
            }
            return contents.calculateLayoutHeight(type, widthHint);
        }

    }

    /** Calculated row/column sizes */
    private static class TrackMetrics {

        LayoutSize prefBreadth = LayoutSize.ZERO;
        LayoutSize minBreadth = LayoutSize.ZERO;
        LayoutSize maxBreadth = LayoutSize.INFINITE;

        double breadth;

        /** Normalize/sanitize values and set the initial breadth */
        public void init() {
            prefBreadth = LayoutSize.max(minBreadth, prefBreadth);
            maxBreadth = LayoutSize.max(prefBreadth, maxBreadth);

            breadth = minBreadth.value(0.0);
        }

        public void updatePrefBreadth(LayoutSize newBreadth) {
            prefBreadth = LayoutSize.max(prefBreadth, newBreadth);
        }
        public void updateMinBreadth(LayoutSize newBreadth) {
            minBreadth = LayoutSize.max(minBreadth, newBreadth);
        }
        public void updateMaxBreadth(LayoutSize newBreadth) {
            maxBreadth = LayoutSize.min(maxBreadth, newBreadth);
        }

        private LayoutSize getSize(LayoutSizeType type) {
            switch (type) {
            case MIN: return minBreadth;
            case PREF: return prefBreadth;
            case MAX: return maxBreadth;
            default: throw new IllegalArgumentException("Unsupported size type: " + type);
            }
        }

        public double grow(LayoutSizeType limitType, double amount) {
            LayoutSize limit = getSize(limitType);
            amount = Math.min(amount, limit.value(Double.MAX_VALUE) - breadth);
            breadth += amount;
            return amount;
        }
    }

}
