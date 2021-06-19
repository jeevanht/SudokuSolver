/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.SudokuGrid;

import grid.KillerSudokuGrid;
import java.util.ArrayList;
import java.util.List;


/**
 * Your advanced solver for Killer Sudoku.
 */
public class KillerAdvancedSolver extends KillerSudokuSolver
{
    // // Attributes for class KillerBackTrackingSolver.
    private int inGrid[][];
    private int inGridSize;
    private final int isEmpty = 0;
    int cageNo = 0;
    List<Cage> cageList = new ArrayList<>();
    private int[] validNum;

    public KillerAdvancedSolver() {
    } // end of KillerAdvancedSolver()


    @Override
    public boolean solve(SudokuGrid grid) 
    {
        this.cageList = grid.getCageList();
        this.inGrid = grid.getGrid();
        this.inGridSize = grid.getGridSize();
        this.validNum = grid.getvalidNumbers();
        boolean solved = solveGrid();
        if (solved) 
        {
            grid.setGrid(inGrid);
        }

        return solved;
    } // end of solve()
    
    public boolean isValidRow(int row, int value) 
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

    // //Method to check if the given number is already present in its column
    public boolean isValidColumn(int col, int value) 
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

    //Method to check if the given number is already present in its cage
    public boolean isValidBox(int row, int col, int value) 
    {
        int boxSize = (int) Math.sqrt(inGridSize);
        int rbRow = row - row % boxSize;
        int rbColumn = col - col % boxSize;

        for (int x = rbRow; x < rbRow + boxSize; x++) 
        {
            for (int y = rbColumn; y < rbColumn + boxSize; y++) 
            {
                if (inGrid[x][y] == value) 
                {
                    return false;
                }
            }
        }
        return true;
    }

    //Method to get expected total of a cage given row and column of one of its cell  
    private int fetchCageSum(int row, int column) 
    {

        for (int x = 0; x < cageList.size(); x++) 
        {
            for (int y = 0; y < cageList.get(x).getIndexList().size(); y++) 
            {
                if (cageList.get(x).getIndexList().get(y).getX() == row && cageList.get(x).getIndexList().get(y).getY() == column) 
                {
                    return cageList.get(x).getCageTotal();
                }
            }
        }

        return 0;
    }



    //Method to get sum of a cage from solution grid for a given 
    //row and column combination of one of its cell
    private int fetchCagepresentSum(int row, int column) 
    {
        int sum = 0;
        for (int x = 0; x < cageList.size(); x++) 
        {
            for (int y = 0; y < cageList.get(x).getIndexList().size(); y++) 
            {
                if (cageList.get(x).getIndexList().get(y).getX() == row && cageList.get(x).getIndexList().get(y).getY() == column) 
                {

                    for (int z = 0; z < cageList.get(x).getIndexList().size(); z++) 
                    {
                        sum += inGrid[cageList.get(x).getIndexList().get(z).getX()][cageList.get(x).getIndexList().get(z).getY()];
                    }
                }
            }
        }

        return sum;
    }

    //Method to check if given cell is empty (filled with zero)
    private boolean isCageEmpty(int row, int column) 
    {
        for (int i = 0; i < cageList.size(); i++) 
        {
            for (int j = 0; j < cageList.get(i).getIndexList().size(); j++) 
            {
                if (cageList.get(i).getIndexList().get(j).getX() == row && cageList.get(i).getIndexList().get(j).getY() == column) 
                {
                	 if (inGrid[cageList.get(i).getIndexList().get(j).getX()][cageList.get(i).getIndexList().get(j).getY()] == 0) 
                	 {

                         return true;

                     }
                }
            }
        }
        return false;
    }

    //Method to check cage constraint   
    private boolean cageConstratint(int row, int column, int input) 
    {
            if (isCageEmpty(row, column)) 
            {
                if (fetchCagepresentSum(row, column) < fetchCageSum(row, column)) 
                {
                    return true;
                } 
                else 
                {
                    return false;
                }
            } 
            return false;
    }

    //Method to check if a given value satisfies all constrains of sudoku
    public boolean isSatisfied(int row, int col, int value) 
    {
        return isValidRow(row, value) && isValidColumn(col, value) && isValidBox(row, col, value) && cageConstratint(row, col, value);
    }
    //Recursive method to solve the sudoku using backtracking
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
                        if (isSatisfied(x, y, validNum[z])) 
                        {
                            inGrid[x][y] = validNum[z];
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

} // end of class KillerAdvancedSolver
