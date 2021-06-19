/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package solver;
import grid.SudokuGrid;
import grid.StdSudokuGrid;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Algorithm X solver for standard Sudoku.
 */
public class AlgorXSolver extends StdSudokuSolver
{
    //Attributes for class AlgorXSolver.
    int[][] inGrid;
    int inGridSize, subsize;
    int[] allValidNumbers;
    boolean[][] ecMatrix;
    int rows, cols;
    int rcc, rvc, cvc, bvc;

    public AlgorXSolver() {
    } // end of AlgorXSolver()


    @Override
    //override method of abstract standard sukodu method to solve the sudoku
    public boolean solve(SudokuGrid grid) 
    {
        inGrid = grid.getGrid();
        inGridSize = grid.getGridSize();
        subsize = (int)Math.sqrt(inGridSize);
        allValidNumbers = grid.getvalidNumbers();
        rcc = inGridSize*inGridSize;
        rvc = rcc + inGridSize*inGridSize;
        cvc = rvc + inGridSize*inGridSize;
        bvc = cvc + inGridSize*inGridSize;
        rows = inGridSize*inGridSize*inGridSize;
        cols = inGridSize*inGridSize*4;
        ecMatrix = new boolean[rows][cols];

        initMatrix();    

        boolean status = solveSudoku();
        if (status) grid.setGrid(inGrid);
        
        return status;
    } // end of solve()

    
    private boolean solveSudoku() 
    {        
        boolean[] baseCoveredRows = new boolean[rows];
        boolean[] baseCoveredCols = new boolean[cols];   
        
        ArrayList<Integer> coverBase = baseSolution(baseCoveredRows, baseCoveredCols);        
        
        boolean status = sudokuRecursive(coverBase, baseCoveredRows, baseCoveredCols);
        if(status) {
            // set solution in puzzle.
            for (int temp : coverBase) 
            {
                int row = temp / (inGridSize*inGridSize);
                int col = (temp % (inGridSize*inGridSize)) / inGridSize;
                int valueAtIndex = (temp % (inGridSize*inGridSize)) % inGridSize;                
                int value = getValueAtIndex(valueAtIndex);
                inGrid[row][col] = value;
            }
        }
        return status;
    }
    
    //Method to identify the numbers present in original sudoku grid and cover them
    private ArrayList<Integer> baseSolution(boolean[] row, boolean[] col) 
    {
        ArrayList<Integer> parsolution = new ArrayList<>();
        for (int x = 0; x < inGridSize; x++) 
        {
            for (int y = 0; y < inGridSize; y++) 
            {
                if (inGrid[x][y] != 0) 
                {
                    int matrixrow = x * inGridSize * inGridSize + y * inGridSize + getIndexAtValue(inGrid[x][y]);
                    parsolution.add(matrixrow);
                    selectrowscols(matrixrow, row, col);
                }
            }
        }
        
        return parsolution;
    }
    
    //Recursive method to solves cover problem for given sudoku
    private boolean sudokuRecursive(ArrayList<Integer> sol, boolean[] row, boolean[] col) 
    {
        if (isEmpty(row, col)) 
        {
            return true;
        }
        
        ArrayList<Integer> selectrow = new ArrayList<>();
        int prioritycol = getPriorityColumn(row, col, selectrow);
        if (selectrow.isEmpty()) 
        	{
        		return false;
        	}
        // we have the selected column and the selected rows at this point
        for (int r : selectrow) 
        {
            sol.add(r);
            boolean[] ndrows = Arrays.copyOf(row, row.length);
            boolean[] ndcols = Arrays.copyOf(col, col.length);
            selectrowscols(r, ndrows, ndcols);
            if (sudokuRecursive(sol, ndrows, ndcols)) 
            	{
            		return true;
            	}
            sol.remove(Integer.valueOf(r));
        }
        
        return false;
    }
    
    //Method to identify the rows and columns to cover from 
    // the number already present or solved in sudoku grid
    private void selectrowscols(int selectRow, boolean[] row, boolean[] col) 
    {
        for (int x = 0; x < col.length; x++) 
        {
            if (ecMatrix[selectRow][x]) 
            {
                col[x] = true;
                for (int y = 0; y < row.length; y++) 
                {
                    if (ecMatrix[y][x]) 
                    {
                        row[y] = true;
                    }
                }
            }
        }
    }
    
    //Method to identify the columns with minimum number of 1's from exact cover matrix
    private int getPriorityColumn(boolean[] row, boolean[] col, ArrayList<Integer> selectRows) {
        int min = Integer.MAX_VALUE;
        int priCol = -1;
        
        for (int x = 0; x < col.length; x++) 
        {
            if (col[x]) continue;
            int counter = 0;
            ArrayList<Integer> rows1s = new ArrayList<>();
            for (int y = 0; y < row.length; y++) 
            {
                if (row[y]) continue;
                if (ecMatrix[y][x])
                {
                    counter++;
                    rows1s.add(y);
                }
            }
            // we have the count of 1s in the column
            if (counter < min) 
            {
                min = counter;
                priCol = x;
                selectRows.clear();
                for (int r : rows1s) selectRows.add(r);
            }
        }
        
        return priCol;
    }
    
    //Method to check if all rows and columns are covered
    private boolean isEmpty(boolean[] drows, boolean[] dcols) 
    {
        for (int x = 0; x < drows.length; x++) 
        {
            if (!drows[x]) 
            	{
            		return false;
            	}
        }
        for (int y = 0; y < dcols.length; y++) 
        {
            if (!dcols[y]) 
            	{
            		return false;
            	}
        }
        
        return true;
    }    
    
    //Method to construct exact cover problem matrix for given sudoku
    private void initMatrix() 
    {
        for (int x = 0; x < rcc; x++) 
        {                
            int grow = x / inGridSize;
            int gcol = x % inGridSize;
            int inirow = grow * inGridSize* inGridSize + gcol * inGridSize;
            for (int y = 0; y < inGridSize; y++) 
            {
                ecMatrix[inirow+y][x] = true;
            }
        }
        
        // run row value constraints
        for (int a = rcc; a < rvc; a++) 
        {
            int b = a - rcc;
            int grow = b / inGridSize;
            int val = b % inGridSize + 1;
            int rowindex = grow * inGridSize * inGridSize + val - 1;
            for (int c = 0; c < inGridSize; c++) 
            {
                ecMatrix[rowindex][a] = true;
                rowindex = rowindex + inGridSize;
            }
        }        
        
        // run column value constraints
        for (int jcv = rvc; jcv < cvc; jcv++) 
        {
            int j = jcv - rvc;
            int gridCol = j / inGridSize;
            int val = j % inGridSize + 1;
            int rindex = gridCol * inGridSize + val - 1;
            for (int i = 0; i < inGridSize; i++) 
            {
                ecMatrix[rindex][jcv] = true;
                rindex += inGridSize*inGridSize;
            }
        }
        for (int jbv = cvc; jbv < bvc; jbv++) 
        {
            int j = jbv - cvc;
            int box = j / inGridSize;
            int gridRow = (box / subsize) * subsize;
            int gridCol = (box % subsize) * subsize;
            int val = j % inGridSize + 1;
            int startingrowindex = gridRow * inGridSize * inGridSize + gridCol * inGridSize + val - 1;
            for (int m = 0; m < subsize; m++) 
            {
                int rowindex = startingrowindex + m * inGridSize * inGridSize;
                for (int n = 0; n < subsize; n++) 
                {
                    ecMatrix[rowindex][jbv] = true;
                    rowindex += inGridSize;
                }
            }
        }
    }
    
    //method to fetch index of a number from sudoku matrix
    private int getIndexAtValue(int val) 
    {
        int prev = -1;
        for (int i = 0; i < allValidNumbers.length; i++) 
        {
            if (val == allValidNumbers[i]) 
            {
                prev = i;
                break;
            }
        }
        
        return prev;
    }
    
    //method to identify the number given the index of it from exact cover matrix 
    private int getValueAtIndex(int index) 
    {
        int retval = 0;
        for (int i = 0; i < allValidNumbers.length; i++) 
        {
            if (i == index) 
            {
                retval = allValidNumbers[i];
                break;
            }
        }
        
        return retval;
    }
    
} // end of class AlgorXSolver
