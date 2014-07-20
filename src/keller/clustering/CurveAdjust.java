package keller.clustering;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import keller.distance.Distance;
import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;
import keller.preprocessing.Preprocess;

public class CurveAdjust {

	// 按照簇中心曲线矫正簇中其他曲线的位置
	public static void setCurveAdjust(List<TimeSeries> dataCluster,
			Map<Integer, Double> centre) throws TimeSeriesNotEquilongException {
		Iterator iter = dataCluster.iterator();
		while (iter.hasNext()) {
			TimeSeries entry = (TimeSeries) iter.next();
			entry.setMap(setCurveAdjustMap(entry.getMap(), centre));
		}
	}

	// 按照簇中心曲线矫正曲线的位置
	public static Map<Integer, Double> setCurveAdjustMap(
			Map<Integer, Double> ts, Map<Integer, Double> centre)
			throws TimeSeriesNotEquilongException {
		if (ts.size() == centre.size()) {
			return Preprocess.setBaselineNormalizationMap(ts, 1);
		} else {
			boolean flag = false;
			double result = -1;
			int size = ts.size();
			int length = centre.size() - ts.size() + 1;
			ts = Preprocess.setBaselineNormalizationMap(ts, 1);
			int start = 0;
			for (int i = 0; i < length; i++) {
				Map<Integer, Double> newMap = new HashMap<Integer, Double>();
				for (int j = 0; j < size; j++) {
					newMap.put(j, centre.get(j + i));
				}
				newMap = Preprocess.setBaselineNormalizationMap(newMap, 1);
				double temp = Distance
						.getEquilongTimeSeriesDistance(newMap, ts);
				if (result == -1) {
					result = temp;
				} else {
					if (result > temp) {
						result = temp;
						start = i;
					}
				}
			}
			Map<Integer, Double> resultMap = new HashMap<Integer, Double>();
			for (int j = 0; j < centre.size(); j++) {
				if (j >= start && j < (start + ts.size())) {
					resultMap.put(j, ts.get(j - start));
				} else {
					resultMap.put(j, (double) 0);
				}
			}
			return resultMap;
		}
	}
}
