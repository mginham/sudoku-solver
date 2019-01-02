package codes;

import java.io.File;
import java.util.Scanner;

public class CGrid {

	private static Cell first;
	private int dimension;
	
	public CGrid(int dimension) {
		this.dimension = dimension;
		
		if(dimension == 1)
			first = new Cell();
		else if(dimension <= 0) {
			first = null;
		}
		else {
			first = new Cell();
			
			Cell temp;
			Cell rowMarker = first;
			Cell colMarker = first;
			
			int counter1 = 0;
			int counter2 = 0;
			
			for(int x = 0; x < dimension-1; x++) { // create first row
				temp = new Cell();
				
				temp.setLeft(colMarker);
				colMarker.setRight(temp);
				
				colMarker = temp;
			}
			
			for(int x = 0; x < dimension-1; x++) { // create the rest of the grid
				temp = new Cell();
				
				temp.setUp(rowMarker);
				rowMarker.setDown(temp);
				
				rowMarker = temp;
				colMarker = rowMarker;
				
				for(int y = 0; y < dimension-1; y++) {
					temp = new Cell();
					
					temp.setLeft(colMarker);
					colMarker.setRight(temp);
					temp.setUp(temp.getLeft().getUp().getRight());
					temp.getUp().setDown(temp);
					
					colMarker = temp;
				}
			}
			
			temp = first;
			rowMarker = first;
			
			for(int a = 0; a < 3; a++) {
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
				counter2 = counter2+3;
				counter1 = counter2;
			}
		}
	} // end constructor
	
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
	
	public static Cell copyCell(Cell oCell) {
		Cell cCell = first;
		
		if(oCell == null) {
			return null;
		}
		else {
			cCell.setData(oCell.getData());
			
			cCell.setBoxID(oCell.getBoxID());
			
			cCell.setDown(oCell.getDown());
			cCell.setUp(oCell.getUp());
			cCell.setLeft(oCell.getLeft());
			cCell.setRight(oCell.getRight());
			
			for(int x = 0; x < 10; x++) {
				cCell.setPossibleVal(x, oCell.getPossibleVal(x));
			}
		}
		
		return cCell;
	} // end copyCell

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

} // end class Grid
