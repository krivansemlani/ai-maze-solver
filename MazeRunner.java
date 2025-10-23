import java.util.*;

public class MazeRunner {
    // I keep a reference to the Maze so I can query start/goal and neighbors.
    // I wrote it like this to keep MazeRunner small and focused on search logic.
    private final Maze maze;

   
    /*
     * Inner Node class used by A*.
     * I'm writing my thoughts here like a student: I wanted a compact container
     * for the current state, its parent (so I can reconstruct the path), and
     * the g/h costs. I made f() a small helper because I used f many times
     * when ordering the frontier and it keeps the comparator legible.
     */
    private static final class Node {
        final State s;
        final Node parent;
        final int g, h;                 
        Node(State s, Node p, int g, int h) { this.s = s; this.parent = p; this.g = g; this.h = h; }
        int f() { return g + h; }
    }

    // Constructor: simple wiring. I did this because I wanted to inject a Maze
    // instance and keep the runner reusable for other mazes if needed.
    public MazeRunner(Maze maze) {
        this.maze = maze;
    }

    
    // Heuristic function h(s).
    // I picked Manhattan distance (|dr| + |dc|) because moves are 4-directional
    // and each move costs 1. Thought process: Manhattan is admissible here
    // since it never overestimates the true shortest-path distance on the grid
    // (you can't do better than moving closer in row/column by 1 each step).
    // This also makes the heuristic consistent (monotone), which simplifies
    // reasoning about revisits and guarantees optimality with A*.
    private int h(State s) {
        State g = maze.goal();
        return Math.abs(s.row - g.row) + Math.abs(s.column - g.column);
    }

    
    // A* search implementation. My step-by-step thinking as I wrote this:
    // 1) Use a PriorityQueue ordered by f = g + h, and break ties with h so
    //    nodes closer to the goal are expanded earlier when f is equal.
    // 2) Keep bestG map to remember the lowest g we've seen for a State.
    // 3) When we pop the goal we reconstruct the path via parent pointers.
    // Hard part: deciding how to handle duplicates. I chose the common
    // approach: only push a neighbor when we find a strictly better g; this
    // avoids a separate closed-set while still being correct for consistent
    // heuristics.
    public List<State> astar() {
        State start = maze.start();
        State goal  = maze.goal();

        PriorityQueue<Node> frontier = new PriorityQueue<>(
            Comparator.<Node>comparingInt(Node::f).thenComparingInt(n -> n.h)
        );
        Map<State, Integer> bestG = new HashMap<>();

        Node startNode = new Node(start, null, 0, h(start));
        frontier.add(startNode);
        bestG.put(start, 0);

        while (!frontier.isEmpty()) {
            Node cur = frontier.poll();

            // If this is the goal, we're done. I relied on parent links to
            // give me the full path. I remember being careful here: some
            // implementations check if the polled g matches bestG to skip
            // stale entries, but because I only push better g values it's
            // okay to accept the first time we see goal.
            if (cur.s.equals(goal)) {
                return reconstruct(cur);
            }

            // Expand neighbors. Every move has unit cost in this maze, so
            // ng = cur.g + 1. I then compare it to bestG and only add if it
            // improves the known path cost to that neighbor.
            for (State nb : maze.neighbors(cur.s)) {
                int ng = cur.g + 1; // unit step
                if (ng < bestG.getOrDefault(nb, Integer.MAX_VALUE)) {
                    bestG.put(nb, ng);
                    frontier.add(new Node(nb, cur, ng, h(nb)));
                }
            }
        }
        // No path found. I like to return an empty list instead of null so
        // callers won't accidentally get a NullPointerException.
        return List.of(); 
    }

   
    // Reconstruct path from goal node by following parent pointers back to
    // the start. I used LinkedList and addFirst so the loop is simple and
    // results are in start->...->goal order. This was a small, practical
    // choice to keep reconstruction O(length of path).
    private List<State> reconstruct(Node goalNode) {
        LinkedList<State> path = new LinkedList<>();
        for (Node n = goalNode; n != null; n = n.parent) path.addFirst(n.s);
        return path;
    }

   
    // Main: tiny driver to run the algorithm and print the path. I print
    // coordinates in the format used by the assignment; the label math
    // (s.column + "" + (3 - s.row)) maps the internal row numbering to the
    // displayed coordinate system the assignment expects.
    public static void main(String[] args) {
        Maze mz = new Maze();
        MazeRunner mr = new MazeRunner(mz);
        List<State> path = mr.astar();

        if (path.isEmpty()) {
            System.out.println("No path. The maze wins, you lose.");
            return;
        }
        for (int i = 0; i < path.size(); i++) {
            State s = path.get(i);
           
           String label = s.column + "" + (3 - s.row);

            System.out.print(label);
            if (i + 1 < path.size()) System.out.print(" -> ");
        }
        System.out.println("\nLength: " + (path.size() - 1) + " steps");
    }
}
