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
     * Constructs a new MoonMap object from an image file. The image is loaded either
     * from the disk or from within the resources of this package.
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
     * Registers a key hook for the given key. The key hook is a lambda
     * function that will be called whenever the key is typed.
     * @param key the key to be hooked.
     * @param hook the lambda function to be called when the key is pressed.
     * @param args the arguments to be passed to the hook when the key is pressed.
     */
    public void setKeyHook(int key, KeyHook hook, Object... args) {
        _drawingFrame.setKeyTypedHook(key, hook, args);
    }

     /**
     * Sets a mouse hook to be called when the left mouse button is clicked.
     * @param mouseHook the mouse hook to be called when the button is clicked
     * or null if the event should not be intercepted.
     * @param args additional arguments to be passed to the mouse hook when called.
     */
    public void setMouseHook(MouseHook mouseHook, Object... args) {
        _drawingFrame.setMouseClickedHook(mouseHook, args);
    }

    /**
     * Gets the row in the image targetted by a mouse event.
     * @param mouseEvent the mouse event that was intercepted.
     * @return the corresponding row in the image.
     */
    public int getRow(MouseEvent mouseEvent) {
        return _drawingFrame.getCanvasY(mouseEvent);
    }

    /**
     * Gets the column in the image targetted by a mouse event.
     * @param mouseEvent the mouse event that was intercepted.
     * @return the corresponding column in the image.
     */
    public int getCol(MouseEvent mouseEvent) {
        return _drawingFrame.getCanvasX(mouseEvent);
    }
    // #endregion: [Public] Key and Mouse hooking methods

    // #region [Private] Helper methods
    private boolean isValidPixel(int row, int col) {
        return row >= 0 && col >= 0 && row < _drawing.getHeight() && col < _drawing.getWidth();
    }

    private boolean isValidArea(int row, int col, int width, int height) {
        return width > 0 && height > 0
            && isValidPixel(row, col)
            && isValidPixel(row + height - 1, col + width - 1);
    }
    // #endregion [Private] Helper methods

    // #region [Public] MoonMap APIs
    /**
     * Takes a snapshot of the current map image. A subsequent call to <i>restore</i> 
     * will reload the map image to the most recent snapshot.
     * @see restore()
     */
    public void snapshot() {
        _drawing.snapshot();
    }

    /**
     * Restores the current map image to the most recent default snapshot.
     */
    public void restore() {
        _drawing.restore();
    }
    /**
     * Gets the colors of each pixel in a specific rectangular area of the map. The area is identified
     * by the row and col coordinates of its top-left corner and its width and height, in pixels.
     * This method expects the entire area to be contained within the image bounds.
     * @param row the row of the top-left corner of the area.
     * @param col the column of the top-left corner of the area.
     * @param width the width of the area, in pixels.
     * @param height the height of the area, in pixels.
     * @return a two dimensional array of Color objects, each identifying the color of the
     * corresponding pixel. The number of rows and columns in the array match the height and width
     * of the target area.
     * @throws IllegalArgumentException if any of the parameters are invalid.
     */
    public Color[][] getArea(int row, int col, int width, int height) {
        if (!isValidArea(row, col, width, height)) {
            throw new IllegalArgumentException("Coordinates out of range");
        }
        int[] rgbArray = new int[width * height];
        _drawing.getImage().getRGB(col, row, width, height, rgbArray, 0, width); 
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
     * Sets the colors of each pixel in a specific rectangular area of the map. The colors are
     * fetched from a two dimensional matrix. The pixels are copied in an area of <i>width</i>
     * and <i>height</i> pixels matching the number of rows and columns in the matrix.
     * The top-left corner of the area is located at (<i>row</i>, <i>col</i>) coordinate.
     * This method expects the entire area to be contained within the image bounds. 
     * @param row the row top-left corner of the area.
     * @param col the column of the top-left corner of the area.
     * @param colors the matrix of col
     * @throws IllegalArgumentException if any of the parameters are invalid.
     */
    public void setArea(int row, int col, Color[][] colors) {
        if (colors == null || colors.length == 0 || colors[0].length == 0) {
            throw new IllegalArgumentException("The colors matrix is invalid");
        }
        int height = colors.length;
        int width = colors[0].length;
        if (!isValidArea(row, col, width, height)) {
            throw new IllegalArgumentException("Coordinates out of range");
        }
        int[] rgbArray = new int[width * height];
        int i = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                rgbArray[i++] = colors[r][c].getRGB();
            }
        }
        _drawing.getImage().setRGB(col, row, width, height, rgbArray, 0, width);
    }
    // #endregion [Public] MoonMap APIs
}
