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

                    score = simulateTurn(grid, row, col);
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestLocation = new Location(row, col);
                    }
                }
            }
        }

        

        return bestLocation;
    }

    private int getEnemyID(Grid g) {
        for (int row = 0; row < g.getRows(); row++) {
            for (int col = 0; col < g.getCols(); col++) {
                int cell = g.getCell(row, col);
                if (cell != -1 && cell != super.getID()) {
                    return cell;
                }
            }
        }
        return -1;
    }

    private int simulateTurn(Grid g, int myRow, int myCol) {
        int enemyId = getEnemyID(g);
        
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

        /*for (int i = 0; i < 5; i++) {
            Grid before = new Grid(grid);
            grid = runGeneration(grid, before);
        }*/

        Grid before = new Grid(grid);
        grid = runGeneration(grid, before);

        Location enemyMove = getEnemyMove(g, grid);
        if (enemyMove.getRow() != -1 && enemyMove.getCol() != -1) {
            if (grid[enemyMove.getRow()][enemyMove.getCol()] == enemyId || grid[enemyMove.getRow()][enemyMove.getCol()] == super.getID()) {
                grid[enemyMove.getRow()][enemyMove.getCol()] = -1;
            } 
            else if (grid[enemyMove.getRow()][enemyMove.getCol()] == -1) {
                grid[enemyMove.getRow()][enemyMove.getCol()] = enemyId;
            }
        }
        before = new Grid(grid);
        grid = runGeneration(grid, before);

        int yourCells = getCellCount(grid, super.getID());

        return yourCells;
    }

    private Location getEnemyMove(Grid g, int[][] original) {
        int enemyId = getEnemyID(g);
        int[][] grid = null;

        int[][] candidateMoves = new int[5][2];
        double[] candidateScores = new double[5];
        double moveScore = 0;

        int bestRow = -1;
        int bestCol = -1;
        int bestScore = Integer.MIN_VALUE;

        for (int row = 0; row < original.length; row++) {
            for (int col = 0; col < original[row].length; col++) {
                if (isViable(original, row, col)) {
                    if (original[row][col] == super.getID()) {
                        moveScore += 2;
                    }
                    else if (original[row][col] == -1) {
                        moveScore++;
                    }

                    int neighbors = 0;
                    for (int r = Math.max(row-1, 0); r <= Math.min(row+1, original.length-1); r++) {
                        for (int c = Math.max(col-1, 0); c <= Math.min(col+1, original[0].length-1); c++) {
                            if (original[r][c] == super.getID()) {
                                moveScore += 1.5;
                                neighbors++;
                            }
                            else if (original[r][c] == getEnemyID(g)) {
                                moveScore++;
                                neighbors++;
                            }
                        }
                    }
                    
                    if (neighbors == 3 || neighbors == 2) {
                        moveScore += 1;
                    }

                    boolean done = false;
                    for (int i = 0; i < candidateMoves.length; i++) {
                        if (candidateScores[i] == 0) {
                            candidateScores[i] = moveScore;
                            candidateMoves[i][0] = row;
                            candidateMoves[i][1] = col;
                            done = true;
                            break;
                        }
                    }

                    if (!done) {
                        int lowestIndex = -1;
                        double lowestScore = Double.MAX_VALUE;
                        for (int i = 0; i < candidateScores.length; i++) {
                            if (candidateScores[i] < lowestScore) {
                                lowestScore = candidateScores[i];
                                lowestIndex = i;
                            }
                        }
                        if (lowestIndex != -1 || moveScore > lowestScore) {
                            candidateScores[lowestIndex] = moveScore;
                            candidateMoves[lowestIndex][0] = row;
                            candidateMoves[lowestIndex][1] = col;
                        }
                    }
                }
            }
        }

        for (int[] move : candidateMoves) {
            grid = new int[original.length][original[0].length];

            for (int r = 0; r < original.length; r++) {
                for (int c = 0; c < original[r].length; c++) {
                    grid[r][c] = original[r][c];
                }
            }

            if (grid[move[0]][move[1]] == enemyId || grid[move[0]][move[1]] == super.getID()) {
                grid[move[0]][move[1]] = -1;
            } 
            else if (grid[move[0]][move[1]] == -1) {
                grid[move[0]][move[1]] = enemyId;
            }

            int cellsBefore = getCellCount(grid, getEnemyID(g));

            Grid before = new Grid(grid);
            grid = runGeneration(grid, before);

            int score = getCellCount(grid, getEnemyID(g)) - cellsBefore;
                    
            if (score > bestScore) {
                bestScore = score;
                bestRow = move[0];
                bestCol = move[1];
            }
        }

        return new Location(bestRow, bestCol);
    }

    private int[][] runGeneration(int[][] grid, Grid g) {
        int[][] next = new int[grid.length][grid[0].length];

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int neighbors = GridFunctions.getNeighbors(row, col, g);

                if (grid[row][col] != -1) {
                    if (neighbors < 2 || neighbors > 3) {
                        next[row][col] = -1;
                    }
                    else {
                        next[row][col] = GridFunctions.mostCommonNeighbor(row, col, g, super.getRandom());
                    }
                }
                else {
                    if (neighbors == 3) {
                        next[row][col] = GridFunctions.mostCommonNeighbor(row, col, g, super.getRandom());
                    }
                    else {
                        next[row][col] = -1;
                    }
                }
            }
        }

        return next;
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