package edu.ftdev.Sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFactory;
import edu.ftdev.DrawingFrame;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Color;

/**
 * SudokuBoard class is providing a framework for interacting with a <a href="https://en.wikipedia.org/wiki/Sudoku" target="_blank"><b>Sudoku</b></a>
 * puzzle. A Sudoku puzzle is a nine-by-nine grid, with the top-left corner at position (0,0). The grid is pre-populated with a few numerical digits
 * (values 1 to 9), each occuring exactly once in:
 * <ul>
 * <li>the row,</li>
 * <li>the column,</li>
 * <li>the 3x3 subgrid it is placed in.</li>
 * </ul>
 * The puzzle is solved when all the remaining open positions are filled in, such that the above rules hold true.
 * <p> A puzzle can be loaded in a SudokuBoard instance which can be used to read or set any digit at any position. In addition, the
 * instance provides methods for determining if a position is set or pinned. The following image shows a sample SudokuBoard: </p>
 * <p> <img src="https://florinteo.github.io/EduCode/DrawingLib/res/Sudoku/SudokuBoard-spec.jpg" alt="SudokuBoard-spec.jpg" width="320">
 * </p>
 * This board is loaded from a text file,
 * <a href="https://github.com/FlorinTeo/EduCode/blob/main/drawing-lib/src/res/Sudoku/Sudoku3.txt" target="_blank"><i>sudoku3.txt</i></a>,
 * provided in the package. The digits in black are pinned (pre-filled), as the starting state of the puzzle.
 * The digits in red were added as demonstrated by the following code snippet:
 * <pre>
 * SudokuBoard board = new SudokuBoard("sudoku3.txt");
 * board.open();
 * board.set(2, 2, 8);
 * board.set(4, 4, 1);
 * board.set(6, 6, 4);
 * board.breakStep();
 * board.isPinned(2, 2);  // returns false
 * board.isSet(7, 5);     // returns true
 * board.isPinnned(7, 5); // returns true
 * board.isSet(8, 8);     // returns false
 * board.get(8, 8);  // returns 0
 * board.get(8, 7);  // returns 3
 * </pre>
 * The SudokuBoard is a subclass of {@link DrawingFactory}, which provides methods for interacting with the window frame, 
 * and for pausing and resuming the execution of the program in any specific code locations.
  */
 public class SudokuBoard extends DrawingFactory {

    // #region [Public] Class constants
    /**
     * Number of rows in the Sudoku puzzle board. The value is 9.
     */
    public int NROWS = 9;
    /**
     * Number of columns in the Sudoku puzzle board. The value is 9.
     */
    public int NCOLS = 9;
    // #endregion [Public] Class constants

    // #region [Private] SudokuCell class
    private class SudokuCell {
        private int _digit;
        private boolean _isPinned;

        public SudokuCell(int digit, boolean isPinned) {
            _digit = digit;
            _isPinned = isPinned;
        }
    }
    // #endregion [Private] SudokuCell class
    
    // #region [Private] Constants and fields
    private final int CANVAS_BORDER = 20;
    private final int CELL_BORDER = 2;
    private final int CELL_SIZE = 50;
    private final int SEP_BORDER = 2;

    private final Color _BOARD_COLOR = Color.GRAY;
    private final Color _EDGE_COLOR_LIGHT = new Color(232, 232, 232);
    private final Color _EDGE_COLOR_DARK = new Color(182, 182, 182);
    private final Color _SEP_COLOR = new Color(0, 0, 0);
    private final Color _TEXT_COLOR = Color.RED;
    private final Font _TEXT_FONT = new Font("Arial", Font.BOLD, 36);

    private SudokuCell[][] _board;
    // #endregion [Private] Constants and fields

    /**
     * Constructs a new SudokuBoard from a text file containing the specification of a puzzle. The format
     * of the specification file is demonstrated in the five puzzles preloaded in this package, which can
     * be viewed <a href="https://github.com/FlorinTeo/EduCode/tree/main/drawing-lib/src/res/Sudoku" target="_blank"><b>here</b></a>.
     * Any other custom puzzle can be loaded as long as the <i>filePath</i> leads to a text file with a valid puzzle format.
     * @param filePath the path to the text file containing the Sudoku board spec.
     * @throws IOException if the Sudoku board spec file cannot be located or loaded.
     */
    public SudokuBoard(String filePath) throws IOException {
        super();
        File sudokuFile = new File(filePath);
        List<String> rawLines = (sudokuFile.exists())
            ? loadFromFile(sudokuFile)
            : loadFromRes(filePath);
        setupPuzzle(rawLines);
        setupBoard();
        _drawing.snapshot();
    }

    // #region [Private] Helper methods
    private List<String> loadFromFile(File file) throws IOException {
        Path filePath = Paths.get(file.getAbsolutePath());
        return Files.readAllLines(filePath);
    }

    private List<String> loadFromRes(String sudokuResPath) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = classLoader.getResourceAsStream("edu/ftdev/res/Sudoku/" + sudokuResPath);
        if (input == null) {
            throw new IOException("Resource not found: " + sudokuResPath);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    private void setupPuzzle(List<String> rawLines) {
        _board = new SudokuCell[9][9];
        int row = 0;
        int col = 0;
        for (String line : rawLines) {
            if (line.trim().isEmpty()) {
                continue;
            }
            col = 0;
            for (char c : line.toCharArray()) {
                if (c != ' ' && c != '.' && (c < '1' || c > '9')) {
                    throw new IllegalArgumentException("Invalid character in Sudoku file: " + c);
                }
                if (c >= '1' && c <= '9') {
                    _board[row][col] = new SudokuCell(c - '0', true);
                } else {
                    _board[row][col] = new SudokuCell(0, false);
                }
                if (c != ' ') {
                    col++;
                }
            }
            if (col != 9) {
                break;
            }
            row++;
        }
        if (row != 9 && col != 9) {
            throw new IllegalArgumentException(
                String.format("Invalid count of values (%d) in Sudoku row %d: ", col, row));
        }
    }

    private void setupBoard() {
        int boardWidth = 2 * CANVAS_BORDER + 9 * CELL_SIZE + 2 * SEP_BORDER;
        int boardHeight = 2 * CANVAS_BORDER + 9 * CELL_SIZE + 2 * SEP_BORDER;

        _drawing = new Drawing(boardWidth, boardHeight, _BOARD_COLOR);
        _drawingFrame = new DrawingFrame(_drawing);
        _drawingFrame.setTitle("Sudoku Board");
        setupBoardFrame();
        setupBoardCells();
    }

    private void setupBoardFrame() {
        Graphics2D g = _drawing.getGraphics();
        int xOrig = CANVAS_BORDER - SEP_BORDER;
        int yOrig = CANVAS_BORDER - SEP_BORDER;
        int frameSize = 9 * CELL_SIZE + 4 * SEP_BORDER;
        // draw the outer 3D frame top and left lines
        g.setColor(_BOARD_COLOR.brighter());
        g.fillRect(
            xOrig - SEP_BORDER, yOrig - SEP_BORDER, 
            frameSize + 2  * SEP_BORDER,
            SEP_BORDER);
        g.fillRect(
            xOrig - SEP_BORDER, yOrig - SEP_BORDER,
            SEP_BORDER, frameSize + 2 * SEP_BORDER);
        // draw the outer 3D frame right and bottom lines
        g.setColor(_BOARD_COLOR.darker());
        g.fillRect(
            xOrig + frameSize, yOrig,
            SEP_BORDER, frameSize + SEP_BORDER);
        g.fillRect(
            xOrig, yOrig + frameSize,
            frameSize + SEP_BORDER, SEP_BORDER);
        g.setColor(_SEP_COLOR);
        // draw the horizontal frame lines
        for (int r = 0; r <= 9; r += 3) {
            g.fillRect(xOrig, yOrig, frameSize, SEP_BORDER);
            yOrig += 3 * CELL_SIZE + SEP_BORDER;
        }
        // draw the vertical frame lines
        yOrig = CANVAS_BORDER - SEP_BORDER;
        for (int c = 0; c <= 9; c += 3) {
            g.fillRect(xOrig, yOrig, SEP_BORDER, frameSize);
            xOrig += 3 * CELL_SIZE + SEP_BORDER;
        }
    }

        
    private int getXOrig(int row, int col) {
        return CANVAS_BORDER + col * CELL_SIZE + (col / 3) * SEP_BORDER;
    }
    
    private int getYOrig(int row, int col) {
        return CANVAS_BORDER + row * CELL_SIZE + (row / 3) * SEP_BORDER;
    }

    private void setupBoardCells() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int xOrigin = getXOrig(row, col);
                int yOrigin = getYOrig(row, col);
                Graphics2D g = _drawing.getGraphics();
                g.setColor(_EDGE_COLOR_DARK);
                // draw vertical left edge of the cell
                g.fillRect(xOrigin, yOrigin, CELL_BORDER, CELL_SIZE );
                // draw horizontal top edge of the cell
                g.fillRect(xOrigin, yOrigin, CELL_SIZE, CELL_BORDER );
                g.setColor(_EDGE_COLOR_LIGHT);
                // draw vertical right edge of the cell
                g.fillRect(
                        xOrigin + CELL_SIZE - CELL_BORDER , yOrigin + CELL_BORDER,
                        CELL_BORDER, CELL_SIZE - CELL_BORDER);
                // draw horizontal bottom edge of the cell
                g.fillRect(
                        xOrigin + CELL_BORDER, yOrigin + CELL_SIZE - CELL_BORDER, 
                        CELL_SIZE - CELL_BORDER, CELL_BORDER);
                // fill the center of the cell
                g.setColor(Color.WHITE);
                g.fillRect(
                    xOrigin + CELL_BORDER, yOrigin + CELL_BORDER,
                    CELL_SIZE - 2 * CELL_BORDER, CELL_SIZE - 2 * CELL_BORDER);
                // finally fill the cell with the digit
                writeCell(row, col);
            }
        }
    }

    private void writeCell(int row, int col) {
        int xOrigin = getXOrig(row, col);
        int yOrigin = getYOrig(row, col);
        Graphics2D g = _drawing.getGraphics();
        
        // set the shade color for this cell
        SudokuCell cell = _board[row][col];

        // fill the shade of the cell
        g.setColor(Color.WHITE);
        g.fillRect(
                xOrigin + CELL_BORDER,  yOrigin + CELL_BORDER, 
                CELL_SIZE - 2 * CELL_BORDER, CELL_SIZE - 2 * CELL_BORDER);

        g.setColor(isPinned(row, col) ? Color.BLACK : _TEXT_COLOR);
        g.setFont(_TEXT_FONT);
        if (cell._digit != 0) {

            // Get the FontMetrics to calculate text dimensions
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(Integer.toString(cell._digit));
            int textHeight = fm.getAscent();

            // Draw the string
            g.drawString(
                Integer.toString(cell._digit),
                xOrigin + CELL_SIZE / 2 - (textWidth / 2),
                yOrigin + CELL_SIZE / 2 + (textHeight / 2) - 2);
        }
    }

    private SudokuCell getCell(int row, int col) {
        if (row < 0 || row > 8 || col < 0 || col > 8) {
            throw new InvalidParameterException(String.format("Invalid board coordinates: (%d, %d)", row, col));
        }
        return _board[row][col];
    }
    // #endregion: [Private] Helper methods 
    
    // #region: [Public] Board methods
    /**
     * Determines if a grid position contains a pinned digit.
     * @param row the row of the grid position to be tested.
     * @param col the column of the grid position to be tested.
     * @return true if the grid position is pinned.
     */
    public boolean isPinned(int row, int col) {
        SudokuCell cell = getCell(row, col);
        return cell._isPinned;
    }

    /**
     * Determines if a grid position is set with a valid one-digit value in the 1-9 range.
     * Note that no check is made for the uniqueness of the digit in the row, column or subgrid.
     * @param row the row of the grid position to be tested.
     * @param col the column of the grid position to be tested.
     * @return true if the grid position is set.
     */
    public boolean isSet(int row, int col) {
        SudokuCell cell = getCell(row, col);
        return (cell._digit != 0);
    }

    /**
     * Gives the digit at a given position in the grid. A digit of 0 means the position is not set.
     * @param row the row of the grid position to be fetched.
     * @param col the column of the grid position to be fetched.
     * @return the digit in the given grid position or 0 if non was set.
     */
    public int get(int row, int col) {
        SudokuCell cell = getCell(row, col);
        return cell._digit;
    }

    /**
     * Sets a digit at a given grid position. Setting a digit of 0 means the position is cleared.
     * @param row the row of the grid position to be set.
     * @param col the column of the grid position to be set.
     * @param digit the digit to be set in the given grid position.
     * @return the previous digit in the given grid position.
     * @throws InvalidParameterException if either the grid position is out of range or the digit is outside the 1-9 range.
     * @throws IllegalStateException if the position to be set already contains a pinned digit (which cannot be changed).
     */
    public int set(int row, int col, int digit) {
        SudokuCell cell = getCell(row, col);
        if (digit < 0 || digit > 9) {
            throw new InvalidParameterException(String.format("Invalid board digit: %d", digit));
        }
        if (cell._isPinned) {
            throw new IllegalStateException(String.format("Can't modify digit of pinned cell: (%d, %d)", row, col));
        }
        int prev = cell._digit;
        cell._digit = digit;
        writeCell(row, col);
        return prev;
    }

    /**
     * Resets the Sudoku board to its initial state: all grid positions are cleared, with the exception
     * of the values originally pinned.
     */
    public void reset() {
        _drawing.restore();
        for(SudokuCell[] boardRow : _board) {
            for (SudokuCell cell : boardRow) {
                if (!cell._isPinned) {
                    cell._digit = 0;
                }
            }
        }
    }
    // #endregion: [Public] Board methods
}
