package nl.weeaboo.vn.gdx.res;

final class DefaultWeigher<T> implements IWeigher<T> {

    @Override
    public int weigh(T object) {
        return 1;
    }

}