package com.meowpaw.mailserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Security;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

/**
 * @author goddie
 *
 */
public class MailUtil {

	/*
	 * 协议 服务器地址 服务器端口号（常规） 服务器端口号（加密） POP3 pop3.mxhichina.com 110 995 SMTP
	 * smtp.mxhichina.com 25 465 IMAP imap.mxhichina.com 143 993
	 */
	private static String user = "goddie@meowpaw.com";
	private static String pwd = "BBBbbb222";
	private static Boolean ssl = true;

	void init() {

	}

	/**
	 * 接收所有邮件
	 */
	public static void storeMails() {

		Properties props = new Properties();
//		if (ssl) {
//			// 使用ssl才要加
//			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
//			props.setProperty("mail.pop3.socketFactory.class",
//					"javax.net.ssl.SSLSocketFactory");
//			props.setProperty("mail.pop3.socketFactory.fallback", "false");
//		}

		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(true);

		Store store = null;
		Folder folder = null;

		try {

			URLName urln = new URLName("pop3", "pop3.mxhichina.com", 110, null,
					user, pwd);

			store = session.getStore(urln);

			// 利用Session对象获得Store对象，并连接pop3服务器
			store.connect();

			// 获得邮箱内的邮件夹Folder对象，以"读-写"打开
			folder = store.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);

			// // 获得邮件夹Folder内的所有邮件Message对象
			Message[] messages = folder.getMessages();

			for (int i = 0; i < 5; i++) {
				System.out.println(messages[i].getSubject());
			}

		} catch (Exception e) {
			// TODO: handle exception
		} finally {

			try {
				if (folder != null) {
					folder.close(true);
				}
				store.close();
			} catch (MessagingException e) {

			}
		}

	}

	/**
	 * 获取所有UID
	 * 
	 * @return
	 */
	public static Set<Long> getAllUID() {
		String imapserver = "imap.mxhichina.com"; // 邮件服务器
		// 获取默认会话
		Properties prop = System.getProperties();
		prop.put("mail.imap.host", imapserver);
		prop.put("mail.imap.auth.plain.disable", "true");

		Session mailsession = Session.getInstance(prop, null);
		mailsession.setDebug(false); // 是否启用debug模式
		IMAPFolder folder = null;
		IMAPStore store = null;

		Set<Long> set = new HashSet<Long>();

		try {
			store = (IMAPStore) mailsession.getStore("imap"); // 使用imap会话机制，连接服务器
			store.connect(imapserver, user, pwd);
			folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
			// 使用只读方式打开收件箱
			folder.open(Folder.READ_WRITE);
			// 获取总邮件数
			// total = folder.getMessageCount();
			// System.out.println("-----------------您的邮箱共有邮件：" + total
			// + " 封--------------");
			// // 得到收件箱文件夹信息，获取邮件列表
			// Message[] msgs = folder.getMessages();
			// System.out.println("\t收件箱的总邮件数：" + msgs.length);
			// System.out.println("\t未读邮件数：" + folder.getUnreadMessageCount());
			// System.out.println("\t新邮件数：" + folder.getNewMessageCount());
			// System.out.println("----------------End------------------");

			Message[] messages = folder.getMessages();

			for (int i = 0; i < messages.length; i++) {

				MimeMessage mimeMessage = (MimeMessage) messages[i];

				set.add(folder.getUID(mimeMessage));

				// String uid = Long.toString(folder.getUID(mimeMessage));
				// System.out.println(mimeMessage.getSubject() +uid);

			}

		} catch (MessagingException ex) {

			ex.printStackTrace();
		} finally {
			// 释放资源
			try {
				if (folder != null)
					folder.close(true); // 退出收件箱时,删除做了删除标识的邮件
				if (store != null)
					store.close();
			} catch (Exception bs) {
				bs.printStackTrace();
			}
		}

		return set;

	}

	/**
	 * 获取所有UID
	 * 
	 * @return
	 */
	public static void storeMailInFolder(Set<Long> uids) {
		String imapserver = "imap.mxhichina.com"; // 邮件服务器
		// 获取默认会话
		Properties prop = System.getProperties();
		prop.put("mail.imap.host", imapserver);
		prop.put("mail.imap.auth.plain.disable", "true");

		Session mailsession = Session.getInstance(prop, null);
		mailsession.setDebug(false); // 是否启用debug模式
		IMAPFolder folder = null;
		IMAPStore store = null;

		try {
			store = (IMAPStore) mailsession.getStore("imap"); // 使用imap会话机制，连接服务器
			store.connect(imapserver, user, pwd);
			folder = (IMAPFolder) store.getFolder("INBOX"); // 收件箱
			// 使用只读方式打开收件箱
			folder.open(Folder.READ_WRITE);

			Long[] nn = uids.toArray(new Long[0]);
			long[] n = new long[nn.length];
			for (int i = 0; i < nn.length; i++) {
				n[i] = nn[i];
			}

			// messages = folder.getMessages();
			final Message[] messages = folder.getMessagesByUID(n);

			// //把邮件存入本地文件
			// TaskManager.getInstance().addTask(new Runnable() {
			//
			// @Override
			// public void run() {
			// FileUtil.saveFile(messages[0]);
			//
			// }
			// });
			
			for (int i = 0; i < messages.length; i++) {
				saveFile(messages[i]);
			}
			
			

			// try {
			// for (int i = 0; i < n.length; i++) {
			// System.out.println( "UID="+ i +" " + messages[i].getSubject());
			// }
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

		} catch (MessagingException ex) {

			ex.printStackTrace();
		} finally {
			// 释放资源
			try {
				if (folder != null)
					folder.close(true); // 退出收件箱时,删除做了删除标识的邮件
				if (store != null)
					store.close();
			} catch (Exception bs) {
				bs.printStackTrace();
			}
		}

	}

	public static void saveFile(Message message) {

		try {

			InternetAddress address[] = (InternetAddress[]) message.getFrom();
			// System.out.println(address[0].getPersonal());

			StringBuffer sb = new StringBuffer();

			sb.append(System.getProperty("user.dir"));
			sb.append(System.getProperty("file.separator"));
			sb.append("MailBox");
			sb.append(System.getProperty("file.separator"));
			sb.append(address[0].getPersonal());
			sb.append(System.getProperty("file.separator"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			sb.append(sdf.format(message.getSentDate()));

			// System.out.println(System.getProperty("user.dir"));
			File file = new File(sb.toString());

			if (!file.getParentFile().exists()) {
				// 如果目标文件所在的目录不存在，则创建父目录
				System.out.println("目标文件所在目录不存在，准备创建它！");
				if (!file.getParentFile().mkdirs()) {
					System.out.println("创建目标文件所在目录失败！");
				}
			}

			file.mkdirs();

			System.out.println(sb.toString());

			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
			
			
			sb.append(System.getProperty("file.separator"));
			sb.append(sdf2.format(message.getSentDate()));
			sb.append(message.getSubject().replace("/", ""));
			sb.append(".html");

			File file2 = new File(sb.toString());

			StringBuffer content = new StringBuffer();
			getMailTextContent((Part) message, content);
			saveHTML(file2, content.toString());

			System.out.println(content.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获得邮件文本内容
	 * 
	 * @param part
	 *            邮件体
	 * @param content
	 *            存储邮件文本内容的字符串
	 * @throws MessagingException
	 * @throws IOException
	 */
	public static void getMailTextContent(Part part, StringBuffer content) {

		try {
			// 如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
			boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
			if (part.isMimeType("text/*") && !isContainTextAttach) {
				content.append(part.getContent().toString());
			} else if (part.isMimeType("message/rfc822")) {
				getMailTextContent((Part) part.getContent(), content);
			} else if (part.isMimeType("multipart/*")) {
				Multipart multipart = (Multipart) part.getContent();
				int partCount = multipart.getCount();
				for (int i = 0; i < partCount; i++) {
					BodyPart bodyPart = multipart.getBodyPart(i);
					getMailTextContent(bodyPart, content);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void saveHTML(File file, String content) throws Exception {
		/**
		 * 创建一个可以往文件中写入字符数据的字符流输出流对象 创建时必须明确文件的目的地 如果文件不存在，这回自动创建。如果文件存在，则会覆盖。
		 * 当路径错误时会抛异常
		 * 
		 * 当在创建时加入true参数，回实现对文件的续写。
		 */
		FileWriter fw = new FileWriter(file);

		fw.write(content);
		/**
		 * 进行刷新，将字符写到目的地中。
		 */
		// fw.flush();
		/**
		 * 关闭流，关闭资源。在关闭前会调用flush方法 刷新缓冲区。关闭后在写的话，会抛IOException
		 */
		fw.close();

	}

}
