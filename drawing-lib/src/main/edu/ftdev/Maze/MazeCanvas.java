package edu.ftdev.Maze;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFactory;
import edu.ftdev.DrawingFrame;

/**
 * MazeCanvas provides the basic building blocks for generating and displaying mazes.
 * A maze is a grid of cells, each cell is a square area of pixels on the canvas. Cell sides can be walled-in or open.
 * A cell can also have a shade (background) color, and a center color. A cell can have a path drawn over it,
 * connecting its Center to any of its Top, Bottom, Left or Right sides. Each path component can be drawn in any color.
 * <p>
 * <img src="https://florinteo.github.io/EduCode/DrawingLib/res/Maze/maze_canvas.png" alt="maze_canvas.png">
 * </p>
 * MazeCanvas provides primitives for drawing and erasing individual cells, walls, paths, and shades. It also provides.
 * As an extension of {@link DrawingFactory}, MazeCanvas also provides simple ways to interact with the maze window
 * and to control the program execution.
 * <p>
 * The following code snippet demonstrates how to use MazeCanvas to draw a simple maze of 4 rows and 10 columns of cells.
 * The removes the wall between cells at coordinates (1, 1) and (1, 2), and draws a path connecting their centers.
 * Lastly, the cell at coordinates (2, 2) is shaded in a light green color:
 * <pre style="margin-left: 20px;">
 * MazeCanvas mc = new MazeCanvas(4, 10, 32);
 * // open the maze canvas
 * mc.open();
 * for (int r = 0; r &lt; mc.getRows(); r++) {
 *     for (int c = 0; c &lt; mc.getCols(); c++) {
 *         mc.drawCell(r, c);
 *     }
 * }
 * // customize cell at coordinates (1, 1)
 * mc.eraseWall(1, 1, Side.Right);
 * mc.drawPath(1, 1, Side.Center, Color.RED.darker());
 * mc.drawPath(1, 1, Side.Right, Color.RED);
 * // customize cell at coordinates (1, 2)
 * mc.eraseWall(1, 2, Side.Left);
 * mc.drawPath(1, 2, Side.Left, Color.RED);
 * mc.drawPath(1, 2, Side.Center, Color.RED.darker());
 * // customize cell at coordinates (2, 1)
 * mc.drawShade(2, 2, Color.GREEN.brighter());
 * // suspend execution until action then terminate
 * mc.breakStep();
 * mc.close();
 * </pre>
 * The result of executing the above code is shown in the image below:
 * <p>
 * <img src="https://florinteo.github.io/EduCode/DrawingLib/res/Maze/maze_canvas-demo.png" alt="maze_canvas.png">
 * </p>
  * @see Side
 * @see DrawingFactory
 */
public class MazeCanvas extends DrawingFactory {
    // #region: [Internal] class constants
    private static final int _PADDING = 10;
    private static final Color _BKG_COLOR = Color.LIGHT_GRAY;

    static final int DEFAULT_MAZE_ROWS = 16;
    static final int DEFAULT_MAZE_COLUMNS = 24;
    static final int DEFAULT_CELL_SIZE = 20;
    //#endregion: [Internal] class constants

    // #region: [Private] fields
    // Padding around the canvas edge
    // Number of rows and columns
    int _nRows;
    int _nCols;
    // Pixel size of one cell of the maze
    int _cellWidth;

    // Width of the pen when drawing walls and tracing path
    private int _pen;
    // Width of the gap between the pen and the path overlay
    private int _pathWidth;
    // Walls colors: Light (Right and Bottom) and Shaded (Left and Top)
    private Color _colLightWall = new Color(_BKG_COLOR.getRed()+20, _BKG_COLOR.getGreen()+20,_BKG_COLOR.getBlue()+20);
    private Color _colShadeWall = new Color(_BKG_COLOR.getRed()-60, _BKG_COLOR.getGreen()-60,_BKG_COLOR.getBlue()-60);
    // Properties of the debug frame (dotted red line around a cell)
    private int _dbgFrameOffset = 5;
    private Color _dbgFrameColor = new Color(255, 134, 13);
    private Stroke _dbgFrameStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
    // #endregion: [Private] fields
    
    // #region: MazeCanvas.Side enum definition
    /**
     * The relevant sides around a maze cell. Cell sides are needed when drawing or erasing a <i>wall</i> at the cell boundary
     * or a <i>path</i> segment crossing through the maze cell.
     * @see MazeCanvas
     * @see MazeCanvas#drawWall(int, int, Side)
     * @see MazeCanvas#eraseWall(int, int, Side)
     * @see MazeCanvas#drawPath(int, int, Side, Color)
     * @see MazeCanvas#erasePath(int, int, Side)
     */
    public enum Side {
        /**
         * The <i>left</i> side of a cell.
         * @see Side
         */
        Left, 
        /**
         * The <i>right</i> side of a cell.
         * @see Side
         */
        Right, 
        /**
         * The <i>top</i> side of a cell.
         * @see Side
         */
        Top, 
        /**
         * The <i>bottom</i> side of a cell.
         * @see Side
         */
        Bottom, 
        /**
         * The <i>center</i> area of a cell.
         * @see Side
         */
        Center
    };
    // #endregion: MazeCanvas.Side enum definition

    // #region: CellState class definition
    /**
     * Extracts the internal state of a maze cell, like the sides walled-in, the sides with paths drawn over, etc.
     */
    protected class CellState {
        private int _row, _col;
        private int _xo, _yo;
        private Color _shadeColor;
        private Color _centerColor;
        
        /**
         * The default color for the shade of the cell.
         * @see MazeCanvas#drawCell(int, int, Color)
         */
        public Color ShadeColor = Color.WHITE;
        /**
         * The default color for the center of the cell.
         * @see MazeCanvas#drawCell(int, int, Color)
         */
        public Color CenterColor = Color.WHITE;
        /**
         * The list of sides walled-in for the cell.
         * @see MazeCanvas#drawWall(int, int, Side)
         */
        public Set<Side> WallSides = new HashSet<Side>();
        /**
         * The list of sides with path drawn over for the cell.
         * @see MazeCanvas#drawPath(int, int, Side, Color)
         */
        public Map<Side, Color> PathSides = new HashMap<Side, Color>();
        /**
         * The validity of the cell state. If false, the cell is out of the maze grid.
         */
        public boolean Valid = false;
        
        /**
         * Class constructor, retaining essential state parameters of the maze cell at the given location.
         * @param row the row of the cell in the maze grid.
         * @param col the column of the cell in the maze grid.
         */
        public CellState(int row, int col) {
            Valid = row >= 0 && row < _nRows && col >= 0 && col < _nCols;
            if (Valid) {
                _row = row;
                _col = col;
                _xo = cellX(_row, _col);
                _yo = cellY(_row, _col);
                _shadeColor = _drawing.getPixel(_xo + _pen, _yo + _pen);
                _centerColor = _drawing.getPixel(_xo + _cellWidth / 2, _yo + _cellWidth / 2);
            }
        }
        
        // #region: [Public] methods
        /**
         * Extracts the shade color for a given cell. If successful, field ShadeColor
         * will contain the current shade color.
         * @return true if successful, false otherwise
         */
        public boolean getShade() {
            if (Valid) {
                ShadeColor = _shadeColor;
            }
            return Valid;
        }
        
        /**
         * Extracts the center color for a given cell. If successful, field CenterColor
         * will contain the current center color.
         * @return true if successful, false otherwise
         */
        public boolean getCenter() {
            if (Valid) {
                CenterColor = _centerColor;
            }
            return Valid;
        }
        
        /**
         * Extracts the list of sides walled-in for a given cell. If successful, object WallSides
         * will contain the list of sides walled-in.
         * @return true if successful, false otherwise
         */
        public boolean getWallSides() {
            if (Valid) {
                if (!_drawing.getPixel(_xo + _pen,  _yo).equals(_shadeColor)) {
                    WallSides.add(Side.Top);
                }
                if (!_drawing.getPixel(_xo,  _yo + _pen).equals(_shadeColor)) {
                    WallSides.add(Side.Left);
                }
                if (!_drawing.getPixel(_xo + _cellWidth - _pen, _yo + _pen).equals(_shadeColor)) {
                    WallSides.add(Side.Right);
                } 
                if (!_drawing.getPixel(_xo + _pen, _yo + _cellWidth - _pen).equals(_shadeColor)) {
                    WallSides.add(Side.Bottom);
                }
            }

            return Valid;
        }
        
        /**
         * Extracts the list of sides with path drawn over for a given cell. If successful, object PathSides
         * will contain the list of sides with the path drawn over.
         * @return true if successful, false otherwise
         */
        public boolean getPathSides() {
            if (Valid) {
                int off = (_cellWidth - _pathWidth) / 2;
                Color leftColor = _drawing.getPixel(_xo + off - 1, _yo + off);
                if (!leftColor.equals(_shadeColor)) {
                    PathSides.put(Side.Left, leftColor);
                }
                Color rightColor = _drawing.getPixel(_xo + off + _pathWidth, _yo + off);
                if (!rightColor.equals(_shadeColor)) {
                    PathSides.put(Side.Right, rightColor);
                }
                Color topColor = _drawing.getPixel(_xo + off, _yo + off - 1);
                if (!topColor.equals(_shadeColor)) {
                    PathSides.put(Side.Top, topColor);
                }
                Color bottomColor = _drawing.getPixel(_xo + off, _yo + off + _pathWidth);
                if (!bottomColor.equals(_shadeColor)) {
                    PathSides.put(Side.Bottom, bottomColor);
                }
                Color centerColor = _drawing.getPixel(_xo + _cellWidth / 2, _yo + _cellWidth / 2);
                if (!centerColor.equals(_shadeColor)) {
                    PathSides.put(Side.Center, centerColor);
                }
            }

            return Valid;
        }
        
        /**
         * Draws a thin focus frame (dotted orange line) around the cell's boundary.
         * This is useful for debugging purposes, to visually identify the cell being processed.
         */
        public void drawFocusFrame() {
            Graphics2D g = _drawing.getGraphics();
            g.setColor(_dbgFrameColor);
            g.setStroke(_dbgFrameStroke);
            g.drawRect(
                    _xo-_dbgFrameOffset, 
                    _yo-_dbgFrameOffset, 
                    _cellWidth+2*_dbgFrameOffset, 
                    _cellWidth+2*_dbgFrameOffset);
            g.dispose();
        }
        // #endregion: [Public] methods

        // #region: [Private] methods
        private void drawWall(Side side) {
            Graphics2D g = _drawing.getGraphics();
            if (side == Side.Top) {
                g.setColor(_colShadeWall);
                g.fillRect(_xo, _yo, _cellWidth, _pen);
            } else if (side == Side.Left) {
                g.setColor(_colShadeWall);
                g.fillRect(_xo, _yo, _pen, _cellWidth);
            } else if (side == Side.Right) {
                g.setColor(_colLightWall);
                g.fillRect(_xo + _cellWidth - _pen, _yo, _pen, _cellWidth);
            } else if (side == Side.Bottom) {
                g.setColor(_colLightWall);
                g.fillRect(_xo,  _yo + _cellWidth - _pen, _cellWidth, _pen);
            }
        }
        
        private void eraseWall(Side side) {
            Graphics2D g = _drawing.getGraphics();
            g.setColor(_shadeColor);
            if (side == Side.Top) {
                g.fillRect(_xo, _yo, _cellWidth, _pen);
            } else if (side == Side.Left) {
                g.fillRect(_xo, _yo, _pen, _cellWidth);
            } else if (side == Side.Right) {
                g.fillRect(_xo + _cellWidth - _pen, _yo, _pen, _cellWidth);
            } else if (side == Side.Bottom) {
                g.fillRect(_xo,  _yo + _cellWidth - _pen, _cellWidth, _pen);
            }
        }
        
        private void redrawWall(Side side) {
            if (WallSides.contains(side)) {
                drawWall(side);
            }
        }

        private void drawPath(Side side, Color color) {
            Graphics2D g = _drawing.getGraphics();
            int off = (_cellWidth - _pathWidth) / 2;
            g.setColor(color);
            if (side == Side.Left) {
                g.fillRect(_xo, _yo + off, off, _pathWidth);
            } else if (side == Side.Right) {
                g.fillRect(_xo + off + _pathWidth, _yo + off, off, _pathWidth);
            } else if (side == Side.Top) { 
                g.fillRect(_xo + off, _yo, _pathWidth, off);
            } else if (side == Side.Bottom) {
                g.fillRect(_xo + off, _yo + off + _pathWidth, _pathWidth, off);
            } else if (side == Side.Center) {
                g.fillRect(_xo + off, _yo + off, _pathWidth, _pathWidth);
            }
        }
        
        private void erasePath(Side side) {
            Graphics2D g = _drawing.getGraphics();
            int off = (_cellWidth - _pathWidth) / 2;
            g.setColor(_shadeColor);
            if (side == Side.Left) {
                g.fillRect(_xo, _yo + off, off, _pathWidth);
            } else if (side == Side.Right) {
                g.fillRect(_xo + off + _pathWidth, _yo + off, off, _pathWidth);
            } else if (side == Side.Top) { 
                g.fillRect(_xo + off, _yo, _pathWidth, off);
            } else if (side == Side.Bottom) {
                g.fillRect(_xo + off, _yo + off + _pathWidth, _pathWidth, off);
            } else if (side == Side.Center) {
                g.fillRect(_xo + off, _yo + off, _pathWidth, _pathWidth);      
            }
        }

        private void redrawPath(Side side) {
            Color redrawColor = PathSides.get(side);
            if (redrawColor != null) {
                drawPath(side, redrawColor);
            }
        }

        private void drawShade(Color color) {
            Graphics2D g = _drawing.getGraphics();
            if (_centerColor.equals(_shadeColor)) {
                _centerColor = color;
            }
            _shadeColor = color;
            g.setColor(color);
            g.fillRect(_xo, _yo, _cellWidth, _cellWidth);
        }
        // #endregion: [Private] methods
    }
    // #endregion: CellState class definition
    
    // #region: [Private] methods
    /**
     * Calculates the pixel X-coordinate of the top-left corner
     * of the cell at the given row and column in the maze.
     * @param r - cell row
     * @param c - cell column
     * @returns pixel X-coordinate of the top-left corner of the cell.
     */
    private int cellX(int r, int c) {
        return _PADDING + c * _cellWidth;
    }
    
    /**
     * Calculates the pixel Y-coordinate of the top-left corner
     * of the cell at the given row and column in the maze.
     * @param r - cell row
     * @param c - cell column
     * @returns pixel Y-coordinate of the top-left corner of the cell.
     */
    private int cellY(int r, int c) {
        return _PADDING + r * _cellWidth;
    }
    // #endregion: [Private] methods
    
    /**
     * Constructs a Maze canvas of 16 rows and 24 columns. Each cell
     * in the maze is sized to 20x20 pixels.<br>Use the {@link MazeCanvas#open()} method
     * to open the window and render the maze further.
     * @see MazeCanvas#MazeCanvas(int, int, int)
     * @see MazeCanvas#open()
     */
    public MazeCanvas() {
        this(DEFAULT_MAZE_ROWS, DEFAULT_MAZE_COLUMNS, DEFAULT_CELL_SIZE);
    }
    
    /**
     * Constructs a Maze canvas of a given number of <i>rows</i> and <i>columns</i>. Each cell
     * in the maze is a square pixel area of the given <i>width</i>.<br>Use the {@link MazeCanvas#open()} method
     * to open the window and render the maze further.
     * @param nRows the number of rows in the grid of maze cells.
     * @param nCols the number of columns in the grid of maze cells.
     * @param cellWidth the size in pixels of the rectangular maze cell's side.
     * @see MazeCanvas#MazeCanvas()
     * @see MazeCanvas#open()
     */
    public MazeCanvas(int nRows, int nCols, int cellWidth) {
        _drawing = new Drawing(2 * _PADDING + nCols * cellWidth, 2 * _PADDING + nRows * cellWidth, _BKG_COLOR);
        _drawingFrame = new DrawingFrame(_drawing);
        _nRows = nRows;
        _nCols = nCols;
        _cellWidth = cellWidth;
        _pen = Math.max(1, Math.min(2, _cellWidth/10));
        int gap = (_cellWidth - 2 * _pen) / 4;
        _pathWidth = Math.max(1, _cellWidth - 2 * _pen - 2 * gap);
        clear();
    }

    // #region: [Public] Core functionality methods
    /**
     * Clears the canvas area of this maze.<br>The window containing this canvas is brought back 
     * to its default state as it was when it was first opened. No cells are drawn, no walls or paths are visible.
     * @see MazeCanvas#open()
     */
    @Override
    public void clear() {
        Graphics2D g = _drawing.getGraphics();
        int borderWidth = _pen; //2 * _pen;
        int xo = cellX(0, 0) - borderWidth;
        int yo = cellY(0, 0) - borderWidth;
        int w = this._nCols * this._cellWidth + 2 * borderWidth;
        int h = this._nRows * this._cellWidth + 2 * borderWidth;
        g.setColor(_colLightWall);
        g.fillRect(xo, yo, w, borderWidth);
        g.fillRect(xo, yo, borderWidth, h);
        g.setColor(_colShadeWall);
        g.fillRect(xo + w - borderWidth, yo, borderWidth, h);
        g.fillRect(xo, yo + h - borderWidth, w, borderWidth);
        _drawingFrame.repaint();
    }
    
    /**
     * Provides the number of rows in the grid of cells for this maze.
     * @return number of rows.
     * @see MazeCanvas#MazeCanvas(int, int, int)
     * @see MazeCanvas#getCols()
     */
    public int getRows() {
        return _nRows;
    }
    
    /**
     * Provides the number of columns in the grid of cells for this maze.
     * @return number of columns.
     * @see MazeCanvas#MazeCanvas(int, int, int) 
     * @see MazeCanvas#getRows()
     */
    public int getCols() {
        return _nCols;
    }
    
    /**
     * Draws a cell on the rendering canvas of this maze.<br>
     * By default the cell is surrounded by walls on all sides, it has a white <i>shade</i>,
     * no <i>center</i> and no connecting <i>paths</i>.
     * @param row the row of the cell in the maze grid.
     * @param col the column of the cell in the maze grid.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see MazeCanvas#open()
     */  
    public boolean drawCell(int row, int col) {
    	return drawCell(row, col, Color.WHITE);
    }
    
    /**
     * Draws a cell on the rendering canvas of this maze.<br>
     * The cell is surrounded by <i>walls</i> on all sides, it has the given <i>shade</i> color, 
     * no <i>center</i> and no connecting <i>paths</i>.
     * @param row the row of the cell in the maze grid.
     * @param col the column of the cell in the maze grid.
     * @param color the shade color for this cell.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see MazeCanvas#open()
     * @see MazeCanvas#drawCell(int, int)
     */    
    public boolean drawCell(int row, int col, Color color) {
        CellState cs = new CellState(row, col);
        if (cs.Valid && _drawingFrame != null) {
            cs.drawShade(color);
            cs.drawWall(Side.Top);
            cs.drawWall(Side.Left);
            cs.drawWall(Side.Right);
            cs.drawWall(Side.Bottom);
            _drawingFrame.repaint();
        }
        
        return cs.Valid;
    }
    
    /**
     * Draws a <i>wall</i> on a given <i>side</i> of a cell.
     * The cell is identified by its row and column location in the maze grid.<br>
     * The wall is rendered over the cell's <i>shade</i> color, if any, and under the same-side <i>path</i> segment
     * if one exist.
     * @param row the row of the cell in the maze grid.
     * @param col the column of the cell in the maze grid.
     * @param side the side of the cell where the wall is to be drawn. If side is center, method does nothing.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see Side
     * @see MazeCanvas#drawCell(int, int)
     * @see MazeCanvas#eraseWall(int, int, Side)
     */  
    public boolean drawWall(int row, int col, Side side) {
        CellState cs = new CellState(row, col);
        if (cs.getPathSides() && _drawingFrame != null) {
            cs.drawWall(side);
            cs.redrawPath(side);
            _drawingFrame.repaint();
            return true;
        }
        
        return false;
    }
    
    /**
     * Erases a <i>wall</i> from the given <i>side</i> of a cell.
     * The cell is identified by its row and column location in the maze grid.<br>
     * The wall is erased without affecting the same-side <i>path</i> segment, if one exists, or the <i>shade</i> of the cell, if any.
     * @param row the row of the cell in the maze grid.
     * @param col the column of the cell in the maze grid.
     * @param side the side of the cell from where the wall is to be erased. If side is center, method does nothing.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see Side
     * @see MazeCanvas#drawCell(int, int)
     * @see MazeCanvas#drawWall(int, int, Side)
     */  
    public boolean eraseWall(int row, int col, Side side) {
        CellState cs = new CellState(row, col);
        
        if (cs.getWallSides() && cs.getPathSides() && _drawingFrame != null) {
            if (side == Side.Top) {
                cs.eraseWall(side);
                cs.redrawWall(Side.Left);
                cs.redrawWall(Side.Right);
                cs.redrawPath(Side.Top);
            } else if (side == Side.Left) {
                cs.eraseWall(side);
                cs.redrawWall(Side.Top);
                cs.redrawWall(Side.Bottom);
                cs.redrawPath(Side.Left);
            } else if (side == Side.Right) {
                cs.eraseWall(Side.Right);
                cs.redrawWall(Side.Top);
                cs.redrawWall(Side.Bottom);
                cs.redrawPath(Side.Right);
            }
            if (side == Side.Bottom) {
            	cs.eraseWall(Side.Bottom);
            	cs.redrawWall(Side.Left);
            	cs.redrawWall(Side.Right);
            	cs.redrawPath(Side.Bottom);
            }
            _drawingFrame.repaint();
            return true;
        }
        
        return false;
    }
    
    /**
     * Draws a <i>path</i> segment on a given <i>side</i> of a cell, in the specified color.
     * The cell is identified by its row and column location in the maze grid.<br>
     * The segment is rendered over the cell's <i>wall</i> and <i>shade</i>, if any exist. 
     * @param row the row of the cell in the maze grid.
     * @param col the column of the cell in the maze grid.
     * @param side the side of the cell where the path is to be drawn.
     * @param color the color of the segment.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see Side
     * @see MazeCanvas#drawCell(int, int)
     * @see MazeCanvas#erasePath(int, int, Side)
     */
    public boolean drawPath(int row, int col, Side side, Color color) {
        CellState cs = new CellState(row, col);
        if (cs.Valid && _drawingFrame != null) {
            cs.drawPath(side, color);
            _drawingFrame.repaint();
            return true;
        }
        
        return false;
    }
    
    /**
     * Erases a <i>path</i> segment from a given <i>side</i> of a cell.
     * The cell is identified by its row and column location in the maze grid.<br>
     * Once the segment is erased, the obscured <i>wall</i> and <i>shade</i>, if any, become visible. 
     * @param row the row of the cell in the maze grid.
     * @param col the column of the cell in the maze grid.
     * @param side the side of the cell from where the path is to be erased.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see Side
     * @see MazeCanvas#drawCell(int, int)
     * @see MazeCanvas#drawPath(int, int, Side, Color)
     */
    public boolean erasePath(int row, int col, Side side) {
        CellState cs = new CellState(row, col);
        if (cs.getWallSides() && _drawingFrame != null) {
            cs.erasePath(side);
            cs.redrawWall(side);
            _drawingFrame.repaint();
            return true;
        }
        
        return false;
    }
    
    /**
     * Draws the <i>shade</i> (background) of a cell in a given color.
     * @param row the row of the cell in the maze grid.
     * @param col the column of the cell in the maze grid.
     * @param color the color to use when drawing the shade over the cell.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see MazeCanvas#open()
     * @see MazeCanvas#eraseShade(int, int)
     */
    public boolean drawShade(int row, int col, Color color) {
        CellState cs = new CellState(row, col);
        
        if (cs.getWallSides() && cs.getPathSides() && _drawingFrame != null) {
            cs.drawShade(color);
            cs.redrawWall(Side.Top);
            cs.redrawWall(Side.Left);
            cs.redrawWall(Side.Right);
            cs.redrawWall(Side.Bottom);
            cs.redrawPath(Side.Top);
            cs.redrawPath(Side.Left);
            cs.redrawPath(Side.Right);
            cs.redrawPath(Side.Bottom);
            cs.redrawPath(Side.Center);
            _drawingFrame.repaint();
            return true;
        }
        
        return false;
    }
    
    /**
    * Erases the shade (background) of a cell to the default white color.
    * @param row the row of the cell in the maze grid.
    * @param col the column of the cell in the maze grid.
    * @return true if successful, false in case of an error: window not opened or coordinates out of range.
    * @see MazeCanvas#open()
    * @see MazeCanvas#drawShade(int, int, Color)
    */
    public boolean eraseShade(int row, int col) {
        return drawShade(row, col, Color.WHITE);
    }
    // #endregion: [Public] Core functionality methods
}
