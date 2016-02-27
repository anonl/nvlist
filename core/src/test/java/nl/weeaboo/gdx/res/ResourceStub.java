package nl.weeaboo.gdx.res;

public class ResourceStub<T> implements IResource<T> {

    private static final long serialVersionUID = 1L;

    private T resource;

    @Override
    public T get() {
        return resource;
    }

    public void set(T resource) {
        this.resource = resource;
    }

}
