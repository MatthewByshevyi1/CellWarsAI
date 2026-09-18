/**
 * STUDENT FILE
 *
 * Name: ______________________________
 * AI Code Name: ______________________
 *
 * Strategy Description:
 * Replace this comment with a short explanation of the strategy your AI uses.
 * Your final strategy must be fundamentally different from the sample AIs.
 */
public class MyAI extends CellAI {

    @Override
    public String getAIName() {
        String name = "LifeStealer";
        return name;
    }

    @Override
    public Location select(Grid grid) {
        int bestScore = Integer.MIN_VALUE;
        Location bestLocation = null;

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                Location loc = new Location(row, col);
                int score = simulateTurn(grid, loc);
                if (score > bestScore) {
                    bestScore = score;
                    bestLocation = loc;
                }
            }
        }

        return bestLocation != null ? bestLocation : new Location(randomInt(grid.getRows()), randomInt(grid.getCols()));
    }

    private int simulateTurn(Grid g, Location l) {
        int[][] grid = g.getGrid();

        Grid before = new Grid(grid);
        int[][] next = new int[grid.length][grid[0].length];

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int neighbors = GridFunctions.getNeighbors(row, col, before);

                if (before.getCell(row, col) != -1) {
                    if (neighbors < 2 || neighbors > 3) {
                        next[row][col] = -1;
                    }
                    else {
                        next[row][col] = GridFunctions.mostCommonNeighbor(row, col, before, super.getRandom());
                    }
                }
                else {
                    if (neighbors == 3) {
                        next[row][col] = GridFunctions.mostCommonNeighbor(row, col, before, super.getRandom());
                    }
                    else {
                        next[row][col] = -1;
                    }
                }
            }
        }

        int enemyId = 0;
        if (super.getID() == 1) {
            enemyId = 2;
        }
        else if (super.getID() == 2) {
            enemyId = 1;
        }

        int yourCells = getCellCount(next, super.getID());
        int enemyCells = getCellCount(next, enemyId);

        return yourCells - enemyCells;
    }

    private int getCellCount(int[][] grid, int id) {
        int count = 0;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == id) {
                    count++;
                }
            }
        }

        return count;
    }
}
/*
    * Replace this starter strategy.
    *
    * Helpful information:
    *   getID()                     -> your cell ID
    *   grid.getRows()              -> number of rows
    *   grid.getCols()              -> number of columns
    *   grid.getCell(r, c)          -> -1 if dead, otherwise an AI ID
    *   GridFunctions.getNeighbors  -> number of living neighbors
    *   GridFunctions.mostCommonNeighbor -> most common neighboring AI
    *   randomInt(bound)            -> reproducible random integer
    */