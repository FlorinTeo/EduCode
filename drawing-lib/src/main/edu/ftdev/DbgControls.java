package edu.ftdev;

public interface DbgControls {
    /**
     * There are four modes in which the program can execute:
     * <ul>
     * <li> "step": when program starts or after resuming execution by pressing '1'.
     * <li> "stop": when resuming execution by pressing '2'
     * <li> "leap": when resuming execution by pressing '3'
     * <li> "fast-forward": when resuming execution by pressing &lt;space&gt;.
     * </ul>
     * In "step" mode this method pauses the execution. It does nothing in any other modes.
     * <br>
     * If paused, user can resume by pressing '1', '2', '3' or &lt; space &gt; to 
     * continue the execution in the corresponding mode.
     * @throws InterruptedException
     * @see #step(long)
     * @see #leap()
     */
    public void step() throws InterruptedException;
    
    /**
     * There are four modes in which the program can execute:
     * <ul>
     * <li> "step": when program starts or after resuming execution by pressing '1'.
     * <li> "stop": when resuming execution by pressing '2'
     * <li> "leap": when resuming execution by pressing '3'
     * <li> "fast-forward": when resuming execution by pressing &lt;space&gt;.
     * </ul>
     * In "step" mode, this method delays execution for the given number of
     * milliseconds. It does nothing in any other mode. 
     * <br>
     * If paused, user can resume by pressing '1', '2', '3' or &lt; space &gt; to 
     * continue the execution in the corresponding mode.
     * @param delay - milliseconds to delay execution in "continuous" mode.
     * @throws InterruptedException
     * @see #step()
     * @see #leap()
     */
    public void step(long delay) throws InterruptedException;
    
    /**
     * There are four modes in which the program can execute:
     * <ul>
     * <li> "step": when program starts or after resuming execution by pressing '1'.
     * <li> "stop": when resuming execution by pressing '2'
     * <li> "leap": when resuming execution by pressing '3'
     * <li> "fast-forward": when resuming execution by pressing &lt;space&gt;.
     * </ul>
     * In "step" or "stop" modes, this method pauses the execution until resumed.
     * It does nothing in "leap" or "fast-forward" mode. 
     * <br>
     * If paused, user can resume by pressing '1', '2', '3' or &lt; space &gt; to 
     * continue the execution in the corresponding mode.
     * @throws InterruptedException
     * @see #step()
     * @see #step(long)
     */
    public void stop() throws InterruptedException;

     /**
     * There are four modes in which the program can execute:
     * <ul>
     * <li> "step": when program starts or after resuming execution by pressing '1'.
     * <li> "stop": when resuming execution by pressing '2'
     * <li> "leap": when resuming execution by pressing '3'
     * <li> "fast-forward": when resuming execution by pressing &lt;space&gt;.
     * </ul>
     * In "step", "stop" or "leap" modes, this method pauses the execution until resumed.
     * It does nothing in "fast-forward" mode. 
     * <br>
     * If paused, user can resume by pressing '1', '2', '3' or &lt; space &gt; to 
     * continue the execution in the corresponding mode.
     * @throws InterruptedException
     * @see #step()
     * @see #step(long)
     */
    public void leap() throws InterruptedException;
}
