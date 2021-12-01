package chap06_ReaderWriter_with_aging;

public class Writer extends Thread{
	private ReadWrite m; 	
	private int index;		// 객체의 WriterList의 인덱스 번호
	private int age = 0; // 우선순위
	
	public Writer(ReadWrite m, int num) {
		super("Writer-" + Integer.toString(num));
		this.index = num;
		this.m = m;
	}
	
	public int getIndex() {
		return index;
	}
	@Override
	public void run() {
		m.beginWrite();
		m.endWrite();
	}

	public int getAge() {
		return this.age;
	}

	public void setAge() {
		if(this.age < 100) {			
			this.age++;
		}
	}
}
