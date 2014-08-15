package keller.main;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import keller.clustering.TimeSeriesCluster;
import keller.distance.Distance;
import keller.distance.STSDistance;
import keller.exception.DataNullException;
import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;
import keller.preprocessing.NewPreprocess;
import keller.preprocessing.Preprocess;
import keller.util.MyRandom;
import keller.visualization.CurveGraph;
import keller.visualization.GraphDemo;

public class MyTest {
	public static void showClusteringResult1() throws ParseException,
			DataNullException, TimeSeriesNotEquilongException, IOException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(new Date()));
		String filepath = "data/搜狗每日热词2";
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
			TimeSeriesCluster tsc = new TimeSeriesCluster(6, 0.005, 20,
					dataList);
			tsc.iterationCluster();
		}
		System.out.println(df.format(new Date()));
	}

	public static void main(String[] args) throws IOException, ParseException,
			TimeSeriesNotEquilongException {
		TimeSeries ts1 = Preprocess.initalTimeSeries("data/百度热门搜索/1号店");
		Preprocess.setLifecycle(ts1);
		NewPreprocess.z_scoreTimeSeries(ts1);
		TimeSeries ts2 = Preprocess.initalTimeSeries("data/百度热门搜索/7k7k小游戏");
		Preprocess.setLifecycle(ts2);
		NewPreprocess.z_scoreTimeSeries(ts2);
		double result = STSDistance.getDistance(ts1, ts2);
		System.out.println(result);
	}
}
