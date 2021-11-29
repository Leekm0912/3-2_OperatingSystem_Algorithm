package chap06_ReaderWriter_using_interrupt;

public class Reader extends Thread{
    private Moniter m;		// 스레드 간 공유 객체 ReadWriteBuffer

    public Reader(Moniter m, int num) {
        super("Reader-" + Integer.toString(num));
        this.m = m;
    }

    @Override
    public void run() {
        m.beginRead();
        m.endRead();
    }
}
