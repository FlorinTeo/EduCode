package edu.ftdev.Map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFactory;
import edu.ftdev.DrawingFrame;

public class MoonCanvas extends DrawingFactory {
    /**
     * Constructs a new MoonCanvas object from a moon map file. The moon map can
     * be a .jpeg file or a resource path. 
     * @param mapImagePath path to the image file, directory or resource.
     * @throws IOException if the map image cannot be located at the given path.
     */
    public MoonCanvas(String mapImagePath) throws IOException {
        File mapImageFile = new File(mapImagePath);
        byte[] rawBytes = (mapImageFile.exists())
            ? bytesFromFile(mapImageFile)
            : bytesFromRes(mapImagePath);
        InputStream imageStream = new ByteArrayInputStream(rawBytes);
        BufferedImage image = ImageIO.read(imageStream);
        _drawing = new Drawing(image);
        _drawingFrame = new DrawingFrame(_drawing);
    }

    // #region [Private]
    private boolean isValidPixel(int x, int y) {
        return x >= 0 && y >= 0 && x < _drawing.getWidth() && y < _drawing.getHeight();
    }

    private boolean isValidArea(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {
        return isValidPixel(topLeftX, topLeftY)
            && isValidPixel(bottomRightX, bottomRightY)
            && topLeftX <= bottomRightX
            && topLeftY <= bottomRightY;
    }
    // #endregion [Private]

    // #region [Public]
    /**
     * Returns the color values of each pixel in a specific area of the map. The area is given as
     * the X and Y coordinates of the top-left and bottom-right corners of the rectangle defining
     * the area. This method expects the (topLeftX, topLeftY) and (bottomRightX, bottomRightY) to
     * be valid, within the map image boundaries. It also expects topLeftX to be lesser or equal
     * to bottomRightX and similarly topLeftY to be lesser or equal to bottomRightY.
     * @param topLeftX the X component of the top-left corner of the area.
     * @param topLeftY the Y component of the top-left corner of the area.
     * @param bottomRightX the X component of the bottom-right corner of the area.
     * @param bottomRightY the Y component of the bottom-right corner of the area.
     * @return a two dimensional array of Color objects, each identifying the color of the
     * corresponding pixel. The size of the array matches the number of rows and columns of
     * the area being inspected.
     * @throws IllegalArgumentException if any of the parameters are invalid.
     */
    public Color[][] getArea(int topLeftX, int topLeftY, int bottomRightX, int bottomRightY) {
        if (!isValidArea(topLeftX, topLeftY, bottomRightX, bottomRightY)) {
            throw new IllegalArgumentException("Coordinates out of range");
        }
        int width = bottomRightX - topLeftX + 1;
        int height = bottomRightY - topLeftY + 1;
        int[] rgbArray = new int[width * height];
        _drawing.getImage().getRGB(topLeftX, topLeftY, width, height, rgbArray, 0, width); 
        Color[][] area = new Color[height][width];
        int i = 0;
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                area[r][c] = new Color(rgbArray[i++]);
            }
        }
        return area;
    }
    // #endregion [Public]
}
