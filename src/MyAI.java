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
                if (isViable(grid.getGrid(), row, col)) {
                    int score = Integer.MAX_VALUE;

                    /*for (int r = 0; r < grid.getRows(); r++) {
                        for (int c = 0; c < grid.getCols(); c++) {
                            if (isViable(grid.getGrid(), r, c)) {

                                int oneScore = simulateTurn(grid, row, col, r, c);

                                if (oneScore < score) {
                                    score = oneScore;
                                }
                            } else {
                                continue;
                            }
                        }
                    }*/

                    score = simulateTurn(grid, row, col);
                    
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestLocation = new Location(row, col);
                        System.out.println("check");
                    }
                }
            }
        }

        

        return bestLocation;
    }

    private int simulateTurn(Grid g, int myRow, int myCol/* , int enemyRow, int enemyCol*/) {
        int enemyId = 0;
        if (super.getID() == 1) {
            enemyId = 2;
        }
        else if (super.getID() == 2) {
            enemyId = 1;
        }
        
        int[][] original = g.getGrid();
        int[][] grid = new int[original.length][original[0].length];

        for (int r = 0; r < original.length; r++) {
            for (int c = 0; c < original[r].length; c++) {
                grid[r][c] = original[r][c];
            }
        }

        if (grid[myRow][myCol] == enemyId || grid[myRow][myCol] == super.getID()) {
            grid[myRow][myCol] = -1;
        } 
        else if (grid[myRow][myCol] == -1) {
            grid[myRow][myCol] = super.getID();
        }

        /*if (grid[enemyRow][enemyCol] == enemyId || grid[enemyRow][enemyCol] == super.getID()) {
            grid[enemyRow][enemyCol] = -1;
        } 
        else if (grid[enemyRow][enemyCol] == -1) {
            grid[enemyRow][enemyCol] = enemyId;
        }*/

        Grid before = new Grid(grid);
        int[][] next = new int[grid.length][grid[0].length];

        //for (int i = 0; i < 2; i++) {
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
            grid = next;
            before = new Grid(grid);
            next = new int[grid.length][grid[0].length];
        //}

        int yourCells = getCellCount(grid, super.getID());
        int enemyCells = getCellCount(grid, enemyId);

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

    private boolean isViable(int[][] grid, int row, int col) {
        for (int r = Math.max(row-2, 0); r <= Math.min(row+2, grid.length-1); r++) {
            for (int c = Math.max(col-2, 0); c <= Math.min(col+2, grid[0].length-1); c++) {
                if (grid[r][c] != -1) {
                    return true;
                }
            }
        }
        return false;
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