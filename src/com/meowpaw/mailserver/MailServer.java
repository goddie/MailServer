package com.meowpaw.mailserver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.internet.MimeUtility;

import com.sun.mail.imap.IMAPFolder;

public class MailServer {

	public static void main(String[] args) {
		// Runnable runnable = new Runnable() {
		// public void run() {
		// // task to run goes here
		// System.out.println("Hello !!");
		// }
		// };

		Runnable runnable = new Runnable() {

			@Override
			public void run() {

				task();

			}
		};

		// ScheduledExecutorService service = Executors
		// .newSingleThreadScheduledExecutor();
		// // service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
		//
		// service.schedule(runnable, 30, TimeUnit.MINUTES);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				task();
				
				
				
			}
		});
		
		t.start();

//		executeEightAtNightPerDay("09:00:00");
//		executeEightAtNightPerDay("12:00:00");
//		executeEightAtNightPerDay("18:30:00");
//		executeEightAtNightPerDay("23:00:00");

		//FileUtil.getInstance().getNameList();
		
	}

	
	/**
	 * 定时执行任务
	 * @param hhmmss 时间格式
	 */
	public static void executeEightAtNightPerDay(String hhmmss) {
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		long oneDay = 24 * 60 * 60 * 1000;
		long initDelay = getTimeMillis(hhmmss) - System.currentTimeMillis();
		initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;

		executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				task();

			}
		}, initDelay, oneDay, TimeUnit.MILLISECONDS);
	}

	/**
	 * 获取指定时间对应的毫秒数
	 * 
	 * @param time
	 *            "HH:mm:ss"
	 * @return
	 */
	private static long getTimeMillis(String time) {
		try {
			DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
			Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " "
					+ time);
			return curDate.getTime();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 加入定时器
	 * 
	 * @param hours
	 *            小时
	 * @param mins
	 *            分钟
	 */
	static void addTimer(int hours, int mins) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				task();
			}
		};

		// 设置执行时间
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);// 每天
		// 定制每天的21:09:00执行，
		calendar.set(year, month, day, hours, mins, 00);
		Date date = calendar.getTime();
		Timer timer = new Timer(true);

		// 每天的date时刻执行task，每隔2秒重复执行
		// timer.schedule(task, date, period);
		// 每天的date时刻执行task, 仅执行一次
		timer.schedule(task, date);
	}

	static void task() {

		Set<Long> mailSets = MailUtil.getAllUID();
		Set<Long> fileSets = new HashSet<Long>();

		Set<Long> result = new HashSet<Long>();
		result.clear();

		result.addAll(mailSets);
		result.removeAll(fileSets);

		MailUtil.storeMailInFolder(result);
		FileUtil.getInstance().genIndex();
	}

}
