package edu.ftdev;

import java.io.Closeable;

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
     * Prints out the given message in the status bar area, the lower right corner
     * of the drawing frame.
     * @param message - message to be printed in the status bar area.
     */
    public void setStatusMessage(String message);

    /**
     * Closes the drawing window.
     */
    @Override
    public void close();
}
