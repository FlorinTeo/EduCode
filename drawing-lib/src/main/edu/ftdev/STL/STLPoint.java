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
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     * @param z The z coordinate of the point.
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
     * Returns a new STLPoint with the given values added to the x, y and z coordinates of this point.
     * @param x The x value to add to the x coordinate of the point.
     * @param y The y value to add to the y coordinate of the point.
     * @param z The z value to add to the z coordinate of the point.
     * @return The new point with the added values.
     */
    public STLPoint add(double x, double y, double z) {
        return new STLPoint(_x + x, _y + y, _z + z);
    }

    /**
     * Returns a new STLPoint with the x, y and z coordinates of the given point added to the x, y and z coordinates of this point.
     * @param other The point to add to this point.
     * @return The new point with the added values.
     */
    public STLPoint add(STLPoint other) {
        return new STLPoint(_x + other.getX(), _y + other.getY(), _z + other.getZ());
    }

    /**
     * Returns a new STLPoint with the given values subtracted from the x, y and z coordinates of this point.
     * @param x The x value to subtract from the x coordinate of the point.
     * @param y The y value to subtract from the y coordinate of the point.
     * @param z The z value to subtract from the z coordinate of the point.
     * @return The new point with the subtracted values.
     */
    public STLPoint subtract(double x, double y, double z) {
        return new STLPoint(_x - x, _y - y, _z - z);
    }

    /**
     * Returns a new STLPoint with the x, y and z coordinates of the given point subtracted from the x, y and z coordinates of this point.
     * @param other The point to subtract from this point.
     * @return The new point with the subtracted values.
     */
    public STLPoint subtract(STLPoint other) {
        return new STLPoint(_x - other.getX(), _y - other.getY(), _z - other.getZ());
    }

    STLPoint crossProduct(STLPoint other) {
        double x = _y * other.getZ() - _z * other.getY();
        double y = _z * other.getX() - _x * other.getZ();
        double z = _x * other.getY() - _y * other.getX();
        return new STLPoint(x, y, z);
    }

    STLPoint normalize() {
        double length = Math.sqrt(_x * _x + _y * _y + _z * _z);
        return new STLPoint(_x / length, _y / length, _z / length);
    }

    @Override
    public String toString() {
        return String.format("STLPoint: %5.1f %5.1f %5.1f", _x, _y, _z);
    }
}
