package com.git.svn.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.git.svn.bean.SvnFile;

/**
 * 
 * @file	FileUtils.java
 * @project	SVNManager
 * @Description:   	文件操作          
 * 
 * @author 	lemon_mj
 * @date 	2017年12月18日下午4:54:14
 */
public class FileUtils {
	
	private static Logger logger = Logger.getLogger(FileUtils.class);


	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:54:23
	 * @description:	集成单文件读取
	 *
	 * @param filePathAndName
	 * @param encoding
	 * @return
	 * @throws Exception
	 * @throws  Exception
	 */
	public static List<SvnFile> parseTextFile(String filePathAndName,String encoding) throws Exception {
		List<SvnFile> fileList = new ArrayList<SvnFile>();
		logger.debug("开始读取文件：" + filePathAndName);
		FileInputStream fs = new FileInputStream(filePathAndName);
		InputStreamReader isr;
		if (encoding == null || encoding.equals(""))
			isr = new InputStreamReader(fs);
		else
			isr = new InputStreamReader(fs, encoding);
		BufferedReader br = new BufferedReader(isr);
		String data = "";
		while ((data = br.readLine()) != null) {
			String[] strs = data.split(" ");
			SvnFile svnFile = new SvnFile();
			svnFile.setFileName(strs[0].trim());
			svnFile.setFileVersion(Long.parseLong(strs[1].trim()));
			fileList.add(svnFile);
		}
		br.close();
		return fileList;
	}

	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:54:34
	 * @description:	创建文件夹
	 *
	 * @param folderPath
	 * @return
	 * 
	 */
	public static String createFolder(String folderPath) {
		String txt = folderPath;
		try {
			File myFilePath = new File(txt);
			if (!myFilePath.exists())
				myFilePath.mkdir();
		} catch (Exception e) {
			logger.error("创建目录操作出错：",e);
		}
		return txt;
	}

	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:54:44
	 * @description:	创建文件夹
	 *
	 * @param folderPath
	 * @param paths
	 * @return
	 * 
	 */
	public static String createFolders(String folderPath, String paths) {
		String txts = folderPath;
		int startNum = -1;
		try {
			StringTokenizer st = new StringTokenizer(paths, "//");
			if (txts == null || "".equals(txts))
				startNum = 0;
			for (int i = 0; st.hasMoreTokens(); ++i) {
				String txt = st.nextToken().trim();
				if (i == startNum)
					txts = txts + txt;
				else if (txts.charAt(txts.length() - 1) == '/')
					txts = createFolder(txts + txt);
				else
					txts = createFolder(txts + "/" + txt);
			}
		} catch (Exception e) {
			logger.error("创建目录操作出错：",e);
		}
		return txts;
	}

	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:55:03
	 * @description:	创建文件
	 *
	 * @param filePathAndName
	 * @param fileContent
	 * 
	 */
	public static void createFile(String filePathAndName, String fileContent) {
		try {
			String filePath = filePathAndName.trim();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				createFolders("",filePath.substring(0, filePath.lastIndexOf("/")));
				myFilePath.createNewFile();
			}
			if (!myFilePath.isDirectory()) {
				FileWriter resultFile = new FileWriter(myFilePath);
				PrintWriter myFile = new PrintWriter(resultFile);
				String strContent = fileContent;
				myFile.println(strContent);
				logger.debug("生成文件 " + filePathAndName + " 成功！");
				myFile.close();
				resultFile.close();
			}
		} catch (IOException e) {
			logger.error("创建文件操作出错：",e);
		}

	}

	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:55:24
	 * @description:	生成集成单
	 *
	 * @param filePathAndName
	 * @param fileContent
	 * @param encoding
	 * @throws IOException
	 */
	public static void createDetailList(String filePathAndName, String fileContent,
			String encoding) throws IOException {
		String tempPath = filePathAndName + ".temp";
		File tempFile = new File(tempPath);
		if (!tempFile.exists()) {
			createFolders("",tempPath.substring(0, tempPath.lastIndexOf("/")));
			tempFile.createNewFile();
			logger.debug("集成单生成临时文件：" + tempPath);
		}
		PrintWriter pw = new PrintWriter(tempPath, encoding);
		String strContent = fileContent;
		pw.println(strContent);
		pw.close();
		int tempTotal = getTotalLines(tempFile);
		logger.debug("临时文件[" + tempPath + "]总行数：" + tempTotal);
		/************************从临时文件中去重，并生成新文件*********************************/
		Map<String,String> map = new TreeMap<String,String>();
		BufferedReader reader = new BufferedReader(new FileReader(tempPath));
		String line = "";
		int errLines = 0;
		while((line = reader.readLine()) != null){
			if(line.trim().length() == 0){
				errLines++;
				logger.debug("删除空行");
				continue;
			}
			if(line.trim().length() == 1 && line.equals("\"")){
				errLines++;
				logger.debug("删除仅有字符\"行");
				continue;
			}
			line = line.replaceAll("\"", "");
			String[] arr = line.split("\\s{1,3}");
			String key = arr[0];
			String value = arr.length == 1 ? "0" : arr[1];
			if (!value.matches("[0-9]+")) {
				errLines++;
				logger.debug("删除行 版本号不符合规范");
				continue;
			}
			if(map.containsKey(key)){
				if(Integer.valueOf(value) > Integer.valueOf(map.get(key))){
					errLines++;
					logger.debug("删除旧版本行：" + key + " " + map.get(key));
					map.put(key, value);
				}
			} else 
				map.put(key, value);
		}
		reader.close();
		logger.debug("删除行数 ： " + errLines + "，剩余有效集成单行数 ：" + map.size());
		File file = new File(filePathAndName);
		if(!file.exists()){
			file.createNewFile();
			logger.debug("集成单生成新文件：" + filePathAndName);
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePathAndName));
		Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String,String> entry = iterator.next();
			writer.write(entry.getKey() + " " + entry.getValue());
			writer.newLine();
		}
		writer.flush();
		writer.close();
		int total = getTotalLines(file);
		logger.debug("新文件[" + filePathAndName + "]总行数：" + total);
	}

	
	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:55:49
	 * @description:	文件删除
	 *
	 * @param filePathAndName
	 * @return
	 *
	 */
	public static boolean delFile(String filePathAndName) {
		boolean flag = false;
		try {
			File myDelFile = new File(filePathAndName);
			if (myDelFile.exists()) {
				myDelFile.delete();
				flag = true;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return flag;
	}


	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:55:57
	 * @description:	文件复制
	 *
	 * @param oldPathFile
	 * @param newPathFile
	 *
	 */
	public static String copyFile(String oldPathFile, String newPathFile) {
		String errMsg = null;
		try {
			int byteread = 0;
			File oldFile = new File(oldPathFile);
			File newFile = new File(newPathFile);
			if (oldFile.exists()) {
				if (oldFile.isDirectory()) {
					createFolders("", newPathFile);
					return errMsg;
				}
				createFile(newPathFile, null);
				if (!newFile.exists()) 
					logger.debug("新文件不存在，生成新文件:" + newPathFile);
				else 
					logger.debug("新文件存在，清空文件:" + newPathFile);
				InputStream inStream = new FileInputStream(oldFile);
				FileOutputStream fs = new FileOutputStream(newFile);
				byte[] budder = new byte[1444];
				while ((byteread = inStream.read(budder)) != -1) {
					fs.write(budder, 0, byteread);
				}
				inStream.close();
				fs.close();
				logger.info("文件[" + oldPathFile + "]复制到 [" + newPathFile + "]完成！");
			} else {
				logger.error("文件：" + oldFile + "不存在或者是文件夹，不执行复制操作！");
				errMsg = "文件：" + oldFile + "不存在或者是文件夹，不执行复制操作！";
			}
		} catch (Exception e) {
			logger.error("复制单个文件操作出错" , e);
			errMsg = "复制单个文件操作出错 " + e.getMessage();
		}
		return errMsg;
	}

	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:56:27
	 * @description:	文件夹移动
	 *
	 * @param oldPath
	 * @param newPath
	 *
	 */
	public static void copyFolder(String oldPath, String newPath) {
		try {
			new File(newPath).mkdir();
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator))
					temp = new File(oldPath + file[i]);
				else
					temp = new File(oldPath + File.separator + file[i]);
				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + temp.getName());
					byte[] b = new byte[5120];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory())
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
			}
		} catch (Exception e) {
			logger.error("复制整个文件夹内容操作出错", e);
		}
	}

	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:56:36
	 * @description:	文件移动
	 *
	 * @param oldPath
	 * @param newPath
	 *
	 */
	public void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);
		logger.debug("文件移动成功！");
	}


	/**
	 * 
	 * @author 	lemon_mj
	 * @date 	2017年12月18日 下午4:56:44
	 * @description:	获取文件总行数	
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 *
	 */
	public static int getTotalLines(File file) throws IOException {
		FileReader in = new FileReader(file);
		LineNumberReader reader = new LineNumberReader(in);
		String line = reader.readLine();
		int lines = 0;
		while (line != null) {
			++lines;
			line = reader.readLine();
		}
		reader.close();
		in.close();
		return lines;
	}

}