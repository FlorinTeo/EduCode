package edu.ftdev.CafeArt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFactory;
import edu.ftdev.DrawingFrame;

/**
 * The CafeWall class is a simple  canvas of 650 by 400 pixels to be used for drawing a cafe wall illusion.
 * The class contains methods for drawing bright or dark squares of various sizes, at specific positions
 * on the canvas.The following image shows a sample CafeWall, drawn by the code in the insets:
 * <p>
 * <img src="https://florinteo.github.io/EduCode/DrawingLib/res/CafeWall/CafeWall-spec.png" alt="CafeWall-spec.png">
 * <p>
 * As a subclass of the DrawingFactory class, CafeWall provides basic window frame and debug controls.
 * @see DrawingFactory
 */
public class CafeWall extends DrawingFactory {
    private static final int _EDGE = 16;
    private static final Color _BKG_COLOR = new Color(144, 144, 144);
    private static final Color _WALL_COLOR = new Color(128, 128, 128);
    private static final Color _BORDER_DARK_COLOR = new Color(64, 64, 64);
    private static final Color _BORDER_LIGHT_COLOR = new Color(192,192,192);

    private static int _WIDTH = 682;
    private static int _HEIGHT = 432;

    private void rawSetup() {
        Graphics2D g = _drawing.getGraphics();
        g.setStroke(new BasicStroke(1));
        // draw the left and upper (darker) border line
        g.setColor(_BORDER_DARK_COLOR);
        g.drawLine(_EDGE, _EDGE, _EDGE, _drawing.getHeight()-_EDGE);
        g.drawLine(_EDGE, _EDGE, _drawing.getWidth()-_EDGE, _EDGE);
        // draw the right and lower (lighter) border line
        g.setColor(_BORDER_LIGHT_COLOR);
        g.drawLine(_EDGE, _drawing.getHeight()-_EDGE, _drawing.getWidth()-_EDGE, _drawing.getHeight()-_EDGE);
        g.drawLine(_drawing.getWidth()-_EDGE, _EDGE, _drawing.getWidth()-_EDGE, _drawing.getHeight()-_EDGE);
        // fill the wall with the wall color
        g.setColor(_WALL_COLOR);
        g.fillRect(_EDGE+1,_EDGE+1, _drawing.getWidth()-2 * _EDGE-1, _drawing.getHeight()-2 * _EDGE-1);
    }

    /**
     * Creates a new CafeWall object with a default size of 650 by 400 pixels.
     */
    public CafeWall() {
        _drawing = new Drawing(_WIDTH, _HEIGHT, _BKG_COLOR);
        _drawingFrame = new DrawingFrame(_drawing);
        rawSetup();
    }

    /**
     * Returns the width of the CafeWall.
     * @return the width as a value in the range [0, 649].
     */
    @Override
    public int getWidth() {
        return _WIDTH - 2 * _EDGE;
    }

    /**
     * Returns the height of the CafeWall.
     * @return the height as a value in the range [0, 399].
     */
    @Override
    public int getHeight() {
        return _HEIGHT - 2 * _EDGE;
    }

    /**
     * Draws a bright square at the <b>x</b>,<b>y</b> pixel coordinates with a side of <b>size</b> pixels.
     * If the coordinates are outside the canvas, the square is clipped.
     * @param x x coordinate.
     * @param y y coordinate.
     * @param size size of the square side.
     */
    public void drawBrightSquare(int x, int y, int size) {
        Graphics2D g = _drawing.getGraphics();
        int tlX = x + _EDGE;
        int tlY = y + _EDGE;
        g.setColor(Color.WHITE);
        g.fillRect(tlX, tlY, size, size);
        _drawingFrame.repaint();
    }
    
    /**
     * Draws a dark square at the <b>x</b>,<b>y</b> pixel coordinates with a side of <b>size</b> pixels.
     * If the coordinates are outside the canvas, the square is clipped.
     * @param x x coordinate.
     * @param y y coordinate.
     * @param size size of the square side.
     */
    public void drawDarkSquare(int x, int y, int size ) {
        Graphics2D g = _drawing.getGraphics();
        int tlX = x + _EDGE;
        int tlY = y + _EDGE;
        g.setColor(Color.BLACK);
        g.fillRect(tlX, tlY, size, size);
        g.setColor(Color.BLUE);
        g.drawLine(tlX, tlY, tlX + size-1, tlY + size-1);
        g.drawLine(tlX, tlY + size-1, tlX + size-1, tlY);
        _drawingFrame.repaint();
    }
}
