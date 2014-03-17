package concurrency;


import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ForkJoinPoolTest {
	
	private static final String GEN_LOCATION = System.getProperty("java.io.tmpdir") + File.separatorChar + "ForkJoinPool_test";;
	private static final Integer  HOW_MANY_FILES = 1000;
	
	
	@Before
	public void setUp() throws Exception {
		Files.createDirectories(Paths.get(GEN_LOCATION));
		
		
		
		for(int i = 0; i < HOW_MANY_FILES; i++){
			String hexString = Integer.toHexString((ThreadLocalRandom.current().nextInt()));
			int depth = ThreadLocalRandom.current().nextInt(1, 4);
			char[] charArray = hexString.substring(0, depth).toCharArray();
			String loc = GEN_LOCATION;
			for(char letter : charArray){
				loc += String.format("/%s", letter);
			}
			//File fileDir = new File(loc);
			//fileDir.mkdirs();
			Files.createDirectories(Paths.get(loc));
			loc += String.format("/%s", UUID.randomUUID().toString());
			Files.createFile(Paths.get(loc));
		}
		
		
		
	}

	
	
	@After
	public void tearDown() throws Exception {
		Path path = Paths.get(GEN_LOCATION);
		removeRecursive(path);
		
	}
	
	

	@Test
	public void test() {
		ForkJoinPool forkJoinPool = new ForkJoinPool(4);
		
		ForkJoinTask<Set<String>> forkJoinTask = forkJoinPool.submit(new ConvertDirectoryRecursiveTask(Paths.get(GEN_LOCATION)));
		
		Set<String> set;
		try {
			set = forkJoinTask.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
		assertEquals(new Integer(set.size()), HOW_MANY_FILES);
	}
	
	public void removeRecursive(Path path) throws IOException
	{
	    Files.walkFileTree(path, new SimpleFileVisitor<Path>()
	    {
	        @Override
	        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	                throws IOException
	        {
	            Files.delete(file);
	            return FileVisitResult.CONTINUE;
	        }

	        @Override
	        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
	        {
	            // try to delete the file anyway, even if its attributes
	            // could not be read, since delete-only access is
	            // theoretically possible
	            Files.delete(file);
	            return FileVisitResult.CONTINUE;
	        }

	        @Override
	        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
	        {
	            if (exc == null)
	            {
	                Files.delete(dir);
	                return FileVisitResult.CONTINUE;
	            }
	            else
	            {
	                // directory iteration failed; propagate exception
	                throw exc;
	            }
	        }
	    });
	}

}
