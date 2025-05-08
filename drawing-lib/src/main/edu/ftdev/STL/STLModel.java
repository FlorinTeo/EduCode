package edu.ftdev.STL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A class representing a 3D model composed of multiple prisms.
 * The model can be serialized in an STL (STereoLithography) text file, which is a 
 * common format for 3D printing. The size of the model is limitted to
 * maximum W:150mm x L:100mm x H:20mm with an outside padding tolerance of max 10mm.
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

    /**
     * Constructs a new empty STLModel.
     */
    public STLModel() {
        _width = 0;
        _length = 0;
        _prisms = new ArrayList<STLPrism>();
    }

    /**
     * Returns the actual width of the model.
     * @return The width of the model in millimeters.
     */
    public double getWidth() {
        return _width;
    }

    /**
     * Returns the actual length of the model.
     * @return The length of the model in millimeters.
     */
    public double getLength() {
        return _length;
    }

    /**
     * Adds one or more prisms to the model given as a vararg parameter.
     * @param prisms The prisms to add to the model.
     */
    public void add(STLPrism... prisms) {
        add(prisms);
    }

    /**
     * Adds one or more prisms to the model from a given collection.
     * @param prisms The prisms to add to the model.
     */
    public void add(Collection<STLPrism> prisms) {
        for (STLPrism prism : prisms) {
            _prisms.add(prism);
            _width = Math.max(_width, prism.getWidth());
            _length = Math.max(_length, prism.getLength());
        }
    }

    /**
     * Serializes the model in an STL (STereoLithography) file format.
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
