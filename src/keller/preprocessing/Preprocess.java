package keller.preprocessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import keller.model.TimeSeries;
import keller.util.DateCalculate;

public class Preprocess {

	// mit论文中增加波动趋势的方法
	public static void setSpikeNormalization1(TimeSeries old, double Alpha) {
		int i = 0;
		double previous = old.getMap().get(i);
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		for (; i < old.getMap().size(); i++) {
			double result = Math.pow(Math.abs(old.getMap().get(i) - previous),
					Alpha);
			map.put(i, result);
			previous = old.getMap().get(i);
		}
		old.setMap(map);
	}

	// 增加波动趋势的方法
	public static void setSpikeNormalization2(TimeSeries old, double Alpha) {
		int i = 0;
		double previous = old.getMap().get(i);
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		map.put(0, previous);
		i++;
		for (; i < old.getMap().size(); i++) {
			double result = Math.pow(Math.abs(old.getMap().get(i) - previous),
					Alpha);
			if (old.getMap().get(i) < previous) {
				result = 0 - result;
			}
			result = map.get(i - 1) + result;
			map.put(i, result);
		}
		old.setMap(map);
	}

	// 增加波动趋势的方法map类型
	public static void setSpikeNormalization2(Map<Integer, Double> old,
			double Alpha) {
		int i = 0;
		double previous = old.get(i);
		Map<Integer, Double> map = new HashMap<Integer, Double>();
		map.put(0, previous);
		i++;
		for (; i < old.size(); i++) {
			double result = Math.pow(Math.abs(old.get(i) - previous), Alpha);
			if (old.get(i) < previous) {
				result = 0 - result;
			}
			result = map.get(i - 1) + result;
			map.put(i, result);
		}
		old = map;
	}

	// 设置Baseline Normalization for TimeSeries
	public static void setBaselineNormalization(TimeSeries old, double beta) {
		Iterator iter = old.getMap().entrySet().iterator();
		double sum = 0;
		while (iter.hasNext()) {
			Entry<Integer, Double> entry = (Entry<Integer, Double>) iter.next();
			double val = entry.getValue();
			sum += val;
		}
		iter = old.getMap().entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, Double> entry = (Entry<Integer, Double>) iter.next();
			int key = entry.getKey();
			double val = entry.getValue();
			entry.setValue(Math.pow((val / sum), beta));
		}
	}

	// 设置Baseline Normalization for TimeSeries Map
	public static Map<Integer, Double> setBaselineNormalizationMap(
			Map<Integer, Double> old, double beta) {
		Iterator iter = old.entrySet().iterator();
		double sum = 0;
		while (iter.hasNext()) {
			Entry<Integer, Double> entry = (Entry<Integer, Double>) iter.next();
			double val = entry.getValue();
			sum += val;
		}
		Map<Integer, Double> newMap = new HashMap<Integer, Double>();
		iter = old.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, Double> entry = (Entry<Integer, Double>) iter.next();
			int key = entry.getKey();
			double val = entry.getValue();
			newMap.put(key, Math.pow((val / sum), beta));
		}
		return newMap;
	}

	// 截取时间序列的生命周期
	public static void setLifecycle(TimeSeries old) throws ParseException {
		int start = 0;
		for (; start < old.getMap().size(); start++) {
			if (old.getMap().get(start) != 0) {
				break;
			}
		}
		if (start != 0) {
			Map<Integer, Double> map = new HashMap<Integer, Double>();
			old.setStartDate(DateCalculate.addDate(old.getStartDate(), start));
			for (int i = 0; i < old.getMap().size() - start; i++) {
				map.put(i, old.getMap().get(i + start));
			}
			old.setMap(map);
		}
		int end = old.getMap().size() - 1;
		for (; end >= 0; end--) {
			if (old.getMap().get(end) != 0) {
				break;
			}
		}
		if (end != old.getMap().size() - 1) {
			Map<Integer, Double> map = new HashMap<Integer, Double>();
			old.setEndDate(DateCalculate.subDate(old.getEndDate(), (old
					.getMap().size() - 1 - end)));
			for (int i = end; i >= 0; i--) {
				map.put(i, old.getMap().get(i));
			}
			old.setMap(map);
		}

	}

	// 读取一个时间序列文件并存储在TimeSeries数据结构中
	public static TimeSeries initalTimeSeries(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			StringBuilder contentBuf = new StringBuilder();
			while ((tempString = reader.readLine()) != null) {
				contentBuf.append(tempString);
			}
			reader.close();
			String buf = new String(contentBuf.toString().getBytes());
			String[] tempArray = buf.split("\"");
			TimeSeries timeSeries = new TimeSeries();
			timeSeries.setKey(tempArray[3]);
			String[] tempDateArray = tempArray[7].split("\\|");
			timeSeries.setStartDate(tempDateArray[0]);
			timeSeries.setEndDate(tempDateArray[1]);
			String[] timeSeriesStrArray = tempArray[11].split(",");
			for (int i = 0; i < timeSeriesStrArray.length; i++) {
				timeSeries.getMap().put(i,
						Double.parseDouble(timeSeriesStrArray[i]));
			}
			return timeSeries;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
