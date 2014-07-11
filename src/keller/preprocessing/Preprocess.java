package keller.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import keller.model.TimeSeries;

public class Preprocess {

	// 读取一个时间序列文件并存储在TimeSeries数据结构中
	public static void initalTimeSeries(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			StringBuilder contentBuf = new StringBuilder();
			while ((tempString = reader.readLine()) != null) {
				contentBuf.append(tempString);
			}
			reader.close();
			String buf = new String(contentBuf.toString().getBytes());
			String[] tempArray = buf.split("\"");
			TimeSeries timeSeries = new TimeSeries();
			timeSeries.setKey(tempArray[3]);
			String[] tempDateArray = tempArray[7].split("\\|");
			timeSeries.setStartDate(tempDateArray[0]);
			timeSeries.setEndDate(tempDateArray[1]);
			String[] timeSeriesStrArray = tempArray[11].split(",");
			for (int i = 0; i < timeSeriesStrArray.length; i++) {
				timeSeries.getMap().put(i,
						Integer.parseInt(timeSeriesStrArray[i]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	public static void main(String[] args) {
		initalTimeSeries("data/百度世说新词/00后");
	}
}
