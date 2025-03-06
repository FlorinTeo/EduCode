package edu.ftdev.Map;

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
}
