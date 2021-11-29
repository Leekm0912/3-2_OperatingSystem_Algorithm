package chap07_Dining_Philosopher_Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

public class PhilosopherWithNonPreemption extends Thread{
    int id;
    Semaphore lStick;
    int lStickNum;
    Semaphore rStick;
    int rStickNum;
    boolean[] lStickUsing;
    boolean[] rStickUsing;
    boolean[] lock;

    public PhilosopherWithNonPreemption(int id, int len, List<Semaphore> stick) {
        this.id = id;
        this.lStick = stick.get(this.id);
        this.lStickNum = this.id;
        this.rStick = stick.get((this.id+1) % len);
        this.rStickNum = (this.id+1) % len;
        this.lStickUsing = new boolean[len];
        this.rStickUsing = new boolean[len];
        this.lock = new boolean[len];
        try {
            sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                lStick.acquire(); // 왼손 집음
                this.lStickUsing[this.lStickNum] = true;
                // 오른손을 집어야 하는데, 누가 쓰고있고, 잠겨있지 않은 경우라면 뺏어버림.
                if(this.rStickUsing[this.rStickNum] && !this.lock[this.rStickNum]){
                    rStick.release();
                    this.rStickUsing[this.rStickNum] = true;
                    rStick.acquire();
                }
                // 잠겨 있으면 왼손을 놓고 처음부터 다시 기다림.
                else if(this.rStickUsing[this.rStickNum] && this.lock[this.rStickNum]){
                    lStick.release();
                    this.lStickUsing[this.lStickNum] = false;
                    continue;
                }
                rStick.acquire(); // 오른손 집음
                this.rStickUsing[this.rStickNum] = true;
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
            e.printStackTrace();
        }
    }

    private void eat() throws InterruptedException {
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
        for(int i =0; i<n; i++){
            PhilosopherWithNonPreemption p = new PhilosopherWithNonPreemption(i, n, stick);
            p.start();
        }
    }
}
