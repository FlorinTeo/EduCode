import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.junit.Test;

import edu.ftdev.Sudoku.SudokuBoard;

public class Sudoku_tests {
    @Test
    public void basicTest() throws IOException {
        SudokuBoard board = new SudokuBoard("sudoku1.txt");
        assertEquals(0, board.getValue(0, 0));
        assertEquals(8, board.getValue(0, 1));
        assertFalse(board.isSet(0, 0));
        assertTrue(board.isPinned(0, 1));
        board.open();
        board.breakStep();
        board.setStatusMessage("Test valid change of a cell");
        board.setValue(0, 0, 3);
        board.breakJump();
        assertEquals(3, board.getValue(0, 0));
        board.close();
    }

    @Test
    public void negativeTest() throws IOException {
        SudokuBoard board = new SudokuBoard("sudoku4.txt");
        board.open();
        board.breakStep();

        board.setStatusMessage("Test invalid grid coordinates");
        Exception exc = assertThrows(InvalidParameterException.class, () -> {
            board.setValue(2, 10, 3);
        });
        assertEquals("Invalid board coordinates: (2, 10)", exc.getMessage());
        board.breakStep();

        board.setStatusMessage("Test invalid grid value");
        exc = assertThrows(InvalidParameterException.class, ()->{
            board.setValue(2, 2, 10);
        });
        assertEquals("Invalid board value: 10", exc.getMessage());
        board.breakStep();

        board.setStatusMessage("Test illegal writing pinned value");
        exc = assertThrows(IllegalStateException.class, ()->{
            board.setValue(0, 0, 3);
        });
        assertEquals("Can't modify value of pinned cell: (0, 0)", exc.getMessage());
        board.breakJump();
        board.close();
    }

    @Test
    public void testReset() throws IOException {
        SudokuBoard board = new SudokuBoard("sudoku3.txt");
        board.open();
        board.breakStep();
        board.setValue(2, 2, 8);
        board.setValue(4, 4, 1);
        board.setValue(6, 6, 4);
        board.breakStep();
        assertFalse(board.isPinned(2, 2));
        assertTrue(board.isSet(7, 5));
        assertTrue(board.isPinned(7, 5));
        assertFalse(board.isSet(8, 8));
        assertEquals(0, board.getValue(8, 8));
        assertEquals(3, board.getValue(8, 7));
        board.reset();
        assertFalse(board.isSet(2, 2));
        assertFalse(board.isSet(4, 4));
        assertFalse(board.isPinned(4, 4));
        assertFalse(board.isSet(6, 6));
        assertTrue(board.isSet(1,1));
        assertTrue(board.isPinned(1, 1));
        board.breakJump();
        board.close();
    }
}
