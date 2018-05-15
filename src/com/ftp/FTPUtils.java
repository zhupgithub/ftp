package com.ftp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUtils {

	public static void main(String[] args) {
		boolean b=windowsForFTPDownFile("192.168.43.47",21,"zhupeng","zhupeng","personPtictures",
				"a3ac977c-2cb0-4213-80eb-785da4367b160ae4c51acacefbc64de0975dd1935ee6.jpg","./file");
		System.out.println(b);
	}
	
	
	
	
	/**
	 * 
	 * @Title: windowsForFTPDownFile 
	 * @Description: 实现windows上将ftp服务器上的文件下载到指定的路径 
	 * @param host   // FTP服务器hostname
	 * @param port   // FTP服务器端口
	 * @param username   // FTP登录账号
	 * @param password    // FTP登录密码
	 * @param remotePath   // 待下载的文件在FTP服务器上的相对路径
	 * @param fileName      // 要下载的文件名
	 * @param localPath     // 下载后保存到本地的路径
	 * @return boolean    返回类型 
	 * @throws
	 */
	public static boolean windowsForFTPDownFile(String host, // FTP服务器hostname
			int port, // FTP服务器端口
			String username, // FTP登录账号
			String password, // FTP登录密码
			String remotePath, // FTP服务器上的相对路径
			String fileName, // 要下载的文件名
			String localPath// 下载后保存到本地的路径

	) {
		
		boolean success = false;
		FTPClient ftp = new FTPClient();
		try {
			int reply;
			
			// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.connect(host, port);
			
			ftp.login(username, password);// 登录
			
			
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			
			ftp.changeWorkingDirectory(remotePath);// 转移到FTP服务器目录
			
			
			FTPFile[] fs = ftp.listFiles();

			for (FTPFile ff : fs) {
				if (ff.getName().equals(fileName)) {
					
					File localFile = new File(localPath + "/" + ff.getName());
					OutputStream os = new FileOutputStream(localFile);
					
					ftp.retrieveFile(ff.getName(), os);
					
					os.close();
					break;
				}
			}
			ftp.logout();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					
				}
			}
		}
		return success;
	}

	
	
	/**
	 * 
	 * @Title: windowsForFTPDownFile 
	 * @Description: 实现linux上将ftp服务器上的文件下载到指定的路径 
	 * @param host   // FTP服务器hostname
	 * @param port   // FTP服务器端口
	 * @param username   // FTP登录账号
	 * @param password    // FTP登录密码
	 * @param remotePath   // 待下载的文件在FTP服务器上的相对路径
	 * @param fileName      // 要下载的文件名
	 * @param localPath     // 下载后保存到本地的路径
	 * @return boolean    返回类型 
	 * @throws
	 */
	public static void linuxFTPDownload(String host,int port,String user,String password,
			String remotePath, // FTP服务器上的相对路径
			String localPath,// 下载后保存到本地的路径
			String fileName  //待下载的文件的名称
			) {
		// 将txt的下载操作和解析操作分成2个独立的操作进行，排除互相间的干扰
		FTPClient ftp = null;
		try {
			// ftp的数据下载
			ftp = new FTPClient();
			ftp.connect(host, port);
			ftp.login(user, password);
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

			// 设置linux环境
			FTPClientConfig conf = new FTPClientConfig(
					FTPClientConfig.SYST_UNIX);
			ftp.configure(conf);

			
			// 判断是否连接成功
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				System.out.println("FTP server refused connection.");
				return;
			}

			// 设置访问被动模式
			ftp.setRemoteVerificationEnabled(false);
			ftp.enterLocalPassiveMode();

			boolean dir = ftp.changeWorkingDirectory(remotePath);
			if (dir) {
				FTPFile[] fs = ftp.listFiles();
				for (FTPFile ff : fs) {
					
					if (ff.getName().equals(fileName)) {
						
						File localFile = new File(localPath + "/" + ff.getName());
						OutputStream os = new FileOutputStream(localFile);
						
						ftp.retrieveFile(ff.getName(), os);
						
						os.close();
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(new Date() + "  ftp下载txt文件发生错误");
		} finally {
			if (ftp != null)
				try {
					ftp.disconnect();
				} catch (IOException ioe) {

				}
		}
	}
}
