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
 * SudokuBoard class is providing a framework for interacting with a <a href="https://en.wikipedia.org/wiki/Sudoku">Sudoku puzzle</a>.
 * A Sudoku board is a 9x9 grid of numbers, with the top-left corner at position (0,0). Each number is in the range
 * 1-9. The puzzle is pre-populated with a few pinned numbers, each occuring once in the row, column and  
 * the 3x3 subgrid it is placed in. The puzzle is solved when each open position is filled in, such that the above rules stand true.
 * A puzzle can be loaded in a SudokuBoard instance which can be used to read or set any valid value at any valid position. In addition, the
 * instance provides methods for determining if a position is set or pinned.
 * <p> The following image shows a sample SudokuBoard: </p>
 * <p>
 * <img src="https://florinteo.github.io/EduCode/DrawingLib/res/Sudoku/SudokuBoard-spec.jpg" alt="SudokuBoard-spec.jpg" width="320">
 * </p>
 * This board is loaded from a text file, "sudoku3.txt". The numbers in black are pinned, as the starting state of the puzzle.
 * The numbers in red were added as demonstrated in the code below:
 * <pre>{@code
 * SudokuBoard board = new SudokuBoard("sudoku3.txt");
 * board.open();
 * board.setValue(2, 2, 8);
 * board.setValue(4, 4, 1);
 * board.setValue(6, 6, 4);
 * board.breakStep();}</pre>
 * The SudokuBoard is a subclass of DrawingFactory, so it provides methods for interacting with the window frame, 
 * and for controlling, pausing and resuming the program execution.
  */
 public class SudokuBoard extends DrawingFactory {
    // #region [Private] SudokuCell class
    private class SudokuCell {
        private int _value;
        private boolean _isPinned;

        public SudokuCell(int value, boolean isPinned) {
            _value = value;
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
     * TODO: Add javadoc
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
        _drawing.freezeFrame();
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
                // finally fill the cell with the value
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
        if (cell._value != 0) {

            // Get the FontMetrics to calculate text dimensions
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(Integer.toString(cell._value));
            int textHeight = fm.getAscent();

            // Draw the string
            g.drawString(
                Integer.toString(cell._value),
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
     * TODO: Add javadoc
     * @param row the row of the grid position to be tested.
     * @param col the column of the grid position to be tested.
     * @return true if the grid position is pinned.
     */
    public boolean isPinned(int row, int col) {
        SudokuCell cell = getCell(row, col);
        return cell._isPinned;
    }

    /**
     * TODO: Add javadoc
     * @param row the row of the grid position to be tested.
     * @param col the column of the grid position to be tested.
     * @return true if the grid position is set.
     */
    public boolean isSet(int row, int col) {
        SudokuCell cell = getCell(row, col);
        return (cell._value != 0);
    }

    /**
     * TODO: Add javadoc
     * @param row the row of the grid position to be fetched.
     * @param col the column of the grid position to be fetched.
     * @return the value in the given grid position.
     */
    public int getValue(int row, int col) {
        SudokuCell cell = getCell(row, col);
        return cell._value;
    }

    /**
     * TODO: Add javadoc
     * @param row the row of the grid position to be fetched.
     * @param col the column of the grid position to be fetched.
     * @param value the value to be set in the given grid position.
     * @return the previous value in the given grid position.
     */
    public int setValue(int row, int col, int value) {
        SudokuCell cell = getCell(row, col);
        if (value < 0 || value > 9) {
            throw new InvalidParameterException(String.format("Invalid board value: %d", value));
        }
        if (cell._isPinned) {
            throw new IllegalStateException(String.format("Can't modify value of pinned cell: (%d, %d)", row, col));
        }
        int prev = cell._value;
        cell._value = value;
        writeCell(row, col);
        return prev;
    }


    /**
     * Resets the Sudoku board to its initial state: all grid positions are cleared, with the exception
     * of the values originally pinned.
     */
    public void reset() {
        _drawing.reset();
        for(SudokuCell[] boardRow : _board) {
            for (SudokuCell cell : boardRow) {
                if (!cell._isPinned) {
                    cell._value = 0;
                }
            }
        }
    }
    // #endregion: [Public] Board methods
}
