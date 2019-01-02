package codes;

import java.util.*;

public class Main {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws Exception {
		int dimension = 9;
		
		Grid board = new Grid(dimension);
		
		board.populate("Hard Sudoku.txt");
		
		System.out.println("Initial board:");
		board.display();
		
		board.solve("Hard Sudoku Solved.txt");
		
		System.out.println("Final board:");
		board.display();
		
		System.out.println(board.checkSolve("Hard Sudoku Solved.txt"));
		
	} // end main
	
} // end class Main
