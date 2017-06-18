package cn.com.Alader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

public class Logs implements Runnable {

	private final Logger logger = (Logger) LoggerFactory.getLogger(Logs.class);
	private String lock = new String();
	List<String> logList = new ArrayList<String>();
	private boolean isNotWrite = false;
	private static ExecutorService singleThread;
	/**
	 * 单例模式
	 */
	private static Logs instance;
	private Logs() {
	}

	public static Logs getInstance() {
		if (instance == null) {
			instance = new Logs();
			ScheduledExecutorService ser = Executors
					.newSingleThreadScheduledExecutor();
			ser.scheduleAtFixedRate(new Thread(Logs.getInstance()), 0, 300,
					TimeUnit.MILLISECONDS);
			singleThread = Executors.newSingleThreadExecutor();
		}
		return instance;
	}

	public void info(String imsn, Class className) {
		synchronized (lock) {
			String logMsn = logMsn(imsn, className, "INFO");
			if (logList.size() > 20000) {
				if (!isNotWrite) {
					logList.add(logMsn("\n next log is miss", className,
							"INFO"));
					isNotWrite = true;
				}
			}
			isNotWrite = false;
			logList.add(logMsn.toString());
			onMessageReceived();
		}
	}

	/**
	 * 读取线程
	 */
	public void run() {
		logStrJoint(300);
	}

	/**
	 * 观察者模式读取
	 */
	public void onMessageReceived() {
		if (logList.size() < 5000) {
			return;
		}
		synchronized (lock) {
			logStrJoint(500);
		}
	}

	/**
	 * 日志信息
	 * 
	 * @param imsn
	 * @param className
	 * @param level
	 * @return
	 */
	public String logMsn(String imsn, Class className, String level) {
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat date = new SimpleDateFormat();
		String format = date.format(new Date());
		String name = Thread.currentThread().getName();
		StringBuffer append = sb.append("\n").append(format).append(" ")
				.append(name).append(" ").append(imsn);
		String logString = append.toString();
		return logString;
	}

	/**
	 * 字符串拼接
	 * @param writeSize
	 */
	private void logStrJoint(int writeSize) {
		
		synchronized (lock) {
			if(logList.size()==0){
				return;
			}
			StringBuffer logStr = new StringBuffer();
			while (writeSize > 0 && logList.size() > 0) {
				logStr = logStr.append(logList.get(0));
				logList.remove(0);
				writeSize--;
			}
			printLog(logStr);
		}
	}

	/**
	 * 日志打印
	 * 
	 * @param logStr
	 */
	private void printLog(final StringBuffer logStr) {
		synchronized (lock) {
			singleThread.submit(new Runnable() {
				public void run() {
					if (!logStr.toString().isEmpty()
							&& logStr.toString() != null) {
						logger.info(logStr.toString());
					}
				}
			});
		}
	}
}
