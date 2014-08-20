package keller.distance;

import java.util.Map;

import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;

public class DTWDistance {

	// 获取时间序列Map数组的DTW距离
	public static double getDTWDistance(Map<Integer, Double> map1,
			Map<Integer, Double> map2) {
		return 0;

	}

	// 获取时间序列间DTW距离
	public static double getDistance(TimeSeries ts1, TimeSeries ts2)
			throws TimeSeriesNotEquilongException {
		return getDTWDistance(ts1.getMap(), ts2.getMap());
	}

}
