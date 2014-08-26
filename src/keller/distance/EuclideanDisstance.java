package keller.distance;

import java.util.HashMap;
import java.util.Map;

import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;

public class EuclideanDisstance {

	// 获取等长时间序列Map数组的欧式距离
	public static double getEquilongEuclideanDistance(
			Map<Integer, Double> map1, Map<Integer, Double> map2)
			throws TimeSeriesNotEquilongException {
		if (map1.size() != map2.size()) {
			// 抛出异常
			throw new TimeSeriesNotEquilongException(
					"TimeSeries is not equilong!");
		} else {
			double sum = 0;
			for (int i = 0; i < map1.size(); i++) {
				sum += Math.pow((map1.get(i) - map2.get(i)), 2);
			}
			return sum / map1.size();
		}
	}

	// 获取时间序列Map数组的欧式距离
	public static double getEuclideanDistance(Map<Integer, Double> map1,
			Map<Integer, Double> map2) throws TimeSeriesNotEquilongException {
		if (map1.size() == map2.size()) {
			return getEquilongEuclideanDistance(map1, map2);
		} else {
			double result = -1;
			int size = map1.size();
			int length = map2.size() - map1.size() + 1;
			Map<Integer, Double> longerMap = map2;
			Map<Integer, Double> shorterMap = map1;
			if (map1.size() > map2.size()) {
				size = map2.size();
				length = map1.size() - map2.size() + 1;
				shorterMap = map2;
				longerMap = map1;
			}
			for (int i = 0; i < length; i++) {
				Map<Integer, Double> tempMap = new HashMap<Integer, Double>();
				for (int j = 0; j < size; j++) {
					tempMap.put(j, longerMap.get(j + i));
				}
				double tempResult = getEquilongEuclideanDistance(tempMap,
						shorterMap);
				if (result == -1) {
					result = tempResult;
				} else {
					if (result > tempResult) {
						result = tempResult;
					}
				}
			}
			return result;
		}
	}

	// 获取时间序列间欧式距离
	public static double getDistance(TimeSeries ts1, TimeSeries ts2)
			throws TimeSeriesNotEquilongException {
		return getEuclideanDistance(ts1.getMap(), ts2.getMap());
	}

	// 获取等长时间序列间欧式距离
	public static double getDistance2(TimeSeries ts1, TimeSeries ts2)
			throws TimeSeriesNotEquilongException {
		return getEquilongEuclideanDistance(ts1.getMap(), ts2.getMap());
	}

}
