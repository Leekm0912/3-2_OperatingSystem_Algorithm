package monitor_use_aging;

public class Reader extends Thread{
	private ReadWrite m;
	
	public Reader(ReadWrite m, int num) {
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



