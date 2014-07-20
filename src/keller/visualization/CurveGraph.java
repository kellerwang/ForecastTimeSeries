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
import java.util.Map;

import javax.swing.JFrame;

import keller.model.TimeSeries;
import JSci.awt.DefaultGraph2DModel;
import JSci.awt.Graph2D;
import JSci.swing.JGraphLayout;
import JSci.swing.JLineGraph;

public class CurveGraph extends Frame {

	private DefaultGraph2DModel valueModel;
	private final Font titleFont = new Font("Default", Font.BOLD, 14);
	private Label title;

	private static DefaultGraph2DModel createValueData(Map<Integer, Double> map) {
		double values[] = new double[map.size()];
		for (int i = 0; i < map.size(); i++) {
			values[i] = map.get(i);
		}
		DefaultGraph2DModel model = new DefaultGraph2DModel();
		model.setXAxis(0.0f, values.length, values.length);
		model.addSeries(values);
		return model;
	}

	public Panel initCurveGraph(Map<Integer, Double> map, int clusterNum) {
		// value graphs
		valueModel = createValueData(map);
		// line graph
		JLineGraph lineGraph = new JLineGraph(valueModel);
		lineGraph.setColor(0, Color.red);
		lineGraph.setColor(1, Color.yellow);
		lineGraph.setGridLines(true);
		lineGraph.setMarker(new Graph2D.DataMarker.Circle(5));
		final Panel lineGraphPanel = new Panel(new JGraphLayout());
		title = new Label("Line graph" + clusterNum, Label.CENTER);
		title.setFont(titleFont);
		lineGraphPanel.add(title, JGraphLayout.TITLE);
		lineGraphPanel.add(lineGraph, JGraphLayout.GRAPH);
		lineGraphPanel.add(new Label("x-axis", Label.CENTER),
				JGraphLayout.X_AXIS);
		return lineGraphPanel;
	}

	public CurveGraph(Map<Integer, TimeSeries> centerMap) {
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
			final Panel lineGraphPanel = initCurveGraph(centerMap.get(i)
					.getMap(), i);
			gb.setConstraints(lineGraphPanel, gbc);
			add(lineGraphPanel);
		}
		setVisible(true);
	}
}
