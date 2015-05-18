package com.meowpaw.mailserver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskManager {

	
	private static TaskManager uniqueInstance = null;
	
	private ExecutorService executorService;
	
	private TaskManager() {
		executorService = Executors.newFixedThreadPool(30);
	}

	public static TaskManager getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new TaskManager();
		}
		return uniqueInstance;
	}
	
	
	/**
	 * 新增任务
	 * @param runnable
	 */
	public void addTask(Runnable runnable)
	{
		executorService.execute(runnable);
	}
	
}
