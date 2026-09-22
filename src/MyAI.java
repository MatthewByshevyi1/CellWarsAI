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
                
                Location my = new Location(row, col);

                int score = Integer.MAX_VALUE;

                for (int r = 0; row < grid.getRows(); r++) {
                    for (int c = 0; col < grid.getCols(); c++) {
                        
                        Location enemy = new Location(r,c);

                        int oneScore = simulateTurn(grid, my, enemy);

                        if (oneScore < score) {
                            score = oneScore;
                        }
                    }
                }
                
                if (score > bestScore) {
                    bestScore = score;
                    bestLocation = my;
                }
            }
        }

        return bestLocation != null ? bestLocation : new Location(randomInt(grid.getRows()), randomInt(grid.getCols()));
    }

    private int simulateTurn(Grid g, Location me, Location enemy) {
        Random random = new Random(RandomHolder.currentSeed);
        for (Integer i : RandomHolder.calls) {
            random.nextInt(i);
        }

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

        if (grid[me.getRow()][me.getCol()] == enemyId || grid[me.getRow()][me.getCol()] == super.getID()) {
            grid[me.getRow()][me.getCol()] = -1;
        } 
        else if (grid[me.getRow()][me.getCol()] == -1) {
            grid[me.getRow()][me.getCol()] = super.getID();
        }

        if (grid[enemy.getRow()][enemy.getCol()] == enemyId || grid[enemy.getRow()][enemy.getCol()] == super.getID()) {
            grid[enemy.getRow()][enemy.getCol()] = -1;
        } 
        else if (grid[enemy.getRow()][enemy.getCol()] == -1) {
            grid[enemy.getRow()][enemy.getCol()] = enemyId;
        }

        Grid before = new Grid(grid);
        int[][] next = new int[grid.length][grid[0].length];

        for (int i = 0; i < 2; i++) {
            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[row].length; col++) {
                    int neighbors = GridFunctions.getNeighbors(row, col, before);

                    if (before.getCell(row, col) != -1) {
                        if (neighbors < 2 || neighbors > 3) {
                            next[row][col] = -1;
                        }
                        else {
                            next[row][col] = GridFunctions.mostCommonNeighbor(row, col, before, random, false);
                        }
                    }
                    else {
                        if (neighbors == 3) {
                            next[row][col] = GridFunctions.mostCommonNeighbor(row, col, before, random, false);
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
        }

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