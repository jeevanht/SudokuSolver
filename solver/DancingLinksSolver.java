/*
 * @author Jeffrey Chan & Minyi Li, RMIT 2020
 */

package solver;

import grid.SudokuGrid;
import grid.StdSudokuGrid;
import java.util.ArrayList;


/**
 * Dancing links solver for standard Sudoku.
 */
public class DancingLinksSolver extends StdSudokuSolver
{
    private class DancingLinkNode {        
        public DancingLinkNode left;
        public DancingLinkNode right;
        public DancingLinkNode up;
        public DancingLinkNode down;
        public DancingLinkNode ch;
        public int mr;    
        public int mc;     
        public int numb1s;       
    }

  //Attributes for class DancingLinksSolver.
    int[][] inGrid;
    int inGridSize, subsize;
    int[] validNumbers;
    boolean[][] ecMatrix;
    int rows, cols;
    int rcc, rvc, cvc, bvc;
    DancingLinkNode headNode;
    DancingLinkNode[][] nodes2d;
    ArrayList<DancingLinkNode> solution;

    public DancingLinksSolver() {
        headNode = new DancingLinkNode();
        solution = new ArrayList<>();
    } // end of DancingLinksSolver()


    @Override
    public boolean solve(SudokuGrid grid) {
        StdSudokuGrid sGrid = (StdSudokuGrid)grid;
        inGrid = grid.getGrid();
        inGridSize = grid.getGridSize();
        subsize = (int)Math.sqrt(inGridSize);
        validNumbers = grid.getvalidNumbers();
        rows = inGridSize*inGridSize*inGridSize;
        cols = inGridSize*inGridSize*4;
        ecMatrix = new boolean[rows+1][cols];
        nodes2d = new DancingLinkNode[rows+1][cols];
        
        for (int x = 0; x <= rows; x++) 
        {
            for (int y = 0; y < cols; y++) 
            {
                nodes2d[x][y] = new DancingLinkNode();
            }
        }
        
        int temp = inGridSize*inGridSize;
        rcc = temp;
        rvc = rcc + temp;
        cvc = rvc + temp;
        bvc = cvc + temp;
        
        initializeBooleanMatrix();
        initializeNodeMatrix();
        genBaseSol();
        boolean status = solveSudoku(0);
        if (status) {
            generateSolvedSudoku();
            grid.setGrid(inGrid);           
        }
        
        return status;
    } 

    private void generateSolvedSudoku() 
    {
        for (DancingLinkNode a : solution) 
        {
        	int temp = inGridSize*inGridSize;
            int solrow = a.mr - 1;
            int row = solrow / (temp);
            int col = (solrow % (temp)) / inGridSize;
            int valIndex = (solrow % (temp)) % inGridSize;                
            int val = fetchValueAtIndex(valIndex);
            inGrid[row][col] = val;
        }        
    }
    

    private void genBaseSol() 
    {
        for (int x = 0; x < inGridSize; x++) 
        {
            for (int y = 0; y < inGridSize; y++) 
            {
                if (inGrid[x][y] != 0) 
                {
                    int matr = x * inGridSize * inGridSize + y * inGridSize + fetchIndexOfElement(inGrid[x][y]) + 1;
                    int matc = x * inGridSize + y;
                    DancingLinkNode node = nodes2d[matr][matc];
                    coverNode(node);
                    for (DancingLinkNode b = node.right; b != node; b = b.right) 
                    {
                        coverNode(b);
                    }
                    solution.add(node);
                }
            }
        }        
    }
    
    private boolean solveSudoku(int k) 
    {
        if(headNode.right == headNode) 
        {
            return true;
        }
        DancingLinkNode c = fetchPriorityCols(); 
        coverNode(c); 

        for(DancingLinkNode x = c.down; x != c; x = x.down) 
        {
            solution.add(x);
            for(DancingLinkNode y = x.right; y != x; y = y.right) 
            {
                coverNode(y); 
            }
            boolean sol = solveSudoku(k+1);
            if(sol) return true;
            int sizelist = solution.size();
            solution.remove(sizelist - 1);
            c = x.ch;
            for(DancingLinkNode z = x.left; z != x; z = z.left) 
            {
                uncoverNode(z);
            }
        }
        uncoverNode(c);
        return false;
    }

    //Method to generate exact cover matrix
    private void initializeNodeMatrix() 
    {
        for(int i = 0; i <= rows; i++) 
        { 
            for(int j = 0; j < cols; j++) 
            { 
                if(ecMatrix[i][j]) 
                {
                    if(i != 0) nodes2d[0][j].numb1s++;
                    nodes2d[i][j].ch = nodes2d[0][j];
                    nodes2d[i][j].mr = i;  
                    int a = i;
                    int b = j;
                    do 
                    {
                        b = (b + cols - 1) % cols;
                    } 
                    while(!ecMatrix[a][b] && b != j);                    
                    nodes2d[i][j].left = nodes2d[i][b]; 
                    a = i; b = j;
                    do 
                    {
                        b = (b + 1) % cols;
                    } 
                    while(!ecMatrix[a][b] && b != j);
                    nodes2d[i][j].right = nodes2d[i][b];  
                    a = i; b = j; 
                    do 
                    {
                        a = (a + rows) % (rows+1);
                    } 
                    while(!ecMatrix[a][b] && a != i);
                    nodes2d[i][j].up = nodes2d[a][j]; 
                    a = i; b = j;
                    do 
                    {
                        a = (a + 1) % (rows + 1);
                    } 
                    while(!ecMatrix[a][b] && a != i);
                    nodes2d[i][j].down = nodes2d[a][j]; 
                }
            }
        }
        headNode.right = nodes2d[0][0];
        headNode.left = nodes2d[0][cols-1]; 
        nodes2d[0][0].left = headNode;
        nodes2d[0][cols-1].right = headNode;
    }
    
    //Method to cover the cells which have satisfied the constraints
    private void coverNode(DancingLinkNode c)
    {
        DancingLinkNode chead = c.ch;
        chead.left.right = chead.right; 
        chead.right.left = chead.left; 
        for(DancingLinkNode i = chead.down; i != chead; i = i.down) 
        {
            for(DancingLinkNode j = i.right; j != i; j = j.right) 
            { 
                j.up.down = j.down; 
                j.down.up = j.up; 
                nodes2d[0][j.mc].numb1s -= 1;
            } 
        }         
    }
    
  //Method to backtrack and uncover the cells which do not satisfied the constraints
    private void uncoverNode(DancingLinkNode c) 
    {
        DancingLinkNode chead = c.ch;
        for(DancingLinkNode i = chead.up; i != chead; i = i.up) 
        { 
            for(DancingLinkNode j = i.left; j != i;  j = j.left) 
            { 
                j.up.down = j;
                j.down.up = j;
                nodes2d[0][j.mc].numb1s += 1;
            } 
        } 
        chead.left.right = chead; 
        chead.right.left = chead;
    }
    
    //Method to fetch the columns with less number of 1s in exact cover matrix
    private DancingLinkNode fetchPriorityCols() 
    {
        DancingLinkNode c = headNode.right;         
        for (DancingLinkNode j = c.right; j != headNode; j = j.right) 
        {
            if(j.numb1s < c.numb1s) 
            {
                c = j; 
            }
        }
        return c;
    }
    
    //Method to initialize the boolean martix
    private void initializeBooleanMatrix() 
    {
        for (int x = 0; x < cols; x++) 
        {
            ecMatrix[0][x] = true;
        }
        
        for (int x = 0; x < rcc; x++) 
        {                
            int puzzRow = x / inGridSize;
            int puzzCol = x % inGridSize;
            int startrow = puzzRow * inGridSize* inGridSize + puzzCol * inGridSize;
            for (int y = 1; y <= inGridSize; y++) 
            {
                ecMatrix[startrow+y][x] = true;
            }
        }
        
        // run row value constraints
        for (int jrv = rcc; jrv < rvc; jrv++) 
        {
            int j = jrv - rcc;
            int puzzRow = j / inGridSize;
            int value = j % inGridSize + 1;
            int rIndex = puzzRow * inGridSize * inGridSize + value;
            for (int x = 0; x < inGridSize; x++) 
            {
                ecMatrix[rIndex][jrv] = true;
                rIndex = rIndex + inGridSize;
            }
        }        
        
        for (int jcv = rvc; jcv < cvc; jcv++) 
        {
            int j = jcv - rvc;
            int puzzC = j / inGridSize;
            int value = j % inGridSize + 1;
            int rIndex = puzzC * inGridSize + value;
            for (int x = 0; x < inGridSize; x++) 
            {
                ecMatrix[rIndex][jcv] = true;
                rIndex = rIndex + inGridSize*inGridSize;
            }
        }
        
        for (int jbv = cvc; jbv < bvc; jbv++) 
        {
            int j = jbv - cvc;
            int box = j / inGridSize;
            int puzzR = (box / subsize) * subsize;
            int puzzC = (box % subsize) * subsize;
            int value = j % inGridSize + 1;
            int startingrowindex = puzzR * inGridSize * inGridSize + puzzC * inGridSize + value;
            for (int x = 0; x < subsize; x++) 
            {
                int rowindex = startingrowindex + x * inGridSize * inGridSize;
                for (int y = 0; y < subsize; y++) 
                {
                    ecMatrix[rowindex][jbv] = true;
                    rowindex = rowindex + inGridSize;
                }
            }
        }
    }
    
    //Method to fetch the index of a element from grid
    private int fetchIndexOfElement(int val) 
    {
        int result = -1;
        for (int x = 0; x < validNumbers.length; x++) 
        {
            if (val == validNumbers[x]) 
            {
                result = x;
                break;
            }
        }
        
        return result;
    }
    
    //Method to fetch value from the index from grid
    private int fetchValueAtIndex(int index) 
    {
        int result = 0;
        for (int x = 0; x < validNumbers.length; x++) 
        {
            if (x == index) {
                result = validNumbers[x];
                break;
            }
        }
        
        return result;
    }
} // end of class DancingLinksSolver
