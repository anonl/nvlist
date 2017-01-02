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

    void update();

}
