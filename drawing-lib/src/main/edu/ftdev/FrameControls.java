package edu.ftdev;

import java.io.Closeable;

/**
 * Interface used to control operations on the drawing frame. Typically implemented by <i>DrawingFrame</i> or
 * subclasses of it, allowing opening and closing the frame window, refreshing its content, and printing
 * custom one-line messages in the status bar area.
 */
public interface FrameControls extends Closeable {

    /**
     * Opens a window frame on the screen, displaying the associated Drawing
     * and the controls for interacting with it.
     */
    public void open();

    /**
     * Forces a refresh of the frame content such that any changes that may have
     * been operated on the associated Drawing are reflected on the screen.
     */
    public void repaint();

    /**
     * Prints out the given message in the status bar area, the lower right corner of
     * the drawing frame. The message is composed by <i>format</i> and <i>args</i>,
     * their usage is defined in {@link String#format(String, Object...)} documentation.
     * @param format the format of the message string.
     * @param args the arguments for the format of the message string.
     */
    public void setStatusMessage(String format, Object... args);

    /**
     * Closes the drawing window.
     */
    @Override
    public void close();
}
