package edu.ftdev.CafeArt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import edu.ftdev.Drawing;

public class CafeWall extends Drawing {
    private static final int _EDGE = 16;
    private static final Color _BKG_COLOR = new Color(144, 144, 144);
    private static final Color _WALL_COLOR = new Color(128, 128, 128);
    private static final Color _BORDER_DARK_COLOR = new Color(64, 64, 64);
    private static final Color _BORDER_LIGHT_COLOR = new Color(192,192,192);

    private void rawSetup() {
        Graphics2D g2d = _image.createGraphics();
        g2d.setStroke(new BasicStroke(1));
        // draw the left and upper (darker) border line
        g2d.setColor(_BORDER_DARK_COLOR);
        g2d.drawLine(_EDGE, _EDGE, _EDGE, _image.getHeight()-_EDGE);
        g2d.drawLine(_EDGE, _EDGE, _image.getWidth()-_EDGE, _EDGE);
        // draw the right and lower (lighter) border line
        g2d.setColor(_BORDER_LIGHT_COLOR);
        g2d.drawLine(_EDGE, _image.getHeight()-_EDGE, _image.getWidth()-_EDGE, _image.getHeight()-_EDGE);
        g2d.drawLine(_image.getWidth()-_EDGE, _EDGE, _image.getWidth()-_EDGE, _image.getHeight()-_EDGE);
        // fill the wall with the wall color
        g2d.setColor(_WALL_COLOR);
        g2d.fillRect(_EDGE+1,_EDGE+1, _image.getWidth()-2 * _EDGE-1, _image.getHeight()-2 * _EDGE-1);
        g2d.dispose();
    }

    public CafeWall() {
        super(682, 432, _BKG_COLOR);
        rawSetup();
    }
}
