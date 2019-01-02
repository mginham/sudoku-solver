package codes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Grid {

	private static Cell first;
	//@SuppressWarnings("unused")
	private static int dimension;
	
	public Grid(int dimension) {
		this.dimension = dimension;
		
		if(dimension == 1)
			setFirst(new Cell());
		else if(dimension <= 0)
			setFirst(null);
		else {
			setFirst(new Cell());
			
			Cell temp;
			Cell columnMarker = first;
			Cell rowMarker = first;
			
			int counter1 = 1;
			int counter2 = 1;
			
			for(int x = 0; x < dimension-1; x++) { // construct first row
				temp = new Cell();
				
				temp.setLeft(columnMarker);
				columnMarker.setRight(temp);
				
				columnMarker = temp;
			}
			
			for(int x = 0; x < dimension-1; x++) { // construct remaining rows
				temp = new Cell();
				
				temp.setUp(rowMarker);
				rowMarker.setDown(temp);
				
				rowMarker = temp;
				columnMarker = rowMarker;
				
				for(int y = 0; y < dimension-1; y++) {
					temp = new Cell();
					
					temp.setLeft(columnMarker);
					columnMarker.setRight(temp);
					temp.setUp(temp.getLeft().getUp().getRight());
					temp.getUp().setDown(temp);
					
					columnMarker = temp;
				}
			}
			
			temp = first;
			rowMarker = first;
			
			for(int a = 0; a < 3; a++) { // assign boxIDs
				for(int x = 0; x < 3; x++) {
					for(int y = 0; y < 3; y++) {
						for(int z = 0; z < 3; z++) {
							temp.setBoxID(counter1);
							temp = temp.getRight();
						}
						counter1++;
					}
					counter1 = counter2;
					rowMarker = rowMarker.getDown();
					temp = rowMarker;
				}
				counter2 = counter2 + 3;
				counter1 = counter2;
			}
		}
	} // end constructor Grid
	
	public static void display() {
		Cell temp = first;
		Cell rowMarker = first;
		
		while(temp != null) {
			while(temp != null) {
				if(temp.getData() > 99) {
					System.out.print(temp.getData() + " ");
				}
				else if(temp.getData() > 9 || temp.getData() < 0) {
					System.out.print(temp.getData() + "  ");
				}
				else {
					System.out.print(temp.getData() + "   ");
				}
				
				temp = temp.getRight();
			}
			
			System.out.println();
			rowMarker = rowMarker.getDown();
			temp = rowMarker;
		}
		
		System.out.println("==================================");
	} // end display
	
	public static void populate(String fileName) throws Exception { // import puzzle from text file
		File infile = new File(fileName);
		Scanner input = new Scanner(infile);
		
		Cell temp = first;
		Cell rowMarker = first;
		
		while(temp != null) {
			while(temp != null) {
				temp.setData(input.nextInt());
				
				temp = temp.getRight();
			}
			
			rowMarker = rowMarker.getDown();
			temp = rowMarker;
		}
		
		input.close();
	} // end populate
	
	public static void solve(String solution) throws Exception {
		int changes = 0;
		int allChanges = 0;
		
		int counter = 0;
		
		possibleValues(first);
		
		do {
			allChanges = 0;
			
			do { // solveMeth1 -- fill single-possibility cells
				changes = 0;
				changes = solveMeth1();
				allChanges += changes;
				
				System.out.println("Changes made (1): " + changes);
			} while(changes > 0);
			
			do { // solveMeth2 -- fill unique possibility cells by box
				changes = 0;
				changes = solveMeth2();
				allChanges += changes;
				
				System.out.println("Changes made (2): " + changes);
			} while(changes > 0);
			
			solveMeth3(); // solveMeth3 -- eliminate twin possibilities from surrounding cells
			System.out.println("(3)");
		} while(allChanges > 0);
		
		System.out.println("Board after method 3:");
		display();
		
		solveMeth4(solution); // solveMeth4 -- guess (recursive)
		System.out.println("(4)");
	} // end solve
	
	public static boolean checkSolve(String solutionFile) throws Exception {
		File solution = new File(solutionFile);
		Scanner input = new Scanner(solution);
		
		boolean solved = true;
		
		int data = 0;
		Cell temp = first;
		Cell rowMarker = first;
		
		while (temp != null) {
			while (temp != null) {
				data = input.nextInt();
				
				if(temp.getData() != data)
					solved = false;
					
				//System.out.println(temp.getData() + " " + data + " " + solved);
				
				temp = temp.getRight();
			}
			rowMarker = rowMarker.getDown();
			temp = rowMarker;
		}
		
		input.close();
		
		return solved;
	} // end checkSolve
	
	public static void fillCell(Cell cell, int value) {
		Cell temp;
		Cell rowMarker;
		
		cell.setData(value);
		
		for(int x = 0; x < 10; x++)
			cell.setPossibleVal(x, false);
		
		temp = cell;
		while(temp.getRight() != null) { // right
			temp = temp.getRight();
			temp.setPossibleVal(value, false);
		}
		
		temp = cell;
		while(temp.getLeft() != null) { // left
			temp = temp.getLeft();
			temp.setPossibleVal(value, false);
		}
		
		temp = cell;
		while(temp.getUp() != null) { // up
			temp = temp.getUp();
			temp.setPossibleVal(value, false);
		}
		
		temp = cell;
		while(temp.getDown() != null) { // down
			temp = temp.getDown();
			temp.setPossibleVal(value, false);
		}
		
		temp = first;
		rowMarker = first;
		while(temp != null) { // box
			while(temp != null) {
				if(temp.getBoxID() == cell.getBoxID())
					temp.setPossibleVal(value, false);
				
				temp = temp.getRight();
			}
			rowMarker = rowMarker.getDown();
			temp = rowMarker;
		}
		
	} // end fillCell
	
	public static void possibleValues(Cell hub) {
		Cell temp = hub;
		Cell columnMarker = hub;
		Cell rowMarker = hub;
		Cell copy = hub;
		Cell copyRow = hub;
		
		while(temp != null) {
			while(temp != null) {
				if(temp.getData() == 0) {
					while(temp.getRight() != null) { // right
						temp = temp.getRight();
						
						if(temp.getData() != 0)
							for(int x = 1; x < 10; x++)
								if(temp.getData() == x)
									columnMarker.setPossibleVal(x, false);
					}
					
					temp = columnMarker;
					
					while(temp.getLeft() != null) { // left
						temp = temp.getLeft();
						
						if(temp.getData() != 0)
							for(int x = 1; x < 10; x++)
								if(temp.getData() == x)
									columnMarker.setPossibleVal(x, false);
					}
					
					temp = columnMarker;
					
					while(temp.getUp() != null) { // up
						temp = temp.getUp();
						
						if(temp.getData() != 0)
							for(int x = 1; x < 10; x++)
								if(temp.getData() == x)
									columnMarker.setPossibleVal(x, false);
					}
					
					temp = columnMarker;
					
					while(temp.getDown() != null) { // down
						temp = temp.getDown();
						
						if(temp.getData() != 0)
							for(int x = 1; x < 10; x++)
								if(temp.getData() == x)
									columnMarker.setPossibleVal(x, false);
					}
				}
				else {
					for(int x = 0; x < 10; x++)
						temp.setPossibleVal(x, false);
					
					copy = first;
					copyRow = first;
					
					while(copy != null) {
						while(copy != null) {
							if(copy.getData() == 0 && copy.getBoxID() == temp.getBoxID()) {
								copy.setPossibleVal(temp.getData(), false);
							}
							
							copy = copy.getRight();
						}
						
						copyRow = copyRow.getDown();
						copy = copyRow;
					}
				}
				
				columnMarker = columnMarker.getRight();
				temp = columnMarker;
			}
			
			rowMarker = rowMarker.getDown();
			columnMarker = rowMarker;
			temp = columnMarker;
		}
		
	} // end possibleValues
	
	public static boolean matchPairs(int[] pair1, int[]pair2) {
		boolean match = true;
		
		match = true; // assume match
		
		for(int x = 0; x < 2; x++) {
			if(pair1[x] != pair2[x]) {
				match = false;
			}
		}
		
		return match;
	} // end matchPairs
	
	public static void purgeBox(Cell pair, Cell pair2, int val1, int val2) {
		Cell colMarker = first;
		Cell rowMarker = first;
		
		int boxID = pair.getBoxID();
		
		while(colMarker != null) {
			while(colMarker != null) {
				if(colMarker.getBoxID() == boxID && colMarker != pair && colMarker != pair2) {
					if(colMarker.getPossibleVal(val1)) {
						colMarker.setPossibleVal(val1, false);
					}
					if(colMarker.getPossibleVal(val2)) {
						colMarker.setPossibleVal(val2, false);
					}
				}
				colMarker = colMarker.getRight();
			}
			rowMarker = rowMarker.getDown();
			colMarker = rowMarker;
		}
	} // purgeBox
	
	public static void purgeRow(Cell pair, Cell pair2, int val1, int val2) {
		Cell colMarker = first;
		
		colMarker = pair.getLeft();
		
		while(colMarker != null) {
			if(colMarker != pair2) {
				if(colMarker.getPossibleVal(val1)) {
					colMarker.setPossibleVal(val1, false);
				}
				if(colMarker.getPossibleVal(val2)) {
					colMarker.setPossibleVal(val2, false);
				}
			}
			colMarker = colMarker.getLeft();
		}
		
		colMarker = pair.getRight();
		
		while(colMarker != null) {
			if(colMarker.getPossibleVal(val1)) {
				colMarker.setPossibleVal(val1, false);
			}
			if(colMarker.getPossibleVal(val2)) {
				colMarker.setPossibleVal(val2, false);
			}
			colMarker = colMarker.getRight();
		}
	} // end purgeRow
	
	public static void purgeCol(Cell pair, Cell pair2, int val1, int val2) {
		Cell rowMarker = first;
		
		rowMarker = pair.getDown();
		
		while(rowMarker != null) {
			if(rowMarker.getPossibleVal(val1)) {
				rowMarker.setPossibleVal(val1, false);
			}
			if(rowMarker.getPossibleVal(val2)) {
				rowMarker.setPossibleVal(val2, false);
			}
			
			rowMarker = rowMarker.getDown();
		}
		
		rowMarker = pair.getUp();
		
		while(rowMarker != null) {
			if(rowMarker.getPossibleVal(val1)) {
				rowMarker.setPossibleVal(val1, false);
			}
			if(rowMarker.getPossibleVal(val2)) {
				rowMarker.setPossibleVal(val2, false);
			}
			
			rowMarker = rowMarker.getUp();
		}
	} // end purgeCol
	
	public static void initiateGuess() {
		Cell temp = first;
		Cell rowMarker = first;
		
		while(temp != null) {
			while(temp != null) {
				if(temp.getPossibleVal(0)) { // first empty cell
					guess(temp, rowMarker);
					
					break;
				}
				
				temp = temp.getRight();
			}
			
			rowMarker = rowMarker.getDown();
			temp = rowMarker;
		}
	} // end initiateGuess
	
	public static void guess(Cell empty, Cell rowMarker) {
		int num = 0;
		
		Cell temp = empty.getRight();
		
		for(int x = 1; x < 10; x++) {
			if(empty.getPossibleVal(x)) {
				num = x;
				
				break;
			}
		}
		
		fillCell(empty, num); // make a guess
		
		while(temp != null) { // search for another empty cell
			while(temp != null) {
				if(temp.getPossibleVal(0)) { // empty cell
					for(int x = 1; x < 10; x++) {
						if(temp.getPossibleVal(x)) {
							fillCell(temp, x);
						}
					}
					
					//fillCell(temp,0); ///TODO: figure out how to reset, jkjkjk just copy and redo, don't reset
					temp = temp.getRight();
				}
				rowMarker = rowMarker.getDown();
				temp = rowMarker;
			}
		}
	} // end guess
	
	public static int solveMeth1() { // fill single-possibility cells
		Cell temp = first;
		Cell rowMarker = first;
		int changes = 0;
		
		while(temp != null) {
			while(temp != null) {
				if(temp.numberPossible() == 1)
					for(int x= 1; x < 10; x++)
						if(temp.getPossibleVal(x) == true) {
							fillCell(temp,x);
							changes++;
						}
				
				temp = temp.getRight();
			}
			rowMarker = rowMarker.getDown();
			temp = rowMarker;
		}
		
		return changes;
	} // end fillSinglePossCells
	
	public static int solveMeth2() { // fill unique possibility cells by box
		Cell temp = first;
		Cell rowMarker = first;
		Cell columnMarker = first;
		Cell cornerMarker = first;
		Cell cornerRow = first;
		Cell place = null;
		int changes = 0;
		int counter = 0;
		int id = 1;
		
		// by box
		while(cornerMarker != null) {
			while(cornerMarker != null) {
				for(int x = 1; x < 10; x++) { // go through possible values
					temp = cornerMarker;
					rowMarker = cornerMarker;
					counter = 0;
					
					// checks single values
					for(int y = 0; y < 3; y++) { // move 3 down
						for(int z = 0; z < 3; z++) { // move 3 right
							if(temp.getData() == 0 && temp.getPossibleVal(x) == true && temp.getBoxID() == id) {
								counter++;
								
								if(counter == 1)
									place = temp;
							}
							temp = temp.getRight();
						}
						rowMarker = rowMarker.getDown();
						temp = rowMarker;
					}
					
					// fill single values
					if(counter == 1) {
						fillCell(place,x);
						changes++;
					}
				}
				cornerMarker = cornerMarker.getRight().getRight().getRight();
				id++;
			}
			cornerRow = cornerRow.getDown().getDown().getDown();
			cornerMarker = cornerRow;
		}
		
		// by row
		temp = first;
		rowMarker = first;
		columnMarker = first;
		
		while(columnMarker != null) {
			while(columnMarker != null) {
				for(int x = 1; x < 10; x++) {
					counter = 0;
					
					temp = columnMarker;
					while(temp != null) { // left
						if(temp.getPossibleVal(x)) {
							counter++;
						}
							
						temp = temp.getLeft();
					}
					
					temp = columnMarker.getRight();
					while(temp != null) { // right
						if(temp.getPossibleVal(x)) {
							counter++;
						}
						
						temp = temp.getRight();
					}
					
					if(counter == 1) {
						temp = columnMarker;
						while(temp != null) { // left
							if(temp.getPossibleVal(x)) {
								fillCell(temp, x);
							}
								
							temp = temp.getLeft();
						}
						
						temp = columnMarker.getRight();
						while(temp != null) { // right
							if(temp.getPossibleVal(x)) {
								fillCell(temp, x);
							}
							
							temp = temp.getRight();
						}
					}
				}
				
				columnMarker = columnMarker.getRight();
			}
			
			rowMarker = rowMarker.getDown();
			columnMarker = rowMarker;
		}
		
		// by column
		temp = first;
		rowMarker = first;
		columnMarker = first;
		
		while(columnMarker != null) {
			while(columnMarker != null) {
				for(int x = 1; x < 10; x++) {
					counter = 0;
					
					temp = columnMarker;
					while(temp != null) { // left
						if(temp.getPossibleVal(x)) {
							counter++;
						}
							
						temp = temp.getDown();
					}
					
					temp = columnMarker.getUp();
					while(temp != null) { // right
						if(temp.getPossibleVal(x)) {
							counter++;
						}
						
						temp = temp.getUp();
					}
					
					if(counter == 1) {
						temp = columnMarker;
						while(temp != null) { // left
							if(temp.getPossibleVal(x)) {
								fillCell(temp, x);
							}
								
							temp = temp.getDown();
						}
						
						temp = columnMarker.getUp();
						while(temp != null) { // right
							if(temp.getPossibleVal(x)) {
								fillCell(temp, x);
							}
							
							temp = temp.getUp();
						}
					}
				}
				
				columnMarker = columnMarker.getRight();
			}
			
			rowMarker = rowMarker.getDown();
			columnMarker = rowMarker;
		}
		
		return changes;
	} // end solveMeth2
	
	public static void solveMeth3() { // eliminate twin possibilities from surrounding cells
		Cell rowMarker = first;
		Cell colMarker = first;
		
		Cell rowMarker2 = first;
		Cell colMarker2 = first;
		
		int boxID = -1;
		int counter = 0;
		
		int[] pair1 = {0,0,0};
		int[] pair2 = {0,0,0};
		
		boolean match = true;
		
		// by box
		while(colMarker != null) {
			while(colMarker != null) {
				boxID = colMarker.getBoxID();
				
				if(colMarker.numberPossible() == 2) { // found a pair
					counter = 0;
					
					for(int x = 1; x < 10; x++) {
						if(colMarker.getPossibleVal(x)) {
							pair1[counter++] = x; // save values
						}
					}
					
					while(colMarker2 != null) {
						while(colMarker2 != null) {
							if(colMarker2.numberPossible() == 2 && colMarker2.getBoxID() == boxID && colMarker2 != colMarker) { // found another pair
								counter = 0;
								
								for(int x = 1; x < 10; x++) {
									if(colMarker2.getPossibleVal(x)) {
										pair2[counter++] = x; // save values again
									}
								}
								
								match = matchPairs(pair1, pair2); // check if they match
								
								if(match) {
									purgeBox(colMarker, colMarker2, pair1[0], pair1[1]); // eliminate pair values from surrounding cells
								}
							}
							colMarker2 = colMarker2.getRight();
						}
						rowMarker2 = rowMarker2.getDown();
						colMarker2 = rowMarker2;
					}
				}
				colMarker = colMarker.getRight();
			}
			rowMarker = rowMarker.getDown();
			colMarker = rowMarker;
		}
		
		// by row
		while(colMarker != null) {
			while(colMarker != null) {
				if(colMarker.numberPossible() == 2) { // found a pair
					counter = 0;
					
					for(int x = 1; x < 10; x++) {
						if(colMarker.getPossibleVal(x)) {
							pair1[counter++] = x; // save values
						}
					}
					
					while(colMarker2 != null) {
						if(colMarker2.numberPossible() == 2 && colMarker2 != colMarker) { // found another pair
							counter = 0;
							
							for(int x = 1; x < 10; x++) {
								if(colMarker2.getPossibleVal(x)) {
									pair2[counter++] = x; // save values again
								}
							}
							
							match = matchPairs(pair1, pair2); // check if they match
							
							if(match) {
								purgeRow(colMarker, colMarker2, pair1[0], pair1[1]); // eliminate pair values from surrounding cells
							}
						}
						colMarker2 = colMarker2.getRight();
					}
				}
				colMarker = colMarker.getRight();
			}
			rowMarker = rowMarker.getDown();
			colMarker = rowMarker;
		}
		
		// by column
		while(colMarker != null) {
			while(colMarker != null) {
				if(colMarker.numberPossible() == 2) { // found a pair
					counter = 0;
					
					for(int x = 1; x < 10; x++) {
						if(colMarker.getPossibleVal(x)) {
							pair1[counter++] = x; // save values
						}
					}
					
					while(colMarker2 != null) {
						if(colMarker2.numberPossible() == 2 && colMarker2 != colMarker) { // found another pair
							counter = 0;
							
							for(int x = 1; x < 10; x++) {
								if(colMarker2.getPossibleVal(x)) {
									pair2[counter++] = x; // save values again
								}
							}
							
							match = matchPairs(pair1, pair2); // check if they match
							
							if(match) {
								purgeCol(colMarker, colMarker2, pair1[0], pair1[1]); // eliminate pair values from surrounding cells
							}
						}
						colMarker2 = colMarker2.getDown();
					}
				}
				colMarker = colMarker.getRight();
			}
			rowMarker = rowMarker.getDown();
			colMarker = rowMarker;
		}
		
	} // end solveMeth3

	public static void solveMeth4(String solution) throws Exception { // guess
		//guess(solution);
		
		String fileName = "";
		CGrid cGrid = new CGrid(dimension);
		
		fileName = printGrid();
		
		cGrid.populate(fileName);
		
		System.out.println("cGrid");
		cGrid.display();
		
		initiateGuess();
		
	} // end solveMeth4
	
	public static String printGrid() throws IOException {
		Cell temp = first;
		Cell rowMarker = first;
		
		String str = "";
		String fileName = "dummy file.txt";
		
		File outfile = new File(fileName);
		PrintWriter output = new PrintWriter(outfile);
		
		while(temp != null) {
			while(temp != null) {
				output.print(temp.getData() + " ");
				
				temp = temp.getRight();
			}
			
			output.println();
			
			rowMarker = rowMarker.getDown();
			temp = rowMarker;
		}
		
		output.close();
		
		/*File infile = new File(fileName);
		Scanner input = new Scanner(infile);
		String data = "";
		
		while(input.hasNext()) {
			data = input.nextLine();
			System.out.println("The data is: " + data);
		}*/
		
		return fileName;
	} // end printGrid

	public static Cell getFirst() {
		return first;
	}

	public static void setFirst(Cell first) {
		Grid.first = first;
	}
	
} // end class Grid
