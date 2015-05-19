package com.meowpaw.mailserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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

		FileOutputStream fos = new FileOutputStream(file);

		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");

		osw.write(content);

		osw.close();
		fos.close();

		// FileWriter fw = new FileWriter(file);
		//
		// String utf8Content = new String(content.getBytes("GBK"),"UTF-8");
		//
		// fw.write(utf8Content);
		/**
		 * 进行刷新，将字符写到目的地中。
		 */
		// fw.flush();
		/**
		 * 关闭流，关闭资源。在关闭前会调用flush方法 刷新缓冲区。关闭后在写的话，会抛IOException
		 */
		// fw.close();

	}

	/**
	 * 生成目录首页
	 */
	public void genIndex() {

		List<NameList> nameLists = getNameList();
		
		Hashtable<String, NameList> ht=new Hashtable<String, NameList>();
		
		
		

		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("user.dir"));
		sb.append(System.getProperty("file.separator"));
		sb.append("Web");
		sb.append(System.getProperty("file.separator"));
		sb.append("MailBox");

		File root = new File(sb.toString());

		// File[] filesName = root.listFiles();

		List<File> filesName = new ArrayList<File>();

		for (NameList nl : nameLists) {
			String pp = sb.toString() + System.getProperty("file.separator")
					+ nl.email;
			File f = new File(pp);
			filesName.add(f);
			
			ht.put(nl.email, nl);
		}

		StringBuilder sbTree = new StringBuilder();

		try {
			for (File name : filesName) { // 姓名文件夹
				if (!name.isDirectory()) {
					continue;
				}

				sbTree.append("<li>");
				//sbTree.append(name.getName());
				sbTree.append(ht.get(name.getName()).getName());

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

						String relLink = "MailBox/" + name.getName() + "/"
								+ date.getName() + "/" + report.getName();

						sbTree.append("<li>");
						sbTree.append("<a href=\"");
						sbTree.append(relLink);

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

	/**
	 * 获取姓名配置
	 * 
	 * @return
	 */
	public List<NameList> getNameList() {

		List<NameList> list = new ArrayList<NameList>();

		try {
			// 从namelist文件里面读取出邮件前缀和姓名关联
			String nameListPath = System.getProperty("user.dir")
					+ System.getProperty("file.separator") + "Web"
					+ System.getProperty("file.separator") + "NameList.txt";
			File nameList = new File(nameListPath);

			FileInputStream in = new FileInputStream(nameList);
			byte b[] = new byte[(int) nameList.length()]; // 创建合适文件大小的数组
			in.read(b); // 读取文件中的内容到b[]数组
			in.close();

			String text = new String(b, "UTF-8");
			
			text = text.replace(System.getProperty("line.separator"), "");
			
			String[] names = text.split(";");

			for (int i = 0; i < names.length; i++) {

				String[] items = names[i].split(",");

				NameList nl = new NameList();

				nl.setEmail(items[0]);
				nl.setName(items[1]);
				nl.setDept(items[2]);

				list.add(nl);
			}

			// System.out.println(list.get(0).getName());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 姓名配置
	 * 
	 * @author goddie
	 *
	 */
	class NameList {

		/**
		 * 姓名
		 */
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		/**
		 * 邮箱前缀
		 */
		private String email;

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		/**
		 * 邮箱前缀
		 */
		private String dept;

		public String getDept() {
			return dept;
		}

		public void setDept(String dept) {
			this.dept = dept;
		}

	}

}
