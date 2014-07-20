package keller.visualization;

import java.awt.BasicStroke;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import keller.clustering.CurveAdjust;
import keller.exception.TimeSeriesNotEquilongException;
import keller.model.TimeSeries;
import JSci.awt.DefaultGraph2DModel;
import JSci.awt.Graph2D;
import JSci.swing.JGraphLayout;
import JSci.swing.JLineGraph;

public class CurveGraph extends Frame {

	private final Font titleFont = new Font("Default", Font.BOLD, 14);
	private Label title;
	private Map<Integer, DefaultGraph2DModel> valueModel = new HashMap<Integer, DefaultGraph2DModel>();

	private static DefaultGraph2DModel createValueData(
			Map<Integer, Double> map, List<TimeSeries> dataCluster) {
		double values[] = new double[map.size()];
		for (int i = 0; i < map.size(); i++) {
			values[i] = map.get(i);
		}
		DefaultGraph2DModel model = new DefaultGraph2DModel();
		model.setXAxis(0.0f, values.length, values.length);
		model.addSeries(values);
		for (int i = 0; i < dataCluster.size(); i++) {
			Map<Integer, Double> tempMap = dataCluster.get(i).getMap();
			double valueNew[] = new double[tempMap.size()];
			for (int j = 0; j < tempMap.size(); j++) {
				valueNew[j] = tempMap.get(j);
			}
			model.addSeries(valueNew);
			model.setSeriesVisible((i + 1), false);
		}
		return model;
	}

	public Panel initCurveGraph(Map<Integer, Double> map,
			List<TimeSeries> dataCluster, final int clusterNum) {
		// value graphs
		final int size = dataCluster.size();
		valueModel.put(clusterNum, createValueData(map, dataCluster));
		// line graph
		final JLineGraph lineGraph = new JLineGraph(valueModel.get(clusterNum));
		lineGraph.setColor(0, Color.red);
		lineGraph.setGridLines(true);
		lineGraph.setMarker(new Graph2D.DataMarker.Circle(5));
		final Panel lineGraphPanel = new Panel(new JGraphLayout());
		title = new Label("Line graph" + clusterNum, Label.CENTER);
		title.setFont(titleFont);
		lineGraphPanel.add(title, JGraphLayout.TITLE);
		lineGraphPanel.add(lineGraph, JGraphLayout.GRAPH);
		Choice choice = new Choice();
		choice.add("centre");
		choice.add("all");
		choice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					if (evt.getItem().toString().equals("centre")) {
//						valueModel.get(clusterNum).setSeriesVisible(0, true);
						for (int i = 0; i < size; i++) {
							valueModel.get(clusterNum).setSeriesVisible(
									(i + 1), false);
						}
					} else if (evt.getItem().toString().equals("all")) {
//						valueModel.get(clusterNum).setSeriesVisible(0, true);
						for (int i = 0; i < size; i++) {
							valueModel.get(clusterNum).setSeriesVisible(
									(i + 1), true);
							lineGraph.setColor((i + 1), Color.blue);
						}
					}
				}
			}
		});
		lineGraphPanel.add(choice, JGraphLayout.Y_AXIS);
		lineGraphPanel.add(new Label("x-axis", Label.CENTER),
				JGraphLayout.X_AXIS);
		return lineGraphPanel;
	}

	// 获得一个簇
	public List<TimeSeries> getOneCluster(int k, List<TimeSeries> data) {
		Iterator<TimeSeries> iter = data.iterator();
		List<TimeSeries> newList = new ArrayList<TimeSeries>();
		while (iter.hasNext()) {
			TimeSeries tempTS = iter.next();
			if (tempTS.getClusterNum() == k) {
				newList.add(tempTS);
			}
		}
		return newList;
	}

	public CurveGraph(Map<Integer, TimeSeries> centerMap, List<TimeSeries> data)
			throws TimeSeriesNotEquilongException {
		super("JSci Graph Demo");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				dispose();
				System.exit(0);
			}
		});
		setSize(1280, 960);
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(gb);
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;

		for (int i = 0; i < centerMap.size(); i++) {
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 0;
			if (i % 2 != 0) {
				gbc.gridx = 1;
			}
			gbc.gridy = i / 2;
			gbc.gridwidth = 1;
			List<TimeSeries> newList = getOneCluster(i, data);
			CurveAdjust.setCurveAdjust(newList, centerMap.get(i).getMap());
			final Panel lineGraphPanel = initCurveGraph(centerMap.get(i)
					.getMap(), newList, i);
			gb.setConstraints(lineGraphPanel, gbc);
			add(lineGraphPanel);
		}
		setVisible(true);
	}
}
