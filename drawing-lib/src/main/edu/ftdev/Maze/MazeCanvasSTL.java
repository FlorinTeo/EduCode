package edu.ftdev.Maze;

import edu.ftdev.STL.STLModel;
import edu.ftdev.STL.STLPrism;
import edu.ftdev.STL.STLPoint;

/**
 * MazeCanvasSTL is a thin extension of the {@link MazeCanvas} class. It is enhancing it with STL (Stereolithography) functionality,
 * allowing for the creation of 3D printable models of the maze.
 */
public class MazeCanvasSTL extends MazeCanvas {

    /**
     * Constructor for the MazeCanvasSTL class.
     * @param nRows the number of rows in the maze
     * @param nCols the number of columns in the maze
     * @param cellWidth the width of each cell in the maze
     */
    public MazeCanvasSTL(int nRows, int nCols, int cellWidth) {
        super(nRows, nCols, cellWidth);
    }

    // #region: [Public] STL model generation methods
    /**
     * The maximum size of a maze cell side, in millimiters.
     */
    public static final double MAX_CELL_SIDE_MM = 6.0;
    /**
     * The padding on the outside of the maze base prism, in millimeters.
     */
    public static final double BASE_PADDING_MM = 2;
    /**
     * The height of the maze base prism , in millimeters.
     */
    public static final double BASE_HEIGHT_MM = 1;
    /**
     * The padding on the outside of the maze plate prism, in millimeters.
     */
    public static final double PLATE_PADDING_MM = 0.5;
    /**
     * The height of the maze plate prism, in millimeters.
     */
    public static final double PLATE_HEIGHT_MM = 0.5;
    /**
     * The height of the maze walls, in millimeters.
     */
    public static final double MAZE_HEIGHT_MM = 4.0;
    /**
     * The thickness of the maze walls, in millimeters.
     */
    public static final double MAZE_WALL_MM = 0.4;
    /**
     * The height of the case box of the maze, in millimeters.
     */
    public static final double CASE_HEIGHT_MM = 10.0;
    /**
     * The thickness of the case box walls, in millimeters.
     */
    public static final double CASE_WALL_MM = 1.0;

    /**
     * Creates an STL model representing the base and plate of the maze.
     * The base is a rectangular prism that serves as the foundation of the maze,
     * while the plate is a thinner rectangular prism placed on top of the base.
     * The dimensions of the base and plate are adjusted to fit within the maximum
     * allowable size for an STL model.
     * @return an {@link STLModel} object containing the base and plate of the maze.
     */
    public STLModel createSTLBase() {
        double cellWidth = Math.min(MAX_CELL_SIDE_MM, _cellWidth);
        double widthMM = _nCols * cellWidth;
        double lengthMM = _nRows * cellWidth;
        if (widthMM > STLModel.MAX_WIDTH_MM || lengthMM > STLModel.MAX_LENGTH_MM) {
            double mazeRatio = widthMM / lengthMM;
            double modelRatio = STLModel.MAX_WIDTH_MM / STLModel.MAX_LENGTH_MM;
            double shrinkRatio = (mazeRatio > modelRatio) 
                ? STLModel.MAX_WIDTH_MM / widthMM
                : STLModel.MAX_LENGTH_MM / lengthMM;
                widthMM *= shrinkRatio;
                lengthMM *= shrinkRatio;
        }

        STLModel model = new STLModel();

        model.add(
            // base
            new STLPrism(
                new STLPoint(-BASE_PADDING_MM, -BASE_PADDING_MM, 0),
                widthMM + 2 * BASE_PADDING_MM,
                lengthMM + 2 * BASE_PADDING_MM,
                BASE_HEIGHT_MM),
            // plate
            new STLPrism(
                new STLPoint(-PLATE_PADDING_MM, -PLATE_PADDING_MM, BASE_HEIGHT_MM),
                widthMM + 2 * PLATE_PADDING_MM,
                lengthMM + 2 * PLATE_PADDING_MM,
                PLATE_HEIGHT_MM));
        return model;
    }

    /**
     * Creates an STL model representing the case of the maze.
     * The case consists of a base and four walls (left, right, top, and bottom) 
     * that enclose the maze. The dimensions of the case are adjusted to fit 
     * within the maximum allowable size for an STL model.
     * @return an {@link STLModel} object containing the case of the maze, including the base and walls.
     */
    public STLModel createSTLCase() {
        double cellWidth = Math.min(MAX_CELL_SIDE_MM, _cellWidth);
        double widthMM = _nCols * cellWidth;
        double lengthMM = _nRows * cellWidth;
        if (widthMM > STLModel.MAX_WIDTH_MM || lengthMM > STLModel.MAX_LENGTH_MM) {
            double mazeRatio = widthMM / lengthMM;
            double modelRatio = STLModel.MAX_WIDTH_MM / STLModel.MAX_LENGTH_MM;
            double shrinkRatio = (mazeRatio > modelRatio) 
                ? STLModel.MAX_WIDTH_MM / widthMM
                : STLModel.MAX_LENGTH_MM / lengthMM;
                widthMM *= shrinkRatio;
                lengthMM *= shrinkRatio;
        }

        widthMM += 2 * BASE_PADDING_MM;
        lengthMM += 2 * BASE_PADDING_MM;
        STLModel model = new STLModel();

        model.add(
            // base
            new STLPrism(
                new STLPoint(-BASE_PADDING_MM, -BASE_PADDING_MM, 0),
                widthMM + 2 * BASE_PADDING_MM,
                lengthMM + 2 * BASE_PADDING_MM,
                BASE_HEIGHT_MM),
            // left wall
            new STLPrism(
                new STLPoint(0, 0, BASE_HEIGHT_MM),
                CASE_WALL_MM,
                lengthMM,
                CASE_HEIGHT_MM),
            // top wall
            new STLPrism(
                new STLPoint(0, 0, BASE_HEIGHT_MM),
                widthMM,
                CASE_WALL_MM,
                CASE_HEIGHT_MM),
            // right wall
            new STLPrism(
                new STLPoint(widthMM - CASE_WALL_MM, 0, BASE_HEIGHT_MM),
                CASE_WALL_MM,
                lengthMM,
                CASE_HEIGHT_MM),
            // top wall
            new STLPrism(
                new STLPoint(0, lengthMM - CASE_WALL_MM, BASE_HEIGHT_MM),
                widthMM,
                CASE_WALL_MM,
                CASE_HEIGHT_MM)
        );

        return model;
    }
    
    /**
     * Get the STLPoint origin (the bottom-left corner) of a maze cell, given the base {@link STLModel} of the maze.
     * @param smBase the base STLModel of the maze.
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @return the {@link STLPoint} representing the bottom-left corner of the cell, within the base {@link STLModel} of the maze.
     */
    public STLPoint getSTLCellOrigin(STLModel smBase, int row, int col) {
        double stlCellSide = getSTLCellSide(smBase);
        double x = col * stlCellSide;
        double y = smBase.getLength() - 2 * BASE_PADDING_MM - row * stlCellSide - stlCellSide;
        return new STLPoint(x, y, BASE_HEIGHT_MM);
    }

    /**
     * Get the length of a maze cell side within the base {@link STLModel} of the maze.
     * @param smBase the base STLModel of the maze.
     * @return the length of a maze cell side within the base {@link STLModel} of the maze.
     */
    public double getSTLCellSide(STLModel smBase) {
        return (smBase.getWidth() - 2 * BASE_PADDING_MM) / _nCols;
    }
    // #endregion: [Public] STL model generation methods
}
