package nl.weeaboo.vn.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;

import nl.weeaboo.vn.render.IDrawBuffer;

public interface INovel extends IUpdateable {

    void start(String mainFunctionName) throws InitException;
    void restart() throws InitException;
    void stop();

    void readAttributes(ObjectInputStream in) throws IOException, ClassNotFoundException;
    void writeAttributes(ObjectOutput out) throws IOException;

    IEnvironment getEnv();
    void draw(IDrawBuffer drawbuffer);

}
