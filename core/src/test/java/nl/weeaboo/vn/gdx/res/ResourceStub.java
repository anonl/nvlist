package nl.weeaboo.vn.gdx.res;

public class ResourceStub<T> implements IResource<T> {

    private static final long serialVersionUID = 1L;

    private T resource;

    public ResourceStub() {
    }

    public ResourceStub(T resource) {
        this.resource = resource;
    }

    @Override
    public T get() {
        return resource;
    }

    /** Sets the inner resource reference. */
    public void set(T resource) {
        this.resource = resource;
    }

}
