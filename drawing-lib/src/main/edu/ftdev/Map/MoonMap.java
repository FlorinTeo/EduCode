package edu.ftdev.Map;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFactory;
import edu.ftdev.DrawingFrame;
import edu.ftdev.KeyInterceptor.KeyHook;
import edu.ftdev.MouseInterceptor.MouseHook;

public class MoonMap extends DrawingFactory {
    /**
     * Create a new MoonMap object from an image file. The image is loaded either
     * from the disk or from within the resources of this package (i.e. "moon.jpg").
     * @param mapImagePath path to the image file, directory or resource.
     * @throws IOException if the map image cannot be located at the given path.
     */
    public MoonMap(String mapImagePath) throws IOException {
        File mapImageFile = new File(mapImagePath);
        byte[] rawBytes = (mapImageFile.exists())
            ? bytesFromFile(mapImageFile)
            : bytesFromRes(mapImagePath);
        InputStream imageStream = new ByteArrayInputStream(rawBytes);
        BufferedImage image = ImageIO.read(imageStream);
        _drawing = new Drawing(image);
        _drawingFrame = new DrawingFrame(_drawing);
    }

    // #region: [Public] Key and Mouse hooking methods
    /**
     * Register a key hook for the given key. The key hook is a lambda
     * function that will be called whenever the key is pressed.
     * @param key the key to be hooked.
     * @param hook the lambda function to be called when the key is pressed.
     * @param args the arguments to be passed to the hook when the key is pressed.
     */
    public void setKeyHook(int key, KeyHook hook, Object... args) {
        _drawingFrame.setKeyPressedHook(key, hook, args);
    }

     /**
     * Set a mouse hook to be called when the left mouse button is clicked.
     * @param mouseHook the mouse hook to be called when the button is clicked
     * or null if the event should not be intercepted.
     * @param args additional arguments to be passed to the mouse hook when called.
     */
    public void setMouseHook(MouseHook mouseHook, Object... args) {
        _drawingFrame.setMouseClickedHook(mouseHook, args);
    }

    /**
     * Get the X-coordinate of the point in the image targeted by a mouse event.
     * @param mouseEvent the mouse event that was intercepted.
     * @return the X coordinate of the mouse pointer.
     */
    public int getX(MouseEvent mouseEvent) {
        return _drawingFrame.getCanvasX(mouseEvent);
    }

    /**
     * Get the Y-coordiante of the point in the image targeted by a mouse event.
     * @param mouseEvent the mouse event that was intercepted.
     * @return the Y-coordiante of the mouse pointer.
     */
    public int getY(MouseEvent mouseEvent) {
        return _drawingFrame.getCanvasY(mouseEvent);
    }
    // #endregion: [Public] Key and Mouse hooking methods

    // #region [Private] Helper methods
    private boolean isValidPixel(int x, int y) {
        return x >= 0 && y >= 0 && x < _drawing.getWidth() && y < _drawing.getHeight();
    }

    private boolean isValidArea(int x, int y, int width, int height) {
        return width > 0 && height > 0
            && isValidPixel(x, y)
            && isValidPixel(x + width - 1, y + height - 1);
    }
    // #endregion [Private] Helper methods

    // #region [Public] Surfaced methods from inner Drawing/DrawingFramework
    /**
     * Take a snapshot of the current MoonMap image. Snapshot is saved internally in the MoonMap object and can be restored later 
     * with subsequent calls to the {@link #restore()} method
     * @see #restore()
     */
    public void snapshot() {
        _drawing.snapshot();
    }

    /**
     * Take a named snapshot of the current MoonMap. Snapshot is saved internally in the MoonMap object and can be restored later
     * with subsequent calls to the {@link #restore(String)} method. If a previous snapshot with an identical <i>name</i> exists it will be overwritten.
     * @see #restore(String)
     */
    public void snapshot(String name) {
        _drawing.snapshot(name);
    }

    /**
     * Indicates whether a snapshot with the given <i>name</i> had been previously taken.
     * @param name the name of the snapshot to check.
     * @return true if the snapshot exists, false otherwise.
     */
    public boolean hasSnapshot(String name) {
        return _drawing.hasSnapshot(name);
    }

    /**
     * Restore and repaints the image from the most recently taken snapshot.
     * By default, a snapshot is taken at the creation of the MoonMap image.
     * @see #snapshot()
     */
    public void restore() {
        _drawing.restore();
        _drawingFrame.repaint();
    }

    /**
     * Restores and repaints the image of the most recently taken <i>named</i> snapshot.
     * If no such snapshot can be located, an exception is thrown.  
     * @see #snapshot(String)
     * @throws IllegalArgumentException if the snapshot cannot be located.
     */
    public void restore(String name) {
        _drawing.restore(name);
        _drawingFrame.repaint();
    }
    // #endregion [Public] Surfaced methods from inner Drawing/DrawingFramework

    // #region [Public] MoonMap APIs
    /**
     * Get a matrix of pixels from a specific rectangular area of the map. The area is identified
     * by the <i>x</i> and <i>y</i> coordinates of its top-left corner and its <i>width</i> and <i>height</i>, in pixels.
     * This method expects the entire area to be contained within the image bounds.
     * @param x the X-coordinate of the top-left corner of the area.
     * @param y the Y-coordinate of the top-left corner of the area.
     * @param width the width of the area, in pixels.
     * @param height the height of the area, in pixels.
     * @return a two dimensional array of Color objects, each identifying the color of the
     * corresponding pixel. The number of rows and columns in the array match the height and width
     * of the target area.
     * @throws IllegalArgumentException if any of the parameters are invalid.
     */
    public Color[][] getArea(int x, int y, int width, int height) {
        if (!isValidArea(x, y, width, height)) {
            throw new IllegalArgumentException("Coordinates out of range");
        }
        int[] rgbArray = new int[width * height];
        _drawing.getImage().getRGB(x, y, width, height, rgbArray, 0, width); 
        Color[][] area = new Color[height][width];
        int i = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                area[r][c] = new Color(rgbArray[i++]);
            }
        }
        return area;
    }

    /**
     * Set a matrix of pixels to a specific rectangular area of the map. The pixels are copied in an
     * area of <i>width</i> and <i>height</i> pixels, matching the number of rows and columns in the matrix.
     * The top-left corner of the area is located at (<i>x</i> and <i>y</i>) coordinate.
     * This method expects the entire area to be contained within the image bounds. 
     * @param x the X-coordinate of the top-left corner of the area.
     * @param y the Y-coordinate of the top-left corner of the area.
     * @param colors the matrix of col
     * @throws IllegalArgumentException if any of the parameters are invalid.
     */
    public void setArea(int x, int y, Color[][] colors) {
        if (colors == null || colors.length == 0 || colors[0].length == 0) {
            throw new IllegalArgumentException("The colors matrix is invalid");
        }
        int height = colors.length;
        int width = colors[0].length;
        if (!isValidArea(x, y, width, height)) {
            throw new IllegalArgumentException("Coordinates out of range");
        }
        int[] rgbArray = new int[width * height];
        int i = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                rgbArray[i++] = colors[r][c].getRGB();
            }
        }
        _drawing.getImage().setRGB(x, y, width, height, rgbArray, 0, width);
    }
    // #endregion [Public] MoonMap APIs
}
