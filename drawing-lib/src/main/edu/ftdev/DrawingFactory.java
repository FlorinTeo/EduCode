package edu.ftdev;

public class DrawingFactory implements DbgControls, FrameControls {
    // instance drawing and drawing frame used for displaying the nap
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

    
    /**
     * Clears the drawing to the initial state when it was created, removing any subsequent overlays, if any.
     */
    public void clear() {
        if (_drawing == null || _drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawing.reset();
        _drawingFrame.repaint();
    }

    /**
     * Gets the width of the drawing area.
     * @return the width of the drawing area.
     */
    public int getWidth() {
        if (_drawing == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        return _drawing.getWidth();
    }

    /**
     * Gets the height of the drawing area.
     * @return the heighth of the drawing area.
     */
    public int getHeight() {
        if (_drawing == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        return _drawing.getHeight();
    }
    // #endregion: FrameControls overrides

    // #region: DbgControls overrides
    /**
     * In "step" mode this method pauses the execution. It does nothing in any other modes.
     * @throws InterruptedException
     * @see DbgControls#breakStep()
     */
    @Override
    public void breakStep() throws InterruptedException {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.breakStep();
    }

    /**
     * In "step" mode, this method delays execution for the given number of
     * milliseconds. It does nothing in any other mode. 
     * @param delay - milliseconds to delay execution in "continuous" mode.
     * @throws InterruptedException
     * @see DbgControls#breakStep(long)
     */
    @Override
    public void breakStep(long delay) throws InterruptedException {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.breakStep(delay);
    }

    /**
     * In "step" or "leap" modes, this method pauses the execution until resumed.
     * It does nothing in "jump" or "run" modes. 
     * @throws InterruptedException
     * @see DbgControls#breakStep()
     * @see DbgControls#breakJump()
     */
    @Override
    public void breakLeap() throws InterruptedException {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.breakLeap();
    }

     /**
     * In "step", "leap" or "jump" modes, this method pauses the execution until resumed.
     * It does nothing in "run" mode. 
     * @throws InterruptedException
     * @see DbgControls#breakLeap()
     */
    @Override
    public void breakJump() throws InterruptedException {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.breakJump();
    }
    // #endregion: DbgControls overrides 
}
