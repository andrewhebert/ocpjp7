package concurrency;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.RecursiveTask;

public class ConvertDirectoryRecursiveTask extends RecursiveTask<Set<String>> {
	
	
	private Path directoryToConvert;

	public ConvertDirectoryRecursiveTask(Path directoryToConvert){
		this.directoryToConvert = directoryToConvert;
		
	}
	
	@Override
	protected Set<String> compute() {

		final List<ConvertDirectoryRecursiveTask> newDirForks = new ArrayList<ConvertDirectoryRecursiveTask>();
		final Set<String> processedFiles = new TreeSet<String>();
		try {
			Files.walkFileTree(directoryToConvert, new SimpleFileVisitor<Path>(){
				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					if(!dir.equals(directoryToConvert)){
						ConvertDirectoryRecursiveTask actionForNextDir = new ConvertDirectoryRecursiveTask(dir);
						actionForNextDir.fork();
						newDirForks.add(actionForNextDir);
						return FileVisitResult.SKIP_SUBTREE;
					}else{
						return FileVisitResult.CONTINUE;
					}
				}
				
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					String name = Thread.currentThread().getName();
					String absolutePath = file.toAbsolutePath().toString();
					System.out.printf("Thread %s is processing %s%n", name, absolutePath);
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					
					processedFiles.add(absolutePath);
					return FileVisitResult.CONTINUE;
				}
				
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		for(ConvertDirectoryRecursiveTask newDirFork : newDirForks){
			processedFiles.addAll(newDirFork.join());
		}
		
		return processedFiles;
		
		
		

	}

}
