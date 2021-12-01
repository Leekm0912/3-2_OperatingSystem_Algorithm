package chap07_Dining_Philosopher_Problem;

import java.util.concurrent.Semaphore;

class PhilosopherWithHoldAndWait extends Thread {
    int id; // 철학자 id
    Semaphore lstick, rstick; // 왼쪽, 오른쪽 젓가락
    PhilosopherWithHoldAndWait(int id, Semaphore lstick, Semaphore rstick) {
       this.id = id;
       this.lstick = lstick;
       this.rstick = rstick;
   }
    public void run() {
   		while(true) {
   			try {
				lstick.acquire(); // 왼쪽 집어들기
				if(!rstick.tryAcquire()){
					lstick.release();// 오른쪽을 사용할 수 없으면 왼쪽 내려놓기
				}else {
					eat(); // 식사
					rstick.release(); // 오른쪽 내려놓기
					lstick.release(); // 왼쪽 내려놓기
					think(); // 생각하기
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
