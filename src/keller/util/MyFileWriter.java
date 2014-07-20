package keller.util;

import java.io.FileWriter;
import java.io.IOException;

public class MyFileWriter {
	// 写入文件
	public static void writeToFile(String filename, String temp)
			throws IOException {
		FileWriter fw = null;
		fw = new FileWriter(filename);
		fw.write(temp);
		fw.close();
	}

	// 写入文件，追加
	public static void writeToFileAppend(String filename, String temp)
			throws IOException {
		FileWriter fw = null;
		fw = new FileWriter(filename, true);
		fw.write(temp);
		fw.close();
	}
}
