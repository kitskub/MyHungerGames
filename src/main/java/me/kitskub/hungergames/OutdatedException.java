package me.kitskub.hungergames;

public class OutdatedException extends Exception {
	private static final long serialVersionUID = 948604L;
	public final int version;

	public OutdatedException(int version) {
		this.version = version;
	}
}
