package codes;

public class Cell {

	private int data;
	private int boxID;
	private Cell up, down, left, right;
	private boolean[] possibleVal = {true, true, true, true, true, true, true, true, true, true};
	private int numberPossible;
	
	public Cell () {
		data = 0;
		boxID = 0;
		
		up = null;
		down = null;
		left = null;
		right = null;
		
	} // end constructor Cell

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public int getBoxID() {
		return boxID;
	}

	public void setBoxID(int boxID) {
		this.boxID = boxID;
	}

	public Cell getUp() {
		return up;
	}

	public void setUp(Cell up) {
		this.up = up;
	}

	public Cell getDown() {
		return down;
	}

	public void setDown(Cell down) {
		this.down = down;
	}

	public Cell getLeft() {
		return left;
	}

	public void setLeft(Cell left) {
		this.left = left;
	}

	public Cell getRight() {
		return right;
	}

	public void setRight(Cell right) {
		this.right = right;
	}

	public boolean getPossibleVal(int x) {
		return possibleVal[x];
	}

	public boolean setPossibleVal(int x, boolean state) {
		possibleVal[x] = state;
		
		return possibleVal[x];
	}
	
	public int numberPossible() {
		numberPossible = 0;
		
		for(int x = 1; x < 10; x++)
			if(getPossibleVal(x) == true)
				numberPossible++;
		
		return numberPossible;
	}
	
} // end class Cell
