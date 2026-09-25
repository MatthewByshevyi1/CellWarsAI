/**
 * STUDENT FILE
 *
 * Name: Matthew Byshevyi
 * AI Code Name: LifeStealer
 *
 * My AI searches through every viable move of mine (within 2 cells of something living)
 * then it chooses 100 of those viable moves to test further based on localScore for that
 * cell, and for each of these 100 cells, it simulates a generation and then runs getEnemyMove()
 * which returns the best enemy move which is chosen by grading all viable enemy moves on 
 * a point system, choosing 5 of the best ones and then running a generation for each of them to find the best one, then 
 * after I get the best enemy move I do it and then run 2 generations, and then calculate the
 * score based on the board situation. This score is then used to compare all 100 moves, and I 
 * choose the one with the best score to return.
 */
public class LifeStealerAI extends CellAI {

    @Override
    public String getAIName() {
        String name = "LifeStealer";
        return name;
    }

    @Override
    public Location select(Grid grid) {
        int bestScore = Integer.MIN_VALUE;
        Location bestLocation = null;
        int[][] candidateMoves = new int[100][2];
        double[] candidateScores = new double[100];

        for (int row = 0; row < grid.getRows(); row++) {
            for (int col = 0; col < grid.getCols(); col++) {
                if (isViable(grid.getGrid(), row, col)) {
                    double score = localScore(grid.getGrid(), row, col, super.getID());

                    boolean done = false;
                    for (int i = 0; i < candidateMoves.length; i++) {
                        if (candidateScores[i] == 0) {
                            candidateScores[i] = score;
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
                        if (lowestIndex != -1 && score > lowestScore) {
                            candidateScores[lowestIndex] = score;
                            candidateMoves[lowestIndex][0] = row;
                            candidateMoves[lowestIndex][1] = col;
                        }
                    }
                }
            }
        }

        for (int[] move : candidateMoves) {
            int score = simulateTurn(grid, move[0], move[1]);
            if (score > bestScore) {
                bestScore = score;
                bestLocation = new Location(move[0], move[1]);
            }
        }

        return bestLocation;
    }

    private int getEnemyID(Grid g, int id) {
        for (int row = 0; row < g.getRows(); row++) {
            for (int col = 0; col < g.getCols(); col++) {
                int cell = g.getCell(row, col);
                if (cell != -1 && cell != id) {
                    return cell;
                }
            }
        }
        return -1;
    }

    private int simulateTurn(Grid g, int myRow, int myCol) {
        int enemyId = getEnemyID(g, super.getID());
        
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

        int yourCells = getCellCount(grid, super.getID());
        int enemyCells = getCellCount(grid, enemyId);

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
        before = new Grid(grid);
        grid = runGeneration(grid, before);

        int myCells = getCellCount(grid, super.getID());
        int theirCells = getCellCount(grid, enemyId);

        if (myCells > theirCells * 8) {
            return (myCells - yourCells) - (theirCells - enemyCells) * 10;
        }
        else if (myCells > theirCells * 3) {
            return (myCells - yourCells) - (theirCells - enemyCells) * 2;
        }
        else if (myCells < theirCells) {
            return (myCells - theirCells) + 30 * (myCells - yourCells) -  5 * (theirCells - enemyCells);
        }
        return (myCells - theirCells)  + 30 * (myCells - yourCells) -  10 * (theirCells - enemyCells);
    }

    private Location getEnemyMove(Grid g, int[][] original) {
        int enemyId = getEnemyID(g, super.getID());
        int[][] grid = null;

        int[][] candidateMoves = new int[5][2];
        double[] candidateScores = new double[5];
        double moveScore = 1;

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
                            if (r == row && c == col) {
                                continue;
                            }
                            else if (original[r][c] == super.getID()) {
                                moveScore += 1.5;
                                neighbors++;
                            }
                            else if (original[r][c] == enemyId) {
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
                        if (lowestIndex != -1 && moveScore > lowestScore) {
                            candidateScores[lowestIndex] = moveScore;
                            candidateMoves[lowestIndex][0] = row;
                            candidateMoves[lowestIndex][1] = col;
                        }
                    }
                    moveScore = 1;
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

            int cellsBefore = getCellCount(grid, getEnemyID(g, super.getID()));

            if (grid[move[0]][move[1]] == enemyId || grid[move[0]][move[1]] == super.getID()) {
                grid[move[0]][move[1]] = -1;
            } 
            else if (grid[move[0]][move[1]] == -1) {
                grid[move[0]][move[1]] = enemyId;
            }

            Grid before = new Grid(grid);
            grid = runGeneration(grid, before);

            int score = getCellCount(grid, getEnemyID(g, super.getID())) - cellsBefore;
                    
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

    private double localScore(int[][] grid, int row, int col, int myId) {
        Grid current = new Grid(grid);
        int enemyId = getEnemyID(current, myId);
        double score = 0;
        int initialState = grid[row][col];

        if (grid[row][col] == -1) {
            grid[row][col] = myId;
        } else {
            grid[row][col] = -1;
        }

        int beforeMyCells = 0;
        int beforeEnemyCells = 0;
        for (int r = Math.max(row-2, 0); r <= Math.min(row+2, grid.length-1); r++) {
            for (int c = Math.max(col-2,0); c <= Math.min(col+2, grid[0].length-1); c++) {
                if (grid[r][c] == myId) {
                    beforeMyCells++;
                } else if (grid[r][c] == enemyId) {
                    beforeEnemyCells++;
                }
            }
        }

        int[][] next = new int[grid.length][grid[0].length];

        for (int r = Math.max(row-2, 0); r <= Math.min(row+2, grid.length-1); r++) {
            for (int c = Math.max(col-2,0); c <= Math.min(col+2, grid[0].length-1); c++) {
                int neighbors = GridFunctions.getNeighbors(r, c, current);

                if (grid[r][c] != -1) {
                    if (neighbors < 2 || neighbors > 3) {
                        next[r][c] = -1;
                    }
                    else {
                        next[r][c] = owner(grid, r, c);
                    }
                }
                else {
                    if (neighbors == 3) {
                        next[r][c] = owner(grid, r, c);
                    }
                    else {
                        next[r][c] = -1;
                    }
                }
            }
        }

        int afterMyCells = 0;
        int afterEnemyCells = 0;
        for (int r = Math.max(row-2, 0); r <= Math.min(row+2, next.length-1); r++) {
            for (int c = Math.max(col-2,0); c <= Math.min(col+2, next[0].length-1); c++) {
                if (next[r][c] == myId) {
                    afterMyCells++;
                } else if (next[r][c] == enemyId) {
                    afterEnemyCells++;
                }
            }
        }

        grid[row][col] = initialState;

        return 3 * (afterMyCells - beforeMyCells) - (afterEnemyCells - beforeEnemyCells);
    }

    private int owner(int[][] grid, int row, int col) {
        int enemyId = getEnemyID(new Grid(grid), super.getID());
        int myNeighbors = 0;
        int enemyNeighbors = 0;

        for (int r = Math.max(row-1, 0); r <= Math.min(row+1, grid.length-1); r++) {
            for (int c = Math.max(col-1,0); c <= Math.min(col+1, grid[0].length-1); c++) {
                if (grid[r][c] == super.getID()) {
                    myNeighbors++;
                } else if (grid[r][c] == enemyId) {
                    enemyNeighbors++;
                }
            }
        }
        
        if (myNeighbors > enemyNeighbors) {
            return super.getID();
        } else if (enemyNeighbors > myNeighbors) {
            return enemyId;
        } else {
            return grid[row][col];
        }
    }
}
/*
    * Replace this starter strategy.
    *
                } else if (grid[r][c] == enemyId) {
                    beforeEnemyCells++;
                }
            }
        }
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