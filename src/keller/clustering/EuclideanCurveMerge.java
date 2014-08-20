package keller.clustering;

import java.util.HashMap;
import java.util.Map;

import keller.distance.EuclideanDisstance;
import keller.distance.STSDistance;
import keller.exception.TimeSeriesNotEquilongException;

public class EuclideanCurveMerge {

	// 获取两个时间序列Map的拟合Map，weight1和weight2分别代表两条时间序列Map的权重
	public static Map<Integer, Double> getMapCurveFitting(
			Map<Integer, Double> map1, Map<Integer, Double> map2,
			double weight1, double weight2)
			throws TimeSeriesNotEquilongException {
		if (map1.size() == map2.size()) {
			return getEqualMapCurveFitting(map1, map2, weight1, weight2);
		} else {
			double result = -1;
			int size = map1.size();
			int length = map2.size() - map1.size() + 1;
			Map<Integer, Double> longerMap = map2;
			Map<Integer, Double> shorterMap = map1;
			double tempWeight1 = weight2;
			double tempWeight2 = weight1;
			if (map1.size() > map2.size()) {
				size = map2.size();
				length = map1.size() - map2.size() + 1;
				shorterMap = map2;
				longerMap = map1;
				tempWeight1 = weight1;
				tempWeight2 = weight2;
			}
			Map<Integer, Double> newTempMap = null;
			int start = 0;
			for (int i = 0; i < length; i++) {
				Map<Integer, Double> tempMap = new HashMap<Integer, Double>();
				for (int j = 0; j < size; j++) {
					tempMap.put(j, longerMap.get(j + i));
				}
				double tempResult = EuclideanDisstance
						.getEquilongEuclideanDistance(tempMap, shorterMap);
				if (result == -1) {
					result = tempResult;
					newTempMap = tempMap;
				} else {
					if (result > tempResult) {
						result = tempResult;
						start = i;
						newTempMap = tempMap;
					}
				}
			}
			Map<Integer, Double> newMap = getEqualMapCurveFitting(newTempMap,
					shorterMap, tempWeight1, tempWeight2);
			Map<Integer, Double> resultMap = new HashMap<Integer, Double>();
			for (int j = 0; j < longerMap.size(); j++) {
				if (j >= start && j < (start + shorterMap.size())) {
					resultMap.put(j, newMap.get(j - start));
				} else {
					resultMap.put(j, longerMap.get(j));
				}
			}
			return resultMap;
		}
	}

	// 获取两个等长时间序列Map的拟合Map，weight1和weight2分别代表两条时间序列Map的权重
	public static Map<Integer, Double> getEqualMapCurveFitting(
			Map<Integer, Double> map1, Map<Integer, Double> map2,
			double weight1, double weight2)
			throws TimeSeriesNotEquilongException {
		if (map1.size() != map2.size()) {
			// 抛出异常
			throw new TimeSeriesNotEquilongException(
					"Two maps are not equilong!");
		} else {
			Map<Integer, Double> newMap = new HashMap<Integer, Double>();
			for (int i = 0; i < map1.size(); i++) {
				double tempValue = (map1.get(i) * weight1 + map2.get(i)
						* weight2)
						/ (weight1 + weight2);
				newMap.put(i, tempValue);
			}
			return newMap;
		}
	}

}
