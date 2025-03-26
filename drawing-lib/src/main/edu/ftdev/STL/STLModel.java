package edu.ftdev.STL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class representing a 3D model composed of multiple prisms.
 * The model can be serialized in an STL file, which is a common format for 3D printing.
 */
public class STLModel {
    private List<STLPrism> _prisms;
   
    /**
     * Constructs a new empty STLModel.
     */
    public STLModel() {
        _prisms = new ArrayList<STLPrism>();
    }

    /**
     * Adds one or more prisms to the model.
     * @param prisms The prisms to add to the model.
     */
    public void add(STLPrism... prisms) {
        _prisms.addAll(Arrays.asList(prisms));
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
