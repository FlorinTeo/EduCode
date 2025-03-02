import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import edu.ftdev.Sudoku.SudokuBoard;

public class Sudoku_tests {
    @Test
    public void basicTest() throws IOException {
        SudokuBoard board = new SudokuBoard("sudoku1.txt");
        assertEquals(0, board.getValue(0, 0));
        assertEquals(8, board.getValue(0, 1));
        board.open();
        board.breakJump();
        board.close();
    }
}
