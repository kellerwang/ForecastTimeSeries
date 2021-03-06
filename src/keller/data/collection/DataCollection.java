package keller.data.collection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import keller.model.TimeSeries;
import keller.util.MyFileWriter;

public class DataCollection {
	// 抓取搜狗指数网页数据
	public static String captureHtmlTimeSeries(String keyStr)
			throws IOException {
		String strURL = "http://zhishu.sogou.com/sidx?type=0&domain=-1&query="
				+ URLEncoder.encode(keyStr, "UTF-8");
		URL url = new URL(strURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		InputStreamReader input = new InputStreamReader(
				httpConn.getInputStream(), "gb2312");
		BufferedReader bufReader = new BufferedReader(input);
		String line = "";
		StringBuilder contentBuf = new StringBuilder();
		while ((line = bufReader.readLine()) != null) {
			contentBuf.append(line);
		}
		String buf = new String(contentBuf.toString().getBytes("gb2312"),
				"gb2312");
		int beginIx = buf.indexOf("\"key\":");
		if (beginIx == -1) {
			System.out.println("captureHtml()的结果：\n" + buf);
			return null;
		} else {
			int endIx = buf.indexOf(",\"isNull\"");
			String result = buf.substring(beginIx, endIx);
			System.out.println("captureHtml()的结果：\n" + result);
			return result;
		}
	}

	// 获取搜狗每日热搜榜数据
	public static void getSouGouHot(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int index = 0;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				if (index == 0) {
					String dateStr = tempString;
					System.out.println(dateStr);
				} else {
					if (index == 11) {
						String dateStr = tempString;
						index = 0;
						System.out.println(dateStr);
					} else {
						String hotword;
						if (index == 10) {
							hotword = tempString.substring(2);
						} else {
							hotword = tempString.substring(1);
						}
						// System.out.println(hotword);
						String result = captureHtmlTimeSeries(hotword);
						if (result != null) {
							MyFileWriter.writeToFile("out/" + hotword, result);
						} else {
							System.out.println("ERROR!");
						}
					}
				}
				index++;
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	// 获取百度搜索风云榜数据
	public static void getBaiduHot(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				String[] tempArray = tempString.split("\t");
				String hotword = tempArray[1].replace("search", "");
				String result = captureHtmlTimeSeries(hotword);
				if (result != null) {
					MyFileWriter.writeToFile("out/" + hotword, result);
				} else {
					System.out.println("ERROR!");
				}
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	// 获取搜狗词库网络流行词数据
	public static void getPopularWord(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				String hotword = tempString.trim();
				String result = captureHtmlTimeSeries(hotword);
				if (result != null) {
					MyFileWriter.writeToFile("out/" + hotword, result);
				} else {
					System.out.println("ERROR!");
				}
				line++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}

	// 抓取http://www.chinarank.org.cn/网页数据
	public static void captureHtmlPaiMing() throws IOException {
		List<String> list = new ArrayList();
		for (int i = 1; i < 6; i++) {
			String strURL = "http://www.chinarank.org.cn/top100/Rank.do?page="
					+ i;
			URL url = new URL(strURL);
			HttpURLConnection httpConn = (HttpURLConnection) url
					.openConnection();
			InputStreamReader input = new InputStreamReader(
					httpConn.getInputStream(), "gb2312");
			BufferedReader bufReader = new BufferedReader(input);
			String line = "";
			StringBuilder contentBuf = new StringBuilder();
			while ((line = bufReader.readLine()) != null) {
				contentBuf.append(line);
			}
			String buf = new String(contentBuf.toString().getBytes("gb2312"),
					"gb2312");
			String[] tempArray = buf.split("class=\"rank_link_g02\">|查看排名信息");
			for (int j = 0; j < tempArray.length; j++) {
				if ((j & 1) != 0) {
					if (j != tempArray.length - 1) {
						String[] tempArray2 = tempArray[j].split("</a>");
						list.add(tempArray2[0]);
					}
				}
			}
		}
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) {
			String temp = iter.next();
			System.out.println(temp);
		}
	}

	public static void main(String[] args) throws IOException {
		getPopularWord("wordbank/中国网站排名top100");
	}
}
