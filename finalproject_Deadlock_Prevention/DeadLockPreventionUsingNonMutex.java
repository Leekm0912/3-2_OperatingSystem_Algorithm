package finalproject_Deadlock_Prevention;

import java.util.*;


public class DeadLockPreventionUsingNonMutex extends Thread {
	private int id;
	private int numOfResource;
	private int maxResource;
	
	private Random random = new Random();
	private List<Integer> resource;
	private Map<Integer, Integer> save = new HashMap<>();
	private int loop = -1;
	public boolean end = false;
	private int sleepTime = 0;

	public DeadLockPreventionUsingNonMutex(int id, int numOfResource, List<Integer> resource, int maxResource, int sleepTime) {
		this.id = id;
		this.resource = resource;
		this.numOfResource = numOfResource;
		this.maxResource = maxResource;
		this.sleepTime = sleepTime;
	}

	public DeadLockPreventionUsingNonMutex(int id, int numOfResource, List<Integer> resource, int maxResource, int sleepTime,
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

			
			work(save.keySet());
			// 작업 후 초기화
			loopCount--;
		}
		this.end = true;
	}

	private void work(Set<Integer> need){
		System.out.println("work : " + this.id + " need : " + Arrays.toString(need.toArray()));
		try {
			sleep(this.sleepTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

		List<Integer> resource = new ArrayList<>();
		for (int i = 0; i < numOfResource; i++) {
			resource.add(i*10);
		}

		long beforeTime = System.currentTimeMillis(); // 시작시간 측정
		List<DeadLockPreventionUsingNonMutex> saveThread = new ArrayList<>();
		for (int i = 0; i < numOfThread; i++) {
			DeadLockPreventionUsingNonMutex p = new DeadLockPreventionUsingNonMutex(i, // Thread id
					numOfResource, resource, maxResource, sleepTime, loop);
			p.start();
			saveThread.add(p);
		}

		// thread가 끝날때까지 대기.
		while (true) {
			boolean threadEnd = true;
			for (int i = 0; i < saveThread.size(); i++) {
				DeadLockPreventionUsingNonMutex temp = saveThread.get(i);
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
