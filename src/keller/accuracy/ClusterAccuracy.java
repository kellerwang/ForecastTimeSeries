package keller.accuracy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import keller.distance.STSDistance;
import keller.model.TimeSeries;
import keller.preprocessing.NewPreprocess;
import keller.preprocessing.Preprocess;

public class ClusterAccuracy {

	public ClusterAccuracy() {
		// TODO Auto-generated constructor stub
	}

	// 以行为单位读取文件，将词汇读入list数组
	public static List<String> getClusterString(String fileName)
			throws IOException {
		File file = new File(fileName);
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file));
		String tempString = null;
		// 一次读入一行，直到读入null为文件结束
		List<String> data = new ArrayList<String>();
		while ((tempString = reader.readLine()) != null) {
			data.add(tempString);
		}
		reader.close();
		return data;

	}

	// 返回百度热门搜索词数组
	public static List<String> getBaiduHotSearches(String filepath)
			throws ParseException {
		File file = new File(filepath);
		if (file.isDirectory()) {
			String[] filelist = file.list();
			List<TimeSeries> dataList = new ArrayList<TimeSeries>();
			List<String> data = new ArrayList<String>();
			for (int i = 0; i < filelist.length; i++) {
				if (filelist[i].contains(".DS_Store")) {
					continue;
				} else {
					String tempString = filelist[i];
					data.add(tempString);
				}
			}
			return data;
		}
		return null;
	}

	// 判断簇中含百度热门搜索数的比例
	public static double getBaiduHotSearchesScale(String name, int clusterNum)
			throws IOException, ParseException {
		double result = 0;
		String fileCluster = "result/data/cluster" + clusterNum;
		String fileBaidu = "data/" + name;
		List<String> dataCluster = getClusterString(fileCluster);
		List<String> dataBaidu = getBaiduHotSearches(fileBaidu);
		int counter = 0;
		Iterator<String> iter = dataCluster.iterator();
		while (iter.hasNext()) {
			String tempCluster = iter.next();
			if (dataBaidu.contains(tempCluster)) {
				counter++;
				System.out.print(tempCluster + " ");
			} else {
				
			}
		}
		System.out.println();
		System.out.println("Cluster" + clusterNum + "含百度热门搜索数: " + counter);
		result = (double) counter / (double) dataCluster.size();
		System.out.println("Cluster" + clusterNum + "百度热门搜索占比例: " + result);
		return result;
	}
	
	// 判断簇中含热门网站数的比例
		public static double getHotWebSiteScale(String name, int clusterNum)
				throws IOException, ParseException {
			double result = 0;
			String fileCluster = "result/data/cluster" + clusterNum;
			String fileBaidu = "data/" + name;
			List<String> dataCluster = getClusterString(fileCluster);
			List<String> dataBaidu = getBaiduHotSearches(fileBaidu);
			int counter = 0;
			Iterator<String> iter = dataCluster.iterator();
			while (iter.hasNext()) {
				String tempCluster = iter.next();
				if (dataBaidu.contains(tempCluster)) {
					counter++;
					System.out.print(tempCluster + " ");
				} else {
					
				}
			}
			System.out.println();
			System.out.println("Cluster" + clusterNum + "热门网站数: " + counter);
			result = (double) counter / (double) dataCluster.size();
			System.out.println("Cluster" + clusterNum + "热门网站占比例: " + result);
			return result;
		}
}
