/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;
import grid.SudokuGrid;
import grid.KillerSudokuGrid;
import java.util.ArrayList;
import java.util.List;


/**
 * Backtracking solver for Killer Sudoku.
 */
public class KillerBackTrackingSolver extends KillerSudokuSolver
{
    // Attributes for class KillerBackTrackingSolver.
    private int inGrid[][];
    private int inGridSize;
    private final int isEmpty = 0;
    int cageNo = 0;
    List<Cage> cageList = new ArrayList<>();
    private int[] validNum;

    public KillerBackTrackingSolver() {
    } // end of KillerBackTrackingSolver()


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
    
    // //Method to check if the given number is already present in its row
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
    public boolean isValidBlock(int row, int col, int value) 
    {
        int boxSize = (int) Math.sqrt(inGridSize);
        int boxRow = row - row % boxSize;
        int boxColumn = col - col % boxSize;

        for (int x = boxRow; x < boxRow + boxSize; x++) 
        {
            for (int y = boxColumn; y < boxColumn + boxSize; y++) 
            {
                if (inGrid[x][y] == value) 
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    //Method to check if a given value satisfies all constrains of sudoku
    public boolean isSatisfied(int prow, int pcol, int value) 
    {
        return isValidRow(prow, value) && isValidColumn(pcol, value) && isValidBlock(prow, pcol, value) && cageConstratint(prow, pcol, value);
    }
    

    //Method to get expected total of a cage given row and column of one of its cell  
    private int checkCageSum(int row, int column) 
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



    //Method to check if given cage is empty (filled with zero)
    private boolean isCageEmpty(int row, int column) 
    {
        for (int x = 0; x < cageList.size(); x++) 
        {
            for (int y = 0; y < cageList.get(x).getIndexList().size(); y++) 
            {
                if (cageList.get(x).getIndexList().get(y).getX() == row && cageList.get(x).getIndexList().get(y).getY() == column) 
                {

                    for (int z = 0; z < cageList.get(x).getIndexList().size(); z++) 
                    {

                        if (z == cageList.get(x).getIndexList().size() - 1) 
                        {
                            return false;
                        }

                        if (inGrid[cageList.get(x).getIndexList().get(z).getX()][cageList.get(x).getIndexList().get(z).getY()] == 0) 
                        {

                            return true;

                        }
                    }
                }
            }
        }
        return false;
    }
    
    //Method to get sum of a cage from solution grid for a given 
    //row and column combination of one of its cell
    private int checkCagePresentSum(int row, int column) 
    {
        int sum = 0;
        for (int x = 0; x < cageList.size(); x++) 
        {
            for (int y = 0; y < cageList.get(x).getIndexList().size(); y++) 
            {
                if (cageList.get(x).getIndexList().get(y).getX() == row && cageList.get(x).getIndexList().get(y).getY() == column) {

                    for (int z = 0; z < cageList.get(x).getIndexList().size(); z++) 
                    {
                        sum = sum + inGrid[cageList.get(x).getIndexList().get(z).getX()][cageList.get(x).getIndexList().get(z).getY()];
                    }
                }
            }
        }

        return sum;
    }

    //Method to check cage constraint   
    private boolean cageConstratint(int cRow, int cCol, int value) 
    {
            if (isCageEmpty(cRow, cCol)) 
            {
                if (checkCagePresentSum(cRow, cCol) < checkCageSum(cRow, cCol)) 
                {
                    return true;
                } 
                else 
                {
                    return false;
                }
            } 
            else 
            {
                if (checkCagePresentSum(cRow, cCol) + value == checkCageSum(cRow, cCol)) 
                {
                    return true;
                } 
                else 
                {
                    return false;
                }
            }
    }

    
    //Recursive method to solve the sudoku using backtracking
    public boolean solveGrid() 
    {
        for (int m = 0; m < inGridSize; m++) 
        {
            for (int n = 0; n < inGridSize; n++) 
            {
                if (inGrid[m][n] == isEmpty) 
                {
                    for (int o = 0; o < inGridSize; o++) 
                    {
                        if (isSatisfied(m, n, validNum[o])) 
                        {
                            inGrid[m][n] = validNum[o];
                            if (solveGrid()) 
                            {
                                return true;
                            } 
                            else 
                            {
                                inGrid[m][n] = isEmpty;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

} // end of class KillerBackTrackingSolver()
