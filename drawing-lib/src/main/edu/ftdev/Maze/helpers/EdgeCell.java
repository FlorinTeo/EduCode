package edu.ftdev.Maze.helpers;

import java.util.List;

import edu.ftdev.Maze.MazeCanvas.Side;

public class EdgeCell extends ShadedCell {

    //Region: Constructor/instantiate/newInstance sequence
    public EdgeCell(MazeCanvas mc, int row, int col) {
        super(mc, row, col, null);
    }
    
    public EdgeCell(MazeCanvas mc, Object o) {
        super(mc, o);
    }
    
    protected void instantiate() throws Exception {
        _wrapObj = getCtor(pkgFix("public #EdgeCell#(edu.ftdev.Maze.MazeCanvas,int,int)"))
                .newInstance(_mc, _row, _col);
    }
    
    public static EdgeCell newInstance(MazeCanvas mc, int row, int col) {
        EdgeCell ec = new EdgeCell(mc, row, col);
        return (EdgeCell) ec.newInstance();
    }
    //EndRegion: Constructor/instantiate/newInstance sequence
    
    @SuppressWarnings("unchecked")
    public List<Side> getWalls() {
        return (List<Side>)super.invoke(pkgFix("public java.util.*List #EdgeCell#.getWalls()"));
    }
}
