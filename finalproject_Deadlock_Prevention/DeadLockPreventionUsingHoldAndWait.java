package finalproject_Deadlock_Prevention;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class DeadLockPreventionUsingHoldAndWait extends Thread {
	private int id;
	private int numOfResource;
	private int maxResource;
	private int require = 0;
	private Random random = new Random();
	private List<Semaphore> resource;
	private Map<Integer, Semaphore> save = new HashMap<>();
	private int loop = -1;
	public boolean end = false;
	private int sleepTime = 0;
	static boolean[] usedResource;

	public DeadLockPreventionUsingHoldAndWait(int id, int numOfResource, List<Semaphore> resource, int maxResource, int sleepTime) {
		this.id = id;
		this.resource = resource;
		this.numOfResource = numOfResource;
		this.maxResource = maxResource;
		this.sleepTime = sleepTime;
	}

	public DeadLockPreventionUsingHoldAndWait(int id, int numOfResource, List<Semaphore> resource, int maxResource, int sleepTime,
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
				// 할당
				while (require < rand) {
					synchronized (usedResource) {
						AtomicBoolean test = new AtomicBoolean(true);
						save.forEach((kk, vv) -> {
							if (usedResource[kk]) {
								test.set(false);
							}
						});
						if (test.get() == false) {
							continue;
						}

						this.save.forEach((k, v) -> {
							try {
								usedResource[k] = true;
								v.acquire();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							require++;
						});
					}
				}
				work(save.keySet());
				// 작업 후 초기화
				save.forEach((k, v)->{
					v.release();
					usedResource[k] = false;
				});
				this.save = new HashMap<Integer, Semaphore>();
				require = 0;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		int sleepTime = 0;
		DeadLockPreventionUsingHoldAndWait.usedResource = new boolean[numOfResource];
		List<Semaphore> resource = new ArrayList<>();
		for (int i = 0; i < numOfResource; i++) {
			resource.add(new Semaphore(1));
		}

		long beforeTime = System.currentTimeMillis(); // 시작시간 측정
		List<DeadLockPreventionUsingHoldAndWait> saveThread = new ArrayList<>();
		for (int i = 0; i < numOfThread; i++) {
			DeadLockPreventionUsingHoldAndWait p = new DeadLockPreventionUsingHoldAndWait(i, // Thread id
					numOfResource, resource, maxResource, sleepTime, loop);
			p.start();
			saveThread.add(p);
		}

		// thread가 끝날때까지 대기.
		while (true) {
			boolean threadEnd = true;
			for (int i = 0; i < saveThread.size(); i++) {
				DeadLockPreventionUsingHoldAndWait temp = saveThread.get(i);
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