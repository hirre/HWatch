package com.hwatch.core;

import java.io.File;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import com.hwatch.core.Interfaces.FileWatchEventInterface;

/**
 * Class for watching basic file system events on a folder.
 * @author Hirad Asadi
 */
public class FileMonitor
{
	/**
	 * Watch for changes in a folder.
	 * @param folder the folder
	 * @param listener an event listener for callback
	 * @param watchRecursively check sub folders recursively
	 * @return WatchControl
	 */
	public static WatchControl WatchForChanges(File folder, 
			FileWatchEventInterface listener, 
			boolean watchRecursively)
	{	
		WatchControl wc = new WatchControl();
		
		if (!folder.isDirectory())
		{
			wc.stopWatching();
			return wc;
		}
		
		// The task to run
		Runnable task = () -> 
		{
			FileSearcher fs = new FileSearcher();
			ConcurrentHashMap<String, Long> lastRevisionMap = null;
			ConcurrentHashMap<String, Long> revisionMap = new ConcurrentHashMap<String, Long>();			

			while (wc.isRunning())
			{
				fs.searchAndStore(folder, revisionMap, watchRecursively);
				fs.WaitUntilComplete();
							
				if (lastRevisionMap != null && revisionMap != null)
				{				
					// File(s)/Folder(s) removed
					if (revisionMap.size() < lastRevisionMap.size())
					{					
						for (Enumeration<String> e = lastRevisionMap.keys(); e.hasMoreElements();)
						{
							String key = e.nextElement();
							
							// The removed file
							if (!revisionMap.containsKey(key))
								listener.FileRemovedEvent(new File(key));
						}
					}
					
					// File(s)/Folder(s) added
					if (revisionMap.size() > lastRevisionMap.size())
					{
						for (Enumeration<String> e = revisionMap.keys(); e.hasMoreElements();)
						{
							String key = e.nextElement();

							// The added file
							if (!lastRevisionMap.containsKey(key))
								listener.FileAddedEvent(new File(key));
						}
					}
					
					// Check for changes
					for (Enumeration<String> e = revisionMap.keys(); e.hasMoreElements();)
					{
						String key = e.nextElement();
						
						if (revisionMap.get(key) != null && lastRevisionMap.get(key) != null &&
							revisionMap.get(key).longValue() != lastRevisionMap.get(key).longValue())
						{
							listener.FileChangedEvent(new File(key));
						}
					}
				}
				
				try
				{
					if (lastRevisionMap != null)
						Thread.sleep(3000);
				} 
				catch (InterruptedException e)
				{
				}
				
				lastRevisionMap = revisionMap;
				revisionMap = new ConcurrentHashMap<String, Long>();
			}
		};
		
		// Start watch task
		Executors.newCachedThreadPool().submit(task);
		
		return wc;
	}
}
