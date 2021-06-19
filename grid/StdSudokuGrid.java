/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import solver.Cage;

/**
 * Class implementing the grid for standard Sudoku.
 * Extends SudokuGrid (hence implements all abstract methods in that abstract
 * class).
 * You will need to complete the implementation for this for task A and
 * subsequently use it to complete the other classes.
 * See the comments in SudokuGrid to understand what each overriden method is
 * aiming to do (and hence what you should aim for in your implementation).
 */
public class StdSudokuGrid extends SudokuGrid
{
    // attributes for StdSudokuGrid class
    int grid[][];
    int gridSize = 0;
    int validNumbers[];

    public StdSudokuGrid() {
        super();

    } // end of StdSudokuGrid()


    /* ********************************************************* */


    @Override
    //Initialize sudoku grid
    public void initGrid(String filename) throws FileNotFoundException, IOException
    {
        int counter = 1;
        File inputFile = new File(filename);
        FileReader fileReader = new FileReader(inputFile);
        BufferedReader bufReader = new BufferedReader(fileReader);
        String readFileLine;
        //Read file till the end of the file
        while ((readFileLine = bufReader.readLine()) != null)
        {
        	//read the grid size from file and assign it to a variable
            if (counter == 1) 
            {
                gridSize = Integer.parseInt(readFileLine);
                grid = new int[gridSize][gridSize];
            } 
            //read the valid numbers for the sudoku from input file and assign it a variable
            else if (counter == 2) 
            {
                char temp[] = readFileLine.replace(" ", "").toCharArray();
                validNumbers = new int[temp.length];
                int y = temp.length;
                for (int x = 0; x < y; x++) 
                {
                    validNumbers[x] = Integer.parseInt(String.valueOf(temp[x]));
                }
            } 
            //Read the numbers and their position already present in original sudoku from input file
            else 
            {
                String temp1[] = readFileLine.split(" ");
                String temp2[] = temp1[0].split(",");
                grid[Integer.parseInt(temp2[0])][Integer.parseInt(temp2[1])] = Integer.parseInt(temp1[1]);
            }
            counter++;
        }
    } // end of initBoard()


    @Override
    //output the solved grid to output file
    public void outputGrid(String filename) throws FileNotFoundException, IOException
    {
        FileWriter fileWriter = new FileWriter(filename + ".exp");
        for (int x = 0; x < gridSize; x++) 
        {
            for (int y = 0; y < grid[x].length; y++) 
            {
                if (y == gridSize - 1) 
                {
                    fileWriter.write(grid[x][y]+"");
                } 
                else 
                {
                    fileWriter.write(grid[x][y] + ",");
                }
            }
            fileWriter.write("\n");
        }
        fileWriter.close();
    } // end of outputBoard()


    @Override
    //Method to converts grid to a String representation.
    public String toString() 
    {
        String sep = ",";
        StringBuffer stringBuffer = new StringBuffer();

        for (int x = 0; x < gridSize; x++) 
        {
            for (int y = 0; y < grid[x].length; y++) 
            {
                if (y == gridSize-1) 
                {
                    stringBuffer.append(grid[x][y]);
                } 
                else 
                {
                    stringBuffer.append(grid[x][y]).append(sep);
                }
            }
            stringBuffer.append('\n');
        }
        return String.valueOf(stringBuffer.toString());
    } // end of toString()

    //Helper method to check if a list of all unique numbers from valid numbers 
    private boolean isListGood(ArrayList<Integer> list) 
    {
        for (int i = 0; i < gridSize; i++) 
        {
            if (!list.contains(validNumbers[i])) 
            {
            	return true;
            }
        }
        
        return false;
    }

    @Override
    //Method to validates all constraints of current grid
    public boolean validate() 
    {        
        if (!validateAllRows() && !validateAllCloumns() && !validateAllGrids()) 
        {
            return true;
        }
        return false;
    } // end of validate()
    
    //Helper method to validate if all rows satisfy row constraint
    private boolean validateAllRows() 
    {
        for (int x = 0; x < gridSize; x++) 
        {
            ArrayList<Integer> tempList = new ArrayList<>();
            for (int y = 0; y < gridSize; y++) 
            {
                tempList.add(grid[x][y]);
            }
            if (isListGood(tempList))
            {
            	return true;
            }
        }
        
        return false;
    }
    
  //Helper method to validate if all columns satisfy column constraint
    private boolean validateAllCloumns() 
    {
        for (int x = 0; x < gridSize; x++) 
        {
            ArrayList<Integer> tempList = new ArrayList<>();
            for (int y = 0; y < gridSize; y++) 
            {
                tempList.add(grid[y][x]);
            }
            if (isListGood(tempList)) 
            {
            	return true;
            }
        }
        
        return false;
    }
    
  //Helper method to validate if all boxes satisfy box constraint
    private boolean validateAllGrids() 
    {
        int boxSize = (int)Math.sqrt(gridSize);
        for (int x = 0; x < gridSize; x++) 
        {
            int boxRow = (x / boxSize) * boxSize;
            int boxColumn = (x % boxSize) * boxSize;
            
            ArrayList<Integer> tempList = new ArrayList<>();
            for (int y = boxRow; y < (boxRow + boxSize); y++) 
            {
                for (int z = boxColumn; z < (boxColumn + boxSize); z++) 
                {
                    tempList.add(grid[y][z]);
                }
            }
            if (isListGood(tempList)) 
            {
            	return true;
            }
        }
        
        return false;
    }
    
    
    @Override
    
    //getter method which returns sudoku grid in 2D array
    public int[][] getGrid() {
        return grid;
    }

    @Override
    //getter method which returns the size of sudoku grid
    public int getGridSize() {
        return gridSize;
    }
    
    @Override
    //getter method which returns integer array of all valid numbers to solve sudoku
    public int[] getvalidNumbers() {
    	return validNumbers;
    }
    
    //setter method to set valid numbers
    public void setValidNumbers(int[] validNumbers) {
        this.validNumbers = validNumbers;
    }

    @Override
    //setter method which assigns sudoku grid by taking 2D integer array as input
    public void setGrid(int[][] grid) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                this.grid[i][j] = grid[i][j];
            }
        }
    }

    @Override
    //this method is implemented only in killer sudoku
    public List<Cage> getCageList() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

} // end of class StdSudokuGrid
