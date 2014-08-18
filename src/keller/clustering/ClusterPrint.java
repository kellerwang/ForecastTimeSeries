package keller.clustering;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import keller.model.TimeSeries;
import keller.util.MyFileWriter;

public class ClusterPrint {

	// 打印簇元素名称到簇文件
	public static void printClusterElement(List<TimeSeries> data)
			throws IOException {
		Iterator<TimeSeries> it = data.iterator();
		while (it.hasNext()) {
			TimeSeries tempTS = it.next();
			MyFileWriter.writeToFileAppend(
					"result/data/cluster" + tempTS.getClusterNum(),
					tempTS.getKey() + "\n");
		}
	}

	// 打印中心曲线数值
	public static void printCentralCurve(Map<Integer, TimeSeries> centerMap,
			int repeatTime) throws IOException {
		for (int i = 0; i < centerMap.size(); i++) {
			Map<Integer, Double> map = centerMap.get(i).getMap();
			StringBuilder contentBuf = new StringBuilder();
			for (int j = 0; j < map.size(); j++) {
				contentBuf.append(map.get(j));
				if (j != (map.size() - 1)) {
					contentBuf.append(",");
				}
			}
			String buf = new String(contentBuf.toString());
			String fileName = "result/centralCurve/" + i + "repeatTime"
					+ repeatTime;
			MyFileWriter.writeToFile(fileName, buf);
		}
	}
}
