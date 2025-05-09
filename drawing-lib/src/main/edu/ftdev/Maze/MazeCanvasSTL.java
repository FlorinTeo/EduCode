package edu.ftdev.Maze;

import edu.ftdev.STL.STLModel;
import edu.ftdev.STL.STLPrism;
import edu.ftdev.STL.STLPoint;

/**
 * MazeCanvasSTL is a thin extension of the {@link MazeCanvas} class. It is enhancing it with STL (Stereolithography) functionality,
 * allowing for the creation of 3D printable models of the maze.
 */
public class MazeCanvasSTL extends MazeCanvas {

    // #region: [Public] STL model constants
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
    // #endregion [Public] STL model constants

    // #region: [Private] Class fields
    private double _cellSideMM; // size of a side of a cell.
    private double _widthMM; // does not include the base and plate padding.
    private double _lengthMM; // does not include the base and plate padding.
    //private double _heightMM;
    // #endregion: [Private] Class fields

    /**
     * Constructs a default Maze canvas with STL capabilities of 16 rows and 24 columns. Each cell
     * in the maze is sized to 20x20 pixels.
     * @see MazeCanvas#MazeCanvas(int, int, int)
     * @see MazeCanvas#open()
     */
    public MazeCanvasSTL() {
        this(DEFAULT_MAZE_ROWS, DEFAULT_MAZE_COLUMNS, DEFAULT_CELL_SIZE);
    }

    /**
     * Constructor for the MazeCanvasSTL class.
     * @param nRows the number of rows in the maze.
     * @param nCols the number of columns in the maze.
     * @param cellWidth the width of each cell in the maze, in pixels.
     */
    public MazeCanvasSTL(int nRows, int nCols, int cellWidth) {
        super(nRows, nCols, cellWidth);
        _cellSideMM = Math.min(MAX_CELL_SIDE_MM, _cellWidth);
        _widthMM = _nCols * _cellSideMM;
        _lengthMM = _nRows * _cellSideMM;
        if (_widthMM > STLModel.MAX_WIDTH_MM || _lengthMM > STLModel.MAX_LENGTH_MM) {
            double mazeRatio = _widthMM / _lengthMM;
            double modelRatio = STLModel.MAX_WIDTH_MM / STLModel.MAX_LENGTH_MM;
            double shrinkRatio = (mazeRatio > modelRatio) 
                ? STLModel.MAX_WIDTH_MM / _widthMM
                : STLModel.MAX_LENGTH_MM / _lengthMM;
                _widthMM *= shrinkRatio;
                _lengthMM *= shrinkRatio;
        }
        //_heightMM = BASE_HEIGHT_MM + PLATE_HEIGHT_MM + MAZE_HEIGHT_MM;
    }

    // #region: [Public] STL model generation methods
    /**
     * Creates an STL model representing the base and plate of the maze. The base is a rectangular prism that serves
     * as the foundation for the maze, while the plate is a thinner rectangular prism placed on top of the base.
     * The dimensions of the base and plate are adjusted to fit within the maximum allowable size for an STL model.
     * @return an {@link STLModel} object containing the base and plate of the maze.
     */
    public STLModel createSTLBase() {
        STLModel model = new STLModel();

        model.add(
            // base
            new STLPrism(
                new STLPoint(-BASE_PADDING_MM, -BASE_PADDING_MM, 0),
                _widthMM + 2 * BASE_PADDING_MM,
                _lengthMM + 2 * BASE_PADDING_MM,
                BASE_HEIGHT_MM),
            // plate
            new STLPrism(
                new STLPoint(-PLATE_PADDING_MM, -PLATE_PADDING_MM, BASE_HEIGHT_MM),
                _widthMM + 2 * PLATE_PADDING_MM,
                _lengthMM + 2 * PLATE_PADDING_MM,
                PLATE_HEIGHT_MM));
        return model;
    }

    /**
     * Creates an STL model representing the case of the maze.
     * The case consists of a roof and four walls (left, right, top, and bottom) that enclose the maze.
     * The dimensions of the case are adjusted to fit within the maximum allowable size for an STL model.
     * @return an {@link STLModel} object containing the case of the maze, including the base and walls.
     */
    public STLModel createSTLCase() {
        STLModel model = new STLModel();
        double caseWidth = _widthMM + 4 * MAZE_WALL_MM;
        double caseLength = _lengthMM + 4 * MAZE_WALL_MM;
        STLPoint blOrigin = new STLPoint(-2 * MAZE_WALL_MM, - 2 * MAZE_WALL_MM, BASE_HEIGHT_MM);
        STLPoint trOrigin = blOrigin.offset(caseWidth, caseLength, BASE_HEIGHT_MM);

        model.add(
            // roof
            new STLPrism(
                new STLPoint(-BASE_PADDING_MM, -BASE_PADDING_MM, 0),
                _widthMM + 2 * BASE_PADDING_MM,
                _lengthMM + 2 * BASE_PADDING_MM,
                BASE_HEIGHT_MM),
            // bottom wall
            new STLPrism(blOrigin, caseWidth, CASE_WALL_MM, CASE_HEIGHT_MM),
            // right wall
            new STLPrism(trOrigin, -CASE_WALL_MM, -caseLength, CASE_HEIGHT_MM),
            // top wall
            new STLPrism(trOrigin, -caseWidth, -CASE_WALL_MM, CASE_HEIGHT_MM),
            // left wall
            new STLPrism(blOrigin, CASE_WALL_MM, caseLength, CASE_HEIGHT_MM)
        );

        return model;
    }
    
    /**
     * Get the STLPoint origin (the bottom-left corner) of a maze cell at the given row and column.
     * @param row the row index of the cell.
     * @param col the column index of the cell.
     * @return the {@link STLPoint} representing the bottom-left corner of the cell, within the base {@link STLModel} of the maze.
     */
    public STLPoint getSTLCellOrigin(int row, int col) {
        double x = col * _cellSideMM;
        double y = _lengthMM - row * _cellSideMM - _cellSideMM;
        return new STLPoint(x, y, BASE_HEIGHT_MM);
    }

    /**
     * Get the length of a maze cell side, in millimeters.
     * @return the length of a maze cell side, in millimeters.
     */
    public double getSTLCellSide() {
        return _cellSideMM;
    }
    // #endregion: [Public] STL model generation methods
}
