package com.hwatch.core;

/**
 * Class for controlling the file monitor watcher.
 * @author Hirad Asadi
 */
public class WatchControl
{
	private boolean _isRunning = true;
	
	/**
	 * Stop watching.
	 */
	public void stopWatching()
	{
		_isRunning = false;
	}
	
	/**
	 * Indicates if watch is running.
	 * @return true if running
	 */
	public boolean isRunning()
	{
		return _isRunning;
	}
}