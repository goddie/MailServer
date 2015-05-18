package com.meowpaw.mailserver;

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
		// ScheduledExecutorService service = Executors
		// .newSingleThreadScheduledExecutor();
		// service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);

		Runnable runnable = new Runnable() {

			@Override
			public void run() {

				Set<Long> mailSets = MailUtil.getAllUID();
				Set<Long> fileSets = new HashSet<Long>();

				Set<Long> result = new HashSet<Long>();
				result.clear();

				result.addAll(mailSets);
				result.removeAll(fileSets);

				MailUtil.storeMailInFolder(result);

				
			}
		};

		Thread t = new Thread(runnable);
		t.start();

	}

}
