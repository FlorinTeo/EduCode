package edu.ftdev;

/**
 * Interface used to control the execution of the program, facilitating step-by-step debugging and
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
 * pressing any of the '1', '2', '3' or '&lt;space&gt;' keys or, by clicking on the corresponding control buttons
 * on the top of the DrawingFrame window.
 * <table style="border-collapse: collapse; border: none;">
 *   <caption>Meaning of the control buttons:</caption>
 *   <tr>
 *     <td style="border: none;"><img src="https://florinteo.github.io/EduCode/DrawingLib/res/ctrl_running.png" alt="ctrl-running.png"></td>
 *     <td style="border: none;">Program is currently running in the execution mode colored in yellow
 *         (<i>step</i> in the example).</td>
 *   </tr>
 *   <tr>
 *     <td style="border: none;"><img src="https://florinteo.github.io/EduCode/DrawingLib/res/ctrl_suspended.png" alt="ctrl-suspended.png"></td>
 *     <td style="border: none;">Program is suspended in a call matching the button highlighted in yellow 
 *         (<i>breakLeap()</i> in the example).</td>
 *   </tr>
 * </table>
 */
public interface DbgControls {
    /**
     * Suspends the execution according to {@link #breakStep(String, Object...)} rules, with a default empty message string.
     * @return true if execution was suspended, false otherwise.
     * @see DbgControls
     * @see #breakStep(String, Object...)
     * @see #breakLeap()
     * @see #breakJump()
     */
    public boolean breakStep();

    /**
     * Suspends the execution according to {@link #breakStep(long, String, Object...)} rules, with a default empty message string.
     * @param delay milliseconds to delay execution in <i>leap</i> mode.
     * @return true if execution was suspended, false otherwise.
     * @see DbgControls
     * @see #breakStep(long, String, Object...)
     * @see #breakLeap()
     * @see #breakJump()
     */
    public boolean breakStep(long delay);

    /**
     * Suspends the execution if the program is running in <i><b>step</b></i> mode. In any other mode, this method does nothing.
     * The execution can be resumed by pressing any of the '1', '2', '3' or '&lt;space&gt;' keys or 
     * by clicking the corresponding buttons on the top of the DrawingFrame window.
     * When execution is suspended, a message composed by <i>format</i> and <i>args</i> is shown in the lower-right status bar. 
     * Their usage is defined in {@link String#format(String, Object...)} documentation.
     * @param format the format of the message string labeling the breaking point.
     * @param args the arguments for the format of the message string.
     * @return true if execution was suspended, false otherwise.
     * @see DbgControls
     * @see #breakLeap(String, Object...)
     * @see #breakJump(String, Object...)
     */
    public boolean breakStep(String format, Object... args);

    /**
     * Suspends the execution if the program is running in <i><b>step</b></i> mode. If the program is running in <i><b>leap</b></i> mode
     * the execution will not be suspended, but it will be delayed by the given number of milliseconds. It any other mode, this method does nothing.
     * The execution can be resumed by pressing any of the '1', '2', '3' or '&lt;space&gt;' keys or 
     * by clicking the corresponding buttons on the top of the DrawingFrame window.
     * When execution is suspended, a message composed by <i>format</i> and <i>args</i> is shown in the lower-right status bar. 
     * Their usage is defined in {@link String#format(String, Object...)} documentation.
     * @param format the format of the message string labeling the breaking point.
     * @param args the arguments for the format of the message string.
     * @param delay milliseconds to delay execution in "continuous" mode.
     * @return true if execution was suspended, false otherwise.
     * @see DbgControls
     * @see #breakLeap()
     * @see #breakJump()
     */
    public boolean breakStep(long delay, String format, Object... args);
    
    /**
     * Suspends the execution according to {@link #breakLeap(String, Object...)} rules, with a default empty message string.
     * @return true if execution was suspended, false otherwise.
     * @see #breakLeap(String, Object...)
     * @see DbgControls
     * @see #breakStep()
     * @see #breakJump()
     */
    public boolean breakLeap();

    /**
     * Suspends the execution if the program is running in either <i><b>step</b></i> or <i><b>leap</b></i> modes. 
     * In any other mode, this method does nothing.
     * The execution can be resumed by pressing any of the '1', '2', '3' or '&lt;space&gt;' keys or 
     * by clicking the corresponding buttons on the top of the DrawingFrame window.
     * When execution is suspended, a message composed by <i>format</i> and <i>args</i> is shown in the lower-right status bar. 
     * Their usage is defined in {@link String#format(String, Object...)} documentation.
     * @param format the format of the message string labeling the breaking point.
     * @param args the arguments for the format of the message string.
     * @return true if execution was suspended, false otherwise.
     * @see DbgControls
     * @see #breakStep(String, Object...)
     * @see #breakJump(String, Object...)
     */
    public boolean breakLeap(String format, Object... args);

     /**
     * Suspends the execution according to {@link #breakJump(String, Object...)} rules, with a default empty message string.
     * @return true if execution was suspended, false otherwise.
     * @see DbgControls
     * @see #breakStep()
     * @see #breakLeap()
     */
    public boolean breakJump();

    /**
     * Suspends the execution if the program is running in either of the <i><b>step</b></i>, <i><b>leap</b></i> or <i><b>jump</b></i> modes. 
     * In <i><b>run</b></i> mode, this method does nothing. The execution can be resumed by pressing any of the '1', '2', '3' or '&lt;space&gt;' keys or 
     * by clicking the corresponding buttons on the top of the DrawingFrame window.
     * When execution is suspended, a message composed by <i>format</i> and <i>args</i> is shown in the lower-right status bar. 
     * Their usage is defined in {@link String#format(String, Object...)} documentation.
     * @param format the format of the message string labeling the breaking point.
     * @param args the arguments for the format of the message string.
     * @return true if execution was suspended, false otherwise.
     * @see DbgControls
     * @see #breakStep(String, Object...)
     * @see #breakLeap(String, Object...)
     */
    public boolean breakJump(String format, Object... args);
}
