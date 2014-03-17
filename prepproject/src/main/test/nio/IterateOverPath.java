package nio;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class IterateOverPath {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		Path path = Paths.get("/home/ahebert/gateway.log").toAbsolutePath().toRealPath();
		for(Path apath : path){
			System.out.println(apath);
		}
		Iterator<Path> iterator = path.iterator();
		Path next = null;
		while(iterator.hasNext()){
			next = iterator.next();
		}
		Assert.assertTrue(next.equals(Paths.get("gateway.log")));
	}

}
