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

import keller.clustering.STSCluster;
import keller.clustering.TimeSeriesCluster;
import keller.distance.Distance;
import keller.distance.STSDistance;
import keller.exception.DataNullException;
import keller.exception.NoCentralCurveException;
import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;
import keller.preprocessing.NewPreprocess;
import keller.preprocessing.Preprocess;
import keller.util.MyFileReader;
import keller.util.MyRandom;
import keller.util.Statistics;
import keller.visualization.CurveGraph;
import keller.visualization.GraphDemo;

public class MyTest {
	public static SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	// 显示STS距离聚类版本的结果
	public static void showClusteringResult2() throws ParseException,
			DataNullException, TimeSeriesNotEquilongException, IOException {
		System.out.println("Start Time: " + df.format(new Date()));
		String filepath = "data/搜狗每日热词2";
		List<TimeSeries> dataList = MyFileReader.getDataFromFilePath(filepath,
				true);
		STSCluster sts = new STSCluster(6, 0.005, 20, dataList);
		sts.iterationCluster();
		sts.showResult();
		System.out.println("End Time: " + df.format(new Date()));
	}

	// 显示聚类版本1的结果
	public static void showClusteringResult1() throws ParseException,
			DataNullException, TimeSeriesNotEquilongException, IOException,
			NoCentralCurveException {
		System.out.println("Start Time: " + df.format(new Date()));
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
		System.out.println("End Time: " + df.format(new Date()));
	}

	public static void main(String[] args) throws IOException, ParseException,
			TimeSeriesNotEquilongException, DataNullException {
		showClusteringResult2();
	}
}
