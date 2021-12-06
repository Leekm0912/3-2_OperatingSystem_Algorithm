package finalproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Philosopher extends Thread{
    int id;
    Semaphore lStick;
    Semaphore rStick;
    Random r = new Random();
    int require;
    List<Semaphore> resource;
    List<Semaphore> save = new ArrayList<>();

    public Philosopher(int id, int len, List<Semaphore> resource){
        this.id = id;
        this.resource = resource;
    }

    @Override
    public void run() {
        int rand = r.nextInt(4)+1; //1~5의 난수 생성
        try {
            while (true) {
                while(rand>require){
                    for(int i=0;i<this.resource.size();i++){
                        Semaphore temp = resource.get(i);
                        if(temp.tryAcquire()){
                            save.add(temp);
                            require++;
                        }
                    }
                }
                work();
                for(Semaphore s : save){
                    s.release();
                }
                require=0;
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }


    private void work() throws InterruptedException {
        System.out.println("eat : "+ this.id);
        sleep(1000);
    }

    public static void main(String[] args) {
        int n = 10;
        List<Semaphore> stick = new ArrayList<>();
        for(int i=0; i<n; i++){
            stick.add(new Semaphore(1));
        }
//        Semaphore ls = new Semaphore(1);
        for(int i =0; i<n; i++){
            Philosopher p = new Philosopher(i, n, stick);
            p.start();
        }
    }
}
