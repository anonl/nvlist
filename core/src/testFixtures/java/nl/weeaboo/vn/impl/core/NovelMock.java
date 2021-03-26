package nl.weeaboo.vn.impl.core;

import java.io.ObjectInputStream;
import java.io.ObjectOutput;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.render.IDrawBuffer;

public final class NovelMock implements INovel {

    private IEnvironment env;

    public NovelMock(IEnvironment env) {
        this.env = env;
    }

    @Override
    public IEnvironment getEnv() {
        return env;
    }

    @Override
    public void start(String mainFunctionName) {
    }

    @Override
    public void restart() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void readAttributes(ObjectInputStream in) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeAttributes(ObjectOutput out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void draw(IDrawBuffer drawbuffer) {
    }

    @Override
    public void updateInRenderThread() {
    }

    @Override
    public void update() {
        env.update();
    }

}
