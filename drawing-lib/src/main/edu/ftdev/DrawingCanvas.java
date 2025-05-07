package edu.ftdev;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

/**
 * The {@code DrawingCanvas} class extends the {@link Canvas} class to provide
 * a drawable area for rendering a {@link Drawing} object. It supports zooming,
 * panning, and resizing to fit the screen dimensions.
 */
public class DrawingCanvas extends Canvas {

    private static final long serialVersionUID = 1L;
    private int _xOrig = 0;
    private int _yOrig = 0;
    private int _scale = 1;
    private Drawing _drwImage;
    
    /**
     * Constructs a {@code DrawingCanvas} with the specified anchor coordinates
     * and the {@link Drawing} object to be displayed.
     *
     * @param xAnchor the x-coordinate of the canvas's top-left corner
     * @param yAnchor the y-coordinate of the canvas's top-left corner
     * @param drwImage the {@link Drawing} object to be rendered on the canvas
     */
    DrawingCanvas(int xAnchor, int yAnchor, Drawing drwImage) {
        _drwImage = drwImage;

        // check if resizing is needed such that entire image is shown on the screen.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        double maxWidth = screenSize.getWidth() * 3 / 4;
        double maxHeight = screenSize.getHeight() * 3 / 4;
        double scaleFactor = Math.min((double)maxWidth / _drwImage.getWidth(), (double)maxHeight / _drwImage.getHeight());
        if (scaleFactor < 1) {
            _drwImage.resize(scaleFactor);
            _drwImage.snapshot();
        }
        
        setBounds(
            xAnchor, yAnchor,
            _drwImage.getWidth(),
            _drwImage.getHeight());
    }
    
    // Region: [Internal] User control methods
    private void constrainMovement() {
        _xOrig = Math.min(0, _xOrig);
        _xOrig = Math.max(_xOrig, getWidth() - _drwImage.getWidth() * _scale);
        _yOrig = Math.min(0, _yOrig);
        _yOrig = Math.max(_yOrig,  getHeight() - _drwImage.getHeight() * _scale);
    }
    
    /**
     * Converts a screen x-coordinate to the corresponding canvas x-coordinate.
     * @param x the x-coordinate on the screen.
     * @return the corresponding x-coordinate on the canvas.
     */
    public int xScreenToCanvas(int x) {
        return (x - _xOrig) / _scale;
    }
    
    /**
     * Converts a screen y-coordinate to the corresponding canvas y-coordinate.
     * @param y the y-coordinate on the screen.
     * @return the corresponding y-coordinate on the canvas.
     */
    public int yScreenToCanvas(int y) {
        return (y - _yOrig) / _scale;
    }
    
    /**
     * Zooms in or out of the canvas, centered around the specified anchor point.
     * @param xAnchor the x-coordinate of the zoom anchor point.
     * @param yAnchor the y-coordinate of the zoom anchor point.
     * @param levels the zoom level increment (positive for zoom in, negative for zoom out).
     */
    public void zoom(int xAnchor, int yAnchor, int levels) {
        if (levels != 1 && levels != -1) {
            //System.out.println("hmm");
        }
        
        int newScale = _scale + levels;
        if(newScale > 0 && newScale <= 8) {
            // find out the pixel in the unscaled image corresponding to (xAnchor, yAnchor)
            int xImg = xScreenToCanvas(xAnchor);
            int yImg = yScreenToCanvas(yAnchor);
            // recalculate the _xOrig & _yOrig
            _xOrig = (xAnchor / newScale - xImg) * newScale;
            _yOrig = (yAnchor / newScale - yImg) * newScale;
            _scale = newScale;
            constrainMovement();
            repaint();
        }
    }

    /**
     * Pans the canvas by the specified x and y offsets.
     * @param xOffset the horizontal offset for panning.
     * @param yOffset the vertical offset for panning.
     */
    public void pan(int xOffset, int yOffset) {
        _xOrig += xOffset;
        _yOrig += yOffset;
        constrainMovement();
        repaint();
    }
    // EndRegion: [Internal] User control methods
    
    // Region: [Public] Canvas overrides
    /**
     * Updates the canvas by calling the {@link #paint(Graphics)} method.
     * @param g the {@link Graphics} object used for rendering.
     */
    @Override
    public void update(Graphics g) {
        paint(g);
    }
    
    @Override
    public void paint(Graphics g) {
        g.drawImage(
                _drwImage.getImage(), 
                _xOrig, _yOrig, 
                _scale * _drwImage.getWidth(),
                _scale * _drwImage.getHeight(),
                null);
    }
    // EndRegion: [Public] Canvas overrides
}