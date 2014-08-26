package keller.clustering;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import keller.accuracy.ClusterAccuracy;
import keller.distance.EuclideanDisstance;
import keller.exception.DataNullException;
import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;
import keller.util.MyRandom;

public class EqualEuclideanCluster {

	private int k = 6;
	private double threshold;
	private int repeat;
	private Map<Integer, TimeSeries> centerMap = new HashMap<Integer, TimeSeries>();
	private List<TimeSeries> data = null;

	public void setData(List<TimeSeries> data) {
		this.data = data;
	}

	// 初始化簇中心曲线集
	public void initCenter() throws DataNullException {
		if (this.data == null) {
			throw new DataNullException("No data!");
		} else {
			Set<Integer> randomSet = MyRandom.getRandomNonRepetitive(this.k,
					this.data.size());
			Iterator<Integer> it = randomSet.iterator();
			int clusterNum = 0;
			while (it.hasNext()) {
				int temp = it.next();
				centerMap.put(clusterNum, new TimeSeries(data.get(temp)
						.getMap()));
				clusterNum++;
			}
		}
	}

	// 构造函数
	public EqualEuclideanCluster(int k, double threshold, int repeat,
			List<TimeSeries> data) throws DataNullException {
		this.k = k;
		this.threshold = threshold;
		this.repeat = repeat;
		setData(data);
		initCenter();
	}

	// 拟合两条时间序列,weight1和weight2分别代表两条时间序列的权重
	public TimeSeries getMergeCurve(TimeSeries ts1, TimeSeries ts2,
			double weight1, double weight2)
			throws TimeSeriesNotEquilongException {
		TimeSeries ts = new TimeSeries();
		ts.setMap(getMergeCurveOfMap(ts1.getMap(), ts2.getMap(), weight1,
				weight2));
		return ts;
	}

	// 拟合两条时间序列Map数据,weight1和weight2分别代表两条时间序列的权重
	public Map<Integer, Double> getMergeCurveOfMap(Map<Integer, Double> map1,
			Map<Integer, Double> map2, double weight1, double weight2)
			throws TimeSeriesNotEquilongException {
		Map<Integer, Double> result = EuclideanCurveMerge
				.getEqualMapCurveFitting(map1, map2, weight1, weight2);
		return result;
	}

	// 把数据集中的每个点归到离它最近的那个质心，同时不断跟新中心点
	public void classifyData2() throws DataNullException,
			TimeSeriesNotEquilongException {
		if (this.data == null) {
			throw new DataNullException("No data!");
		} else {
			Map<Integer, Integer> mapClusterCounter = new HashMap<Integer, Integer>();
			for (int i = 0; i < this.k; i++) {
				mapClusterCounter.put(i, 1);
			}
			Iterator<TimeSeries> iter = data.iterator();
			while (iter.hasNext()) {
				TimeSeries tempTS = iter.next();
				double tempPro = EuclideanDisstance.getDistance2(tempTS,
						centerMap.get(0));
				int clusterNum = 0;
				for (int i = 1; i < k; i++) {
					double temp = EuclideanDisstance.getDistance2(tempTS,
							centerMap.get(i));
					if (temp < tempPro) {
						clusterNum = i;
						tempPro = temp;
					}
				}
				tempTS.setClusterNum(clusterNum);
				// 更新簇中心
				int tempWeight = mapClusterCounter.get(clusterNum);
				TimeSeries newCenterTS = getMergeCurve(
						centerMap.get(clusterNum), tempTS, tempWeight, 1);
				centerMap.remove(clusterNum);
				centerMap.put(clusterNum, newCenterTS);
				mapClusterCounter.remove(clusterNum);
				mapClusterCounter.put(clusterNum, tempWeight + 1);
			}
		}
	}

	// 计算最优函数值
	public double getOptimizingValue() throws DataNullException,
			TimeSeriesNotEquilongException {
		if (this.data == null) {
			throw new DataNullException("No data!");
		} else {
			double result = 0;
			Iterator<TimeSeries> iter = data.iterator();
			while (iter.hasNext()) {
				TimeSeries tempTS = iter.next();
				result += EuclideanDisstance.getDistance2(tempTS,
						centerMap.get(tempTS.getClusterNum()));
			}
			return result;
		}
	}

	// 打印显示中心曲线结果
	public Map<Integer, TimeSeries> showResult()
			throws TimeSeriesNotEquilongException, IOException, ParseException {
		ClusterPrint.printClusterElement(data);
		for (int i = 0; i < k; i++) {
			ClusterAccuracy.getHotWebSiteScale("网站top100_8.21", i);
		}
		return centerMap;
	}

	// 获得聚类结果的D-Value值
	public double getDValue() throws TimeSeriesNotEquilongException {
		double result = 0;
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < k; j++) {
				if (i != j) {
					result += EuclideanDisstance.getDistance(centerMap.get(i),
							centerMap.get(j));
				}
			}
		}
		return result;
	}

	// 迭代聚类
	public void iterationCluster() throws DataNullException,
			TimeSeriesNotEquilongException {
		double preOptimizingValue = 0;
		double tempThreshold = 0;
		int i = 0;
		for (; i < repeat; i++) {
			if (i >= 10 && tempThreshold < threshold) {
				break;
			}
			classifyData2();
			double tempOptimizingValue = getOptimizingValue();
			tempThreshold = Math.abs(preOptimizingValue - tempOptimizingValue);
			preOptimizingValue = tempOptimizingValue;
		}
		System.out.println("实际迭代次数: " + i);
		System.out.println("F值: " + preOptimizingValue);
		System.out.println("D值: " + getDValue());
	}

}
