package chap06_ReaderWriter_using_interrupt;

public class Writer extends Thread{
    private Moniter m; 	// 스레드 간 공유 객체 ReadWriteBuffer

    public Writer(Moniter m, int num) {
        super("Writer-" + Integer.toString(num));
        this.m = m;
    }

    @Override
    public void run() {
        m.beginWrite();
        m.endWrite();
    }
}