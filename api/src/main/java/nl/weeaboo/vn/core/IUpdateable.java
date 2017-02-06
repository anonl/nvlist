package nl.weeaboo.vn.core;

/**
 * Interface for objects that require a periodic tick function.
 */
public interface IUpdateable {

    IUpdateable EMPTY = new IUpdateable() {
        @Override
        public void update() {
        }
    };

    /**
     * Perform some task. This method is more or less equivalent to {@link Runnable#run()}.
     */
    void update();

}
