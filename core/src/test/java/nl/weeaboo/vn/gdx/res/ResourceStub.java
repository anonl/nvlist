package nl.weeaboo.vn.gdx.res;

import nl.weeaboo.vn.gdx.res.IResource;

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

    public void set(T resource) {
        this.resource = resource;
    }

}
