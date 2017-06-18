
package cn.com.alader.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.Alader.Logs;

public class Logstest {
	public static void main(String[] args) {
		new Thread(Logs.getInstance()).start();
		for (int i = 0; i < 100000; i++) {
			Logs.getInstance().info("aa", Logstest.class);
		}
	/*	ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(2);
		newFixedThreadPool.execute(new Runnable(){
			public void run() {
				// TODO Auto-generated method stub
				for (int i = 0; i < 100000; i++) {
					Logs.getInstance().info("aa", Logstest.class);
				}
			}	
		});*/
	}
}
