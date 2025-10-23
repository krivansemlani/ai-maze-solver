import java.util.ArrayList;
import java.util.List;

final class Maze {
    // Board of State objects. I chose 4x4 because the assignment used a
    // fixed-size example. Each State is a shared object representing a cell;
    // sharing means neighbors can return references without allocating new
    // objects all the time.
    final State[][] board = new State[4][4];
    // Four boolean matrices to indicate if movement in a direction is
    // allowed from each cell. I used separate arrays instead of a single
    // structure because it's straightforward and explicit for the small grid.
    final boolean[][] north = new boolean[4][4];
    final boolean[][] south = new boolean[4][4];
    final boolean[][] east  = new boolean[4][4];
    final boolean[][] west  = new boolean[4][4];

    // Constructor: initialize all States and manually set the walls.
    // Thought process: For this small maze it was easiest to hard-code the
    // connectivity using the boolean arrays. If the maze were larger I'd read
    // it from a file or generate it programmatically.
    Maze() {
       
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                board[i][j] = new State(i, j);

        
        // row 0 (top): 03 13 23 33
        north[0][0]=false; east[0][0]=true;  south[0][0]=false; west[0][0]=false;
        north[0][1]=false; east[0][1]=true;  south[0][1]=false; west[0][1]=true;
        north[0][2]=false; east[0][2]=true;  south[0][2]=true;  west[0][2]=true;
        north[0][3]=false; east[0][3]=false; south[0][3]=true;  west[0][3]=true;

        // row 1: 02 12 22 32
        north[1][0]=false; east[1][0]=true;  south[1][0]=true;  west[1][0]=false;
        north[1][1]=false; east[1][1]=true;  south[1][1]=false; west[1][1]=true;   
        north[1][2]=true;  east[1][2]=false; south[1][2]=false; west[1][2]=true;
        north[1][3]=true;  east[1][3]=false; south[1][3]=false; west[1][3]=false;

        // row 2: 01 11 21 31
        north[2][0]=true;  east[2][0]=true;  south[2][0]=true;  west[2][0]=false;
        north[2][1]=false; east[2][1]=true;  south[2][1]=false; west[2][1]=true;
        north[2][2]=false; east[2][2]=true;  south[2][2]=false; west[2][2]=true;
        north[2][3]=false; east[2][3]=false; south[2][3]=true;  west[2][3]=true;   

        // row 3 (bottom): 00 10 20 30
        north[3][0]=true;  east[3][0]=false; south[3][0]=false; west[3][0]=false;
        north[3][1]=false; east[3][1]=true;  south[3][1]=false; west[3][1]=false;
        north[3][2]=false; east[3][2]=true;  south[3][2]=false; west[3][2]=true;
        north[3][3]=true;  east[3][3]=false; south[3][3]=false; west[3][3]=true;
    }

    /*
     * neighbors(s): return the adjacent cells that are reachable from s.
     * I checked bounds (r>0, r<3 etc.) to avoid array OOB. The reason I
     * check both the direction boolean and the bounds is that the boolean
     * encodes connectivity while bounds guard against referencing invalid
     * rows/columns.
     */
    List<State> neighbors(State s) {
        int r = s.row, c = s.column;
        List<State> out = new ArrayList<>(4);
        if (north[r][c] && r > 0) out.add(board[r-1][c]);
        if (south[r][c] && r < 3) out.add(board[r+1][c]);
        if (east[r][c]  && c < 3) out.add(board[r][c+1]);
        if (west[r][c]  && c > 0) out.add(board[r][c-1]);
        return out;
    }

    // start() and goal(): small helpers so other classes don't need to know
    // the indices of the start/goal. I hard-coded the corners to match the
    // assignment but wrapping them in methods improves readability.
    State start() { return board[0][0]; } 
    State goal()  { return board[3][3]; } 
}
