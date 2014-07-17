package keller.main;

import java.text.ParseException;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import keller.distance.Distance;
import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;
import keller.preprocessing.Preprocess;
import keller.util.MyRandom;

public class MyTest {
	public static void main(String[] args) throws ParseException {
		// TimeSeries timeSeries1 = Preprocess
		// .initalTimeSeries("data/搜狗每日热词/广州火车站砍人");
		// Preprocess.setLifecycle(timeSeries1);
		// TimeSeries timeSeries2 =
		// Preprocess.initalTimeSeries("data/百度世说新词/00后");
		// Preprocess.setLifecycle(timeSeries2);
		// try {
		// double result = Distance.getDistance(timeSeries1, timeSeries2);
		// System.out.println(result);
		// } catch (TimeSeriesNotEquilongException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		Set<Integer> r = MyRandom.getRandomNonRepetitive(6, 100);
		Iterator<Integer> it = r.iterator();
		while (it.hasNext()) {
			int temp = it.next();
			System.out.println(temp);
		}
	}
}
