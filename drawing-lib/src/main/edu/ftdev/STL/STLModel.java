package edu.ftdev.STL;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a 3D model composed of multiple prisms.
 * The model can be serialized in an STL file, which is a common format for 3D printing.
 */
public class STLModel {
    /**
     * The maximum admisible width of the model, in millimeters. 
     */
    public static final double MAX_WIDTH_MM = 150;
    /**
     * The maximum admisible length of the model, in millimeters.
     */
    public static final double MAX_LENGTH_MM = 100;
    /**
     * The maximum admisible height of the model, in millimeters.
     */
    public static final double MAX_HEIGHT_MM = 20;
    /**
     * The maximum admisible padding of the model, in millimeters.
     */
    public static final double MAX_PADDING_MM = 10;

    // the actual width and length of the model.
    private double _width;
    private double _length;
    // the prisms that compose the model.
    private List<STLPrism> _prisms;

    
    void checkBounds(STLPrism prism) {
        double minX = Math.min(prism._origin.getX(), prism._origin.getX() + prism._width);
        double maxX = Math.max(prism._origin.getX(), prism._origin.getX() + prism._width);
        if (minX < -STLModel.MAX_PADDING_MM || maxX > STLModel.MAX_WIDTH_MM + STLModel.MAX_PADDING_MM) {
            throw new IllegalArgumentException("Prism width is out of adminisble range!");
        }

        double minY = Math.min(prism._origin.getY(), prism._origin.getY() + prism._length);
        double maxY = Math.max(prism._origin.getY(), prism._origin.getY() + prism._length);
        if (minY < -STLModel.MAX_PADDING_MM || maxY > STLModel.MAX_LENGTH_MM + STLModel.MAX_PADDING_MM) {
            throw new IllegalArgumentException("Prism length is out of admisible range!");
        }

        double minZ = Math.min(prism._origin.getZ(), prism._origin.getZ() + prism._height);
        double maxZ = Math.max(prism._origin.getZ(), prism._origin.getZ() + prism._height);
        if (minZ < 0 || maxZ > STLModel.MAX_HEIGHT_MM) {
            throw new IllegalArgumentException("Prism height is out of admisible range!");
        }
    }
    
    /**
     * Constructs a new empty STLModel.
     */
    public STLModel(double width, double length) {
        if (width <= 0 || width > MAX_WIDTH_MM) {
            throw new IllegalArgumentException("Width must be positive and less than " + MAX_WIDTH_MM + " mm.");
        }
        if (length <= 0 || length > MAX_LENGTH_MM) {
            throw new IllegalArgumentException("Length must be positive and less than " + MAX_LENGTH_MM + " mm.");
        }
        _width = width;
        _length = length;
        _prisms = new ArrayList<STLPrism>();
    }

    /**
     * Returns the width of the model.
     * @return The width of the model in millimeters.
     */
    public double getWidth() {
        return _width;
    }

    /**
     * Returns the length of the model.
     * @return The length of the model in millimeters.
     */
    public double getLength() {
        return _length;
    }

    /**
     * Adds one or more prisms to the model.
     * @param prisms The prisms to add to the model.
     */
    public void add(STLPrism... prisms) {
        for (STLPrism prism : prisms) {
            checkBounds(prism);
            _prisms.add(prism);            
        }
    }

    /**
     * Serializes the model in an STL file format.
     * @return A string representing the model in the STL format.
     */
    public String serialize() {
        String output = "";
        for (STLPrism prism : _prisms) {
            output += prism.serialize();
            output += "\n";
        }
        return output;
    }
}
