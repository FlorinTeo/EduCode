package edu.ftdev.STL;

/**
 * A class representing a prism in the 3D space, with a rectangular (width x length) base and height,
 * expressed in millimeters. The prism can be serialized in an STL (STereoLithography) text.
 */
public class STLPrism {

    // Private class modeling a single rectangular face of the prism.
    private class _STLFace {

        // Private class modeling a single triangular facet of the face.
        private class _STLFacet {
            private STLPoint _normal;
            private STLPoint[] _vertices;
            
            private _STLFacet(STLPoint... vertices) {
                if (vertices.length != 3) {
                    throw new IllegalArgumentException("Facet must have exactly 3 vertices");
                }
                _vertices = vertices;
                _normal = calculateNormal();
            }
    
            private STLPoint calculateNormal() {
                STLPoint v1 = _vertices[1].offset(_vertices[0].opposite());
                STLPoint v2 = _vertices[2].offset(_vertices[0].opposite());
                return v1.crossProduct(v2).normalize();
            }

            private _STLFacet offset(double width, double length, double height) {
                return new _STLFacet(
                    _vertices[0].offset(width, length, height),
                    _vertices[1].offset(width, length, height),
                    _vertices[2].offset(width, length, height));
            }
    
            private String serialize() {
                String output = "";
                output += String.format("  facet normal %f %f %f\n", _normal.getX(), _normal.getY(), _normal.getZ());
                output += "    outer loop\n";
                for (STLPoint vertex : _vertices) {
                    output += String.format("      vertex %f %f %f\n", vertex.getX(), vertex.getY(), vertex.getZ());
                }
                output += "    endloop\n";
                output += "  endfacet\n";
                return output;
            }

            @Override
            public String toString() {
                String output = "";
                for (STLPoint vertex : _vertices) {
                    output += String.format("     STLFacet: %5.1f %5.1f %5.1f\n", vertex.getX(), vertex.getY(), vertex.getZ());
                }
                return output;
            }
        }
    
        private _STLFacet[] _facets;

        private _STLFace(STLPoint... vertices) {
            if (vertices.length != 4) {
                throw new IllegalArgumentException("Face must have exactly 4 vertices");
            }
            _facets = new _STLFacet[2];
            _facets[0] = new _STLFacet(vertices[0], vertices[1], vertices[2]);
            _facets[1] = new _STLFacet(vertices[0], vertices[2], vertices[3]);
        }

        private _STLFace(_STLFacet... facets) {
            if (facets.length != 2) {
                throw new IllegalArgumentException("Face must have exactly 2 facets");
            }
            _facets = facets;
        }

        private _STLFace offset(double width, double length, double height) {
            return new _STLFace(
                _facets[0].offset(width, length, height),
                _facets[1].offset(width, length, height));
        }

        private String serialize() {
            String output = "";
            for (_STLFacet facet : _facets) {
                output += facet.serialize();
            }
            return output;
        }

        /**
         * Returns a custom string representation of the face.
         * @return the string representation of the face.
         */
        @Override
        public String toString() {
            String output = "  STLFace:\n";
            for (_STLFacet facet : _facets) {
                output += facet.toString();
            }
            return output;
        }
    }

    // Internal fields of the prism.
    private STLPoint _origin;
    private double _length;
    private double _width;
    private double _height;
    private _STLFace[] _faces;

    /**
     * Checks if the prism is within the bounds of the STL model.
     * @param origin the 3D point origin of the prism.
     * @param width the width of the prism in the x direction.
     * @param length the length of the prism in the y direction.
     * @param height the height of the prism in the z direction.
     */
    private static void checkBounds(STLPoint origin, double width, double length, double height) {
        double minX = Math.min(origin.getX(), origin.getX() + width);
        double maxX = Math.max(origin.getX(), origin.getX() + width);
        if (minX < -STLModel.MAX_PADDING_MM || maxX > STLModel.MAX_WIDTH_MM + STLModel.MAX_PADDING_MM) {
            throw new IllegalArgumentException("Prism width is out of adminisble range!");
        }

        double minY = Math.min(origin.getY(), origin.getY() + length);
        double maxY = Math.max(origin.getY(), origin.getY() + length);
        if (minY < -STLModel.MAX_PADDING_MM || maxY > STLModel.MAX_LENGTH_MM + STLModel.MAX_PADDING_MM) {
            throw new IllegalArgumentException("Prism length is out of admisible range!");
        }

        double minZ = Math.min(origin.getZ(), origin.getZ() + height);
        double maxZ = Math.max(origin.getZ(), origin.getZ() + height);
        if (minZ < 0 || maxZ > STLModel.MAX_HEIGHT_MM) {
            throw new IllegalArgumentException("Prism height is out of admisible range!");
        }
    }

    /**
     * Constructs a new prism with the given origin, width, length, and height.
     * @param origin the 3D point origin of the prism.
     * @param width the width of the prism in the x direction.
     * @param length the length of the prism in the y direction.
     * @param height the height of the prism in the z direction.
     */
    public STLPrism(STLPoint origin, double width, double length, double height) {
        checkBounds(origin, width, length, height);
        _origin = origin;
        _width = width;
        _length = length;
        _height = height;
        _faces = new _STLFace[6];
        // xy face
        _faces[0] = new _STLFace(
            _origin,
            _origin.offset(_width, 0, 0),
            _origin.offset(_width, _length, 0),
            _origin.offset(0, _length, 0));
        // yz face
        _faces[1] = new _STLFace(
            _origin,
            _origin.offset(0, _length, 0),
            _origin.offset(0, _length, _height),
            _origin.offset(0, 0, _height));
        // xz face
        _faces[2] = new _STLFace(
            _origin,
            _origin.offset(_width, 0, 0),
            _origin.offset(_width, 0, _height),
            _origin.offset(0, 0, _height));
        // extruded xy face
        _faces[3] = _faces[0].offset(0, 0, _height);
        // extruded yz face
        _faces[4] = _faces[1].offset(_width, 0, 0);
        // extruded xz face
        _faces[5] = _faces[2].offset(0, _length, 0);
    }

    /**
     * Gets the origin of the prism.
     * @return the origin STLPoint of the prism.
     */
    public STLPoint getOrigin() {
        return _origin;
    }

    /**
     * Gets the width of the prism (the distance along the x axis).
     * @return the width of the prism.
     */
    public double getWidth() {
        return _width;
    }

    /**
     * Gets the length of the prism (the distance along the y axis).
     * @return the length of the prism.
     */
    public double getLength() {
        return _length;
    }

    /**
     * Gets the height of the prism (the distance along the z axis).
     * @return the height of the prism.
     */
    public double getHeight() {
        return _height;
    }

    /**
     * Serializes the prism in the STL format.
     * @return a string representing the prism in the STL format.
     */
    public String serialize() {
        String output = "solid prism\n";
        for (_STLFace face : _faces) {
            output += face.serialize();
        }
        output += "endsolid prism\n";
        return output;
    }

    /**
     * Returns a custom string representation of the prism.
     * @return the string representation of the prism.
     */
    @Override
    public String toString() {
        String output = "STLPrism:\n";
        for (_STLFace face : _faces) {
            output += face.toString();
        }
        return output;
    }
}
