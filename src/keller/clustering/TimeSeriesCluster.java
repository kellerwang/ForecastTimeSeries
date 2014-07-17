package keller.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import keller.distance.Distance;
import keller.exception.DataNullException;
import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;
import keller.util.MyRandom;

public class TimeSeriesCluster {
	private int k = 6;
	private double threshold;
	private int repeat;
	private Map<Integer, TimeSeries> centerMap = new HashMap<Integer, TimeSeries>();
	private List<TimeSeries> data = null;

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public void setData(List<TimeSeries> data) {
		this.data = data;
	}

	// 构造函数
	public TimeSeriesCluster(int k, double threshold, int repeat,
			List<TimeSeries> data) throws DataNullException {
		this.k = k;
		this.threshold = threshold;
		this.repeat = repeat;
		setData(data);
		initCenter();
	}

	// 把数据集中的每个点归到离它最近的那个质心
	public void classifyData() throws DataNullException,
			TimeSeriesNotEquilongException {
		if (this.data == null) {
			throw new DataNullException("TimeSeries is not equilong!");
		} else {
			Iterator<TimeSeries> iter = data.iterator();
			while (iter.hasNext()) {
				TimeSeries tempTS = iter.next();
				double tempPro = Distance.getDistance(tempTS, centerMap.get(0));
				int clusterNum = 0;
				for (int i = 1; i < k; i++) {
					double temp = Distance
							.getDistance(tempTS, centerMap.get(i));
					if (temp < tempPro) {
						clusterNum = i;
						tempPro = temp;
					}
				}
				tempTS.setClusterNum(clusterNum);
			}
		}
	}

	// 计算新的簇中心曲线集
	public void calNewCenter() throws TimeSeriesNotEquilongException {
		for (int i = 0; i < k; i++) {
			List<TimeSeries> newList = getOneCluster(i);
			centerMap.remove(i);
			TimeSeries newCentralCurve = getCentralCurveCluster(newList);
			if (newCentralCurve != null) {
				centerMap.put(i, newCentralCurve);
			} else {
				// 抛出异常
			}
		}
	}

	// 拟合两条时间序列
	public TimeSeries getCentralCurve(TimeSeries ts1, TimeSeries ts2)
			throws TimeSeriesNotEquilongException {
		TimeSeries ts = new TimeSeries();
		ts.setMap(getCentralCurveMap(ts1.getMap(), ts2.getMap()));
		return ts;
	}

	// 拟合两条时间序列Map数据
	public Map<Integer, Double> getCentralCurveMap(Map<Integer, Double> map1,
			Map<Integer, Double> map2) throws TimeSeriesNotEquilongException {
		Map<Integer, Double> result = CurveFitting.getTimeSeriesMapFitting(
				map1, map2);
		return result;
	}

	// TimeSeries集合计算中心曲线
	public TimeSeries getCentralCurveCluster(List<TimeSeries> cluster)
			throws TimeSeriesNotEquilongException {
		TimeSeries tsPro = cluster.get(0);
		for (int i = 0; i < cluster.size(); i++) {
			TimeSeries tsNow = cluster.get(i);
			tsPro = getCentralCurve(tsPro, tsNow);
		}
		return tsPro;
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

	// 初始化簇中心曲线集
	public void initCenter() throws DataNullException {
		if (this.data == null) {
			throw new DataNullException("TimeSeries is not equilong!");
		} else {
			Set<Integer> randomSet = MyRandom.getRandomNonRepetitive(this.k,
					this.data.size());
			Iterator<Integer> it = randomSet.iterator();
			int clusterNum = 0;
			while (it.hasNext()) {
				int temp = it.next();
				centerMap.put(clusterNum, data.get(temp));
				clusterNum++;
			}
		}
	}
}
