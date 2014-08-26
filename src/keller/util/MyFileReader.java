package keller.util;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import keller.model.TimeSeries;
import keller.preprocessing.NewPreprocess;
import keller.preprocessing.Preprocess;

public class MyFileReader {

	// 在文件夹中读取时间序列文件到时间序列数组
	public static List<TimeSeries> getDataFromFilePath(String filepath,
			boolean is_zscore, int minLongOfTS) throws ParseException {
		File file = new File(filepath);
		if (file.isDirectory()) {
			String[] filelist = file.list();
			List<TimeSeries> dataList = new ArrayList<TimeSeries>();
			for (int i = 0; i < filelist.length; i++) {
				if (filelist[i].contains(".DS_Store")) {
					continue;
				} else {
					String readfile = filepath + "/" + filelist[i];
					TimeSeries tsTemp = Preprocess.initalTimeSeries(readfile);
					if (Preprocess.setLifecycle(tsTemp) >= minLongOfTS) {
						if (is_zscore) {
							NewPreprocess.z_scoreTimeSeries(tsTemp);
						}
						dataList.add(tsTemp);
					}
				}
			}
			return dataList;
		}
		return null;
	}

	// 在文件夹中读取时间序列文件到时间序列数组
	public static List<TimeSeries> getDataFromFilePathEqual(String filepath,
			boolean is_zscore) throws ParseException {
		File file = new File(filepath);
		if (file.isDirectory()) {
			String[] filelist = file.list();
			List<TimeSeries> dataList = new ArrayList<TimeSeries>();
			for (int i = 0; i < filelist.length; i++) {
				if (filelist[i].contains(".DS_Store")) {
					continue;
				} else {
					String readfile = filepath + "/" + filelist[i];
					TimeSeries tsTemp = Preprocess.initalTimeSeries(readfile);
					if (is_zscore) {
						NewPreprocess.z_scoreTimeSeries(tsTemp);
					}
					dataList.add(tsTemp);
				}
			}
			return dataList;
		}
		return null;
	}
}
