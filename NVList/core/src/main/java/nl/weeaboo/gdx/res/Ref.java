package nl.weeaboo.gdx.res;

final class Ref<T> {

    private T referent;

    public Ref(T referent) {
        this.referent = referent;
    }
    
    public T get() {
        return referent;
    }
    
    public void invalidate() {
        referent = null;
    }
    
}
