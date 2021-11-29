package chap06_monitor_use_aging;

import java.util.*;
import java.util.concurrent.Semaphore;

public class ReadWrite{
	int readers = 0; // reader의 수
	boolean canRead = false; // Writer가 쓰고있을 경우 false
	boolean canWrite = true; // Reader가 읽고있을 경우 false
	List<Writer> writerList = new ArrayList<>(); // writer가 저장되어있는 리스트. main에서 주소를 넘겨받음.
	private final Semaphore semaphore = new Semaphore(1);
	private int resource = -1;
	
	public ReadWrite(List<Writer> writerList) {
		this.writerList = writerList;
	}
	public void beginRead() {
		// TODO Auto-generated method stub
		// Writer가 쓰고있거나(canRead가 false) 쓰려고 대기중(queue에 있으면)이면 모니터 밖에서 대기.
		while(!canRead && !writerList.isEmpty()) {};
		// while문을 빠져나오면, Writer가 작업을 안하는 상황임.
		// Read 작업을 할꺼라고 Writer에게 알림.
		canWrite = false;

		System.out.println("[" + Thread.currentThread().getName() + "] reader resource 접근 시작"); 
		// queue에서 Reader를 하나 빼온 후 읽기 작업 수행.
		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!Read 작업 수행! : "+resource);
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
				System.out.println("대기중인 Writer수 : " + writerList.size());
				Writer w = (Writer)Thread.currentThread();
				// 자신이 우선순위 가장 높은 스레드면 실행. 
				int max = -1;
				while(true) {
					for(int i=0; i<writerList.size(); i++) {
						int now = writerList.get(i).getAge();
						if(max < now) {
							max = now;
						}else {
							w.setAge();
						}
					}
					if(max <= w.getAge()) {
						// 실행시킬때 list에서 제거시켜줌.
						writerList.remove(w);
						break;
					}
				}
				try {
					this.semaphore.acquire(); // Thread 가 semaphore에게 시작을 알림 
					//임계영역 코드 작성
					System.out.println("[" + Thread.currentThread().getName() + "] writer age("+w.getAge()+")resource 접근 시작"); 
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
		// 읽고있는 reader가 없으면 writer가 쓸 수 있도록 바꿔줌.
		this.canRead = true;

	}
	
	public static void main(String[] args) {
		Reader[] readers = new Reader[100];
		List<Writer> writerList = new ArrayList<>();
		ReadWrite m = new ReadWrite(writerList);
		
		int writeridx = 0;
		int readidx = 0;
		
		for(int i=0; i<readers.length - 1; ++i) {
			readers[readidx] = new Reader(m, readidx);
			readers[readidx++].start();
			
			Writer w = new Writer(m, writeridx++);
			writerList.add(w);
			w.start();
			
		}
		// 맨 마지막에 writer 값을 찍어주기 위해 대기 후 Reader 실행.
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		readers[readidx] = new Reader(m, readidx);
		readers[readidx++].start();
	}
}
