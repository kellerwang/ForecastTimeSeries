package keller.model;

import java.util.HashMap;
import java.util.Map;

public class TimeSeries {
	private String key;
	private String startDate = null;
	private String endDate = null;
	private Map<Integer, Double> map = null;
	private int clusterNum;

	public TimeSeries() {
		map = new HashMap<Integer, Double>();
	}

	public TimeSeries(Map<Integer, Double> old) {
		this.map = new HashMap<Integer, Double>(old);
	}

	public int getClusterNum() {
		return clusterNum;
	}

	public void setClusterNum(int clusterNum) {
		this.clusterNum = clusterNum;
	}

	public void setMap(Map<Integer, Double> map) {
		this.map = map;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public Map<Integer, Double> getMap() {
		return map;
	}
}
