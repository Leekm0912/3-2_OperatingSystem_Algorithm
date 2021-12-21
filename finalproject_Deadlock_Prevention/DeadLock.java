package finalproject_Deadlock_Prevention;

import java.util.*;
import java.util.concurrent.Semaphore;

public class DeadLock extends Thread {
	// 스레드의 id
	private int id;
	// 공유 자원의 개수
	private int numOfResource;
	// 스레드가 필요로하는 자원의 최대 개수 (1 ~ maxResource개 랜덤 요구함)
	private int maxResource;
	// 스레드가 필요로 하는 자원을 몇개 가져왔는지.
	private int require = 0;
	// 랜덤 사용을 위한 외부 객체.
	private Random random = new Random();
	// 모든 공유자원을 저장해놓은 리스트.
	private List<Semaphore> resource;
	// 자신이 가져온 공유자원을 저장한 리스트
	private Map<Integer, Semaphore> save = new HashMap<>();
	// 몇회 반복할 것인지. -1이면 무한반복
	private int loop = -1;
	// 스레드의 작업이 끝났는지.
	public boolean end = false;
	// work 메서드가 얼마동안 작업(현재는 sleep)을 할지.
	private int sleepTime = 0;

	public DeadLock(int id, int numOfResource, List<Semaphore> resource, int maxResource, int sleepTime) {
		this.id = id;
		this.resource = resource;
		this.numOfResource = numOfResource;
		this.maxResource = maxResource;
		this.sleepTime = sleepTime;
	}

	public DeadLock(int id, int numOfResource, List<Semaphore> resource, int maxResource, int sleepTime,
			int loopCount) {
		this(id, numOfResource, resource, maxResource, sleepTime);
		this.loop = loopCount;
	}

	@Override
	public void run() {
		// loop가 -1이 아니면 무한반복 안하도록 설정.
		int loopCount = 0;
		if (loop != -1) {
			loopCount = loop;
		}
		while (loopCount > 0 || this.loop == -1) {
			try {
				// 스레드가 몇개의 자원을 필요로 할지. 1 ~ maxResource 사이의 값이 랜덤으로 사용됨.
				int rand = random.nextInt(this.maxResource) + 1;
				// 고른 자원을 save에 추가
				while (save.size() < rand) {
					int temp = random.nextInt(this.numOfResource);
					if (!save.containsKey(temp)) {
						save.put(temp, this.resource.get(temp));
					}
				}
				System.out.println("id " + this.id + " need : " + Arrays.toString(save.keySet().toArray()));

				while (require < rand) {
					this.save.forEach((k, v) -> {
						if (v.tryAcquire()) {
							require++;
						} else {
							// System.out.println(Thread.currentThread() + " " + k + " fail");
						}
					});
				}
				work(save.keySet());
				// 작업 후 초기화
				for (Semaphore s : this.save.values()) {
					s.release();
				}
				this.save = new HashMap<Integer, Semaphore>();
				require = 0;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			loopCount--;
		}
		this.end = true;
	}

	private void work(Set<Integer> need) throws InterruptedException {
		System.out.println("work : " + this.id + " need : " + Arrays.toString(need.toArray()));
		sleep(this.sleepTime);
	}

	public static void main(String[] args) {
		// 리소스의 개수
		int numOfResource = 20;
		// 스레드의 개수
		int numOfThread = 15;
		// 스레드가 필요로 하는 최대 리소스 개수
		int maxResource = 5;
		// 반복 횟수. -1이면 무한반복
		int loop = -1;
		// work의 sleep 시간
		int sleepTime = 1000;

		List<Semaphore> resource = new ArrayList<>();
		for (int i = 0; i < numOfResource; i++) {
			resource.add(new Semaphore(1));
		}

		long beforeTime = System.currentTimeMillis(); // 시작시간 측정
		List<DeadLock> saveThread = new ArrayList<DeadLock>();
		for (int i = 0; i < numOfThread; i++) {
			DeadLock p = new DeadLock(i, // Thread id
					numOfResource, resource, maxResource, sleepTime, loop);
			p.start();
			saveThread.add(p);
		}

		// thread가 끝날때까지 대기.
		while (true) {
			boolean threadEnd = true;
			for (int i = 0; i < saveThread.size(); i++) {
				DeadLock temp = saveThread.get(i);
				if (temp.end == false) {
					threadEnd = false;
				}
			}
			if (threadEnd) {
				break;
			}
		}
		long afterTime = System.currentTimeMillis(); // 코드 실행 후의 시간 측정
		System.err.println(" 소요시간(ms) : " + (afterTime - beforeTime));
	}
}