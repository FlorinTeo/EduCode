package edu.ftdev;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class is an abstract factory grouping together a Drawing image and a DrawingFrame window. These can be jointly
 * be used by subclasses to display and interact with the image for specific needs and behaviors.
 * The class provides functionality common to all cases, such as opening, closing, repainting and clearing the frame
 * or suspending the execution of the program at specific code locations, useful for debugging.
 * Examples of subclasses: {@link edu.ftdev.CafeArt.CafeWall} and {@link edu.ftdev.Equestria.EquestriaMap}.
 * @see Drawing
 * @see DrawingFrame
 */
public abstract class DrawingFactory implements DbgControls, FrameControls {
    /**
     * The Drawing object encapsulating the image to be displayed in the DrawingFrame.
     */
    protected Drawing _drawing = null; // drawing is intended to be used by subclasses doing heavier graphics.
    /**
     * The DrawingFrame object hosting the Drawing object and providing the UI controls for interacting with it.
     */
    protected DrawingFrame _drawingFrame = null;

    // #region: [Public] File loading helpers
    /**
     * Reads the entire content of a file from the disk and returns it as an array of bytes.
     * @param file the path to the file on the disk.
     * @return the content of the file as a byte array.
     * @throws IOException if the file cannot be located or read from the disk.
     */
    public static byte[] bytesFromFile(File file) throws IOException {
        Path filePath = Paths.get(file.getAbsolutePath());
        byte[] rawBytes = Files.readAllBytes(filePath);
        return rawBytes;
    }

    /**
     * Reads the entire content of a file from within the resources of the class's package and returns it as an array of bytes.
     * @param mapImageRes the path to the file within the package (typically just the file and its extension).
     * @return the content of the file as a byte array.
     * @throws IOException if the file cannot be located or read from within the package.
     */
    public static byte[] bytesFromRes(String mapImageRes) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("edu/ftdev/res/Map/" + mapImageRes);
        if (input == null) {
            throw new IOException("Resource not found: " + mapImageRes);
        }
        byte[] rawBytes = readAllBytes(input);
        return rawBytes;
    }

    private static byte[] readAllBytes(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
    // #endregion: [Public] File loading helpers

    /**
     * Creates a new DrawingFactory object, with a null _drawing and _drawingFrame.
     */
    protected DrawingFactory() {
        // Default constructor
    }

    // #region: [Public] FrameControl overrides
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
    // #endregion: [Public] FrameControls overrides

    // #region: [Public] DbgControls overrides
    /**
     * When running in <i>step</i> mode, this method suspends the execution waiting for an explicit action to continue.
     * It does nothing in any other modes.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see #breakStep(String)
     */
    @Override
    public void breakStep() {
        breakStep("");
    }

    /**
     * When running in <i>step</i> mode, this method suspends the execution waiting for an explicit action to continue.
     * It does nothing in any other modes.
     * When execution is paused, <i>breakMessage</i> is shown in the lower-right status bar. 
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param breakMessage the message labeling the breaking point.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakStep()
     */
    @Override
    public void breakStep(String breakMessage) {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.breakStep(breakMessage);
    }

    /**
     * When running in <i>step</i> mode, this method delays the execution for the given number of milliseconds.
     * It does nothing in any other mode.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param delay milliseconds to delay the execution.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see #breakStep(long, String)
     */
    @Override
    public void breakStep(long delay) {
        breakStep(delay,"");
    }

    /**
     * When running in <i>step</i> mode, this method delays the execution for the given number of milliseconds.
     * It does nothing in any other mode.
     * When execution is paused, <i>breakMessage</i> is shown in the lower-right status bar. 
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param delay milliseconds to delay the execution.
     * @param breakMessage the message labeling the breaking point.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakStep(long)
     */
    @Override
    public void breakStep(long delay, String breakMessage) {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.breakStep(delay, breakMessage);
    }

    /**
     * When running in <i>step</i> or <i>leap</i> modes, this method pauses the execution waiting for an explicit action to continue.
     * It does nothing in <i>jump</i> or <i>run</i> modes. 
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakLeap()
     */
    @Override
    public void breakLeap() {
        breakLeap("");
    }

    /**
     * When running in <i>step</i> or <i>leap</i> modes, this method pauses the execution waiting for an explicit action to continue.
     * It does nothing in <i>jump</i> or <i>run</i> modes. 
     * When execution is paused, <i>breakMessage</i> is shown in the lower-right status bar. 
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param breakMessage the message labeling the breaking point.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakLeap()
     */
    @Override
    public void breakLeap(String breakMessage) {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.breakLeap(breakMessage);
    }

     /**
     * When running in <i>step</i>, <i>leap</i> or <i>jump</i> modes, this method pauses the execution waiting for an explicit action to continue.
     * It does nothing in <i>run</i> mode.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakJump()
     */
    @Override
    public void breakJump() {
        breakJump("");
    }
    // #endregion: [Public] DbgControls overrides

    /**
     * When running in <i>step</i>, <i>leap</i> or <i>jump</i> modes, this method pauses the execution waiting for an explicit action to continue.
     * It does nothing in <i>run</i> mode.
     * When execution is paused, <i>breakMessage</i> is shown in the lower-right status bar. 
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param breakMessage the message labeling the breaking point.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakJump()
     */
    @Override
    public void breakJump(String breakMessage) {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.breakJump(breakMessage);
    }
    // #endregion: [Public] DbgControls overrides 
}
