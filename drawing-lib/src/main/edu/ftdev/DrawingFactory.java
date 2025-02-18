package edu.ftdev;

public class DrawingFactory implements DbgControls, FrameControls {
    // instance drawing and drawing frame used for displaying the nap
    @SuppressWarnings("unused")
    protected Drawing _drawing = null; // drawing is intended to be used by subclasses doing heavier graphics.
    protected DrawingFrame _drawingFrame = null;

    // #region: FrameControl overrides
    /**
     * Opens a window on the screen, displaying the associated Drawing
     * and the controls for interacting with it. 
     */
    @Override
    public void open() {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.open();
    }

    /**
     * Forces a refresh of the window content such that any changes that may have been
     * operated on the associated Drawing are reflected on the screen.
     */
    @Override
    public void repaint() {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.repaint();
    }

    /**
     * Prints out the given message in the status bar area, the lower right corner of
     * the drawing window.
     * @param message - message to be printed in the status bar area.
     */
    @Override
    public void setStatusMessage(String message) {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.setStatusMessage(message);
    }

    /**
     * Closes the window.
     */
    @Override
    public void close() {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.close();
        _drawingFrame = null;
    }
    // #endregion: FrameControls overrides

    // #region: DbgControls overrides
    /**
     * In "step" mode this method pauses the execution. It does nothing in any other modes.
     * @throws InterruptedException
     * @see DbgControls#step()
     */
    @Override
    public void step() throws InterruptedException {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.step();
    }

    /**
     * In "step" mode, this method delays execution for the given number of
     * milliseconds. It does nothing in any other mode. 
     * @param delay - milliseconds to delay execution in "continuous" mode.
     * @throws InterruptedException
     * @see DbgControls#step(long)
     */
    @Override
    public void step(long delay) throws InterruptedException {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.step(delay);
    }

    /**
     * In "step" or "stop" modes, this method pauses the execution until resumed.
     * It does nothing in "leap" or "fast-forward" mode. 
     * @throws InterruptedException
     * @see DbgControls#stop()
     */
    @Override
    public void stop() throws InterruptedException {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.stop();
    }

     /**
     * In "step", "stop" or "leap" modes, this method pauses the execution until resumed.
     * It does nothing in "fast-forward" mode. 
     * @throws InterruptedException
     * @see DbgControls#leap()
     */
    @Override
    public void leap() throws InterruptedException {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.leap();
    }
    // #endregion: DbgControls overrides 
}
