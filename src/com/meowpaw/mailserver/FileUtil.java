package com.meowpaw.mailserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class FileUtil {

	private Hashtable<String, String> tempHash = new Hashtable<String, String>();

	public Hashtable<String, String> getTempHash() {
		return tempHash;
	}

	// 私有的默认构造子
	private FileUtil() {
		loadAllTemplates();
	}

	// 注意，这里没有final
	private static FileUtil single = null;

	// 静态工厂方法
	public static FileUtil getInstance() {
		if (single == null) {
			single = new FileUtil();
		}
		return single;
	}

	/**
	 * 缓存所有模板
	 */
	private void loadAllTemplates() {
		File root = new File("Web/Templates");
		File[] files = root.listFiles();

		for (File file : files) {
			System.out.println(file.getAbsolutePath());

			try {
				String encoding = "UTF-8";
				if (file.isFile() && file.exists()) { // 判断文件是否存在
					InputStreamReader read = new InputStreamReader(
							new FileInputStream(file), encoding);// 考虑到编码格式
					BufferedReader bufferedReader = new BufferedReader(read);
					String lineTxt = null;

					StringBuilder sb = new StringBuilder();

					while ((lineTxt = bufferedReader.readLine()) != null) {
						// System.out.println(lineTxt);
						sb.append(lineTxt);
					}
					read.close();

					tempHash.put(file.getName(), sb.toString());

				} else {
					System.out.println("找不到指定的文件");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public void saveHTML(File file, String content) throws Exception {
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

	/**
	 * 生成目录首页
	 */
	public void genIndex() {

		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.dir"));
		sb.append(System.getProperty("file.separator"));
		sb.append("Web");
		sb.append(System.getProperty("file.separator"));
		sb.append("MailBox");

		File root = new File(sb.toString());
		File[] filesName = root.listFiles();

		StringBuilder sbTree = new StringBuilder();

		try {
			for (File name : filesName) { // 姓名文件夹
				if (!name.isDirectory()) {
					continue;
				}

				sbTree.append("<li>");
				sbTree.append(name.getName());

				File[] filesDate = name.listFiles();

				for (File date : filesDate) { // 日期文件夹
					if (!date.isDirectory()) {
						continue;
					}

					sbTree.append("<div class=\"date\">"); // --date
					sbTree.append("<ul>");
					sbTree.append("<li>");
					sbTree.append("<span class=\"datebtn\">");
					sbTree.append(date.getName());
					sbTree.append("</span>");
					File[] filesReport = date.listFiles();
					sbTree.append("<div class=\"report\">"); // --report
					sbTree.append("<ul>");
					for (File report : filesReport) { // 日志文件

						sbTree.append("<li>");
						sbTree.append("<a href=\"");
						sbTree.append(report.getAbsolutePath());
						sbTree.append("\" target=\"_blank\">");
						sbTree.append(report.getName());
						sbTree.append("</a>");
						sbTree.append("</li>");

					}
					sbTree.append("</ul>");
					sbTree.append("</div>"); // --report end

					sbTree.append("</li>");
					sbTree.append("</ul>");
					sbTree.append("</div>");// --date end
				}

				sbTree.append("</li>");

			}

			File index = new File(sb.append(".html").toString());

			// 使用模板把邮件内容放进去
			String indexTemplate = FileUtil.getInstance().getTempHash()
					.get("MailBox.html");
			indexTemplate = indexTemplate.replace("#content#",
					sbTree.toString());

			saveHTML(index, indexTemplate);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
