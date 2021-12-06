package finalproject;

import java.util.concurrent.Semaphore;

class PhilosopherWithHoldAndWait extends Thread {
    int id;
    Semaphore lstick, rstick;
    int count;
    boolean end = false;

    PhilosopherWithHoldAndWait(int id, Semaphore lstick, Semaphore rstick) {
       this.id = id;
       this.lstick = lstick;
       this.rstick = rstick;
       this.count = 0;
    }
    public void run() {
   		for(int i=0; i<100;i++){
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
           System.out.println(Thread.currentThread() + "자원 사용횟수 : "+this.count);
           this.end = true;
    }
    private void think() {
        System.out.println("think : "+ this.id);
//        try {
//			sleep(1000);
//		} catch (InterruptedException e) {
//		}
    }
    private void eat() {
        System.out.println("eat : "+ this.id);
        count++;
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
        while(true){
            boolean isEnd = true;
            for(int j=0; j<phil.length; j++){
                if(phil[j].end != true){
                    isEnd = false;
                }
            }
            if(isEnd){
                break;
            }
        }
        int result = 0;
        for(int j=0; j<phil.length; j++){
            result += phil[j].count;
        }
        System.out.println("자원을 사용한 횟수 : "+result);
    }
}
