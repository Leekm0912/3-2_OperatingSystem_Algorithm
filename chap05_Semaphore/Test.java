package chap05_Semaphore;

public class Test {
	public static void main(String[] args) { 
		
		int maxThread = 1000;
		Share share = new Share(maxThread);
		final SomeResource resource = new SomeResource(1, share); //바이너리세마포어 임계영역에 동시에 하나의 스레드만 접근
		long start = System.currentTimeMillis();
		for(int i = 1 ; i <= maxThread; i++){ 
			Thread t = new Thread(new Runnable() { 
				public void run() { 
					resource.use(); 
				} 
			}); 
			t.start(); 
		} 
		while(share.end > 0) {
			System.out.println("main 대기중");
		}
		long end = System.currentTimeMillis();
		long result = (end - start);
		System.out.println("실행 시간(ms) : " + result);
	}
}