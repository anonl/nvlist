package nl.weeaboo.vn.script.impl;

import java.util.HashMap;
import java.util.Map;

import nl.weeaboo.entity.Part;
import nl.weeaboo.entity.World;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.IScriptPart;
import nl.weeaboo.vn.script.IScriptThread;

public class ScriptPart extends Part implements IScriptPart {

    private static final long serialVersionUID = 1L;

    private final ScriptThreadCollection<IScriptThread> threads = new ScriptThreadCollection<IScriptThread>();
    private final Map<String, IScriptFunction> functions = new HashMap<String, IScriptFunction>();

    @Override
    public void onDetached(World w) {
        super.onDetached(w);

        threads.destroyThreads();
    }

    @Override
    public void attachThread(IScriptThread thread) {
        threads.add(thread);
    }

    @Override
    public void attachFunction(String name, IScriptFunction function) {
        detachFunction(name);

        functions.put(name, function);
    }

    @Override
    public IScriptFunction detachFunction(String name) {
        return functions.remove(name);
    }

    @Override
    public IScriptFunction getFunction(String name) {
        return functions.get(name);
    }

}
