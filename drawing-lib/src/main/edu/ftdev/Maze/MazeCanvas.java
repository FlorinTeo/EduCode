package edu.ftdev.Maze;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.ftdev.Drawing;

public class MazeCanvas extends Drawing {
    private static final int _PADDING = 10;
    private static final Color _BKG_COLOR = Color.LIGHT_GRAY;

    // #region: private fields
    // Padding around the canvas edge
    // Number of rows and columns
    private int _nRows = 80;
    private int _nCols = 50;
    // Pixel size of one cell of the maze
    private int _cellWidth = 20;

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
    // #endregion: private fields
    
    /**
     * Identifies a side of a maze cell. The cell is a square area having the following sides:
     * <li>{@link Side#Left }</li>
     * <li>{@link Side#Right }</li>
     * <li>{@link Side#Top }</li>
     * <li>{@link Side#Bottom }</li>
     * <li>{@link Side#Center }</li>
     * <br>
     * Cell sides are needed when drawing or erasing a <i>wall</i>, or a <i>path</i> segment of a maze cell.
     * <p>
     * @see MazeCanvas#drawWall(int, int, Side)
     * @see MazeCanvas#eraseWall(int, int, Side)
     * @see MazeCanvas#drawPath(int, int, Side, Color)
     * @see MazeCanvas#erasePath(int, int, Side)
     */
    public enum Side {
        /**
         * Identifies the <i>left</i> side of a cell.
         * @see Side
         */
        Left, 
        /**
         * Identifies the <i>right</i> side of a cell.
         * @see Side
         */
        Right, 
        /**
         * Identifies the <i>top</i> side of a cell.
         * @see Side
         */
        Top, 
        /**
         * Identifies the <i>bottom</i> side of a cell.
         * @see Side
         */
        Bottom, 
        /**
         * Identifies the <i>center</i> area of a cell.
         * @see Side
         */
        Center
    };

    /**
     * Extracts the internal state of a maze cell, like the sides walled-in, the sides with paths drawn over, etc.
     */
    protected class CellState {
        private int _row, _col;
        private int _xo, _yo;
        private Color _shadeColor;
        private Color _centerColor;
        
        public Color ShadeColor = Color.WHITE;
        public Color CenterColor = Color.WHITE;
        public Set<Side> WallSides = new HashSet<Side>();
        public Map<Side, Color> PathSides = new HashMap<Side, Color>();
        public boolean Valid = false;
        
        /**
         * Class constructor, retaining essential state parameters of the maze cell at the given location.
         * @param row - row coordinate
         * @param col - column coordinate
         */
        public CellState(int row, int col) {
            Valid = row >= 0 && row < _nRows && col >= 0 && col < _nCols;
            if (Valid) {
                _row = row;
                _col = col;
                _xo = cellX(_row, _col);
                _yo = cellY(_row, _col);
                _shadeColor = getPixel(_xo + _pen, _yo + _pen);
                _centerColor = getPixel(_xo + _cellWidth / 2, _yo + _cellWidth / 2);
            }
        }
        
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
        
        public boolean getCenter() {
            if (Valid) {
                CenterColor = _centerColor;
            }
            return Valid;
        }
        
        /**
         * Extracts the list of sides walled-in for a given cell. If successful, object WallSides
         * will contain the list of sides walled-in.
         * @param row - row coordinate
         * @param col - column coordinate
         * @return true if successful, false otherwise
         */
        public boolean getWallSides() {
            if (Valid) {
                if (!getPixel(_xo + _pen,  _yo).equals(_shadeColor)) {
                    WallSides.add(Side.Top);
                }
                if (!getPixel(_xo,  _yo + _pen).equals(_shadeColor)) {
                    WallSides.add(Side.Left);
                }
                if (!getPixel(_xo + _cellWidth - _pen, _yo + _pen).equals(_shadeColor)) {
                    WallSides.add(Side.Right);
                } 
                if (!getPixel(_xo + _pen, _yo + _cellWidth - _pen).equals(_shadeColor)) {
                    WallSides.add(Side.Bottom);
                }
            }

            return Valid;
        }
        
        /**
         * Extracts the list of sides with path drawn over for a given cell. If successful, object PathSides
         * will contain the list of sides with the path drawn over.
         * @param row - row coordinate
         * @param col - column coordinate
         * @return true if successful, false otherwise
         */
        public boolean getPathSides() {
            if (Valid) {
                int off = (_cellWidth - _pathWidth) / 2;
                Color leftColor = getPixel(_xo + off - 1, _yo + off);
                if (!leftColor.equals(_shadeColor)) {
                    PathSides.put(Side.Left, leftColor);
                }
                Color rightColor = getPixel(_xo + off + _pathWidth, _yo + off);
                if (!rightColor.equals(_shadeColor)) {
                    PathSides.put(Side.Right, rightColor);
                }
                Color topColor = getPixel(_xo + off, _yo + off - 1);
                if (!topColor.equals(_shadeColor)) {
                    PathSides.put(Side.Top, topColor);
                }
                Color bottomColor = getPixel(_xo + off, _yo + off + _pathWidth);
                if (!bottomColor.equals(_shadeColor)) {
                    PathSides.put(Side.Bottom, bottomColor);
                }
                Color centerColor = getPixel(_xo + _cellWidth / 2, _yo + _cellWidth / 2);
                if (!centerColor.equals(_shadeColor)) {
                    PathSides.put(Side.Center, centerColor);
                }
            }

            return Valid;
        }
        
        public void drawDbgFrame() {
            _g2d.setColor(_dbgFrameColor);
            _g2d.setStroke(_dbgFrameStroke);
            _g2d.drawRect(
                    _xo-_dbgFrameOffset, 
                    _yo-_dbgFrameOffset, 
                    _cellWidth+2*_dbgFrameOffset, 
                    _cellWidth+2*_dbgFrameOffset);
            _g2d.dispose();
        }

        private void drawWall(Side side) {
            if (side == Side.Top) {
                _g2d.setColor(_colShadeWall);
                _g2d.fillRect(_xo, _yo, _cellWidth, _pen);
            } else if (side == Side.Left) {
                _g2d.setColor(_colShadeWall);
                _g2d.fillRect(_xo, _yo, _pen, _cellWidth);
            } else if (side == Side.Right) {
                _g2d.setColor(_colLightWall);
                _g2d.fillRect(_xo + _cellWidth - _pen, _yo, _pen, _cellWidth);
            } else if (side == Side.Bottom) {
                _g2d.setColor(_colLightWall);
                _g2d.fillRect(_xo,  _yo + _cellWidth - _pen, _cellWidth, _pen);
            }
        }
        
        private void eraseWall(Side side) {
            _g2d.setColor(_shadeColor);
            if (side == Side.Top) {
                _g2d.fillRect(_xo, _yo, _cellWidth, _pen);
            } else if (side == Side.Left) {
                _g2d.fillRect(_xo, _yo, _pen, _cellWidth);
            } else if (side == Side.Right) {
                _g2d.fillRect(_xo + _cellWidth - _pen, _yo, _pen, _cellWidth);
            } else if (side == Side.Bottom) {
                _g2d.fillRect(_xo,  _yo + _cellWidth - _pen, _cellWidth, _pen);
            }
        }
        
        private void redrawWall(Side side) {
            if (WallSides.contains(side)) {
                drawWall(side);
            }
        }

        private void drawPath(Side side, Color color) {
            int off = (_cellWidth - _pathWidth) / 2;
            _g2d.setColor(color);
            if (side == Side.Left) {
                _g2d.fillRect(_xo, _yo + off, off, _pathWidth);
            } else if (side == Side.Right) {
                _g2d.fillRect(_xo + off + _pathWidth, _yo + off, off, _pathWidth);
            } else if (side == Side.Top) { 
                _g2d.fillRect(_xo + off, _yo, _pathWidth, off);
            } else if (side == Side.Bottom) {
                _g2d.fillRect(_xo + off, _yo + off + _pathWidth, _pathWidth, off);
            } else if (side == Side.Center) {
                _g2d.fillRect(_xo + off, _yo + off, _pathWidth, _pathWidth);
            }
        }
        
        private void erasePath(Side side) {
            int off = (_cellWidth - _pathWidth) / 2;
            _g2d.setColor(_shadeColor);
            if (side == Side.Left) {
                _g2d.fillRect(_xo, _yo + off, off, _pathWidth);
            } else if (side == Side.Right) {
                _g2d.fillRect(_xo + off + _pathWidth, _yo + off, off, _pathWidth);
            } else if (side == Side.Top) { 
                _g2d.fillRect(_xo + off, _yo, _pathWidth, off);
            } else if (side == Side.Bottom) {
                _g2d.fillRect(_xo + off, _yo + off + _pathWidth, _pathWidth, off);
            } else if (side == Side.Center) {
                _g2d.fillRect(_xo + off, _yo + off, _pathWidth, _pathWidth);      
            }
        }

        private void redrawPath(Side side) {
            Color redrawColor = PathSides.get(side);
            if (redrawColor != null) {
                drawPath(side, redrawColor);
            }
        }

        private void drawShade(Color color) {
            if (_centerColor.equals(_shadeColor)) {
                _centerColor = color;
            }
            _shadeColor = color;
            _g2d.setColor(color);
            _g2d.fillRect(_xo, _yo, _cellWidth, _cellWidth);
        }
    }
    
    // #region: private methods
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
    // #endregion: private methods
    
    /**
     * Constructs a Maze canvas of 16 rows and 24 columns. Each cell
     * in the maze is sized to 20x20 pixels.<br>Use the {@link MazeCanvas#open()} method
     * to open the window and render the maze further.
     * @see MazeCanvas#MazeCanvas(int, int, int)
     * @see MazeCanvas#open()
     */
    public MazeCanvas() {
        this(16,24,20);
    }
    
    /**
     * Constructs a Maze canvas of a given number of <i>rows</i> and <i>columns</i>. Each cell
     * in the maze is a square pixel area of the given <i>width</i>.<br>Use the {@link MazeCanvas#open()} method
     * to open the window and render the maze further.
     * @param nRows - number of rows in the grid of maze cells.
     * @param nCols - number of columns in the grid of maze cells.
     * @param cellWidth - pixel size of one maze cell.
     * @see MazeCanvas#MazeCanvas()
     * @see MazeCanvas#open()
     */
    public MazeCanvas(int nRows, int nCols, int cellWidth) {
        super(2 * _PADDING + nCols * cellWidth, 2 * _PADDING + nRows * cellWidth, _BKG_COLOR);
        _nRows = nRows;
        _nCols = nCols;
        _cellWidth = cellWidth;
        _pen = Math.max(1, Math.min(2, _cellWidth/10));
        int gap = (_cellWidth - 2 * _pen) / 4;
        _pathWidth = Math.max(1, _cellWidth - 2 * _pen - 2 * gap);
        clear();
    }
    
    // #region: public methods
    /**
     * Provides the number of rows in the grid of cells for this maze.
     * @return number of rows.
     * @see MazeCanvas#MazeCanvas(int, int, int)
     */
    public int getRows() {
        return this._nRows;
    }
    
    /**
     * Provides the number of columns in the grid of cells for this maze.
     * @return number of columns.
     * @see MazeCanvas#MazeCanvas(int, int, int) 
     */
    public int getCols() {
        return this._nCols;
    }
    
    /**
     * Clears the rendering canvas of this maze.<br>The window containing this canvas is brought back 
     * to its default state as it was when it was first opened. All prior rendering for
     * individual maze cells is lost.
     * @return true if successful, false if the window is not opened.
     * @see MazeCanvas#open()
     */
    public boolean clear() {
        int borderWidth = _pen; //2 * _pen;
        int xo = cellX(0, 0) - borderWidth;
        int yo = cellY(0, 0) - borderWidth;
        int w = this._nCols * this._cellWidth + 2 * borderWidth;
        int h = this._nRows * this._cellWidth + 2 * borderWidth;
        _g2d.setColor(_colLightWall);
        _g2d.fillRect(xo, yo, w, borderWidth);
        _g2d.fillRect(xo, yo, borderWidth, h);
        _g2d.setColor(_colShadeWall);
        _g2d.fillRect(xo + w - borderWidth, yo, borderWidth, h);
        _g2d.fillRect(xo, yo + h - borderWidth, w, borderWidth);
        return true;
    }
    
    /**
     * Draws a cell on the rendering canvas of this maze.<br>
     * By default the cell is surrounded by <i>walls</i> on all sides, it has a white <i>shade</i>,
     * no <i>center</i> and no connecting <i>paths</i>.
     * @param row - row of the cell in the maze grid.
     * @param col - column of the cell in the maze grid.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see MazeCanvas#open()
     * @see Side
     */  
    public boolean drawCell(int row, int col) {
    	return drawCell(row, col, Color.WHITE);
    }
    
    /**
     * Draws a cell on the rendering canvas of this maze.<br>
     * The cell is surrounded by <i>walls</i> on all sides, it has the given <i>shade</i> color, 
     * no <i>center</i> and no connecting <i>paths</i>.
     * @param row - row of the cell in the maze grid.
     * @param col - column of the cell in the maze grid.
     * @param color - the shade color for this cell.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see MazeCanvas#open()
     * @see MazeCanvas#drawCell(int, int)
     * @see Side
     */    
    public boolean drawCell(int row, int col, Color color) {
        CellState cs = new CellState(row, col);
        if (cs.Valid) {
            cs.drawShade(color);
            cs.drawWall(Side.Top);
            cs.drawWall(Side.Left);
            cs.drawWall(Side.Right);
            cs.drawWall(Side.Bottom);
            _drwCanvas.repaint();
        }
        
        return cs.Valid;
    }
    
    /**
     * Draws a <i>wall</i> on a given <i>side</i> of a cell.
     * The cell is identified by its row and column location in the maze grid.<br>
     * The wall is rendered over the cell's <i>shade</i> color, if any, and under the same-side <i>path</i> segment
     * if one exist.
     * @param row - row of the cell in the maze grid.
     * @param col - column of the cell in the maze grid.
     * @param side - side of the cell where the wall is to be drawn.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see MazeCanvas#drawCell(int, int)
     * @see Side
     * @see MazeCanvas#eraseWall(int, int, Side)
     */  
    public boolean drawWall(int row, int col, Side side) {
        CellState cs = new CellState(row, col);
        if (cs.getPathSides()) {
            cs.drawWall(side);
            cs.redrawPath(side);
            _drwCanvas.repaint();
        }
        
        return cs.Valid;
    }
    
    /**
     * Erases a <i>wall</i> from the given <i>side</i> of a cell.
     * The cell is identified by its row and column location in the maze grid.<br>
     * The wall is erased without affecting the same-side <i>path</i> segment, if one exists, or the <i>shade</i> of the cell, if any.
     * @param row - row of the cell in the maze grid.
     * @param col - column of the cell in the maze grid.
     * @param side - side of the cell where the wall is to be drawn.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see MazeCanvas#drawCell(int, int)
     * @see Side
     * @see MazeCanvas#drawWall(int, int, Side)
     */  
    public boolean eraseWall(int row, int col, Side side) {
        CellState cs = new CellState(row, col);
        
        if (cs.getWallSides() && cs.getPathSides()) {
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
            _drwCanvas.repaint();
        }
        
        return cs.Valid;
    }
    
    /**
     * Draws a <i>path</i> segment on a given <i>side</i> of a cell, in the specified color.
     * The cell is identified by its row and column location in the maze grid.<br>
     * The segment is rendered over the cell's <i>wall</i> and <i>shade</i>, if any exist. 
     * @param row - row of the cell in the maze grid.
     * @param col - column of the cell in the maze grid.
     * @param side - side of the cell where the segment is to be drawn.
     * @param color - color of the segment.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see MazeCanvas#drawCell(int, int)
     * @see Side
     * @see MazeCanvas#erasePath(int, int, Side)
     */
    public boolean drawPath(int row, int col, Side side, Color color) {
        CellState cs = new CellState(row, col);
        if (cs.Valid) {
            cs.drawPath(side, color);
            _drwCanvas.repaint();
        }
        
        return cs.Valid;
    }
    
    /**
     * Erases a <i>path</i> segment from a given <i>side</i> of a cell.
     * The cell is identified by its row and column location in the maze grid.<br>
     * Once the segment is erased, the obscured <i>wall</i> and <i>shade</i>, if any, become visible. 
     * @param row - row of the cell in the maze grid.
     * @param col - column of the cell in the maze grid.
     * @param side - side of the cell from which to erase the segment.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see MazeCanvas#drawCell(int, int)
     * @see Side
     * @see MazeCanvas#drawPath(int, int, Side, Color)
     */
    public boolean erasePath(int row, int col, Side side) {
        CellState cs = new CellState(row, col);
        if (cs.getWallSides()) {
            cs.erasePath(side);
            cs.redrawWall(side);
            _drwCanvas.repaint();
        }
        
        return cs.Valid;
    }
    
    /**
     * Draws the <i>shade</i> (background) of a cell in a given color.
     * @param row - row of the cell in the maze grid.
     * @param col - column of the cell in the maze grid.
     * @param color - color of the center.
     * @return true if successful, false in case of an error: window not opened or coordinates out of range.
     * @see MazeCanvas#open()
     * @see MazeCanvas#eraseShade(int, int)
     */
    public boolean drawShade(int row, int col, Color color) {
        CellState cs = new CellState(row, col);
        
        if (cs.getWallSides() && cs.getPathSides()) {
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
            _drwCanvas.repaint();
        }
        
        return cs.Valid;
    }
    
    /**
    * Erases the shade (background) of a cell to the default white color.
    * @param row - row of the cell in the maze grid.
    * @param col - column of the cell in the maze grid.
    * @return true if successful, false in case of an error: window not opened or coordinates out of range.
    * @see MazeCanvas#open()
    * @see MazeCanvas#drawShade(int, int, Color)
    */
    public boolean eraseShade(int row, int col) {
        return drawShade(row, col, Color.WHITE);
    }
    // #endregion: public methods
}
