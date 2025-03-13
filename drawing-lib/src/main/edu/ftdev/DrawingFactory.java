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
     * the drawing frame. The message is composed by <i>format</i> and <i>args</i>,
     * their usage is defined in {@link String#format(String, Object...)} documentation.
     * @param format the format of the message string.
     * @param args the arguments for the format of the message string.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     */
    @Override
    public void setStatusMessage(String format, Object... args) {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawingFrame.setStatusMessage(format, args);
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
        // attempt to close the drawing frame. If there are custom mouse/keyboard hooks
        // active on the frame, this operation is a no-op. Subsequent .isOpened() call
        // tells if that's the case.
        _drawingFrame.close();
        // dispense of the _drawingFrame if it was successfully closed.
        if (!_drawingFrame.isOpened()) {
            _drawingFrame = null;
        }
    }
    
    /**
     * Resets the Drawing back to its original state, when it was creating, removing any subsequent overlays that
     * may have been added to it.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see Drawing#restore()
     */
    public void clear() {
        if (_drawing == null || _drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        _drawing.restore();
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
     * Suspends the execution if the program runs in <i><b>step</b></i> mode. It does nothing in any other modes.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @return true if execution was suspended, false otherwise.
     * @see DbgControls#breakStep()
     * @see #breakStep(String, Object...)
     */
    @Override
    public boolean breakStep() {
        return breakStep("");
    }

    /**
     * Suspends the execution if the program runs in <i><b>step</b></i> mode.
     * When execution is suspended, the <i>format</i> and <i>args</i> parameters are used to compose a message which is shown in the lower-right status bar. 
     * The message is formatted according to syntax described in {@link String#format(String, Object...)} documentation.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param format the format of the message string labeling the breaking point.
     * @param args the arguments for the format of the message string.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakStep(String, Object...)
     * @see #breakStep(long, String, Object...)
     */
    @Override
    public boolean breakStep(String format, Object... args) {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        return _drawingFrame.breakStep(format, args);
    }

    /**
     * Suspends the execution if the program runs in <i><b>step</b></i> mode, delays it for a number of milliseconds in <i><b>leap</b></i> mode.
     * It does nothing in any other mode.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param delay - milliseconds to delay execution in "continuous" mode.
     * @return true if execution was suspended, false otherwise.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakStep(long)
     * @see #breakStep(long, String, Object...)
     */
    @Override
    public boolean breakStep(long delay) {
        return breakStep(delay,"");
    }

    /**
     * Suspends the execution if the program runs in <i><b>step</b></i> mode, delays it for a number of milliseconds in <i><b>leap</b></i> mode.
     * It does nothing in any other mode.
     * When execution is suspended, the <i>format</i> and <i>args</i> parameters are used to compose a message which is shown in the lower-right status bar. 
     * The message is formatted according to syntax described in {@link String#format(String, Object...)} documentation.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param delay - milliseconds to delay execution in "continuous" mode.
     * @param format the format of the message string labeling the breaking point.
     * @param args the arguments for the format of the message string.
     * @return true if execution was suspended, false otherwise.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakStep(long, String, Object...)
     * @see #breakLeap()     */
    @Override
    public boolean breakStep(long delay, String format, Object... args) {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        return _drawingFrame.breakStep(delay, format, args);
    }

    /**
     * Suspends the execution if the program runs in <i><b>step</b></i> or <i><b>leap</b></i> modes.
     * It does nothing in <i><b>jump</b></i> or <i><b>run</b></i> modes.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @return true if execution was suspended, false otherwise.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakLeap()
     * @see #breakLeap(String, Object...)
     * 
     */
    @Override
    public boolean breakLeap() {
        return breakLeap("");
    }

    /**
     * Suspends the execution if the program runs in <i><b>step</b></i> or <i><b>leap</b></i> modes.
     * It does nothing in <i><b>jump</b></i> or <i><b>run</b></i> modes.
     * When execution is suspended, the <i>format</i> and <i>args</i> parameters are used to compose a message which is shown in the lower-right status bar. 
     * The message is formatted according to syntax described in {@link String#format(String, Object...)} documentation.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param format the format of the message string labeling the breaking point.
     * @param args the arguments for the format of the message string.
     * @return true if execution was suspended, false otherwise.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakLeap(String, Object...)
     * @see #breakJump()
     */
    @Override
    public boolean breakLeap(String format, Object... args) {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        return _drawingFrame.breakLeap(format, args);
    }

     /**
     * Suspends the execution if the program runs in <i><b>step</b></i>, <i><b>leap</b></i> or <i><b>jump</b></i> modes.
     * It does nothing in <i><b>run</b></i> mode.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @return true if execution was suspended, false otherwise.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakJump()
     * @see #breakJump(String, Object...)
     */
    @Override
    public boolean breakJump() {
        return breakJump("");
    }

    /**
     * Suspends the execution if the program runs in <i><b>step</b></i>, <i><b>leap</b></i> or <i><b>jump</b></i> modes.
     * It does nothing in <i><b>run</b></i> mode.
     * When execution is suspended, the <i>format</i> and <i>args</i> parameters are used to compose a message which is shown in the lower-right status bar. 
     * The message is formatted according to syntax described in {@link String#format(String, Object...)} documentation.
     * To resume, press any of the '1', '2', '3' or '&lt;space&gt;' keys or click on the the corresponding button on the DrawingFrame.
     * @param format the format of the message string labeling the breaking point.
     * @param args the arguments for the format of the message string.
     * @return true if execution was suspended, false otherwise.
     * @throws IllegalStateException if the DrawingFrame has not been initialized.
     * @see DbgControls#breakJump(String, Object...)
     */
    @Override
    public boolean breakJump(String format, Object... args) {
        if (_drawingFrame == null) {
            throw new IllegalStateException("Drawing window not initialized.");
        }
        return _drawingFrame.breakJump(format, args);
    }
    // #endregion: [Public] DbgControls overrides 
}
