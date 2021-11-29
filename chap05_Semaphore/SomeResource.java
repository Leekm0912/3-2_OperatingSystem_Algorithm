import java.util.concurrent.Semaphore;

public class SomeResource {
	
	private final Semaphore semaphore; 
	Share share;
	
	public SomeResource(int concurrent, Share share) { 
		this.share = share;
		this.semaphore = new Semaphore(concurrent); 
	} 
	
	public void use() { 
		try { 
			semaphore.acquire(); // Thread 가 semaphore에게 시작을 알림 
			System.out.println("[" + Thread.currentThread().getName() + "]" + "임계영역 시작"); 
			//임계영역 코드 작성
			share.end--;
			System.out.println("[" + Thread.currentThread().getName() + "]" + "임계영역 종료"); 
			semaphore.release(); // Thread 가 semaphore에게 종료를 알림 
		} catch (InterruptedException e) { 
			e.printStackTrace(); 
		} 
	}
}
