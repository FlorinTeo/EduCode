package edu.ftdev.Equestria;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFactory;
import edu.ftdev.DrawingFrame;

/**
 * EquestriaMap is a class providing a canvas for the map of equestria, with a coordinate system overlay.
 * The map is a 37x27 grid, with the origin in the top-left corner, and the horizontal range [0-37] and the vertical range [0-27].
 * The class provides methods for plotting points, drawing lines and circles on the map.
 * <p>
 * The following image shows a sample EquestriaMap, drawn by the code in the insets:
 * </p>
 * <p>
 * <img src="https://florinteo.github.io/EduCode/DrawingLib/res/Equestria/equestria_map.jpg" alt="equestria_map.jpg" width="400">
 * </p>
 */
public class EquestriaMap extends DrawingFactory {

    /**
     * Creates a canvas displaying the map of Equestria overlaid with its coordinate
     * system. Origin of the map (0, 0) is in the top-left corner, horizontal (X) range is [0-37],
     * vertical (Y) range is [0-27].
     * @see EquestriaMap#open()
     * @see EquestriaMap#plot(int, int)
     * @see EquestriaMap#line(int, int, int, int)
     * @see EquestriaMap#lineTo(int, int)
     * @see EquestriaMap#circle(int, int, int)
     */
    public EquestriaMap() {
        super();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream("edu/ftdev/res/Equestria/equestria_map.jpg");
            BufferedImage img = ImageIO.read(input);
            _drawing = new Drawing(img);
            _drawingFrame = new DrawingFrame(_drawing);
            _drawingFrame.open();
        } catch (IOException e) {
            // can't happen - resource is in the JAR
            e.printStackTrace();
        }
    }

    // #region: Class constants
    private static final int _PINSIZE = 10;
    private static final int[] _XGRID = {
            96, 130, 159, 189, 217,
            246, 276, 304, 334, 363,
            392, 422, 451, 480, 510,
            539, 567, 596, 625, 654,
            683, 712, 742, 771, 801,
            830, 859, 888, 917, 946,
            976, 1004, 1034, 1064, 1093,
            1122, 1151, 1177 };
    private static final int[] _YGRID = {
            102, 122, 147, 173, 199,
            224, 250, 276, 300, 327,
            353, 378, 404, 429, 455,
            480, 506, 531, 557, 583,
            609, 634, 660, 685, 711,
            736, 761 };
    // #endregion: Class constants

    // #region: Members and methods internal to the class
    private BasicStroke _strokePin = new BasicStroke(1);
    private BasicStroke _strokeLine = new BasicStroke(4);
    private int _lastX = 0;
    private int _lastY = 0;

    private int transX(int x) {
        return _XGRID[x];
    }

    private int transY(int y) {
        return _YGRID[y];
    }
    // #endregion: Members and methods internal to the class

    // #region: Public methods
    /**
     * Draws a point/bubble on Equestria map, at the given coordinates
     * @param x x coordinate
     * @param y y coordinate
     * @return true if successful, false otherwise
     * @see EquestriaMap#open()
     * @see EquestriaMap#clear()
     */
    public boolean plot(int x, int y) {
        boolean success = (_drawing != null);
        if (x < 0 || x >= _XGRID.length || y < 0 || y >= _YGRID.length) {
            throw new IllegalArgumentException("Coordinates outside the map!");
        }
        if (success) {
            Graphics2D g = _drawing.getGraphics();
            g.setColor(Color.WHITE);
            g.setStroke(_strokePin);
            g.fillOval(transX(x) - _PINSIZE / 2, transY(y) - _PINSIZE / 2, _PINSIZE, _PINSIZE);
            _lastX = x;
            _lastY = y;
            _drawingFrame.repaint();
        }
        return success;
    }

    /**
     * Draws a line from the last position drawn on the map to the given coordinate.
     * i.e.: if last draw operation was line(10, 10, 20, 20), the line drawn by
     * line(42, 64) has the starting point at (20, 20) and the ending point at (42,
     * 64)
     * @param x x coordinate of the ending point of the line
     * @param y y coordinate of the ending point of the line
     * @return true if successful, false otherwise
     * @see EquestriaMap#line(int, int, int, int)
     */
    public boolean lineTo(int x, int y) {
        boolean success = (_drawing != null);
        if (x < 0 || x >= _XGRID.length || y < 0 || y >= _YGRID.length) {
            throw new IllegalArgumentException("Coordinates outside the map!");
        }
        if (success) {
            Graphics2D g = _drawing.getGraphics();
            g.setColor(Color.WHITE);
            g.setStroke(_strokeLine);
            g.drawLine(transX(_lastX), transY(_lastY), transX(x), transY(y));
            _lastX = x;
            _lastY = y;
            plot(_lastX, _lastY);
            _drawingFrame.repaint();
        }
        return success;
    }

    /**
     * Draws a line in between the (xFrom, yFrom) and (xTo, yTo) coordinates.
     * @param xFrom x coordinate of the starting point
     * @param yFrom y coordinate of the starting point
     * @param xTo   x coordinate of the ending point
     * @param yTo   y coordinate of the ending point
     * @return true if successful, false otherwise
     * @see EquestriaMap#lineTo(int, int)
     */
    public boolean line(int xFrom, int yFrom, int xTo, int yTo) {
        boolean success = (_drawing != null);
        if (success) {
            plot(xFrom, yFrom);
            lineTo(xTo, yTo);
        }
        return success;
    }

    /**
     * Draws a circle with the given center and of the given diameter. If the circle
     * is not fully contained on the map, the method will not fail, but only the portions
     * that intersect the map area will be shown. The center of the circle is also drawn
     * on the map as if it was drawn by plot().
     * @param xCenter x coordinate of the center of the circle.
     * @param yCenter y coordinate of the center of the circle.
     * @param radius the radius of the circle
     * @return true if successful, false otherwise
     * @see EquestriaMap#plot(int, int)
     */
    public boolean circle(int xCenter, int yCenter, int radius) {
        int xtl = xCenter - radius;
        int ytl = yCenter - radius;
        int xbr = xCenter + radius;
        int ybr = yCenter + radius;
        boolean success = (_drawing != null);
        if (xCenter < 0 || xCenter >= _XGRID.length || yCenter < 0 || yCenter >= _YGRID.length) {
            throw new IllegalArgumentException("Coordinates outside the map!");
        }
        plot(xCenter, yCenter);
        xtl = (xtl < 0) ? transX(0) - Math.abs(xtl) * 32 : transX(xtl);
        ytl = (ytl < 0) ? transY(0) - Math.abs(ytl) * 32 : transY(ytl);
        xbr = (xbr >= _XGRID.length) ? transX(_XGRID.length - 1) + (xbr - _XGRID.length + 1) * 32 : transX(xbr);
        ybr = (ybr >= _YGRID.length) ? transY(_YGRID.length - 1) + (ybr - _YGRID.length + 1) * 32 : transY(ybr);

        if (success) {
            Graphics2D g = _drawing.getGraphics();
            g.setColor(Color.WHITE);
            g.setStroke(_strokeLine);
            g.drawOval(xtl, ytl, xbr - xtl, ybr - ytl);
            _lastX = xCenter;
            _lastY = yCenter;
            plot(_lastX, _lastY);
        }
        return success;
    }

    /**
     * Returns the width of the map, in Equestria coordinate system.
     * @return Width of the map (extent of the x axis)
     */
    public int getWidth() {
        return _XGRID.length;
    }

    /**
     * Returns the height of the map, in Equestria coordinate system.
     * @return Height of the map (extent of the y axis)
     */
    public int getHeight() {
        return _YGRID.length;
    }

    /**
     * Returns the minimum value of the x axis of the Equestria map.
     * @return minimum x value in Equestria map coordinate system.
     */
    public int getXMin() {
        return 0;
    }

    /**
     * Returns the minimum value of the y axis of the Equestria map.
     * @return minimum y value in Equestria map coordinate system.
     */
    public int getYMin() {
        return 0;
    }

    /**
     * Returns the maximum value of the x axis of the Equestria map.
     * 
     * @return maximum x value in Equestria map coordinate system.
     */
    public int getXMax() {
        return getWidth() - 1;
    }

    /**
     * Returns the maximum value of the y axis of the Equestria map.
     * 
     * @return maximum y value in Equestria map coordinate system.
     */
    public int getYMax() {
        return getHeight() - 1;
    }
    // #endregion: Public methods
}
