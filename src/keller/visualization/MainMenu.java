package keller.visualization;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import keller.clustering.CurveAdjust;
import keller.exception.DataNullException;
import keller.exception.NoCentralCurveException;
import keller.exception.TimeSeriesNotEquilongException;
import keller.main.MainInterface;
import keller.model.TimeSeries;
import JSci.awt.DefaultGraph2DModel;
import JSci.awt.Graph2D;
import JSci.swing.JGraphLayout;
import JSci.swing.JLineGraph;

public class MainMenu extends JFrame {

	private int k;
	private double threshold;
	private int repeat;
	private String filePath = null;
	private int minLongOfTS;
	private int choiceMethod = 0;
	private final Font titleFont = new Font("Default", Font.BOLD, 14);
	private Label title;
	private Map<Integer, DefaultGraph2DModel> valueModel = new HashMap<Integer, DefaultGraph2DModel>();
	private Panel myLineGraphPanel = null;
	private File file;
	private JTextField jtfFilePath;

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

	public Panel initCurveGraph(Map<Integer, Double> map, final int clusterNum) {
		// value graphs
		valueModel.put(clusterNum, createValueData(map));
		// line graph
		final JLineGraph lineGraph = new JLineGraph(valueModel.get(clusterNum));
		lineGraph.setColor(0, Color.red);
		lineGraph.setGridLines(true);
		lineGraph.setMarker(new Graph2D.DataMarker.Circle(5));
		Panel lineGraphPanel = new Panel(new JGraphLayout());
		title = new Label("Line graph" + clusterNum, Label.CENTER);
		title.setFont(titleFont);
		lineGraphPanel.add(title, JGraphLayout.TITLE);
		lineGraphPanel.add(lineGraph, JGraphLayout.GRAPH);
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

	public void CreateJFrame(String title) {// 定义一个方法
		JFrame jf = new JFrame(title);// 实例化一个对象
		final Container container = jf.getContentPane();// 获取一个容器
		container.setBackground(Color.CYAN);
		final GridBagLayout gb = new GridBagLayout();
		container.setLayout(gb);
		JPanel panel = new JPanel();
		final JLabel labelK = new JLabel("簇数：", JLabel.CENTER);
		final JTextField jtfK = new JTextField(5);
		JLabel labelThreshold = new JLabel("终止临界条件：", JLabel.CENTER);
		final JTextField jtfThreshold = new JTextField(5);
		JLabel labelRepeat = new JLabel("迭代次数：", JLabel.CENTER);
		final JTextField jtfRepeat = new JTextField(5);
		JLabel labelMinLongOfTS = new JLabel("最小时间序列长：", JLabel.CENTER);
		final JTextField jtfMinLongOfTS = new JTextField(5);
		jtfFilePath = new JTextField(5);
		jtfFilePath.setEditable(false);

		Choice choiceDistance = new Choice();
		choiceDistance.add("欧式距离");
		choiceDistance.add("STS距离");
		choiceDistance.add("欧式距离z_score");
		choiceDistance.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					if (evt.getItem().toString().equals("欧式距离")) {
						choiceMethod = 0;
					} else if (evt.getItem().toString().equals("STS距离")) {
						choiceMethod = 1;
					} else if (evt.getItem().toString().equals("欧式距离z_score")) {
						choiceMethod = 2;
					}
				}
			}
		});

		JButton buttonFilePath = new JButton("选择文件");
		buttonFilePath.addActionListener(new ActionListener() {// 为按钮添加鼠标单击事件
					public void actionPerformed(ActionEvent e) {
						do_button_actionPerformed(e);
					}
				});

		JButton button = new JButton("确定");
		button.addActionListener(new ActionListener() {// 为按钮添加鼠标单击事件
			public void actionPerformed(ActionEvent e) {
				if (!jtfK.getText().isEmpty()
						&& !jtfThreshold.getText().isEmpty()
						&& !jtfRepeat.getText().isEmpty()
						&& !jtfMinLongOfTS.getText().isEmpty()
						&& filePath != null) {
					k = Integer.parseInt(jtfK.getText());
					threshold = Double.parseDouble(jtfThreshold.getText());
					repeat = Integer.parseInt(jtfRepeat.getText());
					minLongOfTS = Integer.parseInt(jtfMinLongOfTS.getText());
					if (myLineGraphPanel != null) {
						container.remove(myLineGraphPanel);
						container.validate();
						container.repaint();
					}
					try {
						Map<Integer, TimeSeries> centerMap = null;
						switch (choiceMethod) {
						case 0:
							centerMap = MainInterface
									.showEuclideanClusteringResult1(k,
											threshold, repeat, filePath,
											minLongOfTS);
							break;
						case 1:
							centerMap = MainInterface
									.showSTSClusteringResult(k, threshold,
											repeat, filePath, minLongOfTS);
							break;
						case 2:
							centerMap = MainInterface
									.showEuclideanClusteringResult2(k,
											threshold, repeat, filePath,
											minLongOfTS);
							break;
						default:
							System.out.println("Nothing to do!");
							break;
						}
						if (centerMap != null) {
							GridBagConstraints gbc = new GridBagConstraints();
							gbc.weightx = 0.5;
							gbc.weighty = 0.5;

							for (int i = 0; i < centerMap.size(); i++) {
								gbc.fill = GridBagConstraints.BOTH;
								gbc.gridx = 0;
								if (i % 2 != 0) {
									gbc.gridx = 1;
								}
								gbc.gridy = i / 2 + 1;
								gbc.gridwidth = 1;

								myLineGraphPanel = initCurveGraph(centerMap
										.get(i).getMap(), i);
								gb.setConstraints(myLineGraphPanel, gbc);
								container.add(myLineGraphPanel);
							}
							container.validate();
							container.repaint();
						}
					} catch (ParseException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (DataNullException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (TimeSeriesNotEquilongException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NoCentralCurveException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});

		panel.add(buttonFilePath);
		panel.add(jtfFilePath);
		panel.add(labelK);
		panel.add(jtfK);
		panel.add(labelThreshold);
		panel.add(jtfThreshold);
		panel.add(labelRepeat);
		panel.add(jtfRepeat);
		panel.add(labelMinLongOfTS);
		panel.add(jtfMinLongOfTS);
		panel.add(choiceDistance);
		panel.add(button);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridheight = 1;
		gbc.gridwidth = 0;
		gbc.gridy = 0;
		gbc.gridx = 0;
		gb.setConstraints(panel, gbc);
		container.add(panel);
		jf.setVisible(true);// 可视化
		jf.setSize(1280, 960);// 设窗体大小
		jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	protected void do_button_actionPerformed(ActionEvent e) {
		JFileChooser chooser = new JFileChooser("./");// 创建文件选择器
		// 设置文件扩展名过滤器
		// chooser.setFileFilter(new FileNameExtensionFilter("文件夹"));
		// 设置文件选择模式
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// 显示文件打开对话框
		int option = chooser.showOpenDialog(this);
		// 确定用户按下打开按钮，而非取消按钮
		if (option != JFileChooser.APPROVE_OPTION)
			return;
		// 获取用户选择的文件对象
		File file = chooser.getSelectedFile();
		// 显示文件信息到文本框
		String temp = file.getPath();
		String[] tempArray = temp.split("\\./");
		jtfFilePath.setText(tempArray[1]);
		filePath = tempArray[1];
	}

	public static void main(String[] args) {// 在主方法中调用CreateJFrame（）方法
		new MainMenu().CreateJFrame("主程序");
	}
}
