/**
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */
package grid;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import solver.Cage;
import solver.Index;


/**
 * Class implementing the grid for Killer Sudoku.
 * Extends SudokuGrid (hence implements all abstract methods in that abstract
 * class).
 * You will need to complete the implementation for this for task E and
 * subsequently use it to complete the other classes.
 * See the comments in SudokuGrid to understand what each overriden method is
 * aiming to do (and hence what you should aim for in your implementation).
 */
public class KillerSudokuGrid extends SudokuGrid
{
    //Attributes for class KillerSudokuGrid
    int grid[][];
    int gridSize = 0;
    int cageNo = 0;
    int validNumbers[];
    List<Cage> cageList = new ArrayList<>();

    public KillerSudokuGrid() {
        super();

    } // end of KillerSudokuGrid()


    /* ********************************************************* */


    @Override
  //Initialize grid for killer sudoku
    public void initGrid(String filename) throws FileNotFoundException, IOException
    {
        int counter = 1;
        File inputFile = new File(filename);
        FileReader fileReader = new FileReader(inputFile);
        BufferedReader buffReader = new BufferedReader(fileReader);
        String readFileLine;
      //Read file till the end of the file
        while ((readFileLine = buffReader.readLine()) != null) 
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
                for (int i = 0; i < temp.length; i++) 
                {
                    validNumbers[i] = Integer.parseInt(String.valueOf(temp[i]));
                }
            } 
            //Read the number of cages in sudoku from input file and store it in a variable
            else if (counter == 3) 
            {
                cageNo = Integer.parseInt(readFileLine);
            }
            //Read the indexes of the cell and the sum of their cage from input file
            else 
            {
                String temp[] = readFileLine.split(" ");
                int cageTotal = Integer.parseInt(temp[0]);
                Cage cage = new Cage();
                cage.setCageTotal(cageTotal);
                List<Index> indexList = new ArrayList<>();
                for (int x = 1; x < temp.length; x++) 
                {
                    Index index = new Index();
                    index.setX(Integer.parseInt(temp[x].split(",")[0]));
                    index.setY(Integer.parseInt(temp[x].split(",")[1]));
                    indexList.add(index);
                }
                cage.setIndexList(indexList);
                cageList.add(cage);
                
            }
            counter++;
        }
    } // end of initBoard()


    @Override
  //output the solved grid to output file
    public void outputGrid(String filename) throws FileNotFoundException, IOException
    {
        FileWriter filewriter = new FileWriter(filename + ".exp");
        for (int x = 0; x < gridSize; x++) 
        {
            for (int y = 0; y < grid[x].length; y++) 
            {
                if (y == gridSize-1) 
                {
                    filewriter.write(grid[x][y]+"");
                } 
                else 
                {
                    filewriter.write(grid[x][y] + ",");
                }
            }
            filewriter.write("\n");
        }
        filewriter.close();
    } // end of outputBoard()


    @Override
  //Method to converts grid to a String representation.
    public String toString() 
    {
        StringBuffer temp = new StringBuffer();
        String sep = ",";
        for (int x = 0; x < gridSize; ++x) 
        {
            for (int y = 0; y < grid[x].length; ++y) 
            {
                if (y == gridSize - 1) 
                {
                    temp.append(grid[x][y]);
                } 
                else 
                {
                    temp.append(grid[x][y]).append(sep);
                }
            }
            temp.append('\n');
        }
        return String.valueOf(temp.toString());
    } // end of toString()
    
    
    //Helper method to check if a list of all unique numbers from valid numbers 
    private boolean isListGood(ArrayList<Integer> list) 
    {
        for (int x = 0; x < gridSize; x++) 
        {
            if (!list.contains(validNumbers[x])) 
            	{
            		return true;
            	}
        }
        
        return false;
    }
    
    //Method to validates all constraints of current grid
    public boolean validate() 
    {        
        if (!validateAllRows() && !validateAllCoulmns() && !validateAllBoxes()) 
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
    private boolean validateAllCoulmns() 
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
    private boolean validateAllBoxes() 
    {
        int boxSize = (int)Math.sqrt(gridSize);
        for (int x = 0; x < gridSize; x++) 
        {
            int boxRow = (x / boxSize) * boxSize;
            int boxcolumn = (x % boxSize) * boxSize;
            
            ArrayList<Integer> tempList = new ArrayList<>();
            for (int i = boxRow; i < (boxRow + boxSize); i++) 
            {
                for (int j = boxcolumn; j < (boxcolumn + boxSize); j++) 
                {
                    tempList.add(grid[i][j]);
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

    @Override
  //setter method which assigns sudoku grid by taking 2D integer array as input
    public void setGrid(int[][] grid) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                this.grid[i][j] = grid[i][j];
            }
        }
    }

    public int getCageNo() {
        return cageNo;
    }

    public void setCageNo(int cageNo) {
        this.cageNo = cageNo;
    }
    
  //getter method which returns integer array of all valid numbers to solve sudoku
    public int[] getValidNumbers() {
        return validNumbers;
    }
    
  //setter method to set valid numbers
    public void setValidNumbers(int[] validNumbers) {
        this.validNumbers = validNumbers;
    }
    // getter method which returns all set of cage and indexes combination
    public List<Cage> getCageList() {
        return cageList;
    }
    

} // end of class KillerSudokuGrid
