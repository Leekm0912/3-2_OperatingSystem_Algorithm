package chap07_Dining_Philosopher_Problem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class PhilosopherWithNonPreemptionSecond extends Thread{
    int id;
    Semaphore lStick;
    int lStickNum;
    Semaphore rStick;
    int rStickNum;
    boolean[] lock;

    public PhilosopherWithNonPreemptionSecond(int id, int len, List<Semaphore> stick) {
        this.id = id;
        this.lStick = stick.get(this.id);
        this.lStickNum = this.id;
        this.rStick = stick.get((this.id+1) % len);
        this.rStickNum = (this.id+1) % len;
        this.lock = new boolean[len];
    }

    @Override
    public void run() {
        try {
            while (true) {
                lStick.acquire(); // 왼손 집음
                // 오른손을 집어야 하는데, 누가 쓰고있고, 잠겨있지 않은 경우라면 뺏어버림.
                if(!this.rStick.tryAcquire()) { 
                // 오른쪽 젓가락이 사용중이면 false, 사용중이지 않으면 acquire하면서 true를 리턴
                	if(!this.lock[this.rStickNum]) {
                		this.rStick.release();
                    	this.rStick.acquire();
                	} else {
                		this.lStick.release();
                		continue;
                	}
                }
                eat();
                rStick.release();
                lStick.release();
                think();
            }
        }catch (Exception e){
            e.printStackTrace();
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
        this.lock[this.id] = true;
        System.out.println("eat : "+ this.id);
//        sleep(1000);
        this.lock[this.id] = false;
//        sleep(1000);
    }

    public static void main(String[] args) {
        int n = 10;
        List<Semaphore> stick = new ArrayList<>();
        for(int i=0; i<n; i++){
            stick.add(new Semaphore(1));
        }
//        Semaphore ls = new Semaphore(1);
        PhilosopherWithNonPreemptionSecond[] p = new PhilosopherWithNonPreemptionSecond[n];
        
        for(int i =0; i<n; i++){
            p[i] = new PhilosopherWithNonPreemptionSecond(i, n, stick);
        }
        // 철학자들을 모두 생성한뒤 스레드 start
        for(int i =0; i<n; i++){
            p[i].start();
        }
    }
}