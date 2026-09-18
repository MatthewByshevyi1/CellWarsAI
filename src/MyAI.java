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
        

        return new Location(randomInt(grid.getRows()), randomInt(grid.getCols()));
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
                        next[row][col] = GridFunctions.mostCommonNeighbor(row, col, before, random);
                    }
                }
                else {
                    if (neighbors == 3) {
                        next[row][col] = GridFunctions.mostCommonNeighbor(row, col, before, random);
                    }
                    else {
                        next[row][col] = -1;
                    }
                }
            }
        }

        return 0;
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