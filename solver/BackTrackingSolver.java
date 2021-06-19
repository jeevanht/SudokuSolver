/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;
import grid.SudokuGrid;


/**
 * Backtracking solver for standard Sudoku.
 */
public class BackTrackingSolver extends StdSudokuSolver
{
    //Attributes for class BackTrackingSolver.
    private int inGrid[][];
    private int inGridSize;
    private final int isEmpty = 0;
    private int[] allValidNumbers;

    public BackTrackingSolver() {
    } // end of BackTrackingSolver()


    @Override
  //override method of abstract standard sukodu method to solve the sudoku
    public boolean solve(SudokuGrid grid) 
    {
        this.inGrid = grid.getGrid();
        this.inGridSize = grid.getGridSize();
        this.allValidNumbers = grid.getvalidNumbers();
        boolean solved = solveGrid();
        if (solved) 
        {
            grid.setGrid(inGrid);
        }

        return solved;
    } // end of solve()

    //Method to check if given number is already present in the given row
    public boolean isRowValid(int row, int value) 
    {
        for (int x = 0; x < inGridSize; x++) 
        {
            if (inGrid[row][x] == value) 
            {
                return false;
            }
        }

        return true;
    }

    //Method to check if given number is already present in the given column
    public boolean isColumnValid(int col, int value) 
    {
        for (int x = 0; x < inGridSize; x++) 
        {
            if (inGrid[x][col] == value) 
            {
                return false;
            }
        }
        return true;
    }

  //Method to check if given number is already present in the given box
    public boolean isBlockValid(int row, int col, int value) 
    {
        int boxSize = (int) Math.sqrt(inGridSize);
        int boxRowSize = row - row % boxSize;
        int boxCloumnSize = col - col % boxSize;

        for (int x = boxRowSize; x < boxRowSize + boxSize; x++) 
        {
            for (int y = boxCloumnSize; y < boxCloumnSize + boxSize; y++) 
            {
                if (inGrid[x][y] == value) 
                {
                    return false;
                }
            }
        }
        return true;
    }

    
    //Method to check if a number satisfies all 3 constraints
    public boolean isPossible(int row, int col, int value) 
    {
        return isRowValid(row, value) && isColumnValid(col, value) && isBlockValid(row, col, value);
    }

  //Recursive method to solve sudoku using backtracking
    public boolean solveGrid() 
    {
        for (int x = 0; x < inGridSize; x++) 
        {
            for (int y = 0; y < inGridSize; y++) 
            {
                if (inGrid[x][y] == isEmpty) 
                {
                    for (int z = 0; z < inGridSize; z++) 
                    {
                        if (isPossible(x, y, allValidNumbers[z])) 
                        {
                            inGrid[x][y] = allValidNumbers[z];
                            if (solveGrid()) 
                            {
                                return true;
                            } 
                            else 
                            {
                                inGrid[x][y] = isEmpty;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }
} // end of class BackTrackingSolver()
