package edu.ftdev.CafeArt;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.ftdev.Drawing;

public class CafeWall extends Drawing {
    private static final int _EDGE = 16;
    private static final Color _BKG_COLOR = new Color(144, 144, 144);
    private static final Color _WALL_COLOR = new Color(128, 128, 128);
    private static final Color _BORDER_DARK_COLOR = new Color(64, 64, 64);
    private static final Color _BORDER_LIGHT_COLOR = new Color(192,192,192);

    private static int _WIDTH = 682;
    private static int _HEIGHT = 432;

    private void rawSetup() {
        _g2d.setStroke(new BasicStroke(1));
        // draw the left and upper (darker) border line
        _g2d.setColor(_BORDER_DARK_COLOR);
        _g2d.drawLine(_EDGE, _EDGE, _EDGE, _image.getHeight()-_EDGE);
        _g2d.drawLine(_EDGE, _EDGE, _image.getWidth()-_EDGE, _EDGE);
        // draw the right and lower (lighter) border line
        _g2d.setColor(_BORDER_LIGHT_COLOR);
        _g2d.drawLine(_EDGE, _image.getHeight()-_EDGE, _image.getWidth()-_EDGE, _image.getHeight()-_EDGE);
        _g2d.drawLine(_image.getWidth()-_EDGE, _EDGE, _image.getWidth()-_EDGE, _image.getHeight()-_EDGE);
        // fill the wall with the wall color
        _g2d.setColor(_WALL_COLOR);
        _g2d.fillRect(_EDGE+1,_EDGE+1, _image.getWidth()-2 * _EDGE-1, _image.getHeight()-2 * _EDGE-1);
    }

    public CafeWall() {
        super(_WIDTH, _HEIGHT, _BKG_COLOR);
        rawSetup();
    }

    /**
     * Draws a bright square at the <b>x</b>,<b>y</b> pixel coordinates with a side of <b>size</b> pixels.
     * @param x - x coordinate.
     * @param y - y coordinate.
     * @param size - size of the square side.
     */
    public void drawBrightSquare(int x, int y, int size) {
        int tlX = x + _EDGE;
        int tlY = y + _EDGE;
        _g2d.setColor(Color.WHITE);
        _g2d.fillRect(tlX, tlY, size, size);
        _drwCanvas.repaint();
    }
    
    /**
     * Draws a dark square at the <b>x</b>,<b>y</b> pixel coordinates with a side of <b>size</b> pixels.
     * @param x - x coordinate.
     * @param y - y coordinate.
     * @param size - size of the square side.
     */
    public void drawDarkSquare(int x, int y, int size ) {
        int tlX = x + _EDGE;
        int tlY = y + _EDGE;
        _g2d.setColor(Color.BLACK);
        _g2d.fillRect(tlX, tlY, size, size);
        _g2d.setColor(Color.BLUE);
        _g2d.drawLine(tlX, tlY, tlX + size-1, tlY + size-1);
        _g2d.drawLine(tlX, tlY + size-1, tlX + size-1, tlY);
        _drwCanvas.repaint();
    }
}
