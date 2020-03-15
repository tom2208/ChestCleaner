package chestcleaner.utils;

/**
 * Just a simple tuple representation in java. Like you know them from math. A
 * pair of objects.
 * 
 * @author Tom2208
 */
public class Tuple<X, Y> {

	private X x;
	private Y y;
	
	public Tuple(X x, Y y) {
		this.setFirstElement(x);
		this.setSecondElement(y);
	}

	public X getFirstElement() {
		return x;
	}

	public void setFirstElement(X e) {
		this.x = e;
	}

	public Y getSecondElement() {
		return y;
	}

	public void setSecondElement(Y e) {
		this.y = e;
	}

}
