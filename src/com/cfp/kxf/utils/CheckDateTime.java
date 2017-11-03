package com.cfp.kxf.utils;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.cfp.kxf.logs.LogUtils;



public class CheckDateTime {
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
//	public static void main(String[] args) {
//		isValidTime(1509678079000L, 0, 0, 0, 0, 8, 0);
//	}
	/**
	 * 判断是否在有效期，需要访问网络
	 * @param startTimeInMillis
	 * @param yearCount
	 * @param monthCount
	 * @param dayCount
	 * @param hrsCount
	 * @param minCount
	 * @param secCount
	 * @return
	 */
	public static boolean isValidTime(long startTimeInMillis, int yearCount,
			int monthCount, int dayCount, int hrsCount, int minCount,
			int secCount) {
		boolean re = false;
		// 创建 Calendar 对象
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(startTimeInMillis);
		// 按特定格式显示刚设置的时间
		LogUtils.getDefaultInstance().d("startTimeInMillis:" + sdf.format(calendar.getTime()));
		if (yearCount > 0) {
			calendar.add(Calendar.YEAR, yearCount);
		}
		if (monthCount > 0) {
			calendar.add(Calendar.MONTH, monthCount);
		}
		if (dayCount > 0) {
			calendar.add(Calendar.DAY_OF_MONTH, dayCount);
		}
		if (hrsCount > 0) {
			calendar.add(Calendar.HOUR, hrsCount);
		}
		if (minCount > 0) {
			calendar.add(Calendar.MINUTE, minCount);
		}
		if (secCount > 0) {
			calendar.add(Calendar.SECOND, secCount);
		}
		long endTime = calendar.getTimeInMillis();
		LogUtils.getDefaultInstance().d("end Time:" + sdf.format(endTime));
		long currentTime = getTimeCurr();
		LogUtils.getDefaultInstance().d("current Time:" + sdf.format(currentTime));
		if (currentTime<endTime) {
			re = true;
		}
		LogUtils.getDefaultInstance().d("re=" + re);
		return re;
	}
	
	public static long getTimeCurr() {
        URLConnection uc = null;
        long ld = System.currentTimeMillis();
        try {
            URL url = new URL("http://www.baidu.com");// 取得资源对象
            uc = url.openConnection();
            uc.connect(); // 发出连接
            ld = uc.getDate(); // 取得网站日期时间
        } catch (Exception e) {
            LogUtils.getDefaultInstance().e("获取网络时间失败！", e);
        }
        LogUtils.getDefaultInstance().d("current Time:" + ld);
        return ld;
    }
}
