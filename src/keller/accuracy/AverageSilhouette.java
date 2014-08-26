package keller.accuracy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import keller.distance.STSDistance;
import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;

public class AverageSilhouette {

	private List<TimeSeries> data = null;
	private int k;

	public void setData(List<TimeSeries> data) {
		this.data = data;
	}

	// 构造函数
	public AverageSilhouette(List<TimeSeries> data, int k) {
		// TODO Auto-generated constructor stub
		this.k = k;
		setData(data);
	}

	// 获取一个时间序列对象和一簇时间序列对象间的dissimilarity
	private double getDissimilarityWithACluster(TimeSeries ts, int numCluster)
			throws TimeSeriesNotEquilongException {
		List<TimeSeries> cluster = getOneCluster(numCluster);
		double sum = 0;
		Iterator<TimeSeries> iter = cluster.iterator();
		while (iter.hasNext()) {
			TimeSeries tempTS = iter.next();
			sum += STSDistance.getDistance(ts, tempTS);
		}
		double num = cluster.size();
		if (num == 1 || num == 0) {
			return 0;
		}
		if (ts.getClusterNum() == numCluster) {
			return sum / (num - 1);
		} else {
			return sum / num;
		}
	}

	// 获得一个簇
	public List<TimeSeries> getOneCluster(int k) {
		Iterator<TimeSeries> iter = data.iterator();
		List<TimeSeries> newList = new ArrayList<TimeSeries>();
		while (iter.hasNext()) {
			TimeSeries tempTS = iter.next();
			if (tempTS.getClusterNum() == k) {
				newList.add(tempTS);
			}
		}
		return newList;
	}

	// 获取一个时间序列对象的AverageSilhouette值
	private double getAverageSilhouette(TimeSeries ts)
			throws TimeSeriesNotEquilongException {
		boolean flag = false;
		double dissimilarityB = 0;
		double dissimilarityA = 0;
		for (int i = 0; i < k; i++) {
			double dissimilarityD = getDissimilarityWithACluster(ts, i);
			if (i == ts.getClusterNum()) {
				dissimilarityA = dissimilarityD;
			} else {
				if (!flag) {
					dissimilarityB = dissimilarityD;
					flag = true;
				} else if (dissimilarityD < dissimilarityB) {
					dissimilarityB = dissimilarityD;
				}
			}
		}
		return getDissimilarityS(dissimilarityA, dissimilarityB);
	}

	// 计算dissimilarityS
	private double getDissimilarityS(double dissimilarityA,
			double dissimilarityB) {
		double max = Math.max(dissimilarityA, dissimilarityB);
		if (max == 0) {
			return 1;
		} else {
			return (dissimilarityB - dissimilarityA) / max;
		}
	}

	// 获取总体AverageSilhouette值
	public double getOverAllAverageSilhouette()
			throws TimeSeriesNotEquilongException {
		double sum = 0;
		double num = data.size();
		Iterator<TimeSeries> iter = data.iterator();
		while (iter.hasNext()) {
			TimeSeries ts = iter.next();
			sum += getAverageSilhouette(ts);
		}
		return sum / num;
	}

}
