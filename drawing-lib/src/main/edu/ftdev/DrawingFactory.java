package edu.ftdev;

/**
 * This class is an abstract factory grouping a Drawing image and a DrawingFrame window which jointly
 * can be used to display and interact with the image in specific implementations.
 * <p>
 * The DrawingFactory is intended to be extended by subclasses that implement specific behaviors. It provides
 * functionality common to all specific needs, such as opening, closing, repainting and clearing the frame or suspending
 * the execution at specific locations in the code for debugging purposes.
 * <p>
 * Examples of subclasses are {@link edu.ftdev.CafeArt.CafeWall} or {@link edu.ftdev.Equestria.EquestriaMap}.
 * @see Drawing
 * @see DrawingFrame
 */
public abstract class DrawingFactory implements DbgControls, FrameControls {
    // instance drawing and drawing frame used for displaying the nap
    protected Drawing _drawing = null; // drawing is intended to be used by subclasses doing heavier graphics.
    protected DrawingFrame _drawingFrame = null;

    // #region: FrameControl overrides
    /**
     * Opens a window on the screen, displaying the associated Drawing
     * and the controls for interacting with it. 
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
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
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
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
     * @param message message to be printed in the status bar area.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
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
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
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
     * Resets the Drawing back to its original state, when it was creating, removing any subsequent overlays that
     * may have been added to it.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see Drawing#reset()
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
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @return the width of the drawing area in pixels.
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
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
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
     * When running in <i>step</i> mode, this method suspends the execution waiting for an explicit action to continue.
     * It does nothing in any other modes.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
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
     * When running in <i>step</i> mode, this method delays the execution for the given number of milliseconds.
     * It does nothing in any other mode.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param delay milliseconds to delay the execution.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
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
     * When running in <i>step</i> or <i>leap</i> modes, this method pauses the execution waiting for an explicit action to continue.
     * It does nothing in <i>jump</i> or <i>run</i> modes. 
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakLeap()
     */
    @Override
    public void breakLeap() throws InterruptedException {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.breakLeap();
    }

     /**
     * When running in <i>step</i>, <i>leap</i> or <i>jump</i> modes, this method pauses the execution waiting for an explicit action to continue.
     * It does nothing in <i>run</i> mode.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakJump()
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
