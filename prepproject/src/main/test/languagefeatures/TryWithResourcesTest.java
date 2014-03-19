package languagefeatures;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TryWithResourcesTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTryWithResources() {
		try( MyBadResource yo = new MyBadResource();
				StringReader reader = new StringReader("HI")){
			yo.heyAnotherMethod();
			
		} catch (ConnectException e) {
			Throwable[] throwables = e.getSuppressed();
			Assert.assertEquals(1, throwables.length);
			Assert.assertEquals("HERE IS MY SUPPRESSED EXCEPTION", throwables[0].getMessage());
		} 
	}
	
	public class MyBadResource implements Closeable{
		
		public void heyAnotherMethod() throws ConnectException{
			if(new String("Y").equals("Y")){
				throw new ConnectException("IOBAD");
			}
		}
		
		/* (non-Javadoc)
		 * @see java.io.Closeable#close() 
		 * Because close doesn't throw any exception i don't need to catch it with 'MyBadResource yo = new MyBadResource();'
		 */
		@Override
		public void close() {
			if(new String("Y").equals("Y")){
				throw new RuntimeException("HERE IS MY SUPPRESSED EXCEPTION");
			}
			
		}
		
	}

}
