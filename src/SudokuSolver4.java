import java.util.ArrayList;

public class SudokuSolver4 {
    public static void main(String[] args) {
        char[][] grid = {
                {'9',' ',' ', ' ',' ',' ', ' ',' ',' '},
                {'2','1',' ', ' ',' ',' ', ' ',' ',' '},
                {' ',' ','7', '2','3','8', ' ',' ',' '},

                {' ',' ',' ', ' ',' ','6', '4',' ',' '},
                {'6','4','1', '5',' ',' ', '3',' ',' '},
                {' ','3','2', ' ','8',' ', '7',' ',' '},

                {' ',' ',' ', '7','6',' ', '9',' ',' '},
                {' ',' ',' ', '1','4',' ', ' ','8',' '},
                {' ',' ',' ', ' ','2',' ', ' ','5','3'},
        };

        ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions = new ArrayList<>(9);

        for(int r=0; r<9; r++) {
            cellSolutions.add(new ArrayList<>(9));
            for (int c=0; c<9; c++) {
                cellSolutions.get(r).add(new ArrayList<>(9));
            }
        }

        for (int r=0; r<9; r++) {
            for (int c=0; c<9; c++) {
                for (int i=1; i<=9; i++) {
                    if (isEmptyCell(grid,r,c))
                    cellSolutions.get(r).get(c).add(i);
                }
            }
        }

        printGrid(grid);

        int candidate;
        int sqIndex;
        int f = checkFilledGrid(grid);
        System.out.println("Filled cells: " +f +"\n");

        do {
            for (candidate = 1; candidate <= 9; candidate++) {
                for (sqIndex = 1; sqIndex <= 9; sqIndex++) {
                    /*
                        1st Iteration:
                        Checks whether each square is missing the current candidate number.
                        If missing, the valid rows and columns are checked and returned in single arrays.
                        Then the occupied squares are removed from the potential solutions and an array of the
                        remaining valid rows/columns is returned.

                        If the solutions array is exactly 2 in length (i.e. single row, single column),
                        the candidate number is added to the corresponding position on the sudoku grid.
                    */

                    if (isValidSq(grid, candidate, sqIndex)) {
                        int[] solutions = solutions(grid, validRows(grid, candidate, sqIndex), validCols(grid, candidate, sqIndex));

                        fillGrid(grid, solutions, candidate);
                        f = checkFilledGrid(grid);

                        /*
                            2nd Iteration: Square Solver
                         */

                        int[] scs2 = squareSolver(grid, candidate, sqIndex);

                        if (scs2.length == 3) {
                            int[] solutionsSq = {scs2[1],scs2[2]};
                            fillGrid(grid, solutionsSq, scs2[0]);
                            f = checkFilledGrid(grid);
                        }


                        /*
                            3rd Iteration: Single Row Solver
                         */

                        missingIntegersRow(grid, candidate - 1);

                        for (int row = 0; row < grid.length; row++) {
                            int[] r = rowSolver(grid, candidate, row);

                            fillGrid(grid, r, candidate);
                            f = checkFilledGrid(grid);
                        }


                        /*
                            4th Iteration: Single Column Solver
                         */
                        missingIntegersCol(grid, candidate - 1);

                        for (int col = 0; col < grid.length; col++) {
                            int[] c = colSolver(grid, candidate, col);

                            fillGrid(grid, c, candidate);
                            f = checkFilledGrid(grid);
                        }


                        /*
                            5th Iteration: Row solver in 3 squares
                         */

                        for (int i = 1; i <= grid.length; i++) {
                            int[] r = rowSqSolver(grid, candidate, i);

                            fillGrid(grid, r, candidate);
                            f = checkFilledGrid(grid);
                        }

                        /*
                            6th Iteration: Col solver in 3 squares
                         */

                        for (int i = 1; i <= grid.length; i++) {
                            int[] c = colSqSolver(grid, candidate, i);

                            fillGrid(grid, c, candidate);
                            f = checkFilledGrid(grid);
                        }

                        /*
                            7th Iteration: Row isolator
                         */

                        for (int i = 1; i < grid.length; i++) {
                            int[] ri = rowIsolator(grid, candidate, i);

                            fillGrid(grid, ri, candidate);
                            f = checkFilledGrid(grid);
                        }

                        /*
                            8th Iteration: Col isolator
                         */

                        for (int i = 1; i <= grid.length; i++) {
                            int[] ci = colIsolator(grid, candidate, i);

                            fillGrid(grid, ci, candidate);
                            f = checkFilledGrid(grid);
                        }

                        /*
                            9th Iteration: Twin isolator
                         */

                        for (int i=1; i<grid.length; i++)
                        {
                            int[] tsi = twinIsolator(grid,candidate,i);

                            fillGrid(grid,tsi,candidate);
                            f = checkFilledGrid(grid);
                        }

                        /*
                            10th Iteration: Cell solutions:
                            Utilises a 3D ArrayList of integers, initially populated with the digits 1-9.
                            Invalid solutions for the given cell are progressively eliminated from the corresponding
                            cell reference in the 3D ArrayList until only a single element is remaining
                            (the cell solution).
                         */

                        cellSolutionsEliminate(cellSolutions,grid);
                        f = checkFilledGrid(grid);
                    }
                }
                // printCellSolutions(cellSolutions);
            }

        }
        while (f < 81);

    }

    public static void printGrid(char[][] arr) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                System.out.print(" " + arr[row][col] + " ");
            }
            System.out.print("|");
            for (int col = 3; col < 6; col++) {
                System.out.print(" " + arr[row][col] + " ");
            }
            System.out.print("|");
            for (int col = 6; col < 9; col++) {
                System.out.print(" " + arr[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println(" - - - - - - - - - - - - - -");

        for (int row = 3; row < 6; row++) {
            for (int col = 0; col < 3; col++) {
                System.out.print(" " + arr[row][col] + " ");
            }
            System.out.print("|");
            for (int col = 3; col < 6; col++) {
                System.out.print(" " + arr[row][col] + " ");
            }
            System.out.print("|");
            for (int col = 6; col < 9; col++) {
                System.out.print(" " + arr[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println(" - - - - - - - - - - - - - -");

        for (int row = 6; row < 9; row++) {
            for (int col = 0; col < 3; col++) {
                System.out.print(" " + arr[row][col] + " ");
            }
            System.out.print("|");
            for (int col = 3; col < 6; col++) {
                System.out.print(" " + arr[row][col] + " ");
            }
            System.out.print("|");
            for (int col = 6; col < 9; col++) {
                System.out.print(" " + arr[row][col] + " ");
            }
            System.out.println();
        }
    }

    public static int[][] makeAllSq(char[][] arr, int r, int c) {
        int[][] test = new int[9][9];

        for (r = 0; r <= 6; r += 3) {
            for (c = 0; c <= 6; c += 3) {
                for (int row = r; row < r + 3; row++) {
                    for (int col = c; col < c + 3; col++) {
                        test[row][col] = Character.getNumericValue(arr[row][col]);
                    }
                }
            }
        }
        return test;
    }

    public static boolean isValidSq(char[][] arr, int index, int sqIndex) {
        int[][] test = new int[3][3];
        int r = getSqIndex(sqIndex)[0];
        int c = getSqIndex(sqIndex)[1];

        for (int row = r; row < r + 3; row++) {
            for (int col = c; col < c + 3; col++) {
                test[row - r][col - c] = Character.getNumericValue(arr[row][col]);

                if (test[row - r][col - c] == index) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int[] getSqIndex(int i) {
        int rShift = 0;
        int cShift = 0;

        if (i == 1) {
            rShift = 0;
            cShift = 0;
        }

        if (i == 2) {
            rShift = 0;
            cShift = 3;
        }

        if (i == 3) {
            rShift = 0;
            cShift = 6;
        }

        if (i == 4) {
            rShift = 3;
            cShift = 0;
        }

        if (i == 5) {
            rShift = 3;
            cShift = 3;
        }

        if (i == 6) {
            rShift = 3;
            cShift = 6;
        }

        if (i == 7) {
            rShift = 6;
            cShift = 0;
        }

        if (i == 8) {
            rShift = 6;
            cShift = 3;
        }

        if (i == 9) {
            rShift = 6;
            cShift = 6;
        }

        int[] sqIndex = {rShift, cShift};

        return sqIndex;
    }

    public static int[] validRows(char[][] arr, int candidate, int sqIndex) {
        String s = "";

        int row = getSqIndex(sqIndex)[0];

        for (int r = row; r < row + 3; r++) {
            int flag = 1;
            for (int col = 0; col < arr.length; col++) {
                if (Character.getNumericValue(arr[r][col]) == candidate) {
                    flag = 0;
                }
            }
            if (flag == 1) {
                s = s + r;
            }
        }

        int[] validRows = new int[s.length()];

        for (int i = 0; i < validRows.length; i++) {
            validRows[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }

        return validRows;
    }

    public static int[] validCols(char[][] arr, int candidate, int sqIndex) {
        String s = "";

        int col = getSqIndex(sqIndex)[1];

        for (int c = col; c < col + 3; c++) {
            int flag = 1;
            for (int row = 0; row < arr.length; row++) {
                if (Character.getNumericValue(arr[row][c]) == candidate) {
                    flag = 0;
                }
            }
            if (flag == 1) {
                s = s + c;
            }
        }

        int[] validCols = new int[s.length()];

        for (int i = 0; i < validCols.length; i++) {
            validCols[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }

        return validCols;
    }

    public static boolean isEmptyCell(char[][] arr, int row, int col) {
        if (Character.getNumericValue(arr[row][col]) > 0) {
            return false;
        }
        return true;
    }

    public static int[] solutions(char[][] arr, int[] rowsToCheck, int[] colsToCheck) {
        String s = "";

        for (int r = 0; r < rowsToCheck.length; r++) {
            for (int c = 0; c < colsToCheck.length; c++) {
                if (isEmptyCell(arr, rowsToCheck[r], colsToCheck[c])) {
                    s = s + rowsToCheck[r] + colsToCheck[c];
                }
            }
        }

        int[] solutions = new int[s.length()];

        for (int i = 0; i < solutions.length; i++) {
            solutions[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }
        return solutions;
    }

    public static void fillGrid(char[][] arr, int[] s, int index) {
        if (s.length == 2 && !isFilled(arr,s)) {
            arr[s[0]][s[1]] = Character.forDigit(index, 10);

            printGrid(arr);

            int f = checkFilledGrid(arr);
            System.out.println("Filled cells: " +f +"\n");
        }
    }

    public static boolean isFilled(char[][] arr, int[] s) {
        /** pass a pair of row/column index values and returns true if
         * the cell reference is already filled with a value **/
        return arr[s[0]][s[1]] != ' ';
    }

    /** checkFilledGrid
     Returns the number of filled cells in the sudoku grid
     */
    public static int checkFilledGrid(char[][] arr) {
        int f = 0;

        for (int row = 0; row < arr.length; row++) {
            for (int col = 0; col < arr.length; col++) {
                if (Character.getNumericValue(arr[row][col]) > 0) {
                    f++;
                }
            }
        }

        return f;
    }

    public static int[] missingIntegersSq(char[][] arr, int sqIndex) {
        int r = getSqIndex(sqIndex)[0];
        int c = getSqIndex(sqIndex)[1];

        String s = "";

        for (int row = r; row < r + 3; row++) {
            for (int col = c; col < c + 3; col++) {
                if (arr[row][col] != ' ') {
                    s = s + arr[row][col];
                }
            }
        }

        String rem = "";

        for (int i = 1; i <= 9; i++) {
            int flag = 0;
            for (int j = 0; j < s.length(); j++) {
                if (Integer.parseInt(String.valueOf(s.charAt(j))) == i) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                rem = rem + i;
            }
        }

        int[] remainingIntegers = new int[rem.length()];

        for (int i = 0; i < remainingIntegers.length; i++) {
            remainingIntegers[i] = Integer.parseInt(String.valueOf(rem.charAt(i)));
        }

        return remainingIntegers;
    }

    /** filledIntegersSq
     Returns an array of integers already populated in a given sudoku square
     */
    public static int[] filledIntegersSq(char[][] arr, int sqIndex) {
        int r = getSqIndex(sqIndex)[0];
        int c = getSqIndex(sqIndex)[1];

        String s = "";

        for (int row = r; row < r + 3; row++) {
            for (int col = c; col < c + 3; col++) {
                if (arr[row][col] != ' ') {
                    s = s + arr[row][col];
                }
            }
        }

        int[] filledIntegersSq = new int[s.length()];

        for (int i=0; i<filledIntegersSq.length; i++) {
            filledIntegersSq[i] = Character.getNumericValue(s.charAt(i));
        }

        return filledIntegersSq;
    }

    public static int[] filledIntegersRow(char[][] arr, int r) {
        String s = "";

        for (int c = 0; c < arr.length; c++ ) {
            if (arr[r][c] != ' ') {
                s = s + arr[r][c];
            }
        }

        int[] filledIntegersRow = new int[s.length()];

        for (int i=0; i<filledIntegersRow.length; i++) {
            filledIntegersRow[i] = Character.getNumericValue(s.charAt(i));
        }

        return filledIntegersRow;
    }

    public static int[] filledIntegersCol(char[][] arr, int c) {
        String s = "";

        for (int r = 0; r < arr.length; r++) {
            if (arr[r][c] != ' ') {
                s = s + arr[r][c];
            }
        }

        int[] filledIntegersCol = new int[s.length()];

        for (int i=0; i<filledIntegersCol.length; i++) {
            filledIntegersCol[i] = Character.getNumericValue(s.charAt(i));
        }

        return filledIntegersCol;
    }

    public static void printCellSolutions(ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions) {
        for (int r=0; r<9; r++) {
            System.out.println(cellSolutions.get(r));
        }
        System.out.println();
    }

    public static void cellSolutionsEliminate(ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {

        // rescan
        cellSolutionsRescanALL(cellSolutions, arr);

        // run pointing pairs row
        pointingPairROW(cellSolutions,arr);

        // rescan
        cellSolutionsRescanALL(cellSolutions, arr);

        // run pointing pairs column
        pointingPairCOLUMN(cellSolutions,arr);

        // rescan
        cellSolutionsRescanALL(cellSolutions, arr);

        // run hidden single row
        hiddenSingleROW(cellSolutions,arr);

        // rescan
        cellSolutionsRescanALL(cellSolutions, arr);

        // run hidden single column
        hiddenSingleCOL(cellSolutions, arr);

        // rescan
        cellSolutionsRescanALL(cellSolutions, arr);

        // run naked pair row
        nakedPairROW(cellSolutions, arr);

        // rescan
        cellSolutionsRescanALL(cellSolutions, arr);

        // run naked pair column
        nakedPairCOL(cellSolutions, arr);

        // rescan
        cellSolutionsRescanALL(cellSolutions, arr);

        // run hidden pair square
        hiddenPairSQ(cellSolutions, arr);

        // rescan
        cellSolutionsRescanALL(cellSolutions, arr);

        // run hidden pair row
        hiddenPairROW(cellSolutions, arr);

        // rescan
        cellSolutionsRescanALL(cellSolutions, arr);

        // run hidden pair column
        hiddenPairCOL(cellSolutions, arr);

        // rescan
        cellSolutionsRescanALL(cellSolutions, arr);
    }

    public static void cellSolutionsRescanSQ(ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        // loop to remove already populated integers in a given square
        for (int sqIndex = 1; sqIndex <=9; sqIndex++) {
            int[] filledIntegersSq = filledIntegersSq(arr,sqIndex);

            int rShift = getSqIndex(sqIndex)[0];
            int cShift = getSqIndex(sqIndex)[1];

            for (int r = rShift; r < rShift+3; r++) {
                for (int c = cShift; c < cShift+3; c++) {
                    for (int i=0; i<filledIntegersSq.length; i++) {
                        int val = filledIntegersSq[i];
                        cellSolutions.get(r).get(c).removeIf(n -> (n == val));
                    }
                }
            }
        }
    }

    public static void cellSolutionsRescanROW(ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        // loop to remove already populated integers in a given row
        for (int r=0; r<arr.length; r++) {
            int[] filledIntegersRow = filledIntegersRow(arr,r);

            for (int c=0; c<arr.length; c++) {
                for (int i=0; i<filledIntegersRow.length; i++) {
                    int val = filledIntegersRow[i];
                    cellSolutions.get(r).get(c).removeIf(n -> (n == val));
                }
            }
        }
    }

    public static void cellSolutionsRescanCOL(ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        // loop to remove already populated integers in a given column
        for (int c=0; c<arr.length; c++) {
            int[] filledIntegersCol = filledIntegersCol(arr, c);

            for (int r = 0; r < arr.length; r++) {
                for (int i = 0; i < filledIntegersCol.length; i++) {
                    int val = filledIntegersCol[i];
                    cellSolutions.get(r).get(c).removeIf(n -> (n == val));
                }
            }
        }
    }

    public static void cellSolutionsRescanALL(ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        cellSolutionsRemovePopulated(cellSolutions, arr);
        cellSolutionsRescanSQ(cellSolutions, arr);
        cellSolutionsRescanROW(cellSolutions, arr);
        cellSolutionsRescanCOL(cellSolutions, arr);
    }

    public static void pointingPairROW(ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {

        // loop to identify pointing pairs ROW, and remove instances of the same integer from the rest of the row
        for (int sqIndex = 1; sqIndex <=9; sqIndex++) {
            int rShift = getSqIndex(sqIndex)[0];
            int cLower = getSqIndex(sqIndex)[1];
            int cUpper = cLower + 2;

            for (int i=1; i<=9; i++) {

                ArrayList<Integer> pointingPairsList = new ArrayList<>();
                ArrayList<Integer> pointingPairsRow = new ArrayList<>();

                for (int r = rShift; r < rShift+3; r++) {
                    for (int c = cLower; c < cLower + 3; c++) {
                        if (cellSolutions.get(r).get(c).contains(i)) {
                            pointingPairsList.add(i);
                            pointingPairsRow.add(r);
                        }
                    }
                }

                // check if 2 instances of the same integer on same row
                if (pointingPairsList.size() == 2 && (pointingPairsRow.get(0).equals(pointingPairsRow.get(1)))) {

                    int row = pointingPairsRow.get(0);

                    for (int col=0; col<9; col++) {
                        if ((cellSolutions.get(row).get(col).contains(i) && col < cLower)
                                ||
                                (cellSolutions.get(row).get(col).contains(i) && col > cUpper)) {
                            int val = i;
                            cellSolutions.get(row).get(col).removeIf(n -> (n == val));
                        }
                    }
                }
            }
        }

        for (int r=0; r<arr.length; r++) {
            for (int c=0; c<arr.length; c++) {
                if (cellSolutions.get(r).get(c).size() == 1) {
                    int j = Integer.parseInt(String.valueOf(cellSolutions.get(r).get(c).get(0)));
                    arr[r][c] = Character.forDigit(j,10);
                    printGrid(arr);
                    int f = checkFilledGrid(arr);
                    System.out.println("Filled cells: " +f);
                    System.out.println("Pointing pairs row\n");
                }
            }
        }
    }

    public static void pointingPairCOLUMN(ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        // loop to identify pointing pairs COLUMN, and remove instances of the same integer from the rest of the column
        for (int sqIndex = 1; sqIndex <= 9; sqIndex++) {
            int rLower = getSqIndex(sqIndex)[0];
            int cShift = getSqIndex(sqIndex)[1];
            int rUpper = rLower + 2;

            for (int i=1; i<=9; i++) {

                ArrayList<Integer> pointingPairsList = new ArrayList<>();
                ArrayList<Integer> pointingPairsCol = new ArrayList<>();

                for (int r = rLower; r < rLower+3; r++) {
                    for (int c = cShift; c < cShift + 3; c++) {
                        if (cellSolutions.get(r).get(c).contains(i)) {
                            pointingPairsList.add(i);
                            pointingPairsCol.add(c);
                        }
                    }
                }

                // check if 2 instances of the same integer on same column
                if (pointingPairsList.size() == 2 && (pointingPairsCol.get(0).equals(pointingPairsCol.get(1)))) {

                    int col = pointingPairsCol.get(0);

                    for (int row=0; row<9; row++) {
                        if ((cellSolutions.get(row).get(col).contains(i) && row < rLower)
                                ||
                                (cellSolutions.get(row).get(col).contains(i) && row > rUpper)) {
                            int val = i;
                            cellSolutions.get(row).get(col).removeIf(n -> (n == val));
                        }
                    }
                }
            }
        }

        for (int r=0; r<arr.length; r++) {
            for (int c=0; c<arr.length; c++) {
                if (cellSolutions.get(r).get(c).size() == 1) {
                    int j = Integer.parseInt(String.valueOf(cellSolutions.get(r).get(c).get(0)));
                    arr[r][c] = Character.forDigit(j,10);
                    printGrid(arr);
                    int f = checkFilledGrid(arr);
                    System.out.println("Filled cells: " +f);
                    System.out.println("Pointing pairs column\n");
                }
            }
        }
    }

    public static void pointingPairSQ(ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {

    }

    public static void hiddenSingleROW (ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        cellSolutionsRescanALL(cellSolutions, arr);

        for (int i=1; i<=9; i++) {

            for (int r=0; r<9; r++) {

                ArrayList<Integer> hiddenSingleList = new ArrayList<>();
                ArrayList<Integer> hiddenSingleCol = new ArrayList<>();

                for (int c=0; c<9; c++) {
                    if (cellSolutions.get(r).get(c).contains(i)) {
                        hiddenSingleList.add(i);
                        hiddenSingleCol.add(c);
                    }
                }

                if (hiddenSingleList.size() == 1) {

                    arr[r][hiddenSingleCol.get(0)] = Character.forDigit(hiddenSingleList.get(0),10);

                    printGrid(arr);
                    int f = checkFilledGrid(arr);
                    System.out.println("Filled cells: " +f);
                    System.out.println("Hidden Single Row\n");

                }

                cellSolutionsRescanALL(cellSolutions, arr);
            }
        }
    }

    public static void hiddenSingleCOL (ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        cellSolutionsRescanALL(cellSolutions, arr);

        for (int i=1; i<=9; i++) {

            for (int c=0; c<9; c++) {

                ArrayList<Integer> hiddenSingleList = new ArrayList<>();
                ArrayList<Integer> hiddenSingleRow = new ArrayList<>();

                for (int r=0; r<9; r++) {
                    if (cellSolutions.get(r).get(c).contains(i)) {
                        hiddenSingleList.add(i);
                        hiddenSingleRow.add(r);
                    }
                }

                if (hiddenSingleList.size() == 1) {

                    arr[hiddenSingleRow.get(0)][c] = Character.forDigit(hiddenSingleList.get(0),10);

                    printGrid(arr);
                    int f = checkFilledGrid(arr);
                    System.out.println("Filled cells: " +f);
                    System.out.println("Hidden Single Row\n");

                }

                cellSolutionsRescanALL(cellSolutions, arr);
            }
        }
    }

    public static void hiddenPairROW (ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {

        for (int i1=1; i1<=8; i1++) {
            for (int i2 = i1+1; i2<=9; i2++) {

                int hiddenPairCellsWithoutCandidates = 0;

                for (int r=0; r<arr.length; r++) {

                    ArrayList<Integer> hiddenPairList = new ArrayList<>();
                    hiddenPairCellsWithoutCandidates = 0;

                    for (int c = 0; c < arr.length; c++) {

                        if (cellSolutions.get(r).get(c).contains(i1) && cellSolutions.get(r).get(c).contains(i2)) {
                            hiddenPairList.add(r);
                            hiddenPairList.add(c);
                        }

                        if (!cellSolutions.get(r).get(c).contains(i1) && !cellSolutions.get(r).get(c).contains(i2)) {
                            hiddenPairCellsWithoutCandidates++;
                        }
                    }

                        // hidden pair found; remove all other integers from the pair of cells
                        if (hiddenPairList.size() == 4 && hiddenPairCellsWithoutCandidates == 7) {

                            int val1 = i1;
                            int val2 = i2;

                            cellSolutions.get(hiddenPairList.get(0)).get((hiddenPairList.get(1))).removeIf(n -> (n != val1 && n != val2));
                            cellSolutions.get(hiddenPairList.get(2)).get((hiddenPairList.get(3))).removeIf(n -> (n != val1 && n != val2));
                        }
                        cellSolutionsRescanALL(cellSolutions, arr);
                }
            }
        }
    }

    public static void hiddenPairCOL (ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        for (int i1=1; i1<=8; i1++) {
            for (int i2 = i1+1; i2<=9; i2++) {

                int hiddenPairCellsWithoutCandidates = 0;

                for (int c = 0; c < arr.length; c++) {

                    ArrayList<Integer> hiddenPairList = new ArrayList<>();
                    hiddenPairCellsWithoutCandidates = 0;

                    for (int r = 0; r < arr.length; r++) {

                        if (cellSolutions.get(r).get(c).contains(i1) && cellSolutions.get(r).get(c).contains(i2)) {
                            hiddenPairList.add(r);
                            hiddenPairList.add(c);
                        }

                        if (!cellSolutions.get(r).get(c).contains(i1) && !cellSolutions.get(r).get(c).contains(i2)) {
                            hiddenPairCellsWithoutCandidates++;
                        }
                    }

                    // hidden pair found; remove all other integers from the pair of cells
                    if (hiddenPairList.size() == 4 && hiddenPairCellsWithoutCandidates == 7) {

                        int val1 = i1;
                        int val2 = i2;

                        cellSolutions.get(hiddenPairList.get(0)).get((hiddenPairList.get(1))).removeIf(n -> (n != val1 && n != val2));
                        cellSolutions.get(hiddenPairList.get(2)).get((hiddenPairList.get(3))).removeIf(n -> (n != val1 && n != val2));
                    }
                    cellSolutionsRescanALL(cellSolutions, arr);
                }
            }
        }
    }

    public static void hiddenPairSQ (ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {

        for (int sq=1; sq<=9; sq++) {
            int r = getSqIndex(sq)[0];
            int c = getSqIndex(sq)[1];

            for (int i1=1; i1<=8; i1++) {
                for (int i2 = i1+1; i2<=9; i2++) {

                    ArrayList<Integer> hiddenPairList = new ArrayList<>();
                    int hiddenPairSqsWithoutCandidates = 0;

                    for (int row = r; row < r+3; row++) {
                        for (int col = c; col < c+3; col++) {

                            if (cellSolutions.get(row).get(col).contains(i1)
                                    && cellSolutions.get(row).get(col).contains(i2)) {
                                hiddenPairList.add(row);
                                hiddenPairList.add(col);
                            }

                            if (!cellSolutions.get(row).get(col).contains(i1)
                                    && !cellSolutions.get(row).get(col).contains(i2)) {
                                hiddenPairSqsWithoutCandidates++;
                            }

                        }
                    }

                    // hidden pair found; remove all other integers from the pair of cells
                    if (hiddenPairList.size() == 4 && hiddenPairSqsWithoutCandidates == 7) {

                        int val1 = i1;
                        int val2 = i2;

                            cellSolutions.get(hiddenPairList.get(0)).get(hiddenPairList.get(1)).removeIf(n -> (n != val1 && n != val2));
                            cellSolutions.get(hiddenPairList.get(2)).get(hiddenPairList.get(3)).removeIf(n -> (n != val1 && n != val2));
                        }
                    }
                    cellSolutionsRescanALL(cellSolutions, arr);
                }
        }
    }

    public static void nakedPairROW (ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        for (int i=1; i<=9; i++) {

            for (int r=0; r<arr.length; r++) {

                ArrayList<Integer> nakedPairList = new ArrayList<>();
                ArrayList<Integer> nakedPairColumns = new ArrayList<>();

                for (int c=0; c<arr.length; c++) {
                    if (cellSolutions.get(r).get(c).contains(i) && cellSolutions.get(r).get(c).size() == 2) {
                        nakedPairList.add(cellSolutions.get(r).get(c).get(0));
                        nakedPairList.add(cellSolutions.get(r).get(c).get(1));

                        nakedPairColumns.add(c);
                    }
                }

                if (nakedPairList.size() == 4
                        && (nakedPairList.get(0).equals(nakedPairList.get(2)))
                        && (nakedPairList.get(1).equals(nakedPairList.get(3))) ) {

                    for (int col=0; col<arr.length; col++) {
                        if (col != nakedPairColumns.get(0) && col != nakedPairColumns.get(1)) {

                            int i1 = nakedPairList.get(0);
                            int i2 = nakedPairList.get(1);

                            cellSolutions.get(r).get(col).removeIf(n -> (n == i1 || n == i2));
                        }
                    }
                }
            }
            cellSolutionsRescanALL(cellSolutions, arr);
        }
    }

    public static void nakedPairCOL (ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        for (int i=1; i<=9; i++) {

            for (int c=0; c<arr.length; c++) {

                ArrayList<Integer> nakedPairList = new ArrayList<>();
                ArrayList<Integer> nakedPairRows = new ArrayList<>();

                for (int r=0; r<arr.length; r++) {
                    if (cellSolutions.get(r).get(c).contains(i) && cellSolutions.get(r).get(c).size() == 2) {

                        nakedPairList.add(cellSolutions.get(r).get(c).get(0));
                        nakedPairList.add(cellSolutions.get(r).get(c).get(1));

                        nakedPairRows.add(r);
                    }
                }

                if (nakedPairList.size() == 4
                        && (nakedPairList.get(0).equals(nakedPairList.get(2)))
                        && (nakedPairList.get(1).equals(nakedPairList.get(3))) ) {
                    for (int row=0; row<arr.length; row++) {
                        if (row != nakedPairRows.get(0) && row != nakedPairRows.get(1)) {

                            int i1 = nakedPairList.get(0);
                            int i2 = nakedPairList.get(1);

                            cellSolutions.get(row).get(c).removeIf(n -> (n == i1 || n == i2));
                        }
                    }
                }
            }
            cellSolutionsRescanALL(cellSolutions, arr);
        }
    }

    public static void cellSolutionsRemovePopulated(ArrayList<ArrayList<ArrayList<Integer>>> cellSolutions, char[][] arr) {
        for (int r=0; r<arr.length; r++) {
            for (int c=0; c<arr.length; c++) {
                if (!isEmptyCell(arr,r,c)) {
                    cellSolutions.get(r).get(c).clear();
                }
            }
        }
    }

    public static int[] remainingCellsSq(char[][] arr, int sqIndex) {
        int r = getSqIndex(sqIndex)[0];
        int c = getSqIndex(sqIndex)[1];

        String s = "";

        for (int row = r; row < r + 3; row++) {
            for (int col = c; col < c + 3; col++) {
                if (isEmptyCell(arr, row, col)) {
                    s = s + row + col;
                }
            }
        }

        int[] remainingCells = new int[s.length()];

        for (int i = 0; i < remainingCells.length; i++) {
            remainingCells[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }

        return remainingCells;
    }

    public static int[] validCellsSq(char[][] arr, int[] remainingCellsSq, int index, int sqIndex) {
        String s = "";

        for (int i = 0; i < remainingCellsSq.length; i += 2) {
            int flag = 1;

            int row = remainingCellsSq[i];
            int col = remainingCellsSq[i + 1];

            for (int r = 0; r < arr.length; r++) {
                if (Character.getNumericValue(arr[r][col]) == index) {
                    flag = 0;
                }
            }

            for (int c = 0; c < arr.length; c++) {
                if (Character.getNumericValue(arr[row][c]) == index) {
                    flag = 0;
                }
            }

            if (flag == 1) {
                s = s + row + col;
            }
        }

        int[] validCellsSq = new int[s.length()];

        for (int i = 0; i < validCellsSq.length; i++) {
            validCellsSq[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }

        return validCellsSq;
    }

    public static int[] squareSolver(char[][] arr, int candidate, int sqIndex) {
        int[] rCells = remainingCellsSq(arr, sqIndex);
        int[] rInts = missingIntegersSq(arr, sqIndex);

        String s = "";
        String t = "";

        // scroll through 'remaining cells' array and return row/column index pair
        for (int i = 0; i < rCells.length; i += 2) {
            int row = rCells[i];
            int col = rCells[i + 1];

            s = "";

            for (int j = 0; j < rInts.length; j++) {
                int rFlag = 1;
                int cFlag = 1;

                for (int cChk = 0; cChk < arr.length; cChk++) {
                    if (Character.getNumericValue(arr[row][cChk]) == rInts[j]) {
                        rFlag = 0;
                    }
                }

                for (int rChk = 0; rChk < arr.length; rChk++) {
                    if (Character.getNumericValue(arr[rChk][col]) == rInts[j]) {
                        cFlag = 0;
                    }
                }

                if (rFlag == 1 && cFlag == 1) {
                    s = s + rInts[j] + row + col;
                }
            }
            if (s.length() == 3) {
                t = t + s;
            }

        }
        int[] squareSolver = new int[t.length()];

        for (int i = 0; i < squareSolver.length; i++) {
            squareSolver[i] = Integer.parseInt(String.valueOf(t.charAt(i)));
        }

        return squareSolver;
    }

    public static int[] missingIntegersRow(char[][] arr, int row) {
        String s = "";

        for (int col = 0; col < arr.length; col++) {
            if (arr[row][col] != ' ') {
                s = s + arr[row][col];
            }
        }

        String t = "";

        for (int i = 1; i <= 9; i++) {
            int flag = 0;
            for (int j = 0; j < s.length(); j++) {
                if (Integer.parseInt(String.valueOf(s.charAt(j))) == i) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                t = t + i;
            }
        }

        int[] rIntsRow = new int[t.length()];

        for (int i = 0; i < rIntsRow.length; i++) {
            rIntsRow[i] = Integer.parseInt(String.valueOf(t.charAt(i)));
        }

        return rIntsRow;
    }

    public static int[] remainingCellsRow(char[][] arr, int row) {
        String s = "";

        for (int col = 0; col < arr.length; col++) {
            if (isEmptyCell(arr, row, col)) {
                s = s + row + col;
            }
        }

        int[] remainingCellsRow = new int[s.length()];

        for (int i = 0; i < remainingCellsRow.length; i++) {
            remainingCellsRow[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }

        return remainingCellsRow;
    }

    public static int[] validCellsRow(char[][] arr, int index, int row) {

        int[] mIntsRow = missingIntegersRow(arr, row);
        int[] remCellsRow = remainingCellsRow(arr, row);

        String s = "";

        int sq1, sq2, sq3;

        if (row / 3 == 0) {
            sq1 = 1;
            sq2 = 2;
            sq3 = 3;
        } else if (row / 3 == 1) {
            sq1 = 4;
            sq2 = 5;
            sq3 = 6;
        } else {
            sq1 = 7;
            sq2 = 8;
            sq3 = 9;
        }

        int[] filledIntsSq1 = filledIntegersSq(arr, sq1);
        int[] filledIntsSq2 = filledIntegersSq(arr, sq2);
        int[] filledIntsSq3 = filledIntegersSq(arr, sq3);


        int flag = 0;
        for (int i = 0; i < mIntsRow.length; i++) {
            if (mIntsRow[i] == index) {
                flag = 1;
            }
        }

        if (flag == 1) {
            for (int i = 0; i < remCellsRow.length; i += 2) {
                int col = remCellsRow[i + 1];
                int flag2 = 1;

                for (int r = 0; r < arr.length; r++) {
                    if (Character.getNumericValue(arr[r][col]) == index) {
                        flag2 = 0;
                    }
                }
                if (flag2 == 1) {
                    s = s + row + col;
                }
            }
        }

        int[] validCellsRow = new int[s.length()];

        if (validCellsRow.length > 0) {
            System.out.println("Valid cells for integer " + index + " in row " + row + ":");
            for (int i = 0; i < validCellsRow.length; i++) {
                validCellsRow[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
                System.out.print(validCellsRow[i] + " ");
            }
            System.out.println();
        }


        return validCellsRow;
    }

    public static int[] rowSolver(char[][] arr, int index, int row) {
        int[] rCells = remainingCellsRow(arr, row);
        int[] mIntsRow = missingIntegersRow(arr, row);

        String s = "";

        for (int i = 0; i < mIntsRow.length; i++) {
            // check the array of currently missing integers from the given row.
            // If one of the integers matches the current index, execute the rowSolver

            if (mIntsRow[i] == index) {
                for (int j = 0; j < rCells.length; j += 2) {
                    int r = rCells[j];
                    int c = rCells[j + 1];

                    int flag = 1;

                    for (int k = 0; k < arr.length; k++) {
                        if (Character.getNumericValue(arr[k][c]) == index) {
                            flag = 0;
                        }
                    }

                    if (flag == 1) {
                        s = s + r + c;
                    }
                }
            }
        }
        int[] rowSolver = new int[s.length()];

        for (int x = 0; x < rowSolver.length; x++) {
            rowSolver[x] = Integer.parseInt(String.valueOf(s.charAt(x)));
        }

        return rowSolver;
    }

    public static int[] missingIntegersCol(char[][] arr, int col) {
        String s = "";

        for (int row = 0; row < arr.length; row++) {
            if (arr[row][col] != ' ') {
                s = s + arr[row][col];
            }
        }

        String t = "";

        for (int i = 1; i <= 9; i++) {
            int flag = 0;
            for (int j = 0; j < s.length(); j++) {
                if (Integer.parseInt(String.valueOf(s.charAt(j))) == i) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                t = t + i;
            }
        }

        int[] rIntsCol = new int[t.length()];

        for (int i = 0; i < rIntsCol.length; i++) {
            rIntsCol[i] = Integer.parseInt(String.valueOf(t.charAt(i)));
        }

        return rIntsCol;
    }

    public static int[] remainingCellsCol(char[][] arr, int col) {
        String s = "";

        for (int row = 0; row < arr.length; row++) {
            if (isEmptyCell(arr, row, col)) {
                s = s + row + col;
            }
        }

        int[] remainingCellsCol = new int[s.length()];

        for (int i = 0; i < remainingCellsCol.length; i++) {
            remainingCellsCol[i] = Integer.parseInt(String.valueOf(s.charAt(i)));
        }

        return remainingCellsCol;
    }

    public static int[] colSolver(char[][] arr, int index, int col) {
        int[] rCells = remainingCellsCol(arr, col);
        int[] mIntsCol = missingIntegersCol(arr, col);

        String s = "";

        for (int i = 0; i < mIntsCol.length; i++) {
            // check the array of currently missing integers from the given column.
            // If one of the integers matches the current index, execute the columnSolver

            if (mIntsCol[i] == index) {
                for (int j = 0; j < rCells.length; j += 2) {
                    int r = rCells[j];
                    int c = rCells[j + 1];

                    int flag = 1;

                    for (int k = 0; k < arr.length; k++) {
                        if (Character.getNumericValue(arr[r][k]) == index) {
                            flag = 0;
                        }
                    }

                    if (flag == 1) {
                        s = s + r + c;
                    }
                }
            }
        }
        int[] colSolver = new int[s.length()];

        for (int x = 0; x < colSolver.length; x++) {
            colSolver[x] = Integer.parseInt(String.valueOf(s.charAt(x)));
        }

        return colSolver;
    }

    public static int[] rowSqSolver(char[][] arr, int index, int sqIndex) {
        int sq1, sq2, sq3;

        String validCellsCombined = "";

        if (sqIndex / 3 == 0) {
            sq1 = 1;
            sq2 = 2;
            sq3 = 3;
        } else if (sqIndex / 3 == 1) {
            sq1 = 4;
            sq2 = 5;
            sq3 = 6;
        } else {
            sq1 = 7;
            sq2 = 8;
            sq3 = 9;
        }

        if (isValidSq(arr, index, sq1)) {
            int[] rCellsSq1 = remainingCellsSq(arr, sq1);
            int[] validCellsSq1 = validCellsSq(arr, rCellsSq1, index, sq1);

            for (int i = 0; i < validCellsSq1.length; i++) {
                validCellsCombined = validCellsCombined + validCellsSq1[i];
            }
        }

        if (isValidSq(arr, index, sq2)) {
            int[] rCellsSq2 = remainingCellsSq(arr, sq2);
            int[] validCellsSq2 = validCellsSq(arr, rCellsSq2, index, sq2);

            for (int i = 0; i < validCellsSq2.length; i++) {
                validCellsCombined = validCellsCombined + validCellsSq2[i];
            }
        }

        if (isValidSq(arr, index, sq3)) {
            int[] rCellsSq3 = remainingCellsSq(arr, sq3);
            int[] validCellsSq3 = validCellsSq(arr, rCellsSq3, index, sq3);

            for (int i = 0; i < validCellsSq3.length; i++) {
                validCellsCombined = validCellsCombined + validCellsSq3[i];
            }
        }

        // run valid row checker to eliminate duplicate rows

        String rss = "";

        int[] vcc = new int[validCellsCombined.length()];

        for (int i = 0; i < vcc.length; i++) {
            vcc[i] = Integer.parseInt(String.valueOf(validCellsCombined.charAt(i)));
        }

        for (int i = 0; i < vcc.length; i += 2) {
            int count = 0;

            for (int j = 0; j < vcc.length; j += 2) {
                if (vcc[j] == vcc[i]) {
                    count++;
                }
            }
            if (count == 1) {
                rss = rss + vcc[i] + vcc[i + 1];
            }
        }

        int[] rowSqSolver = new int[rss.length()];

        for (int i = 0; i < rowSqSolver.length; i++) {
            rowSqSolver[i] = Integer.parseInt(String.valueOf(rss.charAt(i)));
        }

        return rowSqSolver;
    }

    public static int[] colSqSolver(char[][] arr, int index, int sqIndex) {
        int sq1, sq2, sq3;

        String validCellsCombined = "";

        if (sqIndex / 3 == 0) {
            sq1 = 1;
            sq2 = 4;
            sq3 = 7;
        } else if (sqIndex / 3 == 1) {
            sq1 = 2;
            sq2 = 5;
            sq3 = 8;
        } else {
            sq1 = 3;
            sq2 = 6;
            sq3 = 9;
        }

        if (isValidSq(arr, index, sq1)) {
            int[] rCellsSq1 = remainingCellsSq(arr, sq1);
            int[] validCellsSq1 = validCellsSq(arr, rCellsSq1, index, sq1);

            for (int i = 0; i < validCellsSq1.length; i++) {
                validCellsCombined = validCellsCombined + validCellsSq1[i];
            }
        }

        if (isValidSq(arr, index, sq2)) {
            int[] rCellsSq2 = remainingCellsSq(arr, sq2);
            int[] validCellsSq2 = validCellsSq(arr, rCellsSq2, index, sq2);

            for (int i = 0; i < validCellsSq2.length; i++) {
                validCellsCombined = validCellsCombined + validCellsSq2[i];
            }
        }

        if (isValidSq(arr, index, sq3)) {
            int[] rCellsSq3 = remainingCellsSq(arr, sq3);
            int[] validCellsSq3 = validCellsSq(arr, rCellsSq3, index, sq3);

            for (int i = 0; i < validCellsSq3.length; i++) {
                validCellsCombined = validCellsCombined + validCellsSq3[i];
            }
        }

        // run valid row checker to eliminate duplicate columns

        String rss = "";

        int[] vcc = new int[validCellsCombined.length()];

        for (int i = 0; i < vcc.length; i++) {
            vcc[i] = Integer.parseInt(String.valueOf(validCellsCombined.charAt(i)));
        }

        for (int i = 1; i < vcc.length; i += 2) {
            int count = 0;

            for (int j = 1; j < vcc.length; j += 2) {
                if (vcc[j] == vcc[i]) {
                    count++;
                }
            }
            if (count == 1) {
                rss = rss + vcc[i - 1] + vcc[i];
            }
        }

        int[] colSqSolver = new int[rss.length()];

        for (int i = 0; i < colSqSolver.length; i++) {
            colSqSolver[i] = Integer.parseInt(String.valueOf(rss.charAt(i)));
        }

        return colSqSolver;
    }

    public static int[] colIsolator(char[][] arr, int index, int sqIndex) {
        // check squares 1, 2, 3 for feasible cells for integer
        int sq1, sq2, sq3;

        String sq1ValidCols = "";
        String sq2ValidCols = "";
        String sq3ValidCols = "";

        if (sqIndex / 3 == 0) {
            sq1 = 1;
            sq2 = 4;
            sq3 = 7;
        } else if (sqIndex / 3 == 1) {
            sq1 = 2;
            sq2 = 5;
            sq3 = 8;
        } else {
            sq1 = 3;
            sq2 = 6;
            sq3 = 9;
        }

        if (isValidSq(arr, index, sq1)) {
            int[] rCellsSq1 = remainingCellsSq(arr, sq1);
            int[] validCellsSq1 = validCellsSq(arr, rCellsSq1, index, sq1);

            for (int i = 1; i < validCellsSq1.length; i += 2) {
                if (isUniqueRowCol(validCellsSq1[i], sq1ValidCols)) {
                    sq1ValidCols = sq1ValidCols + validCellsSq1[i];
                }
            }
        }

        if (isValidSq(arr, index, sq2)) {
            int[] rCellsSq2 = remainingCellsSq(arr, sq2);
            int[] validCellsSq2 = validCellsSq(arr, rCellsSq2, index, sq2);

            for (int i = 1; i < validCellsSq2.length; i += 2) {
                if (isUniqueRowCol(validCellsSq2[i], sq2ValidCols)) {
                    sq2ValidCols = sq2ValidCols + validCellsSq2[i];
                }
            }
        }

        if (isValidSq(arr, index, sq3)) {
            int[] rCellsSq3 = remainingCellsSq(arr, sq3);
            int[] validCellsSq3 = validCellsSq(arr, rCellsSq3, index, sq3);

            for (int i = 1; i < validCellsSq3.length; i += 2) {
                if (isUniqueRowCol(validCellsSq3[i], sq3ValidCols)) {
                    sq3ValidCols = sq3ValidCols + validCellsSq3[i];
                }
            }
        }

        int[] colIsolator = rowColDownTo1(sq1ValidCols, sq2ValidCols, sq3ValidCols);

        String solvedColIsolator = "";

        if (colIsolator.length == 3) {
            String sq1FilteredCols = "";
            String sq2FilteredCols = "";
            String sq3FilteredCols = "";

            int[] s1 = validCellsSq(arr, remainingCellsSq(arr, sq1), index, sq1);
            int[] s2 = validCellsSq(arr, remainingCellsSq(arr, sq2), index, sq2);
            int[] s3 = validCellsSq(arr, remainingCellsSq(arr, sq3), index, sq3);


            for (int i = 1; i < s1.length; i += 2) {
                if (s1[i] == colIsolator[0]) {
                    sq1FilteredCols = sq1FilteredCols + s1[i - 1] + s1[i];
                }
            }

            for (int i = 1; i < s2.length; i += 2) {
                if (s2[i] == colIsolator[1]) {
                    sq2FilteredCols = sq2FilteredCols + s2[i - 1] + s2[i];
                }
            }

            for (int i = 1; i < s3.length; i += 2) {
                if (s3[i] == colIsolator[2]) {
                    sq3FilteredCols = sq3FilteredCols + s3[i - 1] + s3[i];
                }
            }

            if (sq1FilteredCols.length() == 2) {
                solvedColIsolator = solvedColIsolator + sq1FilteredCols;
            }

            if (sq2FilteredCols.length() == 2) {
                solvedColIsolator = solvedColIsolator + sq2FilteredCols;
            }

            if (sq3FilteredCols.length() == 2) {
                solvedColIsolator = solvedColIsolator + sq3FilteredCols;
            }
        } else {
            solvedColIsolator = "";
        }


        int[] solution = new int[solvedColIsolator.length()];

        for (int i = 0; i < solution.length; i++) {
            solution[i] = Integer.parseInt(String.valueOf(solvedColIsolator.charAt(i)));
        }

        return solution;
    }

    public static boolean isUniqueRowCol(int RowCol, String validRowsCols) {
        for (int i = 0; i < validRowsCols.length(); i++) {
            if (RowCol == Integer.parseInt(String.valueOf(validRowsCols.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    public static int[] rowColDownTo1(String sq1, String sq2, String sq3) {
        String sq1_loop1 = "";
        String sq2_loop1 = "";
        String sq3_loop1 = "";

        String sq1_loop2 = "";
        String sq2_loop2 = "";
        String sq3_loop2 = "";

        String s1 = "";
        String s2 = "";
        String s3 = "";

        String combined = "";

        if (sq1.length() == 1) {
            s1 = sq1;

            for (int i = 0; i < sq2.length(); i++) {
                if (sq2.charAt(i) != sq1.charAt(0)) {
                    sq2_loop1 = sq2_loop1 + sq2.charAt(i);
                }
            }

            for (int j = 0; j < sq3.length(); j++) {
                if (sq3.charAt(j) != sq1.charAt(0)) {
                    sq3_loop1 = sq3_loop1 + sq3.charAt(j);
                }
            }

            if (sq2_loop1.length() == 1) {
                s2 = sq2_loop1;

                for (int i = 0; i < sq3_loop1.length(); i++) {
                    if (sq3_loop1.charAt(i) != sq2_loop1.charAt(0)) {
                        sq3_loop2 = sq3_loop2 + sq3_loop1.charAt(i);
                    }
                }

                s3 = sq3_loop2;
            } else if (sq3_loop1.length() == 1) {
                s3 = sq3_loop1;

                for (int j = 0; j < sq2_loop1.length(); j++) {
                    if (sq2_loop1.charAt(j) != sq3_loop1.charAt(0)) {
                        sq2_loop2 = sq2_loop2 + sq2_loop1.charAt(j);
                    }
                }

                s2 = sq2_loop2;
            }
        } else if (sq2.length() == 1) {
            s2 = sq2;

            for (int i = 0; i < sq1.length(); i++) {
                if (sq1.charAt(i) != sq2.charAt(0)) {
                    sq1_loop1 = sq1_loop1 + sq1.charAt(i);
                }
            }

            for (int j = 0; j < sq3.length(); j++) {
                if (sq3.charAt(j) != sq2.charAt(0)) {
                    sq3_loop1 = sq3_loop1 + sq3.charAt(j);
                }
            }

            if (sq1_loop1.length() == 1) {
                s1 = sq1_loop1;

                for (int i = 0; i < sq3_loop1.length(); i++) {
                    if (sq3_loop1.charAt(i) != sq1_loop1.charAt(0)) {
                        sq3_loop2 = sq3_loop2 + sq3_loop1.charAt(i);
                    }
                }

                s3 = sq3_loop2;
            } else if (sq3_loop1.length() == 1) {
                s3 = sq3_loop1;

                for (int j = 0; j < sq1_loop1.length(); j++) {
                    if (sq1_loop1.charAt(j) != sq3_loop1.charAt(0)) {
                        sq1_loop2 = sq1_loop2 + sq1_loop1.charAt(j);
                    }
                }

                s1 = sq1_loop2;
            }
        } else if (sq3.length() == 1) {
            s3 = sq3;

            for (int i = 0; i < sq1.length(); i++) {
                if (sq1.charAt(i) != sq3.charAt(0)) {
                    sq1_loop1 = sq1_loop1 + sq1.charAt(i);
                }
            }

            for (int j = 0; j < sq2.length(); j++) {
                if (sq2.charAt(j) != sq3.charAt(0)) {
                    sq2_loop1 = sq2_loop1 + sq2.charAt(j);
                }
            }

            if (sq1_loop1.length() == 1) {
                s1 = sq1_loop1;

                for (int i = 0; i < sq2_loop1.length(); i++) {
                    if (sq2_loop1.charAt(i) != sq1_loop1.charAt(0)) {
                        sq2_loop2 = sq2_loop2 + sq2_loop1.charAt(i);
                    }
                }

                s2 = sq2_loop2;
            } else if (sq2_loop1.length() == 1) {
                s2 = sq2_loop1;

                for (int j = 0; j < sq1_loop1.length(); j++) {
                    if (sq1_loop1.charAt(j) != sq2_loop1.charAt(0)) {
                        sq1_loop2 = sq1_loop2 + sq1_loop1.charAt(j);
                    }
                }

                s1 = sq1_loop2;
            }
        }

        combined = s1 + s2 + s3;

        int[] rowColDownTo1 = new int[combined.length()];

        for (int i = 0; i < combined.length(); i++) {
            rowColDownTo1[i] = Integer.parseInt(String.valueOf(combined.charAt(i)));
        }

        return rowColDownTo1;
    }

    public static int[] rowIsolator(char[][] arr, int index, int sqIndex) {
        // check squares 1, 2, 3 for feasible cells for integer
        int sq1, sq2, sq3;

        String sq1ValidRows = "";
        String sq2ValidRows = "";
        String sq3ValidRows = "";

        if (sqIndex / 3 == 0) {
            sq1 = 1;
            sq2 = 2;
            sq3 = 3;
        } else if (sqIndex / 3 == 1) {
            sq1 = 4;
            sq2 = 5;
            sq3 = 6;
        } else {
            sq1 = 7;
            sq2 = 8;
            sq3 = 9;
        }

        if (isValidSq(arr, index, sq1)) {
            int[] rCellsSq1 = remainingCellsSq(arr, sq1);
            int[] validCellsSq1 = validCellsSq(arr, rCellsSq1, index, sq1);

            for (int i = 0; i < validCellsSq1.length; i += 2) {
                if (isUniqueRowCol(validCellsSq1[i], sq1ValidRows)) {
                    sq1ValidRows = sq1ValidRows + validCellsSq1[i];
                }
            }
        }

        if (isValidSq(arr, index, sq2)) {
            int[] rCellsSq2 = remainingCellsSq(arr, sq2);
            int[] validCellsSq2 = validCellsSq(arr, rCellsSq2, index, sq2);

            for (int i = 0; i < validCellsSq2.length; i += 2) {
                if (isUniqueRowCol(validCellsSq2[i], sq2ValidRows)) {
                    sq2ValidRows = sq2ValidRows + validCellsSq2[i];
                }
            }
        }

        if (isValidSq(arr, index, sq3)) {
            int[] rCellsSq3 = remainingCellsSq(arr, sq3);
            int[] validCellsSq3 = validCellsSq(arr, rCellsSq3, index, sq3);

            for (int i = 0; i < validCellsSq3.length; i += 2) {
                if (isUniqueRowCol(validCellsSq3[i], sq3ValidRows)) {
                    sq3ValidRows = sq3ValidRows + validCellsSq3[i];
                }
            }
        }

        int[] rowIsolator = rowColDownTo1(sq1ValidRows, sq2ValidRows, sq3ValidRows);

        String solvedRowIsolator = "";

        if (rowIsolator.length == 3) {
            String sq1FilteredRows = "";
            String sq2FilteredRows = "";
            String sq3FilteredRows = "";

            int[] s1 = validCellsSq(arr, remainingCellsSq(arr, sq1), index, sq1);
            int[] s2 = validCellsSq(arr, remainingCellsSq(arr, sq2), index, sq2);
            int[] s3 = validCellsSq(arr, remainingCellsSq(arr, sq3), index, sq3);


            for (int i = 0; i < s1.length; i += 2) {
                if (s1[i] == rowIsolator[0]) {
                    sq1FilteredRows = sq1FilteredRows + s1[i] + s1[i + 1];
                }
            }

            for (int i = 0; i < s2.length; i += 2) {
                if (s2[i] == rowIsolator[1]) {
                    sq2FilteredRows = sq2FilteredRows + s2[i] + s2[i + 1];
                }
            }

            for (int i = 0; i < s3.length; i += 2) {
                if (s3[i] == rowIsolator[2]) {
                    sq3FilteredRows = sq3FilteredRows + s3[i] + s3[i + 1];
                }
            }

            if (sq1FilteredRows.length() == 2) {
                solvedRowIsolator = solvedRowIsolator + sq1FilteredRows;
            }

            if (sq2FilteredRows.length() == 2) {
                solvedRowIsolator = solvedRowIsolator + sq2FilteredRows;
            }

            if (sq3FilteredRows.length() == 2) {
                solvedRowIsolator = solvedRowIsolator + sq3FilteredRows;
            }
        } else {
            solvedRowIsolator = "";
        }

        int[] solution = new int[solvedRowIsolator.length()];

        for (int i = 0; i < solution.length; i++) {
            solution[i] = Integer.parseInt(String.valueOf(solvedRowIsolator.charAt(i)));
        }

        return solution;
    }

    private static int[] twinIsolator(char[][] arr, int index, int sqIndex)
    {
        int sq0, sqR1, sqR2, sqC1, sqC2;

        sq0 = sqIndex;

        String s = "";

        if (sq0 == 1)
            s = s + "2347";
        else if (sq0 == 2)
            s = s + "1358";
        else if (sq0 == 3)
            s = s + "1269";
        else if (sq0 == 4)
            s = s + "1567";
        else if (sq0 == 5)
            s = s + "2468";
        else if (sq0 == 6)
            s = s + "3459";
        else if (sq0 == 7)
            s = s + "1489";
        else if (sq0 == 8)
            s = s + "2579";
        else
            s = s + "3678";

        int sq1 = Integer.parseInt(String.valueOf(s.charAt(0)));
        int sq2 = Integer.parseInt(String.valueOf(s.charAt(1)));
        int sq3 = Integer.parseInt(String.valueOf(s.charAt(2)));
        int sq4 = Integer.parseInt(String.valueOf(s.charAt(3)));

        String sq0ValidCells = "";
        String sq1ValidCells = "";
        String sq2ValidCells = "";
        String sq3ValidCells = "";
        String sq4ValidCells = "";

        String comparisonCells = "";

        if (isValidSq(arr, index, sq0)) {
            int[] rCellsSq1 = remainingCellsSq(arr, sq0);
            int[] validCellsSq0 = validCellsSq(arr, rCellsSq1, index, sq0);

            for (int value : validCellsSq0) {
                sq0ValidCells = sq0ValidCells + value;
            }
        }

        if (isValidSq(arr, index, sq1)) {
            int[] rCellsSq1 = remainingCellsSq(arr, sq1);
            int[] validCellsSq1 = validCellsSq(arr, rCellsSq1, index, sq1);

            for (int value : validCellsSq1) {
                sq1ValidCells = sq1ValidCells + value;
            }
        }

        if (isValidSq(arr, index, sq2)) {
            int[] rCellsSq2 = remainingCellsSq(arr, sq2);
            int[] validCellsSq2 = validCellsSq(arr, rCellsSq2, index, sq2);

            for (int value : validCellsSq2) {
                sq2ValidCells = sq2ValidCells + value;
            }
        }

        if (isValidSq(arr, index, sq3)) {
            int[] rCellsSq3 = remainingCellsSq(arr, sq3);
            int[] validCellsSq3 = validCellsSq(arr, rCellsSq3, index, sq3);

            for (int value : validCellsSq3) {
                sq3ValidCells = sq3ValidCells + value;
            }
        }

        if (isValidSq(arr, index, sq4)) {
            int[] rCellsSq4 = remainingCellsSq(arr, sq4);
            int[] validCellsSq4 = validCellsSq(arr, rCellsSq4, index, sq4);

            for (int value : validCellsSq4) {
                sq4ValidCells = sq4ValidCells + value;
            }
        }

        comparisonCells = comparisonCells + sq1ValidCells + sq2ValidCells + sq3ValidCells + sq4ValidCells;

        String solutionString = "";

        for (int i=0; i<sq0ValidCells.length(); i+=2)
        {
            int flag = 1;

            for (int j=0; j<comparisonCells.length(); j+=2)
            {
                if (sq0ValidCells.charAt(i) == comparisonCells.charAt(j) || sq0ValidCells.charAt(i+1) == comparisonCells.charAt(j+1))
                {
                    flag = 0;
                }
            }
            if (flag == 1)
            {
                solutionString = solutionString + sq0ValidCells.charAt(i) + sq0ValidCells.charAt(i+1);
            }

        }

        int[] solution = new int[solutionString.length()];

        for (int i=0; i<solution.length; i++)
        {
            solution[i] = Integer.parseInt(String.valueOf(solutionString.charAt(i)));
        }

        return solution;
    }


}




