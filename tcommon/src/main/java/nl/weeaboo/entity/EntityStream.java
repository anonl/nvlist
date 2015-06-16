package nl.weeaboo.entity;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

/**
 * Maintains a filtered and sorted view of a collection of entities.
 */
public final class EntityStream implements Iterable<Entity> {

    private final EntityStreamDef esd;
    private Iterable<Entity> sourceCollection = null;

    private transient Itr itr;
    private transient Entity[] tempArray;
    private transient int tempArrayL;

    public EntityStream(EntityStreamDef esd) {
        this.esd = esd;
    }

    @Override
    public Iterator<Entity> iterator() {
        validate();

        invalidateItr();
        itr = new Itr(tempArray, tempArrayL);
        return itr;
    }

    private void invalidateItr() {
        if (itr != null) {
            itr.invalidate();
            itr = null;
        }
    }

    public void invalidate() {
        invalidateItr();

        tempArray = null;
        tempArrayL = 0;
    }

    public boolean contains(Entity e) {
        validate();

        for (int n = 0; n < tempArrayL; n++) {
            if (e == tempArray[n] || (e != null && e.equals(tempArray[n]))) {
                return true;
            }
        }
        return false;
    }

    public int count() {
        validate();
        return tempArrayL;
    }

    private void validate() {
        if (tempArray != null) {
            return;
        }

        tempArray = new Entity[16];
        tempArrayL = 0;

        // Perform filtering
        if (sourceCollection != null) {
            for (Entity e : sourceCollection) {
                if (esd.accept(e)) {
                    // Reserve room in array if needed
                    if (tempArrayL >= tempArray.length) {
                        Entity[] newArray = new Entity[tempArray.length * 2];
                        System.arraycopy(tempArray, 0, newArray, 0, tempArrayL);
                        tempArray = newArray;
                    }
                    tempArray[tempArrayL++] = e;
                }
            }
        }

        // Sort result
        Arrays.sort(tempArray, 0, tempArrayL, esd);
    }

    public void sendSignal(ISignal signal) {
        for (Entity e : this) {
            if (signal.isHandled()) {
                break;
            }

            e.handleSignal(signal);            
        }
    }
    
    /**
     * Sets the source collection this entity stream is a sorted, filtered view of.
     */
    public void setSource(Iterable<Entity> source) {
        sourceCollection = source;
        invalidate();
    }

    // Inner classes
    private static class Itr implements Iterator<Entity> {

        private final Entity[] array;
        private final int len;

        private int cursor = -1;

        public Itr(Entity[] array, int len) {
            this.array = array;
            this.len = len;
        }

        private void invalidate() {
            cursor = Integer.MIN_VALUE;
        }

        private void checkValid() {
            if (cursor == Integer.MIN_VALUE) {
                throw new ConcurrentModificationException("Iterator is no longer valid");
            }
        }

        @Override
        public boolean hasNext() {
            checkValid();
            return cursor + 1 < len;
        }

        @Override
        public Entity next() {
            checkValid();
            return array[++cursor];
        }

        @Override
        public void remove() {
            checkValid();
            array[cursor].destroy();
        }

    }

}
