package chestcleaner.timer;

public class Counter extends Thread {

	public void run() {

		try {
			while (true) {
				Thread.sleep(1000);
				Timer.update();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
