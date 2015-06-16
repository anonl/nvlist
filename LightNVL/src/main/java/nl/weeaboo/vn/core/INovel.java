package nl.weeaboo.vn.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;

public interface INovel extends IUpdateable {

    public void start(String mainFunctionName);
    public void stop();

    void readAttributes(ObjectInputStream in) throws IOException, ClassNotFoundException;
    void writeAttributes(ObjectOutput out) throws IOException;

    public IEnvironment getEnv();

}
