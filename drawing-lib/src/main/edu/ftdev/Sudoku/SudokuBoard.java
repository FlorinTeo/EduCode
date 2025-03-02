package edu.ftdev.Sudoku;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import edu.ftdev.Drawing;
import edu.ftdev.DrawingFactory;
import edu.ftdev.DrawingFrame;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Color;

public class SudokuBoard extends DrawingFactory {
    // #region [Private] Constants and fields
    private final int CANVAS_BORDER = 20;
    private final int CELL_BORDER = 2;
    private final int CELL_SIZE = 50;
    private final int SEP_BORDER = 2;

    private final Color _BOARD_COLOR = Color.GRAY;
    private final Color _EDGE_COLOR_LIGHT = new Color(232, 232, 232);
    private final Color _EDGE_COLOR_DARK = new Color(182, 182, 182);
    private final Color _SEP_COLOR = new Color(0, 0, 0);
    private final Color _TEXT_COLOR = new Color(24, 24, 186);
    private final Font _TEXT_FONT = new Font("Arial", Font.BOLD, 36);

    private int[][] _board;
    // #endregion [Private] Constants and fields

    public SudokuBoard(String filePath) throws IOException {
        super();
        File sudokuFile = new File(filePath);
        List<String> rawLines = (sudokuFile.exists())
            ? loadFromFile(sudokuFile)
            : loadFromRes(filePath);
        setupPuzzle(rawLines);
        setupBoard();
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
        _board = new int[9][9];
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
                _board[row][col] = (c >= '1' && c <= '9')
                    ? c - '0'
                    : 0;

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
            }
        }
    }
    // #endregion: [Private] Helper methods 
    
    // #region: [Public] Board methods
    public int getValue(int row, int col) {
        return _board[row][col];
    }

    public int setValue(int row, int col, int value) {
        int prev = _board[row][col];
        _board[row][col] = value;
        return prev;
    }
    // #endregion: [Public] Board methods
}
