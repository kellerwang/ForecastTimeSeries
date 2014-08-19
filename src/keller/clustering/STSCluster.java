package keller.clustering;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import keller.accuracy.ClusterAccuracy;
import keller.distance.Distance;
import keller.distance.STSDistance;
import keller.exception.DataNullException;
import keller.exception.NoCentralCurveException;
import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;
import keller.preprocessing.Preprocess;
import keller.util.MyRandom;
import keller.visualization.CurveGraph;

public class STSCluster {
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
	public STSCluster(int k, double threshold, int repeat, List<TimeSeries> data)
			throws DataNullException {
		this.k = k;
		this.threshold = threshold;
		this.repeat = repeat;
		setData(data);
		initCenter();
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

	// 计算新的簇中心曲线集
	public double calNewCenter() throws TimeSeriesNotEquilongException,
			NoCentralCurveException {
		double result = 0;
		for (int i = 0; i < k; i++) {
			List<TimeSeries> newList = getOneCluster(i);
			TimeSeries oldCentralCurve = centerMap.get(i);
			TimeSeries newCentralCurve = getCentralCurve4Cluster(newList);
			if (newCentralCurve != null) {
				result += STSDistance.getDistance(oldCentralCurve,
						newCentralCurve);
				centerMap.remove(i);
				centerMap.put(i, newCentralCurve);
			} else {
				// 抛出异常
				throw new NoCentralCurveException("No Central Curve!");
			}
		}
		return (result / ((double) k));
	}

	// TimeSeries集合计算中心曲线
	public TimeSeries getCentralCurve4Cluster(List<TimeSeries> cluster)
			throws TimeSeriesNotEquilongException {
		TimeSeries tsPro = cluster.get(0);
		for (int i = 1; i < cluster.size(); i++) {
			TimeSeries tsNow = cluster.get(i);
			tsPro = getMergeCurve(tsPro, tsNow, i, 1);
		}
		return tsPro;
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
		Map<Integer, Double> result = STSCurveMerge.getMapCurveFitting(map1,
				map2, weight1, weight2);
		return result;
	}

	// 把数据集中的每个点归到离它最近的那个质心
	public void classifyData() throws DataNullException,
			TimeSeriesNotEquilongException {
		if (this.data == null) {
			throw new DataNullException("No data!");
		} else {
			Iterator<TimeSeries> iter = data.iterator();
			while (iter.hasNext()) {
				TimeSeries tempTS = iter.next();
				double tempPro = STSDistance.getDistance(tempTS,
						centerMap.get(0));
				int clusterNum = 0;
				for (int i = 1; i < k; i++) {
					double temp = STSDistance.getDistance(tempTS,
							centerMap.get(i));
					if (temp < tempPro) {
						clusterNum = i;
						tempPro = temp;
					}
				}
				tempTS.setClusterNum(clusterNum);
			}
		}
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
				double tempPro = STSDistance.getDistance(tempTS,
						centerMap.get(0));
				int clusterNum = 0;
				for (int i = 1; i < k; i++) {
					double temp = STSDistance.getDistance(tempTS,
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
				result += STSDistance.getDistance(tempTS,
						centerMap.get(tempTS.getClusterNum()));
			}
			return result;
		}
	}

	// 打印显示中心曲线结果
	public void showResult() throws TimeSeriesNotEquilongException,
			IOException, ParseException {
		CurveGraph cg = new CurveGraph(centerMap, data);
		ClusterPrint.printCentralCurve(centerMap, repeat);
		ClusterPrint.printClusterElement(data);
		for (int i = 0; i < k; i++) {
			ClusterAccuracy.getBaiduHotSearchesScale("百度热门搜索", i);
		}
	}

	// 迭代聚类
	public void iterationCluster() throws DataNullException,
			TimeSeriesNotEquilongException {
		double preOptimizingValue = 0;
		double tempThreshold = 0;
		for (int i = 0; i < repeat; i++) {
			if (i >= 10 && tempThreshold < threshold) {
				break;
			}
			classifyData2();
			double tempOptimizingValue = getOptimizingValue();
			tempThreshold = Math.abs(preOptimizingValue - tempOptimizingValue);
			preOptimizingValue = tempOptimizingValue;
		}
		System.out.println("tempThreshold: " + tempThreshold);
	}
}
