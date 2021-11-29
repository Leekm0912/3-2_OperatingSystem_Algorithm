package chap06_project_monitor_use_busy_wating;

import java.util.concurrent.Semaphore;

public class Moniter{
	int readers = 0; // reader의 수
	int writers = 0; // writer의 수
	boolean canRead = false; // Writer가 쓰고있을 경우 false
	boolean canWrite = true; // Reader가 읽고있을 경우 false
	private final Semaphore semaphore = new Semaphore(1);
	private int resource = -1;
	
	public void beginRead() {
		// TODO Auto-generated method stub
		// Writer가 쓰고있거나(canRead가 false) 쓰려고 대기중(queue에 있으면)이면 모니터 밖에서 대기.
		while(!canRead || writers != 0) {};
		// while문을 빠져나오면, Writer가 작업을 안하는 상황임.
		// Read 작업을 할꺼라고 Writer에게 알림.
		canWrite = false;

		System.out.println("[" + Thread.currentThread().getName() + "] reader resource 접근 시작"); 
		// queue에서 Reader를 하나 빼온 후 읽기 작업 수행.
		System.err.println("Read 작업 수행! : "+resource);
		System.out.println("[" + Thread.currentThread().getName() + "] reader  resource 접근 종료");
			

	}

	public void endRead() {
		// TODO Auto-generated method stub
		// 읽고있는 reader가 없으면 writer가 쓸 수 있도록 바꿔줌.	
		this.canWrite = true;
	}

	public void beginWrite() {
		// TODO Auto-generated method stub
		// Reader가 쓰고있거나(canRead가 false) 쓰려고 대기중(queue에 있으면)이면 모니터 밖에서 대기.
				while(!canWrite || readers != 0) {};
				// while문을 빠져나오면, Reader가 작업을 안하는 상황임.
				// write 작업을 할꺼라고 Reader에게 알림.
				canRead = false;
				// queue에 writer 추가(+1해주기)
				System.out.println("대기중인 Writer수 : " + ++this.writers);
				try {
					this.semaphore.acquire(); // Thread 가 semaphore에게 시작을 알림 
					//임계영역 코드 작성
					System.out.println("[" + Thread.currentThread().getName() + "] writer resource 접근 시작"); 
					// queue에서 Writer를 하나 빼온 후 쓰기 작업 수행.
					this.resource += 10;
					System.out.println("writer가 resource값을 "+this.resource+"로 변경");
					System.out.println("[" + Thread.currentThread().getName() + "] writer resource 접근 종료");
					//임계영역 코드 작성
					this.semaphore.release(); // Thread 가 semaphore에게 종료를 알림 
				} catch (InterruptedException e) { 
					e.printStackTrace();
				}
	}

	public void endWrite() {
		// TODO Auto-generated method stub
		-- this.writers;
		// 읽고있는 reader가 없으면 writer가 쓸 수 있도록 바꿔줌.
		if(writers == 0) {			
			this.canRead = true;
		}
	}
	
	public static void main(String[] args) {
		Moniter m = new Moniter();

		Reader[] readers = new Reader[20];
		Writer[] writers = new Writer[20];
		
		int writeridx = 0;
		int readidx = 0;
		
		for(int i=0; i<10; ++i) {
			readers[readidx] = new Reader(m, readidx);
			readers[readidx++].start();	
			writers[writeridx] = new Writer(m, writeridx);
			writers[writeridx++].start();
		}
	}
}
