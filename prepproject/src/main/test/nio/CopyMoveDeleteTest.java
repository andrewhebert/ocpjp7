package nio;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CopyMoveDeleteTest {

	private Path directory;
	private Path secondDirectory;

	@Before
	public void setUp() throws Exception {
		directory = Files.createDirectory(Paths.get("HI"));
		Path filePath = directory.resolve("ANewFile");
		Files.createFile(filePath);
		
		secondDirectory = Files.createDirectory(Paths.get("BYE"));
	}

	@Test
	public void testExists() {
		Assert.assertTrue(Files.exists(directory));
	}
	
	@Test 
	public void testCanMoveDir() throws IOException{
		Path subDire = secondDirectory.resolve(directory);
		//This has files in it and it works...its on 
		//the same FileStore
		Files.move(directory, subDire, StandardCopyOption.ATOMIC_MOVE);
		
		Path resolve = subDire.resolve("ANewFile");
		
		Assert.assertTrue(Files.exists(subDire));
		Assert.assertTrue(Files.notExists(directory));
		
		//contents within directory are moved with directory move..aka directory is actually renamed
		Assert.assertTrue(Files.exists(resolve));
	}
	
	@Test
	public void testCanCopyDir() throws IOException{
		Path subDire = secondDirectory.resolve(directory);
		Files.copy(directory, subDire);
		Assert.assertTrue(Files.exists(subDire));
		
		Path resolve = subDire.resolve("ANewFile");
		//contents within directory do not move with Files.copy..just the directory (name) is copied
		Assert.assertTrue(Files.notExists(resolve));
	}
	
	

	@After
	public void tearDown() throws Exception {
		//Files.delete(directory); cant do this
		SimpleFileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<Path>(){
			
			
			
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				
				Files.delete(file);
				
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				
				if(exc == null){
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}else{
					throw exc;
				}
			}
		};
		if(Files.exists(directory)){
			Files.walkFileTree(directory, simpleFileVisitor);
		}
		if(Files.exists(secondDirectory)){
			Files.walkFileTree(secondDirectory, simpleFileVisitor);
		}
	}


}
