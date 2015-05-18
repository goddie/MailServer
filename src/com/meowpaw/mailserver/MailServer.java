package com.meowpaw.mailserver;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
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

				Calendar calendar = Calendar.getInstance();

				int hour = calendar.get(Calendar.HOUR_OF_DAY);

				if (hour != 9 || hour != 23) {
					return;
				}

				task();

				// MailUtil.storeMails();

				// System.out.println(FileUtil.getInstance().getTempHash());

			}
		};

		ScheduledExecutorService service = Executors
				.newSingleThreadScheduledExecutor();
		// service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);

		service.schedule(runnable, 30, TimeUnit.MINUTES);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				task();
			}
		});
		t.start();

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
