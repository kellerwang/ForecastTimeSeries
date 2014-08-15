package keller.util;

import java.util.Map;

public class Statistics {

	double[] data = null;

	public Statistics(Map<Integer, Double> map) {
		data = new double[map.size()];
		for (int i = 0; i < map.size(); i++) {
			data[i] = map.get(i);
		}
	}

	// 求数组数值和
	public double getSum() {
		if (data == null || data.length == 0)
			return -1;
		int len = data.length;
		double sum = 0;
		for (int i = 0; i < len; i++) {
			sum = sum + data[i];
		}
		return sum;
	}

	// 求平均值
	public double getAverage() {
		if (data == null || data.length == 0)
			return -1;
		int len = data.length;
		double result;
		result = getSum() / len;
		return result;
	}

	// 求平方和
	public double getSquareSum() {
		if (data == null || data.length == 0)
			return -1;
		int len = data.length;
		double sqrsum = 0.0;
		for (int i = 0; i < len; i++) {
			sqrsum = sqrsum + data[i] * data[i];
		}
		return sqrsum;
	}

	// 求方差
	public double getVariance() {
		if (data == null || data.length == 0)
			return -1;
		int len = data.length;
		double average = getAverage();
		double result = 0;
		for (int i = 0; i < len; i++) {
			result += (data[i] - average) * (data[i] - average);
		}
		return result / len;
	}

	// 求标准差
	public double getStandardDiviation() {
		if (data == null || data.length == 0)
			return -1;
		return Math.sqrt(Math.abs(getVariance()));
	}

}
