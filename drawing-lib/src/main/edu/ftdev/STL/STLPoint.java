package edu.ftdev.STL;

/**
 * A class representing a three dimensional point.
 */
public class STLPoint {
    private double _x;
    private double _y;
    private double _z;

    /**
     * Constructs a new point with the given x, y, and z coordinates.
     * @param x the x coordinate of the point.
     * @param y the y coordinate of the point.
     * @param z the z coordinate of the point.
     */
    public STLPoint(double x, double y, double z) {
        _x = x;
        _y = y;
        _z = z;
    }

    /**
     * Gets the x coordinate of the point.
     * @return The x coordinate of the point.
     */
    public double getX() {
        return _x;
    }

    /**
     * Gets the y coordinate of the point.
     * @return The y coordinate of the point.
     */
    public double getY() {
        return _y;
    }

    /**
     * Gets the z coordinate of the point.
     * @return The z coordinate of the point.
     */
    public double getZ() {
        return _z;
    }

    /**
     * Creates a new STLPoint by applying an offset to each of the current coordinates. The offset
     * values are taken from the corresponding coordinates of another point.
     * @param other the point to containing the offset values in its coordinates.
     * @return the new point with new offset coordinates.
     */
    public STLPoint offset(STLPoint other) {
        return new STLPoint(_x + other.getX(), _y + other.getY(), _z + other.getZ());
    }

    /**
     * Creates a new STLPoint by applying an offset to each of the current coordinates. The offset
     * values are given as parameteres to the method.
     * @param xOffset the offset to apply to the x coordinate.
     * @param yOffset the offset to apply to the y coordinate.
     * @param zOffset the offset to apply to the z coordinate.
     * @return the new point with new offset coordinates.
     */
    public STLPoint offset(double xOffset, double yOffset, double zOffset) {
        return new STLPoint(_x + xOffset, _y + yOffset, _z + zOffset);
    }

    STLPoint crossProduct(STLPoint other) {
        double x = _y * other.getZ() - _z * other.getY();
        double y = _z * other.getX() - _x * other.getZ();
        double z = _x * other.getY() - _y * other.getX();
        return new STLPoint(x, y, z);
    }

    STLPoint opposite() {
        return new STLPoint(-_x, -_y, -_z);
    }

    STLPoint normalize() {
        double length = Math.sqrt(_x * _x + _y * _y + _z * _z);
        return new STLPoint(_x / length, _y / length, _z / length);
    }

    /**
     * Returns a string representation of the point in the format "STLPoint: x y z".
     * @return A string representation of the point.
     */
    @Override
    public String toString() {
        return String.format("STLPoint: %5.1f %5.1f %5.1f", _x, _y, _z);
    }
}
