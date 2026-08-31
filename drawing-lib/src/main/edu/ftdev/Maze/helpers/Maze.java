package edu.ftdev.Maze.helpers;

import edu.ftdev.Maze.MazeCanvas.Side;

public class Maze extends WClass {
    protected MazeCanvas _mc;
    
    //Region: Constructor/instantiate/newInstance sequence
    public Maze(MazeCanvas mc) {
        _mc = mc;
    }
    
    protected void instantiate() throws Exception {
        _wrapObj = getCtor(pkgFix("public #Maze#(edu.ftdev.Maze.MazeCanvas)"))
                .newInstance(_mc);
    }
    
    public static Maze newInstance(MazeCanvas mc) {
        Maze m = new Maze(mc);
        return (Maze) m.newInstance();
    }
    //EndRegion: Constructor/instantiate/newInstance sequence
    
    public void genSnake() {
        super.invoke(pkgFix("public void #Maze#.genSnake()"));
    }
    
    public void initialize() {
        super.invoke(pkgFix("public void #Maze#.initialize()"));
    }
    
    public Cell getCell(int row, int col) {
        Object cell = super.invoke(pkgFix("public #Cell# #Maze#.getCell(int,int)"), row, col);
        if (cell != null) {
            String cellType = cell.getClass().getName();
            if (cellType.equals(pkgFix("#Cell#"))) {
                cell = new Cell(_mc, cell);
            } else if (cellType.equals(pkgFix("#EdgeCell#"))) {
                cell = new EdgeCell(_mc, cell);
            } else if (cellType.equals(pkgFix("#EntryCell#"))) {
                cell = new EntryCell(_mc, cell);
            } else if (cellType.equals(pkgFix("#ExitCell#"))) {
                cell = new ExitCell(_mc, cell);
            } else if (cellType.equals(pkgFix("#BlockCell#"))) {
                cell = new BlockCell(_mc, cell);
            } else {
                cell = null;
            }
        }
        return (Cell)cell;
    }
    
    public Cell getEntryCell() {
        Object cell = super.invoke(pkgFix("public #Cell# #Maze#.getEntryCell()"));
        if (cell != null) {
            String cellType = cell.getClass().getName();
            if (cellType.equals(pkgFix("#EntryCell#"))) {
                cell = new EntryCell(_mc, cell);
            } else {
                cell = null;
            }
        }
        return (Cell)cell;
    }
    
    public Cell getExitCell() {
        Object cell = super.invoke(pkgFix("public #Cell# #Maze#.getExitCell()"));
        if (cell != null) {
            String cellType = cell.getClass().getName();
            if (cellType.equals(pkgFix("#ExitCell#"))) {
                cell = new ExitCell(_mc, cell);
            } else {
                cell = null;
            }
        }
        return (Cell)cell;
    }
    
    public Cell getNeighbor(Cell cell, Side side) {
        Object neighbor = super.invoke(pkgFix("public #Cell# #Maze#.getNeighbor(#Cell#,edu.ftdev.Maze.MazeCanvas$Side)"), 
                cell.getInstance(), side);
        if (neighbor != null) {
            String cellType = neighbor.getClass().getName();
            if (cellType.equals(pkgFix("#Cell#"))) {
                neighbor = new Cell(_mc, neighbor);
            } else if (cellType.equals(pkgFix("#EdgeCell#"))) {
                neighbor = new EdgeCell(_mc, neighbor);
            } else if (cellType.equals(pkgFix("#EntryCell#"))) {
                neighbor = new EntryCell(_mc, neighbor);
            } else if (cellType.equals(pkgFix("#ExitCell#"))) {
                neighbor = new ExitCell(_mc, neighbor);
            } else if (cellType.equals(pkgFix("#BlockCell#"))) {
                neighbor = new BlockCell(_mc, neighbor);
            } else {
                neighbor = null;
            }
        }
        return (Cell)neighbor;
    }
}
