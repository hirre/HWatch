package com.hwatch.test;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

import com.hwatch.core.FileMonitor;
import com.hwatch.core.WatchControl;
import com.hwatch.core.Interfaces.FileWatchEventInterface;

/**
 * Test class for HWatch.
 * @author Hirad Asadi
 *
 */
public class HWatchTest
{
	public static void main(String[] args)
	{	
		if (args.length == 0)
		{
			System.out.println("USAGE: java HWatchTest <full path to a folder>");
			System.exit(1);
		}
		
		File watchFolder = null;
		
		try
		{
			watchFolder = new File(args[0]);
		}
		catch(Exception e)
		{
			System.err.println("Could not open folder!");
			System.exit(1);
		}
		
		System.out.println("Start watching on \"" + args[0] + "\" ...");
		
		FileWatchListener wl = new FileWatchListener();
		WatchControl wc = FileMonitor.WatchForChanges(watchFolder, wl);		
		Instant start = Instant.now();
				
		while(wc.isRunning())
		{
			try
			{
				Thread.sleep(1000);
			} 
			catch (InterruptedException e)
			{
			}
			
			// Run test for 2 minutes
			if (Duration.between(Instant.now(), start).abs().toMinutes() >= 2 && wc.isRunning())
			{
				wc.stopWatching();
			}
		}
		
		System.out.println("Done watching!");
	}
	
	/**
	 * A test listener for HWatch.
	 * @author Hirad Asadi
	 *
	 */
	public static class FileWatchListener implements FileWatchEventInterface
	{
		@Override
		public void FileAddedEvent(File file)
		{
			System.out.println(file + " was added.");					
		}

		@Override
		public void FileRemovedEvent(File file)
		{
			System.out.println(file + " was removed.");			
		}

		@Override
		public void FileChangedEvent(File file)
		{
			System.out.println(file + " was changed.");			
		}		
	}

}
