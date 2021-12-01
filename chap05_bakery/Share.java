package chap05_bakery;

// Shared.java : 스레드간의 공유 객체로 사용.
public class Share {
	public boolean[] choosing;		// Thread들이 티켓 선택 프로세스를 사용했음을 알려주는 choosing
	public int[] ticket;			// Thread가 가지고 있는 티켓 값을 담는 ticket 배열
	public int[] threadPriority;	// Thread가 가지고 있는 우선순위를 담는 배열
	public int totalThreadCount;	// 베이커리 스레드에 참여하는 총 스레드 개수
	public int prevTicket = 0;		// ticket값을 구하기 위한 변수.
	public int end;
	
	// Constructor -> "n"개의 스레드
	public Share(int n) {
		choosing = new boolean[n];
		ticket = new int[n];
		threadPriority = new int[n];
		totalThreadCount = n;
		this.end = n;
	}
	
	public int maxValue() {
		return prevTicket++;
	}
}