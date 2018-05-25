package com.example.nihongo.util;

import java.lang.Thread.State;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

/**
 * 线城池 只给下载线程用 会开另一个线程循环遍历自己
 * 
 * @author administrator1
 * 
 */
public class DownloadPool {
	private List<IdThread> threadList;
	// private int poolSize;
	private static DownloadPool intance = null;

	public static synchronized DownloadPool getInstance() // 静态，同步，公开访问点
	{
		if (intance == null) {
			intance = new DownloadPool();
		}
		return intance;
	}

	private DownloadPool() {
		threadList = new LinkedList<IdThread>();
		new Thread() {
			public void run() {
				loop(threadList);
			};
		}.start();
	}

	public synchronized boolean execute(IdThread thread) {
		try {
			threadList.add(thread);
			thread.start();
			return true;
		} catch (Exception e) {
			Log.d("MyPool", "添加出错，撤销添加操作");
			try {
				threadList.remove(thread);
			} catch (Exception e2) {
			}
			return false;
		}
	}


	/**
	 * list中有未运行的thread就运行thread，thread已经运行完了就移除 应该开个线程去运行这个方法
	 */
	private void loop(List<IdThread> threadList) {
		while (true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (threadList == null || threadList.size() == 0) {
				continue;
			}
			for (int i = 0; i < threadList.size(); i++) {
				IdThread thread = threadList.get(i);
				State state = thread.getState();
				if (state == State.NEW) {
					thread.start();
				} else if (state == State.TERMINATED) {
					try {
						threadList.remove(thread);
					} catch (Exception e) {
						Log.e("DownloadPool",
								"error when looping to remove thread"
										+ thread.getFileId());
						e.printStackTrace();
					}
				} else {
					// 其它情况不动它
				}
			}
		}
	}

	/**
	 * 是否线程池中所有线程都TERMINATED了
	 * @return
	 */
	public boolean isAllThreadSilenced() {
		boolean ret = true;
		for (int i = 0; i < threadList.size(); i++) {
			IdThread thread = threadList.get(i);
			State state = thread.getState();
			if (state != State.TERMINATED || thread.isAlive()) {
				ret = false;
			}
		}
		return ret;
	}


	public int getRunningThreadCount() {
		if (threadList != null && threadList.size() > 0) {
			for (IdThread thread : threadList) {
				Log.d("MyPool", "遍历pool pool中线程的id： " + thread.getFileId());
			}
		} else {
			Log.d("MyPool", "遍历pool pool中没有内容");
		}
		if (threadList == null) {
			return 0;
		} else {
			return threadList.size();
		}
	}

}
