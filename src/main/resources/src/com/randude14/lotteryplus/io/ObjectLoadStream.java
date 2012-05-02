package com.randude14.lotteryplus.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;

public class ObjectLoadStream {

	private final ObjectInputStream ois;
	
	public ObjectLoadStream(File file) throws FileNotFoundException, IOException {
		ois = new ObjectInputStream(new FileInputStream(file));
	}
	
	public ObjectLoadStream(String path) throws FileNotFoundException, IOException {
		this(new File(path));
	}
	
	public Object readObject() throws IOException, ClassNotFoundException {
		return ois.readObject();
	}
	
	public void close() throws IOException {
		ois.close();
	}
	
}
