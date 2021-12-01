package chap05_DijkstraAlgorithm;

// Shared.java : 스레드간의 공유 객체로 사용.
public class Share {
	public int totalThreadCount; // 총 스레드 개수
	public int[] flag; // 스레드 별 상태
	public int turn = 0; // 스레드 실행시킬 차례
	public int end = 0;
	
	private static Share instance = null;
	// Constructor -> "n"개의 스레드
	private Share(int n) {
		totalThreadCount = n;
		this.flag = new int[n];
		this.end = n;
	}
	
	public static Share getInstance(int n) {
		if(instance == null) {
			instance = new Share(n);
		}
		return instance;
	}
}