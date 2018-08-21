package com.qjs.utils;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class FTPConfig {

	private static Logger logger = Logger.getLogger(FTPConfig.class);

	@Value("${ftp.host:#{null}}")
	private static String host;

	@Value("${ftp.port:#{null}}")
	private static int port;

	@Value("${ftp.userName:#{null}}")
	private static String userName;

	@Value("${ftp.password:#{null}}")
	private static String password;

	// ftp连接地址
	public static final String SERVER = host;

	// 登陆用户名
	public static final String USERNAME = userName;

	// 登陆密码
	public static final String PASSWORD = password;

	// 服务器上文件夹名称
	public static final String FILE_PRE = "files";

	public static void main(String args[]) {
//		Calendar start = Calendar.getInstance();
//		start.set(201, 10, 01);
//
//		Calendar end = Calendar.getInstance();
//		end.set(2015, 10, 12);

		getDataFiles("140227198910040019/CRM/ClearProve/2c908a885963383c01596351eacc0056.pdf",
				"c:" + File.separator + "erpv3" + File.separator + "an");

		// "OA/announcement/"+DateUtil.getCurrentTime(DateUtil.STYLE_5)
		// uploadFile("OA/announcement/"+DateUtil.getCurrentTime(DateUtil.STYLE_5),"哈哈.jpg","e:"+File.separator+"erpv3"+File.separator+"201509290041514755.jpg");
	}

	/**
	 * 
	 *  功能说明：下载文件  yann  2015-10-12
	 *  @param "fileUrl":"服务器上下载路径"，"destinationFolder":"下载文件后保存的路径"  @return   
	 *  @throws  最后修改时间：最后修改时间 修改人：admin 修改内容： 修改注意点：
	 */
	public static void getDataFiles(String fileUrl, String destinationFolder) {
		FTPClient ftp = null;
		// 创建文件输出流(文件)
		FileOutputStream fos = null;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			// 切换工作路径（服务器上某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);

			// 循环切换文件路径
			String[] str = fileUrl.split("/");
			for (int i = 0; i < str.length; i++) {
				// 切换工作路径（上传到某个文件夹）
				ftp.changeWorkingDirectory(str[i]);
			}
			// 获取当前文件夹下面的所有文件
			// FTPFile[] files = ftp.listFiles();

			// 编码转换
			String fileNameTmp = new String(str[str.length - 1].getBytes("GBK"), "iso-8859-1");

			// 创建文件(文件路径);
			File file = new File(destinationFolder);

			// 文件夹如不存在则创建
			if (!file.exists()) {
				file.mkdirs();
			}

			// 创建文件输出流(文件)
			fos = new FileOutputStream(new File(destinationFolder + File.separator + str[str.length - 1]));

			// 从服务器检索命名文件并将其写入给定的OutputStream中
			boolean result = ftp.retrieveFile(fileNameTmp, fos);

			// 关闭流
			fos.close();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn(e.getMessage(), e);
		} finally {
			try {
				// 退出登录
				ftp.logout();
				// 销毁连接
				ftp.disconnect();
				// 关闭流
				fos.close();
			} catch (Exception e) {
				// e.printStackTrace();
				logger.warn(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 *  功能说明：上传文件  yann  2015-10-12
	 *  @param "fileUrl":"保存到服务器上的文件目录","fileName":"保存文件名称","sourceFile":"上传文件路径"
	 *  @return     @throws  最后修改时间：最后修改时间 修改人：admin 修改内容： 修改注意点：
	 */
	public static void uploadFile(String fileUrl, String fileName, String sourceFile) {

		FTPClient ftp = null;
		// 创建输入流（文件）
		FileInputStream fis = null;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");

			// 切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);

			// 截取要保存的文件目录
			String[] str = fileUrl.split("/");

			for (String string : str) {
				// 创建新的文件夹
				ftp.makeDirectory(string);
				// 切换工作路径(新创建的文件夹)
				ftp.changeWorkingDirectory(string);
			}
			// 创建一个文件保存上传文件（文件路径）
			File file = new File(sourceFile);

			// 创建输入流（文件）
			fis = new FileInputStream(file);
			// 设置以二进制流的方式传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // binary type
			// 创建文件（文件名，文件），返回布尔类型
			boolean result = ftp.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"), fis);
			// 关闭ftp
			fis.close();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn(e.getMessage(), e);
		} finally {
			try {
				// 退出登录
				ftp.logout();
				// 销毁链接
				ftp.disconnect();
				fis.close();
			} catch (Exception e) {
				// e.printStackTrace();
				logger.warn(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 *  功能说明：上传文件  yann  2015-10-12
	 *  @param "fileUrl":"保存到服务器上的文件目录","fileName":"保存文件名称","inputStream":"上传文件流"
	 *  @return     @throws  最后修改时间：最后修改时间 修改人：admin 修改内容： 修改注意点：
	 */
	public static void uploadFile(String fileUrl, String fileName, InputStream inputStream) {

		FTPClient ftp = null;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			// 切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);

			// 截取要保存的文件目录
			String[] str = fileUrl.split("/");

			for (String string : str) {
				// 创建新的文件夹
				ftp.makeDirectory(string);
				// 切换工作路径(新创建的文件夹)
				ftp.changeWorkingDirectory(string);
			}

			// 设置以二进制流的方式传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // binary type
			// 创建文件（文件名，文件），返回布尔类型
			boolean result = ftp.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"), inputStream);

			// 关闭流
			inputStream.close();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn(e.getMessage(), e);
		} finally {
			try {
				// 退出登录
				ftp.logout();
				// 销毁链接
				ftp.disconnect();
				// 关闭流
				inputStream.close();
			} catch (Exception e) {
				// e.printStackTrace();
				logger.warn(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 *  功能说明：上传文件  yann  2015-10-12
	 *  @param "fileUrl":"保存到服务器上的文件目录","fileName":"保存文件名称","inputStream":"上传文件流"
	 *  @return     @throws  最后修改时间：最后修改时间 修改人：admin 修改内容： 修改注意点：
	 */
	public static boolean uploadFile(String fileUrl, String fileName, InputStream inputStream, JSONObject json) {

		FTPClient ftp = null;
		boolean result = false;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			// 切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);

			// 截取要保存的文件目录
			String[] str = fileUrl.split("/");

			for (String string : str) {
				// 创建新的文件夹
				ftp.makeDirectory(string);
				// 切换工作路径(新创建的文件夹)
				ftp.changeWorkingDirectory(string);
			}

			// 设置以二进制流的方式传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // binary type
			// 创建文件（文件名，文件），返回布尔类型
			result = ftp.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"), inputStream);

			// 关闭流
			inputStream.close();
		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn(e.getMessage(), e);
		} finally {
			try {
				// 退出登录
				ftp.logout();
				// 销毁链接
				ftp.disconnect();
				// 关闭流
				inputStream.close();
			} catch (Exception e) {
				// e.printStackTrace();
				logger.warn(e.getMessage(), e);
			}
		}
		return result;
	}

	/**
	 * 
	 *  功能说明：删除文件  yann  2015-10-12
	 *  @param "fileUrl":"保存到服务器上的文件目录","fileName":"保存文件名称","inputStream":"上传文件流"
	 *  @return     @throws  最后修改时间：最后修改时间 修改人：admin 修改内容： 修改注意点：
	 */
	public static boolean deleteFile(String fileUrl) {

		FTPClient ftp = null;
		boolean result = false;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			// 切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);
			// 截取要保存的文件目录
			/*
			 * String[] str = fileUrl.split("/");
			 * 
			 * for (String string : str) { //创建新的文件夹 ftp.makeDirectory(string);
			 * //切换工作路径(新创建的文件夹) ftp.changeWorkingDirectory(string); }
			 */

			// 设置以二进制流的方式传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // binary type
			// 创建文件（文件名，文件），返回布尔类型
			result = ftp.deleteFile(fileUrl);
		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn(e.getMessage(), e);
		} finally {
			try {
				// 退出登录
				ftp.logout();
				// 销毁链接
				ftp.disconnect();
			} catch (Exception e) {
				// e.printStackTrace();
				logger.warn(e.getMessage(), e);
			}
		}
		return result;
	}

	/**
	 * 
	 *  功能说明：上传文件  yann  2015-10-12
	 *  @param "fileUrl":"保存到服务器上的文件目录","fileName":"保存文件名称","inputStream":"上传文件流"
	 * map:后期扩充参数集合  @return   boolean  @throws  最后修改时间：最后修改时间 修改人：admin 修改内容：
	 * 修改注意点：
	 */
	public static boolean uploadFile(String fileUrl, String fileName, InputStream inputStream, Map map) {
		boolean flag = false;
		FTPClient ftp = null;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			// 切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);

			// 截取要保存的文件目录
			String[] str = fileUrl.split("/");

			for (String string : str) {
				// 创建新的文件夹
				ftp.makeDirectory(string);
				// 切换工作路径(新创建的文件夹)
				ftp.changeWorkingDirectory(string);
			}

			// 设置以二进制流的方式传输
			ftp.setFileType(FTP.BINARY_FILE_TYPE); // binary type
			// 创建文件（文件名，文件），返回布尔类型
			boolean result = ftp.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"), inputStream);
			// 关闭流
			inputStream.close();
			flag = result;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn(e.getMessage(), e);
		} finally {
			try {
				// 退出登录
				ftp.logout();
				// 销毁链接
				ftp.disconnect();
				// 关闭流
				inputStream.close();
			} catch (Exception e) {
				// e.printStackTrace();
				logger.warn(e.getMessage(), e);
			}
		}
		return flag;
	}

	/**
	 * 
	 *  功能说明：下载文件  yann  2015-10-12  @param "fileUrl":"服务器上下载路径"  @return   
	 *  @throws  最后修改时间：最后修改时间 修改人：admin 修改内容：增加httprequest 修改注意点：位置
	 */
	public static void getDataFiles(HttpServletResponse response, HttpServletRequest request, String fileUrl) {
		FTPClient ftp = null;
		// 创建文件输出流
		OutputStream os = null;
		BufferedInputStream input = null;
		int reply;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			// 切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);
			fileUrl = URLDecoder.decode(fileUrl, "UTF-8");
			// 循环切换文件路径
			String[] str = fileUrl.split("/");
			for (int i = 0; i < str.length; i++) {
				// 切换工作路径（上传到某个文件夹）
				ftp.changeWorkingDirectory(str[i]);
				reply = ftp.getReplyCode();
				// 最后一段路径有可能是文件名，不检查是否成功
				if (i < str.length - 1 && !FTPReply.isPositiveCompletion(reply)) {
					logger.warn(fileUrl + "路径不存在！");
					return;
				}
			}
			// ---------------费新玮2015-12-4修改
			// 编码转换
			String fileNameTmp = new String(str[str.length - 1].getBytes("GBK"), "iso-8859-1");
			// ----------------

			// 创建缓冲流读取ftp文件
			input = new BufferedInputStream(ftp.retrieveFileStream(fileNameTmp));
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositivePreliminary(reply)) {
				logger.warn(fileNameTmp + "文件打开失败！");
				return;
			}
			// 创建文件输出流
			os = response.getOutputStream();
			// 定义字节
			int read = 0;
			byte[] buffBytes = new byte[1024];
			while ((read = input.read(buffBytes)) != -1) {
				os.write(buffBytes, 0, read);
			}

			// 清空流
			os.flush();
			// 关闭流
			os.close();

			input.close();
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		} finally {
			try {
				if (ftp != null && ftp.isConnected()) {
					// 退出登录

					// ftp.logout();
					// 销毁链接
					ftp.disconnect();
				}
				if (input != null) {
					input.close();
				}
				// 关闭流
				if (os != null) {
					os.close();
				}
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 *  功能说明： 从ftp服务器下载图片，并以BufferedImage返回  关福旺  2015-10-16 18:42:29
	 *  @param "fileUrl":"服务器上下载路径"  @return     @throws  最后修改时间：最后修改时间
	 * 修改人：admin 修改内容： 修改注意点：
	 */
	public static BufferedImage getDataFilesByUrl(String fileUrl) {
		FTPClient ftp = null;

		BufferedImage file = null;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			// 切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);

			// 循环切换文件路径
			String[] str = fileUrl.split("/");
			for (int i = 0; i < str.length; i++) {
				// 切换工作路径（上传到某个文件夹）
				ftp.changeWorkingDirectory(str[i]);

			}

			// 编码转换
			String fileNameTmp = new String(str[str.length - 1].getBytes("GBK"), "iso-8859-1");

			// 创建缓冲流读取ftp文件
			BufferedInputStream input = new BufferedInputStream(ftp.retrieveFileStream(fileNameTmp));
			file = ImageIO.read(input);

		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn(e.getMessage(), e);
		} finally {
			try {
				// 退出登录
				ftp.logout();
				// 销毁链接
				ftp.disconnect();
			} catch (Exception e) {
				// e.printStackTrace();
				logger.warn(e.getMessage(), e);
			}
		}
		return file;
	}

	public static boolean getInputStreamAndFtp(String fileUrl, OutputStream outputstream) {
		BufferedInputStream input = null;
		FTPClient ftp = null;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			ftp.setControlEncoding("UTF-8");
			// 切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);

			// 循环切换文件路径
			String[] str = fileUrl.split("/");
			for (int i = 0; i < str.length; i++) {
				// 切换工作路径（上传到某个文件夹）
				ftp.changeWorkingDirectory(str[i]);

			}
			// 编码转换
			String fileNameTmp = new String(str[str.length - 1].getBytes("GBK"), "ISO-8859-1");
			// 创建缓冲流读取ftp文件
			input = new BufferedInputStream(ftp.retrieveFileStream(fileNameTmp));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = input.read(b)) != -1) {
				outputstream.write(b, 0, len);
			}
			input.close();
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn(e.getMessage(), e);
		} finally {
			try {
				// 退出登录
				ftp.logout();
				// 销毁连接
				ftp.disconnect();
			} catch (Exception e1) {
				logger.warn("退出登录" + e1.getMessage(), e1);
			}
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					logger.warn("input.close() " + e.getMessage(), e);
				}
			}
		}
		return false;
	}

	/**
	 *  功能说明：删除文件  feixinwei  2016-3-23  @param 方法里面接收的参数及其含义  @return
	 * 该方法的返回值的类型，含义     @throws  该方法可能抛出的异常，异常的类型、含义。 最后修改时间：最后修改时间
	 * 修改人：Administrator 修改内容： 修改注意点：
	 */
	public static void deleteFileFtp(String filePath) {
		FTPClient ftp = null;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);
			ftp.deleteFile(filePath);

		} catch (Exception e) {
			// e.printStackTrace();
			logger.warn(e.getMessage(), e);
		} finally {
			try {
				// 退出登录
				ftp.logout();
				// 销毁连接
				ftp.disconnect();
			} catch (Exception e) {
				// e.printStackTrace();
				logger.warn(e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 *  功能说明：下载文件  yann  2015-10-12  @param "fileUrl":"服务器上下载路径"  @return   
	 *  @throws  最后修改时间：最后修改时间 修改人：admin 修改内容： 修改注意点：
	 */
	public static void getDataFiles(HttpServletResponse response, String fileUrl) {
		FTPClient ftp = null;
		try {
			// 创建一个新的ftp
			ftp = new FTPClient();
			// 链接ftp服务（ftp地址）
			ftp.connect(SERVER);
			// 登陆ftp（用户名，密码）
			ftp.login(USERNAME, PASSWORD);

			// 切换工作路径（上传到某个文件夹）
			ftp.changeWorkingDirectory(FILE_PRE);

			// 设置端口模式， 备选
			ftp.enterLocalPassiveMode();

			// 设置文件类型
			ftp.setFileType(FTP.BINARY_FILE_TYPE);

			// 循环切换文件路径
			String[] str = fileUrl.split("/");
			for (int i = 0; i < str.length; i++) {
				// 切换工作路径（上传到某个文件夹）
				ftp.changeWorkingDirectory(str[i]);
			}

			// 编码转换
			String fileNameTmp = new String(str[str.length - 1].getBytes("GBK"), "iso-8859-1");

			// 创建缓冲流读取ftp文件
			BufferedInputStream input = new BufferedInputStream(ftp.retrieveFileStream(fileNameTmp));
			// 创建文件输出流
			OutputStream os = response.getOutputStream();
			// 定义字节
			byte[] buffBytes = new byte[1024];
			int read = 0;
			while ((read = input.read(buffBytes)) != -1) {
				os.write(buffBytes, 0, read);
			}
			// 清空流
			os.flush();
			// 关闭流
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 退出登录
				ftp.logout();
				// 销毁链接
				ftp.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private InputStream retrieveFileStream(FTPClient ftp, String fileNameTmp) throws Exception {
		// 创建缓冲流读取ftp文件
		BufferedInputStream input = new BufferedInputStream(ftp.retrieveFileStream(fileNameTmp));
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositivePreliminary(reply)) {
			logger.warn(fileNameTmp + "文件打开失败！");
			throw new Exception(fileNameTmp + "文件打开失败！");
		}
		return input;
	}

	public static boolean finalizeFtp(FTPClient ftp) throws IOException {
		if (!ftp.completePendingCommand()) {
			ftp.logout();
			ftp.disconnect();
			return false;
		}
		return true;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
