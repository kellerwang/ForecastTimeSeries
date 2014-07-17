package keller.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MyRandom {
	public static Set<Integer> getRandomNonRepetitive(int k, int max) {
		Set<Integer> set = new HashSet<Integer>();
		Random random = new Random();
		while (set.size() < k) {
			set.add(random.nextInt(max));
		}
		return set;
	}
}
