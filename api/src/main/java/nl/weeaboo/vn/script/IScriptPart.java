package nl.weeaboo.vn.script;

public interface IScriptPart {

    /**
     * Attaches a thread. When this part becomes detached, the thread's destroy method is called. When the
     * thread finishes, it's automatically detached.
     *
     * @param thread The thread to attach.
     */
    public void attachThread(IScriptThread thread);

    /**
     * Attaches a named function, replacing any previously attached functions with the same name.
     *
     * @param name The name to register the function under.
     * @param function The function to register.
     */
    public void attachFunction(String name, IScriptFunction function);

    /**
     * Detaches a previously attached function.
     *
     * @param name The name of the function to detach.
     * @return The detached function, or {@code null} if no function with the given name could be detached.
     * @see #attachFunction(String, IScriptFunction)
     */
    public IScriptFunction detachFunction(String name);

    /**
     * Returns an attached function.
     *
     * @return The function registered under the given name, or {@code null} if no such function exists.
     * @see #attachFunction(String, IScriptFunction)
     */
    public IScriptFunction getFunction(String name);

}
