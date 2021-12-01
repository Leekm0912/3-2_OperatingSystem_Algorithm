package chap06_ReaderWriter_using_buzy_wait;

public class Reader extends Thread{
	private Moniter m;		// 스레드 간 공유 객체 ReadWriteBuffer
	
	public Reader(Moniter m, int num) {
		super("Reader-" + Integer.toString(num));
		this.m = m;
	}
	
	public void read(int resource) {
		System.out.println(resource);
	}
	
	@Override
	public void run() {
		m.beginRead();
		m.endRead(); 
	}
}



