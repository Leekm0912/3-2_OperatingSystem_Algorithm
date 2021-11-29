package Philosopher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class PhilosopherWithCircularAtmosphere extends Thread{
    int id;
    Semaphore lStick;
    Semaphore rStick;

    public PhilosopherWithCircularAtmosphere(int id, int len, List<Semaphore> stick){
        this.id = id;
        this.lStick = stick.get(this.id);
        this.rStick = stick.get((this.id+1) % len);
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 짝수번째 아이디는 왼손부터, 홀수번째 아이디는 오른쪽부터 집어서 순환 대기를 방지.
                if(id % 2 == 0) {
                    lStick.acquire(); // 왼손 집음
                    rStick.acquire(); // 오른손 집음
                }else{
                    rStick.acquire(); // 오른손 집음
                    lStick.acquire(); // 왼손 집음
                }
                eat();
                rStick.release();
                lStick.release();

                think();
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private void think() {
        System.out.println("think : "+ this.id);
    }

    private void eat() throws InterruptedException {
        System.out.println("eat : "+ this.id);
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
            PhilosopherWithCircularAtmosphere p = new PhilosopherWithCircularAtmosphere(i, n, stick);
            p.start();
        }
    }
}
