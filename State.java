public class State{
    // Simple container for a grid cell's coordinates. I intentionally kept
    // this lightweight because States are created once and reused from the
    // Maze.board array.
    int row;
    int column;

    public State(int row, int column){
        // Basic constructor. Thought: it's tempting to add methods like
        // equals/hashCode here, but for the assignment the default
        // identity-based equals might be OK because Maze returns the same
        // State objects from the board array. If this class were used in a
        // different context I'd implement equals/hashCode based on row/col.
        this.row = row;
        this.column = column;
    }
    // Small helpers that encode where the goal is for this maze. I left
    // them returning constants because the maze is fixed-size; an
    // alternative would be to ask the Maze instance for its dimensions.
    public int getGoalColumn(){
        return 3;
    }
    public int getGoalRow(){
        return 3;
    }
}