package com.hwatch.core.Interfaces;

import java.io.File;

/**
 * Interface for file watching listeners.
 * @author Hirad Asadi
 *
 */
public interface FileWatchEventInterface
{
	/**
	 * Occurs when when a file has been added.
	 * @param file the added file
	 */
	public void FileAddedEvent(File file);
	
	/**
	 * Occurs when a file has been removed.
	 * @param file the removed file
	 */
	public void FileRemovedEvent(File file);
	
	/**
	 * Occurs when a file has been changed.
	 * @param file the changed file
	 */
	public void FileChangedEvent(File file);
}
