package keller.clustering;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import keller.distance.Distance;
import keller.exception.TimeSeriesNotEquilongException;
import keller.preprocessing.Preprocess;

public class CurveFitting {

	public static Map<Integer, Double> getEqualTimeSeriesMapFittingEquilong(
			Map<Integer, Double> ts1, Map<Integer, Double> ts2, double weight,
			boolean flag) {
		Map<Integer, Double> newMap = new HashMap<Integer, Double>();
		if (flag) {
			for (int i = 0; i < ts1.size(); i++) {
				newMap.put(i,
						((ts1.get(i) * weight + ts2.get(i)) / (weight + 1)));
			}
		} else {
			for (int i = 0; i < ts1.size(); i++) {
				newMap.put(i,
						((ts1.get(i) + ts2.get(i) * weight) / (weight + 1)));
			}
		}
		return newMap;
	}

	public static Map<Integer, Double> getEqualTimeSeriesMapFitting(
			Map<Integer, Double> ts1, Map<Integer, Double> ts2, double weight) {
		Map<Integer, Double> newMap1 = Preprocess.setBaselineNormalizationMap(
				ts1, 1);
		Map<Integer, Double> newMap2 = Preprocess.setBaselineNormalizationMap(
				ts2, 1);
		Map<Integer, Double> newMap = getEqualTimeSeriesMapFittingEquilong(
				newMap1, newMap2, weight, true);
		double multiple = ts1.get(0) / newMap1.get(0);
		Iterator iter = newMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, Double> entry = (Entry<Integer, Double>) iter.next();
			entry.setValue(entry.getValue() * multiple);
		}
		return newMap;
	}

	// 获取两个时间序列Map的拟合Map，前者是已经拟合的曲线，weight是已经拟合的曲线数量
	public static Map<Integer, Double> getTimeSeriesMapFitting(
			Map<Integer, Double> ts1, Map<Integer, Double> ts2, double weight)
			throws TimeSeriesNotEquilongException {
		if (ts1.size() == ts2.size()) {
			return getEqualTimeSeriesMapFitting(ts1, ts2, weight);
		} else {
			boolean flag = false;
			double result = -1;
			int size = ts1.size();
			int length = ts2.size() - ts1.size() + 1;
			Map<Integer, Double> temp1 = ts1;
			Map<Integer, Double> temp2 = ts2;
			if (ts1.size() > ts2.size()) {
				size = ts2.size();
				length = ts1.size() - ts2.size() + 1;
				temp1 = ts2;
				temp2 = ts1;
				flag = true;
			}
			temp1 = Preprocess.setBaselineNormalizationMap(temp1, 1);
			Map<Integer, Double> newTempMap = null;
			int start = 0;
			for (int i = 0; i < length; i++) {
				Map<Integer, Double> newMap = new HashMap<Integer, Double>();
				for (int j = 0; j < size; j++) {
					newMap.put(j, temp2.get(j + i));
				}
				newMap = Preprocess.setBaselineNormalizationMap(newMap, 1);
				double temp = Distance.getEquilongTimeSeriesDistance(newMap,
						temp1);
				if (result == -1) {
					result = temp;
					newTempMap = newMap;
				} else {
					if (result > temp) {
						result = temp;
						start = i;
						newTempMap = newMap;
					}
				}
			}
			double multiple;
			int i = start;
			Map<Integer, Double> temp3 = ts2;
			if (ts1.size() > ts2.size()) {
				temp3 = ts1;
			}
			while (temp3.get(i) == 0) {
				i++;
			}
			multiple = temp3.get(i) / newTempMap.get(i - start);
			Map<Integer, Double> temp4 = getEqualTimeSeriesMapFittingEquilong(
					newTempMap, temp1, weight, flag);
			Iterator iter = temp4.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<Integer, Double> entry = (Entry<Integer, Double>) iter
						.next();
				entry.setValue(entry.getValue() * multiple);
			}
			Map<Integer, Double> resultMap = new HashMap<Integer, Double>();
			for (int j = 0; j < temp3.size(); j++) {
				if (j >= start && j < (start + temp4.size())) {
					resultMap.put(j, temp4.get(j - start));
				} else {
					resultMap.put(j, temp3.get(j));
				}
			}
			return resultMap;
		}
	}
}
