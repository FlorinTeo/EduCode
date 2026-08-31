package edu.ftdev.Maze.helpers;

import java.awt.Color;

public class ShadedCell extends Cell {
    protected Color _shadeColor;
    
    //Region: Constructor/instantiate/newInstance sequence
    public ShadedCell(MazeCanvas mc, int row, int col, Color shadeColor) {
        super(mc, row, col);
        _shadeColor = shadeColor;
    }
    
    public ShadedCell(MazeCanvas mc, Object o) {
        super(mc, o);
    }
    
    protected void instantiate() throws Exception {
        _wrapObj = getCtor(pkgFix("public #ShadedCell#(edu.ftdev.Maze.MazeCanvas,int,int,java.awt.Color)"))
                .newInstance(_mc, _row, _col, _shadeColor);
    }
    
    public static ShadedCell newInstance(MazeCanvas mc, int row, int col, Color shadeColor) {
        ShadedCell c = new ShadedCell(mc, row, col, shadeColor);
        return (ShadedCell) c.newInstance();
    }
    //EndRegion: Constructor/instantiate/newInstance sequence
    
}
