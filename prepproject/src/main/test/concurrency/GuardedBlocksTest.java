package concurrency;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * This one takes a few seconds to run. This class deals with
 * http://docs.oracle.com/javase/tutorial/essential
 * /concurrency/QandE/questions.html Exercise 2
 * 
 * @author ahebert
 * 
 */
@RunWith(JUnit4.class)
public class GuardedBlocksTest {

	public class Consumer implements Runnable {
		private SynchronousQueue<String> drop;

		public Consumer(SynchronousQueue<String> drop) {
			this.drop = drop;
		}

		public void run() {
			Random random = new Random();
			try {
				for (String message = drop.take(); !message.equals("DONE"); message = drop
						.take()) {
					System.out.format("MESSAGE RECEIVED: %s%n", message);

					Thread.sleep(random.nextInt(5000));

				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public class Producer implements Runnable {
		private SynchronousQueue<String> drop;

		public Producer(SynchronousQueue<String> drop) {
			this.drop = drop;
		}

		public void run() {
			String importantInfo[] = { "Mares eat oats", "Does eat oats",
					"Little lambs eat ivy", "A kid will eat ivy too" };
			Random random = new Random();

			for (int i = 0; i < importantInfo.length; i++) {
				try {
					drop.put(importantInfo[i]);
				} catch (InterruptedException e1) {
					throw new RuntimeException(e1);
				}
				try {
					Thread.sleep(random.nextInt(5000));
				} catch (InterruptedException e) {
				}
			}
			try {
				drop.put("DONE");
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Test
	public void testSyncQueue() {
		PrintStream orgOut = System.out;
		ByteArrayOutputStream systemOut = new ByteArrayOutputStream();
		PrintStream systemOutPrintStream = new PrintStream(systemOut, true);
		System.setOut(systemOutPrintStream);

		ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
		SynchronousQueue<String> synchronousQueue = new SynchronousQueue<String>();

		Future<?> producerFuture = newCachedThreadPool.submit(new Producer(
				synchronousQueue));
		Future<?> consumerFuture = newCachedThreadPool.submit(new Consumer(
				synchronousQueue));

		try {
			producerFuture.get();
			consumerFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String output = systemOut.toString();
		System.setOut(orgOut);
		System.out.println(output);

		String[] split = output.split("\n");

		Assert.assertEquals(split[3],
				"MESSAGE RECEIVED: A kid will eat ivy too");

	}

}
