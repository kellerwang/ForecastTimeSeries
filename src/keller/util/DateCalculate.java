package keller.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateCalculate {
	// 日期加天数
	public static String addDate(String strOld, long day) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date oldDate = sdf.parse(strOld);
		long time = oldDate.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time += day;
		Date newDate = new Date(time);
		String strNew = sdf.format(newDate);
		return strNew;
	}
	
	// 日期减天数
	public static String subDate(String strOld, long day) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date oldDate = sdf.parse(strOld);
		long time = oldDate.getTime();
		day = day * 24 * 60 * 60 * 1000;
		time -= day;
		Date newDate = new Date(time);
		String strNew = sdf.format(newDate);
		return strNew;
	}

	// 计算日期差，输入格式yyyy-MM-dd
	public static long getQuot(String time1, String time2) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return quot;
	}
	
	public static void main(String[] args) throws ParseException{
	}
}
