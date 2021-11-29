import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class Moniter{
	int readers = 0; // reader의 수
	boolean writeLock = false;
	private Queue<Thread> canWrite = new LinkedList<>();
	private Queue<Thread> canRead = new LinkedList<>();
	private final Semaphore semaphore = new Semaphore(1);
	private int resource = 0;
	
	public void beginRead() {
		// TODO Auto-generated method stub
		// Writer가 쓰고있거나(canRead가 false) 쓰려고 대기중(queue에 있으면)이면 모니터 밖에서 대기.
		Thread thisThread = Thread.currentThread();

		if(writeLock || !canWrite.isEmpty()) {
				try {
					try {
						this.semaphore.acquire();
						canRead.add(thisThread);
						this.semaphore.release();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					Thread.sleep(10000);
				} catch (InterruptedException e) {}
		}
		++readers;
		System.out.println("[" + thisThread.getName() + "]"+" 작업 수행! : "+resource);
	}

	public void endRead() {
		// TODO Auto-generated method stub
		// 읽고있는 reader가 없으면 writer가 쓸 수 있도록 바꿔줌.	

			--readers;
			try {
				this.semaphore.acquire();
				if(readers == 0 && !canWrite.isEmpty()) {
					canWrite.poll().interrupt();
				}
				this.semaphore.release();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void beginWrite() {
		// TODO Auto-generated method stub
		// Reader가 쓰고있거나(canRead가 false) 쓰려고 대기중(queue에 있으면)이면 모니터 밖에서 대기.
		
		Thread thisThread = Thread.currentThread();
			if(readers>0 || writeLock) {
				try {
					this.semaphore.acquire();
					canWrite.add(thisThread);
					this.semaphore.release();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
				}
			}
				
		// while문을 빠져나오면, Reader가 작업을 안하는 상황임.
		// write 작업을 할꺼라고 Reader에게 알림.
		// queue에 writer 추가(+1해주기)
		writeLock = true;
		try {
			this.semaphore.acquire(); // Thread 가 semaphore에게 시작을 알림 
			//임계영역 코드 작성
			System.out.println("대기중인 Writer수 : " + canWrite.size());
			System.out.println("[" + thisThread.getName() + "] writer resource 접근 시작"); 
			// queue에서 Writer를 하나 빼온 후 쓰기 작업 수행.
			this.resource += 10;
			System.out.println("writer가 resource값을 "+this.resource+"로 변경");
			System.out.println("[" + thisThread.getName() + "] writer resource 접근 종료");
			//임계영역 코드 작성
			this.semaphore.release(); // Thread 가 semaphore에게 종료를 알림 
		} catch (InterruptedException e) { 
		}
		
	}

	public void endWrite() {
		// TODO Auto-generated method stub
		writeLock = false;
		try {
			this.semaphore.acquire();
			if(!canRead.isEmpty()) {
				canRead.poll().interrupt();
			}else if(!canWrite.isEmpty()){
				canWrite.poll().interrupt();
			}
			this.semaphore.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args) {
	
		Moniter monitor = new Moniter();
		int readidx = 0;
		int writeidx = 0;

		for(int i=0; i<10; ++i) {
			Writer w = new Writer(monitor, ++writeidx);
			w.start();
			Reader r = new Reader(monitor, ++readidx);
			r.start();
		}
	}
}
