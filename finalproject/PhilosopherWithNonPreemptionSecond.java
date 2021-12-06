package finalproject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class PhilosopherWithNonPreemptionSecond extends Thread{
    int id;
    Semaphore lStick, rStick;
    int lStickNum, rStickNum;
    boolean[] lock;
    boolean end;
    int count;

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
            for(int i = 0; i <50; i++) {
                lStick.acquire();
                // 오른손을 집어야 하는데, 누가 가지고 있지만 사용 중이지 않은 경우라면 뺏어버림.
                if(!this.rStick.tryAcquire()) {
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
        System.out.println(Thread.currentThread()+"자원 사용횟수 : "+count);
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
        this.lock[this.id] = true;
        System.out.println("eat : "+ this.id);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.lock[this.id] = false;
        count++;
    }

    public static void main(String[] args) {
        int n = 10;
        List<Semaphore> stick = new ArrayList<>();
        for(int i=0; i<n; i++){
            stick.add(new Semaphore(1));
        }
        PhilosopherWithNonPreemptionSecond[] p = new PhilosopherWithNonPreemptionSecond[n];
        
        for(int i =0; i<n; i++){
            p[i] = new PhilosopherWithNonPreemptionSecond(i, n, stick);
        }
        // 철학자들을 모두 생성한뒤 스레드 start
        for(int i =0; i<n; i++){
            p[i].start();
        }
        while(true){
            boolean isEnd = true;
            for(int j=0; j<p.length; j++){
                if(p[j].end != true){
                    isEnd = false;
                }
            }
            if(isEnd){
                break;
            }
        }
        int result = 0;
        for(int j=0; j<p.length; j++){
            result += p[j].count;
        }
        System.out.println("자원을 사용한 횟수 : "+result);
    }
}