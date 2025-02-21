package edu.ftdev;

public interface DbgControls {
    /**
     * There are four modes in which the program can execute:
     * <ul>
     * <li> "step": when program starts or after resuming execution by pressing '1'.
     * <li> "leap": when resuming execution by pressing '2'
     * <li> "jump": when resuming execution by pressing '3'
     * <li> "run": when resuming execution by pressing &lt;space&gt;.
     * </ul>
     * In "step" mode this method pauses the execution. It does nothing in any other modes.
     * <br>
     * If paused, user can resume by pressing '1', '2', '3' or &lt; space &gt; to 
     * continue the execution in the corresponding mode.
     * @throws InterruptedException
     * @see #step(long)
     * @see #leap()
     * @see #jump()
     */
    public void step() throws InterruptedException;
    
    /**
     * There are four modes in which the program can execute:
     * <ul>
     * <li> "step": when program starts or after resuming execution by pressing '1'.
     * <li> "leap": when resuming execution by pressing '2'
     * <li> "jump": when resuming execution by pressing '3'
     * <li> "run": when resuming execution by pressing &lt;space&gt;.
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
     * @see #jump()
     */
    public void step(long delay) throws InterruptedException;
    
    /**
     * There are four modes in which the program can execute:
     * <ul>
     * <li> "step": when program starts or after resuming execution by pressing '1'.
     * <li> "leap": when resuming execution by pressing '2'
     * <li> "jump": when resuming execution by pressing '3'
     * <li> "run": when resuming execution by pressing &lt;space&gt;.
     * </ul>
     * In "step" or "leap" modes, this method pauses the execution until resumed.
     * It does nothing in "jump" or "run" mode. 
     * <br>
     * If paused, user can resume by pressing '1', '2', '3' or &lt; space &gt; to 
     * continue the execution in the corresponding mode.
     * @throws InterruptedException
     * @see #step()
     * @see #step(long)
     * @see #jump()
     */
    public void leap() throws InterruptedException;

     /**
     * There are four modes in which the program can execute:
     * <ul>
     * <li> "step": when program starts or after resuming execution by pressing '1'.
     * <li> "leap": when resuming execution by pressing '2'
     * <li> "jump": when resuming execution by pressing '3'
     * <li> "run": when resuming execution by pressing &lt;space&gt;.
     * </ul>
     * In "step", "leap" or "jump" modes, this method pauses the execution until resumed.
     * It does nothing in "run" mode. 
     * <br>
     * If paused, user can resume by pressing '1', '2', '3' or &lt; space &gt; to 
     * continue the execution in the corresponding mode.
     * @throws InterruptedException
     * @see #step()
     * @see #step(long)
     * @see #leap()
     */
    public void jump() throws InterruptedException;
}
