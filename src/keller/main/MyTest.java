package keller.main;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import keller.accuracy.ClusterAccuracy;
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
	public static void showSTSClusteringResult(int k, double threshold,
			int repeat, String filepath, int minLongOfTS)
			throws ParseException, DataNullException,
			TimeSeriesNotEquilongException, IOException {
		System.out.println("Start Time: " + df.format(new Date()));
		List<TimeSeries> dataList = MyFileReader.getDataFromFilePath(filepath,
				true, minLongOfTS);
		STSCluster sts = new STSCluster(k, threshold, repeat, dataList);
		sts.iterationCluster();
		sts.showResult();
		System.out.println("End Time: " + df.format(new Date()));
	}

	// 显示聚类版本1的结果
	public static void showEuclideanClusteringResult1(int k, double threshold,
			int repeat, String filepath, int minLongOfTS)
			throws ParseException, DataNullException,
			TimeSeriesNotEquilongException, IOException,
			NoCentralCurveException {
		System.out.println("Start Time: " + df.format(new Date()));
		List<TimeSeries> dataList = MyFileReader.getDataFromFilePath(filepath,
				false, minLongOfTS);
		TimeSeriesCluster tsc = new TimeSeriesCluster(k, threshold, repeat,
				dataList);
		tsc.iterationCluster();
		System.out.println("End Time: " + df.format(new Date()));
	}

	public static void main(String[] args) throws IOException, ParseException,
			TimeSeriesNotEquilongException, DataNullException,
			NoCentralCurveException {
		String filepath = "data/数据1";
	}
}
