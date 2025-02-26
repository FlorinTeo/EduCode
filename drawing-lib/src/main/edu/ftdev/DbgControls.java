package edu.ftdev;

/**
 * This interface is used to control the execution of the program, facilitating step-by-step debugging and
 * analysis of code. Placing calls to these methods in code, will cause the execution of the program to be
 * suspended or resumed according to the following rules:
 * <ul>
 *  <li> <i><b>step</b></i>: active when the program starts, or after pressing the key '1'.
 *       In this mode the program runs until it encounters any of the <i>breakStep()</i>, <i>breakLeap()</i>
 *       or <i>breakJump()</i> methods.
 *  <li> <i><b>leap</b></i>: active after pressing the key '2'.
 *       In this mode the program runs until it encounters any of the <i>breakLeap()</i> or <i>breakJump()</i>
 *       methods. Note that <i>breakStep()</i> is ignored in this mode.
 *  <li> <i><b>jump</b></i>: active after pressing the key '3'.
 *       In this mode the program runs until it encounters the <i>breakJump()</i> method. Note that
 *       <i>breakStep()</i> and <i>breakLeap()</i> are ignored in this mode.
 *  <li> <i><b>run</b></i>: active after pressing the &lt;space&gt; key.
 *       In this mode the program runs uninterrupted until it terminates or encounters an exception.
 *       Note that all break methods are ignored in this mode.
 * </ul>
 * At any time during the program execution, the user can switch the execution mode by 
 * pressing any of the '1', '2', '3' or '&lt;space&gt;' keys or, by clicking on the corresponding buttons
 * on the top of the DrawingFrame window.
 */
public interface DbgControls {
    /**
     * Suspends the execution if the program is running in <i>step</i> mode. In any other mode, this method does nothing.
     * The execution can be resumed by pressing any of the '1', '2', '3' or '&lt;space&gt;' keys or 
     * by clicking the corresponding buttons on the top of the DrawingFrame window.
     * @throws InterruptedException if the program gets interrupted while suspended.
     * @see DbgControls
     * @see #breakLeap()
     * @see #breakJump()
     */
    public void breakStep() throws InterruptedException;
    
    /**
     * Pauses the execution for a given number of milliseconds if the program is running in <i>step</i> mode.
     * It any other mode, this method does nothing.
     * The execution can be resumed by pressing any of the '1', '2', '3' or '&lt;space&gt;' keys or 
     * by clicking the corresponding buttons on the top of the DrawingFrame window.
     * @param delay - milliseconds to delay execution in "continuous" mode.
     * @throws InterruptedException if the program gets interrupted while suspended.
     * @see DbgControls
     * @see #breakLeap()
     * @see #breakJump()
     */
    public void breakStep(long delay) throws InterruptedException;
    
    /**
     * Suspends the execution if the program is running in either <i>step</i> or <i>leap</i> modes. 
     * In any other mode, this method does nothing.
     * The execution can be resumed by pressing any of the '1', '2', '3' or '&lt;space&gt;' keys or 
     * by clicking the corresponding buttons on the top of the DrawingFrame window.
     * @throws InterruptedException if the program gets interrupted while suspended.
     * @see DbgControls
     * @see #breakStep()
     * @see #breakJump()
     */
    public void breakLeap() throws InterruptedException;

     /**
     * Suspends the execution if the program is running in either of the <i>step</i>, <i>leap</i> or <i>jump</i> modes. 
     * In <i>run</i> mode, this method does nothing.
     * The execution can be resumed by pressing any of the '1', '2', '3' or '&lt;space&gt;' keys or 
     * by clicking the corresponding buttons on the top of the DrawingFrame window.
     * @throws InterruptedException if the program gets interrupted while suspended.
     * @see DbgControls
     * @see #breakStep()
     * @see #breakLeap()
     */
    public void breakJump() throws InterruptedException;
}
