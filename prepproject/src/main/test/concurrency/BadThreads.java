package concurrency;


/**
 * This deals with Exercise 1 on
 * http://docs.oracle.com/javase/tutorial/essential
 * /concurrency/QandE/questions.html
 * 
 * @author ahebert
 * 
 */
public class BadThreads {

	static String message;

	private static class CorrectorThread extends Thread {

		public void run() {
	
				message = "Mares do eat oats";

		}
	}

	public static void main(String args[]) throws InterruptedException {

		CorrectorThread correctorThread = new CorrectorThread();
		correctorThread.start();
		correctorThread.join();
		System.out.println(message);		
		message = "Mares do not eat oats.";
		

		


	}
}
