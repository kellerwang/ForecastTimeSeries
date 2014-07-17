package keller.distance;

import java.util.HashMap;
import java.util.Map;

import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;
import keller.preprocessing.Preprocess;

public class Distance {

	// 获取等长时间序列距离
	public static double getEquilongTimeSeriesDistance(
			Map<Integer, Double> ts1, Map<Integer, Double> ts2)
			throws TimeSeriesNotEquilongException {
		if (ts1.size() != ts2.size()) {
			// 抛出异常
			throw new TimeSeriesNotEquilongException(
					"TimeSeries is not equilong!");
		} else {
			double sum = 0;
			for (int i = 0; i < ts1.size(); i++) {
				sum += Math.pow((ts1.get(i) - ts2.get(i)), 2);
			}
			return sum;
		}
	}

	// 获取不等长时间序列距离
	public static double getTimeSeriesDistance(Map<Integer, Double> ts1,
			Map<Integer, Double> ts2) throws TimeSeriesNotEquilongException {
		if (ts1.size() == ts2.size()) {
			Map<Integer, Double> newMap1 = Preprocess
					.setBaselineNormalizationMap(ts1, 1);
			Map<Integer, Double> newMap2 = Preprocess
					.setBaselineNormalizationMap(ts2, 1);
			return getEquilongTimeSeriesDistance(newMap1, newMap2);
		} else {
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
			}
			temp1 = Preprocess.setBaselineNormalizationMap(temp1, 1);
			for (int i = 0; i < length; i++) {
				Map<Integer, Double> newMap = new HashMap<Integer, Double>();
				for (int j = 0; j < size; j++) {
					newMap.put(j, temp2.get(j + i));
				}
				newMap = Preprocess.setBaselineNormalizationMap(newMap, 1);
				double temp = getEquilongTimeSeriesDistance(newMap, temp1);
				if (result == -1) {
					result = temp;
				} else {
					if (result > temp) {
						result = temp;
					}
				}
			}
			return result;
		}
	}

	// 获取时间序列间距离
	public static double getDistance(TimeSeries ts1, TimeSeries ts2)
			throws TimeSeriesNotEquilongException {
		return getTimeSeriesDistance(ts1.getMap(), ts2.getMap());
	}
}
