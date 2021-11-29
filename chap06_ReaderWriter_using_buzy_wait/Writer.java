package chap06_project;

public class Writer extends Thread{
	private Moniter m; 	// 스레드 간 공유 객체 ReadWriteBuffer
	private int val;		// Writer Thread가 작성할 값. -> 스레드 번호를 값으로 씀
	
	public Writer(Moniter m, int num) {
		super("Writer-" + Integer.toString(num));
		this.val = num;
		this.m = m;
	}
	
	public int write() {
		return val;
	}
	@Override
	public void run() {
		m.beginWrite();
		m.endWrite();
	}
}


