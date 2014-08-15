package keller.preprocessing;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import keller.model.TimeSeries;
import keller.util.Statistics;

public class NewPreprocess {
	// 对TimeSeries数据结构中Map数组做zscore标准化改造
	public static void z_scoreTimeSeries(TimeSeries ts) {
		z_score(ts.getMap());
	}

	// 对Map数组做zscore标准化改造
	public static void z_score(Map<Integer, Double> map) {
		Statistics statistics = new Statistics(map);
		double average = statistics.getAverage();
		double standardDiviation = statistics.getStandardDiviation();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, Double> entry = (Entry<Integer, Double>) iter.next();
			entry.setValue((entry.getValue() - average) / standardDiviation);
		}
	}
}
