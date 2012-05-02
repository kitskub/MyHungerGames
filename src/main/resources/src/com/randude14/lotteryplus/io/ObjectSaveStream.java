package com.randude14.lotteryplus.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.File;

public class ObjectSaveStream {

	private final ObjectOutputStream oos;
	
	public ObjectSaveStream(File file) throws FileNotFoundException, IOException {
		oos = new ObjectOutputStream(new FileOutputStream(file));
	}
	
	public ObjectSaveStream(String path) throws FileNotFoundException, IOException {
		this(new File(path));
	}
	
	public void writeObject(Object obj) throws IOException {
		oos.writeObject(obj);
	}
	
	public void close() throws IOException {
		oos.flush();
		oos.close();
	}
	
}
