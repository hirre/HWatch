package com.hwatch.core;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A class for searching files in folders.
 * @author Hirad Asadi
 */
public class FileSearcher
{
	private ExecutorService _executor = getExecutor();
	private Instant _latestInstant = Instant.now();
	private int _sleepTime = 0;
	private int _waitTimeLimit = 2;
		
	/**
	 * Get executor service.
	 * @return Executor service
	 */
	private ExecutorService getExecutor()
	{
		return Executors.newCachedThreadPool();
	}
	
	/**
	 * Set sleep time (default 0).
	 * @param sleepTime in milliseconds
	 */
	public void setSleepTime(int sleepTime)
	{
		if (sleepTime < 0)
			return;
		
		_sleepTime = sleepTime;
	}
	
	/**
	 * Set wait time limit (default 2 seconds). 
	 * If wait time exceeds limit we not longer wait.
	 * @param waitTimeLimit in seconds
	 */
	public void setWaitTimeLimit(int waitTimeLimit)
	{
		if (waitTimeLimit < 0)
			return;
		
		_waitTimeLimit = waitTimeLimit;
	}
	
	/**
	 * Searches a folder and stores file revision in a map.
	 * @param folder The folder to start the search from
	 * @param revisionMap The revision result map
	 * @param searchRecursively Indicates to extend search recursively in sub folders
	 */
	public void searchAndStore(File folder, 
			ConcurrentHashMap<String, Long> revisionMap, 
			boolean searchRecursively)
	{
		if (revisionMap == null)
			return;
		
		if (_executor.isShutdown() || _executor.isTerminated())
			_executor = getExecutor();
				
		// Store last modified
		revisionMap.putIfAbsent(folder.toString(), folder.lastModified());
		
		if (!folder.isDirectory())
			return;
			
		// Go through directory files
		for (File f : folder.listFiles())
		{		
			_latestInstant = Instant.now();

			// Store last modified
			revisionMap.put(f.toString(), f.lastModified());

			//	Recursive parallel call if found file is directory
			if (searchRecursively && f.isDirectory())
			{
				_executor.submit(() -> searchAndStore(f, revisionMap, searchRecursively));
			}
			
			try
			{
				Thread.sleep(_sleepTime);
			} 
			catch (InterruptedException e)
			{
			}			
		}			
	}
	
	/**
	 * Wait until search is completed (blocking).
	 */
	public void WaitUntilComplete()
	{
		if (_latestInstant == null)
			return;

		// Wait if new search has been initiated before timeout limit
		while (Duration.between(Instant.now(), _latestInstant).abs().toSeconds() < _waitTimeLimit)
		{
			try
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e)
			{
			}
		}
		
		try
		{
			_executor.shutdown();
			_executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.NANOSECONDS);
		} 
		catch (Exception e)
		{
		}
	}
}
