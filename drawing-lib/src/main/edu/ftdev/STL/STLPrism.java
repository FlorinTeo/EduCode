package edu.ftdev.STL;

/**
 * A class representing a prism in the 3D space, with a rectangular base and height.
 * The prism can be serialized in an STL file, which is a common format for 3D printing.
 */
public class STLPrism {

    // Internal class modeling a single rectangular face of the prism.
    class _STLFace {

        // Internal class modeling a single triangular facet of the face.
        class _STLFacet {
            private STLPoint _normal;
            private STLPoint[] _vertices;
            
            public _STLFacet(STLPoint... vertices) {
                if (vertices.length != 3) {
                    throw new IllegalArgumentException("Facet must have exactly 3 vertices");
                }
                _vertices = vertices;
                _normal = calculateNormal();
            }
    
            private STLPoint calculateNormal() {
                STLPoint v1 = _vertices[1].subtract(_vertices[0]);
                STLPoint v2 = _vertices[2].subtract(_vertices[0]);
                return v1.crossProduct(v2).normalize();
            }

            public _STLFacet add(double width, double length, double height) {
                return new _STLFacet(
                    _vertices[0].add(width, length, height),
                    _vertices[1].add(width, length, height),
                    _vertices[2].add(width, length, height));
            }
    
            public String serialize() {
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

        public _STLFace(STLPoint... vertices) {
            if (vertices.length != 4) {
                throw new IllegalArgumentException("Face must have exactly 4 vertices");
            }
            _facets = new _STLFacet[2];
            _facets[0] = new _STLFacet(vertices[0], vertices[1], vertices[2]);
            _facets[1] = new _STLFacet(vertices[0], vertices[2], vertices[3]);
        }

        public _STLFace(_STLFacet... facets) {
            if (facets.length != 2) {
                throw new IllegalArgumentException("Face must have exactly 2 facets");
            }
            _facets = facets;
        }

        public _STLFace add(double width, double length, double height) {
            return new _STLFace(
                _facets[0].add(width, length, height),
                _facets[1].add(width, length, height));
        }

        public String serialize() {
            String output = "";
            for (_STLFacet facet : _facets) {
                output += facet.serialize();
            }
            return output;
        }

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
    STLPoint _origin;
    double _length;
    double _width;
    double _height;
    _STLFace[] _faces;

    /**
     * Constructs a new prism with the given origin, width, length, and height.
     * @param origin The 3D point origin of the prism.
     * @param width The width of the prism in the x direction.
     * @param length The length of the prism in the y direction.
     * @param height The height of the prism in the z direction.
     */
    public STLPrism(STLPoint origin, double width, double length, double height) {
        _origin = origin;
        _width = width;
        _length = length;
        _height = height;
        _faces = new _STLFace[6];
        // xy face
        _faces[0] = new _STLFace(
            _origin,
            _origin.add(_width, 0, 0),
            _origin.add(_width, _length, 0),
            _origin.add(0, _length, 0));
        // yz face
        _faces[1] = new _STLFace(
            _origin,
            _origin.add(0, _length, 0),
            _origin.add(0, _length, _height),
            _origin.add(0, 0, _height));
        // xz face
        _faces[2] = new _STLFace(
            _origin,
            _origin.add(_width, 0, 0),
            _origin.add(_width, 0, _height),
            _origin.add(0, 0, _height));
        // extruded xy face
        _faces[3] = _faces[0].add(0, 0, _height);
        // extruded yz face
        _faces[4] = _faces[1].add(_width, 0, 0);
        // extruded xz face
        _faces[5] = _faces[2].add(0, _length, 0);
    }

    /**
     * Serializes the prism in the STL format.
     * @return A string representing the prism in the STL format.
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
