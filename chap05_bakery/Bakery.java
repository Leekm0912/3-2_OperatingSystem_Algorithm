package chap05_bakery;

public class Bakery extends Thread {
	private Share share; // 공유 객체 필드
	private int threadNum; // 현재 스레드의 번호를 나타내줌.

	// name - 스레드의 이름, share - 공유 객체
	public Bakery(String name, Share share) {
		super(name); // 스레드의 이름 지정
		this.share = share;
		threadNum = Integer.parseInt(name);
	}

	@Override
	public void run() {
		share.threadPriority[threadNum] = threadNum + 1; // 스레드의 우선순위를 지정 "스레드 번호 + 1"값의 우선순위를 가진다.

		try {
			Thread.sleep((int) (Math.random()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Thread" + threadNum + " Start!");

		// 베이커리 알고리즘의 "티켓 우선순위 비교" 알고리즘에서 그대로 채택해온 방식이다.
		// 티켓 우선순위가 아닌, "스레드의 우선순위"를 비교하여 우선순위가 높은 스레드부터 티켓을 뽑게하는 방식이다.
		for (int i = 0; i < share.totalThreadCount; ++i) {

			if (i == threadNum) {
				continue;
			}

			while (share.threadPriority[i] != 0 && share.threadPriority[i] < share.threadPriority[threadNum]) {
				// sleep의 이유 -> 바쁜 대기를 하면서 while의 속도가 너무 빨라 sleep을 주지 않으면 프로그램이 다운되버리는 현상이 발생.
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		// 아래부터는 책의 알고리즘과 동일하다.
		share.choosing[threadNum] = true; // 티켓 선택을 시작.
		try {
			Thread.sleep((int) (Math.random() * 10));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		share.ticket[threadNum] = share.maxValue() + 1; // 티켓 값을 할당받는다.
		share.choosing[threadNum] = false; // 티켓 선택 종료
		share.threadPriority[threadNum] = 0; // 스레드가 티켓을 뽑았다면, 다음 우선순위가 높은 스레드에게 티켓을 뽑을 수 있도록 한다
		System.out.println("Thread" + threadNum + " Got Ticket" + share.ticket[threadNum]);

		for (int i = 0; i < share.totalThreadCount; i++) {
			// 자신의 티켓은 확인 할 필요가 없음.
			if (i == threadNum)
				continue;

			// choosing[n]이 true이면? -> n번 스레드는 현재 "티켓을 뽑는 중"인 상태다.
			while (share.choosing[i] != false) {
				System.out.println("Thread" + threadNum + " Wait Until Thread" + i + " Got Ticket");
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// i번 스레드가 현재 스레드보다 티켓 우선순위가 높은 경우,
			// 현재 스레드는 i번 스레드가 끝날때 까지(ticket[i] == 0) 바쁜 대기.
			while (share.ticket[i] != 0 && share.ticket[i] < share.ticket[threadNum]) {
				System.out.println("Thread" + threadNum + " Wait Until Thread" + i + " Enter Critical Section");
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			;

			// 만약 i번 스레드와 현재 스레드의 티켓 우선순위가 같다면?
			// 베이커리 알고리즘은 "더 낮은 번호"의 스레드를 선호한다.
			if (share.ticket[i] == share.ticket[threadNum] && i < threadNum) {
				while (share.ticket[i] != 0) {
					System.out.println("Thread" + threadNum + " Wait Until Thread" + i + " Enter Critical Section");
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("Thread" + threadNum + " Enter Critical Section.");
		/*
		 * 임계영역 코드는 여기에 작성
		 */
		System.out.println("Thread" + threadNum + " Exit Critical Section.");
		System.out.println("end : " + --this.share.end);
		share.ticket[threadNum] = 0; // exitMutualExclusion() - 종료작업.

	}

	// 실험 방식은 다음과 같다. "스레드의 이름 = 스레드의 번호" 라고 가정하여 테스트를 수행하였다.
	// 스레드의 우선순위는 다음과 같이 지정했다. "스레드의 번호 = 스레드의 우선순위"
	// 이 때, 번호가 낮은 스레드일수록 높은 우선순위를 가지게 하였다. 즉 "0"번 스레드가 가장 높은 우선순위를 갖는다.
	public static void main(String[] args) {
		int n = 100;
		Share share = new Share(n);

		long beforeTime = System.currentTimeMillis(); // 시작시간 측정
		for (int i = n - 1; i >= 0; --i) {
			Thread th = new Bakery(Integer.toString(i), share);
			th.start();
		}

		while (share.end > 0) {
			System.out.println("main 대기중");
		}
		long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
		long secDiffTime = (afterTime - beforeTime); // 두 시간에 차 계산
		System.err.println("소요시간(s) : " + (double)secDiffTime/1000);

	}
}