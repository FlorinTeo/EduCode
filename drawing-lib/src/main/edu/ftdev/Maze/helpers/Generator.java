package edu.ftdev.Maze.helpers;

import java.util.List;

import edu.ftdev.Maze.MazeCanvas.Side;

public class Generator extends WClass {
    private MazeCanvas _mc;
    private Maze _m;

    //Region: Constructor/instantiate/newInstance sequence
    public Generator(MazeCanvas mc, Maze m) {
        _mc = mc;
        _m = m;
    }
    
    protected void instantiate() throws Exception {
        _wrapObj = getCtor(pkgFix("public #Generator#(edu.ftdev.Maze.MazeCanvas,#Maze#)"))
                .newInstance(_mc, _m.getInstance());
    }
    
    public static Generator newInstance(MazeCanvas mc, Maze m) {
        Generator g = new Generator(mc, m);
        return (Generator) g.newInstance();
    }
    //EndRegion: Constructor/instantiate/newInstance sequence

    public boolean run() {
        return (boolean)super.invoke("* boolean *.run()");
    }
    
    @SuppressWarnings("unchecked")
    public List<Side> shuffle(List<Side> sides) {
        return (List<Side>)super.invoke("* java.util.*List *.shuffle(java.util.*List)", sides);
    }
    
    public Side getOpposite(Side side) {
        return (Side)super.invoke("* edu.ftdev.Maze.MazeCanvas$Side *.getOpposite(edu.ftdev.Maze.MazeCanvas$Side)", side);
    }
    
    public boolean onEnterCell(Cell cell, Side side) {
        return (boolean)super.invoke(pkgFix("* boolean *.onEnterCell(#Cell#,edu.ftdev.Maze.MazeCanvas$Side)"), cell.getInstance(), side);
    }
    
    @SuppressWarnings("unchecked")
    public List<Side> onGetNextSteps(Cell cell) {
        return (List<Side>)super.invoke(pkgFix("* java.util.*List *.onGetNextSteps(#Cell#)"), cell.getInstance());
    }
    
    public void onStepForward(Cell cell, Side side) {
        super.invoke(pkgFix("* void *.onStepForward(#Cell#,edu.ftdev.Maze.MazeCanvas$Side)"), cell.getInstance(), side);
    }
    
    public void onStepBack(boolean done, Cell cell, Side side) {
        super.invoke(pkgFix("* void *.onStepBack(boolean,#Cell#,edu.ftdev.Maze.MazeCanvas$Side)"), done, cell.getInstance(), side);
    }

    public void onExitCell(boolean done, Cell cell, Side side) {
        super.invoke(pkgFix("* void *.onExitCell(boolean,#Cell#,edu.ftdev.Maze.MazeCanvas$Side)"), done, cell.getInstance(), side);
    }
}
