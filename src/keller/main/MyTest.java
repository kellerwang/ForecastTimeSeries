package keller.main;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import keller.clustering.TimeSeriesCluster;
import keller.distance.Distance;
import keller.exception.DataNullException;
import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;
import keller.preprocessing.Preprocess;
import keller.util.MyRandom;

public class MyTest {
	public static void main(String[] args) throws ParseException,
			DataNullException, TimeSeriesNotEquilongException {
		String filepath = "data/百度世说新词";
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
					Preprocess.setLifecycle(tsTemp);
					dataList.add(tsTemp);
				}
			}
			TimeSeriesCluster tsc = new TimeSeriesCluster(6, 0.01, 15, dataList);
			tsc.iterationCluster();
		}
	}
}
