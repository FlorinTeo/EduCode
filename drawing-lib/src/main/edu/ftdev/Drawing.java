package edu.ftdev;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Encapsulates a representation of a generic image file. A Drawing object 
 * can be created only by providing a valid image file as argument to its constructor. 
 * In return, the object can be used for accessing and modifying the image at pixel level.
 */
public class Drawing implements AutoCloseable {
    
    /**
     * Internal reference to the drawing canvas hosting this image.
     * This is directly set by the DrawingFrame when the drawing is loaded into the canvas, is intended
     * to be used by subclasses doing heavier graphics, in order trigger a canvas repaint.
     */
    protected DrawingCanvas _drwCanvas = null;
    /**
     * Internal reference to the image file path from which this drawing was created.
     */
    protected BufferedImage _image = null;
    /**
     * Internal reference to the original image file from which this drawing was created.
     * This is used to reset the image to its original state.
     */
    protected BufferedImage _origImage = null;
    /**
     * Internal reference to the Graphics2D object associated with the drawing image.
     */
    protected Graphics2D _g2d = null;
    
    /**
     * Creates an instance of a Drawing object encapsulating the representation of 
     * the imageFile given as argument.
     * @param imageFile - the path to the image file to be loaded.
     * @throws IOException if the image file does not exist or is a directory.
     * @return a Drawing object encapsulating the image file.
     */
    public static Drawing read(String imageFile) throws IOException {
        File drwFile = new File(imageFile);
        if (!drwFile.exists() || drwFile.isDirectory()) {
            throw new IOException();
        }
        return new Drawing(ImageIO.read(drwFile));
    }
    
    /**
     * Creates an instance of a Drawing object encapsulating the representation of 
     * the image given as argument.
     * @param image a Drawing object encapsulating the image file.
     */
    public Drawing(BufferedImage image) {
        _image = image;
        _g2d = _image.createGraphics();
        snapshot();
    }
    
    /**
     * Creates an instance of a Drawing as a rectangular image with the given width and height
     * and filled with the given background color.
     * @param width - width of the drawing image in pixels.
     * @param height - height of the drawing image in pixels.
     * @param bkgColor - the background color of the drawing image.
     */
    public Drawing(int width, int height, Color bkgColor) {
        _image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        _g2d = _image.createGraphics();
        _g2d.setColor(bkgColor);
        _g2d.fillRect(0, 0, width, height);
        snapshot();
    }

    /**
     * Gets the image encapsulated by this Drawing object.
     * @return the image encapsulated by this Drawing object.
     */
    public BufferedImage getImage() {
        return _image;
    }

    /**
     * Resizes the drawing image on both width and height dimensions with a specific scale factor.
     * For instance a factor of 0.5 means the picture is shrunk to half its size on both dimensions.
     * @param scaleFactor the factor by which the image should be scaled.
     */
    public void resize(double scaleFactor) {
        int newWidth = (int)(_image.getWidth() * scaleFactor);
        int newHeight = (int)(_image.getHeight() * scaleFactor);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, _image.getType());
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(_image, 0, 0, newWidth, newHeight, null);
        _g2d.dispose();
        _image = resizedImage;
        _g2d = g;
    }

    /**
     * Resets the drawing image to its original state. If the image was created from a file,
     * this method will reload the image from the file. If the image was created as a blank canvas,
     * this method will clear the canvas to its original blank state.
     * @see snapshot
     */
    public void reset() {
        Graphics2D g = _image.createGraphics();
        g.drawImage(_origImage, 0, 0, null);
        g.dispose();
    }

    /**
     * Takes a snapshot of the current state of the drawing image. Subsequent reset operations
     * will reload the image to this state.
     * @see reset
     */
    public void snapshot() {
        _origImage = new BufferedImage(_image.getWidth(), _image.getHeight(), _image.getType());
        Graphics2D g = _origImage.createGraphics();
        g.drawImage(_image, 0, 0, null);
        g.dispose();
    }

    /**
     * Gets the width of the drawing image.
     * @return the width of the drawing image in pixels.
     */
    public int getWidth() {
        return _image.getWidth();
    }
    
    /**
     * Gets the height of the drawing image.
     * @return the height of the drawing image in pixels.
     */
    public int getHeight() {
        return _image.getHeight();
    }

    /**
     * Gets the Graphics2D object associated with the drawing image.
     * @return the Graphics2D object associated with the drawing image.
     */
    public Graphics2D getGraphics() {
        return _g2d;
    }
    
    /**
     * Indicates whether the pixel coordinates given as arguments fall within the bounds
     * of the image. The top-left valid coordinate of the image is (0, 0), 
     * the bottom-right valid coordinate is (width-1, height-1). 
     * @param x - x coordinate value.
     * @param y - y coordinate value.
     * @return true if both x and y are within their respective ranges, false otherwise.
     */
    public boolean isValidPixel(int x, int y) {
        return (x >= 0 && x <= _image.getWidth()-3 && y >=0 && y <= _image.getHeight()-3);
    }
    
    /**
     * Indicates whether the pixel at the given x and y coordinates is of a bright-toned color.
     * This is defined as a color where each of the three color-components (R, G and B)
     * have a value larger than 220.
     * @param x - x coordinate value.
     * @param y - y coordinate value.
     * @return true if the pixel has a bright-toned color, false otherwise.
     */
    public boolean isBrightPixel(int x, int y) {
        Color c = new Color(_image.getRGB(x, y));
        return c.getRed() > 220 && c.getGreen() > 220 && c.getBlue() > 220;
    }
    
    /**
     * Indicates whether the pixel at the given x and y coordinates is of a dark-toned color.
     * This is defined as a color where each of the three color-components (R, G and B)
     * have a value lesser than 30.
     * @param x - x coordinate value.
     * @param y - y coordinate value.
     * @return true if the pixel has a dark-toned color, false otherwise.
     */
    public boolean isDarkPixel(int x, int y) {
        Color c = new Color(_image.getRGB(x, y));
        return c.getRed() < 30 && c.getGreen() < 30 && c.getBlue() < 30;
    }
    
    /**
     * Gets the color of the pixel at the given x and y coordinates.
     * @param x - x coordinate value.
     * @param y - y coordinate value.
     * @return the Color value at the given coordinates.
     */
    public Color getPixel(int x, int y) {
        return new Color(_image.getRGB(x, y));
    }
    
    /**
     * Sets the pixel of the given x and y coordinates to the given color. 
     * @param x - x coordinate value.
     * @param y - y coordinate value.
     * @param c - the Color value to be set at the given coordinates.
     */
    public void setPixel(int x, int y, Color c) {
        _image.setRGB(x, y, c.getRGB());
    }

    @Override
    public void close() {
        if (_g2d != null) {
            _g2d.dispose();
        }
        if (_image != null) {
            _image.flush();
            _image = null;
        }
    }
}
