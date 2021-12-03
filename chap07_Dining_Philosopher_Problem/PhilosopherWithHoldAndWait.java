package chap07_Dining_Philosopher_Problem;

import java.util.concurrent.Semaphore;

class PhilosopherWithHoldAndWait extends Thread {
    int id;
    Semaphore lstick, rstick;

    PhilosopherWithHoldAndWait(int id, Semaphore lstick, Semaphore rstick) {
       this.id = id;
       this.lstick = lstick;
       this.rstick = rstick;
    }
    public void run() {
   		while(true) {
   			try {
				lstick.acquire();
				if(!rstick.tryAcquire()){
					lstick.release();
				}else {
					eat();
					rstick.release();
					lstick.release();
					think();
				}
			} catch (InterruptedException e) {
			} 
   		}
    }
    private void think() {
        System.out.println("think : "+ this.id);
        try {
			sleep(1000);
		} catch (InterruptedException e) {
		}
    }
    private void eat() {
        System.out.println("eat : "+ this.id);
    }
    
    public static void main(String[] args) {
        int i;
        int num = 10;

        Semaphore[] stick = new Semaphore[num];

        for(i = 0; i < num; i++)
            stick[i] = new Semaphore(1);

        PhilosopherWithHoldAndWait[] phil = new PhilosopherWithHoldAndWait[num];

        for(i = 0; i < num; i++) {
        	phil[i] = new PhilosopherWithHoldAndWait(i, stick[i], stick[(i + 1) % num]);       	
        }
        for(i = 0; i < num; i++) {
        	phil[i].start();
        }
    }
}
