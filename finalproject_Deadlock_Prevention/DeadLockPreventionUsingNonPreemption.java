package finalproject_Deadlock_Prevention;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Semaphore;

public class DeadLockPreventionUsingNonPreemption extends Thread {
	private int id; // 스레드의 id
	private int numOfResource; // 공유 자원의 개수
	private int maxResource; // 스레드가 필요로하는 자원의 최대 개수 (1 ~ maxResource개 랜덤 요구함)
	private int require = 0; // 스레드가 필요로 하는 자원을 몇개 가져왔는지. 
	private Random random = new Random();
	private List<Semaphore> resource; // 모든 공유자원을 저장해놓은 리스트.
	private Map<Integer, Semaphore> save = new HashMap<>(); // 자신이 가져온 공유자원을 저장한 리스트
	private boolean[] lock; // 공유 자원이 작업중인지 판단하기 위한 배열.
	private int loop = -1; // 몇회 반복할 것인지. -1이면 무한반복
	public boolean end = false; // 스레드의 작업이 끝났는지.
	private int sleepTime = 0; // work 메서드가 얼마동안 작업(현재는 sleep)을 할지.

	public DeadLockPreventionUsingNonPreemption(int id, int numOfResource, List<Semaphore> resource, int maxResource,
			int sleepTime) {
		this.id = id;
		this.resource = resource;
		this.numOfResource = numOfResource;
		this.lock = new boolean[numOfResource];
		this.maxResource = maxResource;
		this.sleepTime = sleepTime;
	}

	public DeadLockPreventionUsingNonPreemption(int id, int numOfResource, List<Semaphore> resource, int maxResource,
			int sleepTime, int loopCount) {
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
			// System.out.println("start "+ id);
			// 스레드가 몇개의 자원을 필요로 할지. 1 ~ maxResource 사이의 값이 랜덤으로 사용됨.
			int rand = random.nextInt(this.maxResource) + 1;
			// 고른 자원을 save에 추가
			while (this.save.size() < rand) {
				int temp = random.nextInt(this.numOfResource);
				if (!this.save.containsKey(temp)) {
					this.save.put(temp, this.resource.get(temp));
				}
			}
			System.out.println("id " + this.id + " need : " + Arrays.toString(this.save.keySet().toArray()));

			try {
				// 필요 자원을 모두 가져올동안 반복함.
				while (require < rand) {
					this.save.forEach((k, v) -> {
						if (v.tryAcquire()) {
							require++;
						} else {
							// 잠겨있지 않으면 뺏어옴
							if (!lock[k]) {
								try {
									v.release();
									v.acquire();
									require++;
								} catch (Exception e) {
								}
							}
							// 잠겨있으면 확보했던 자원들을 놓고 처음으로 돌아감.
							else {
								save.forEach((kk, vv) -> {
									vv.release();
								});
								require = 0;
								return;
							}
							// System.out.println(Thread.currentThread() + " " + k + " fail");
						}
					});
					// System.out.println(require);
				}
				// 반복문을 빠져나왔으면 필요 자원을 모두 가져온것. 작업 수행하면 됨.
				work(this.save.keySet());
				// 작업 후 초기화
				for (Semaphore s : this.save.values()) {
					s.release();
				}
				this.save = new HashMap<Integer, Semaphore>();
				this.require = 0;
				loopCount--;
				// System.out.println("초기화 완료 " + id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// 스레드의 작업이 끝나면 true로 바꿔 작업이 끝난걸 알려줌.
		this.end = true;
	}

	private void work(Set<Integer> need) throws InterruptedException {
		// 작업 중일때는 사용중인 자원에 lock을 걸어 선점이 불가능하게 함.
		this.save.forEach((k, v) -> {
			this.lock[k] = true;
		});
		// 작업 수행
		System.out.println("work : " + this.id + " use : " + Arrays.toString(need.toArray()));
		sleep(this.sleepTime);
		// 작업 수행
		// 작업이 끝나면 lock을 풀어주어 선점이 가능하게 함.
		this.save.forEach((k, v) -> {
			this.lock[k] = false;
		});
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
		List<DeadLockPreventionUsingNonPreemption> saveThread = new ArrayList<DeadLockPreventionUsingNonPreemption>();
		// 스레드 생성 및 실행
		for (int i = 0; i < numOfThread; i++) {
			DeadLockPreventionUsingNonPreemption p = new DeadLockPreventionUsingNonPreemption(
					i, // Thread id
					numOfResource, resource, maxResource, sleepTime, loop
			);
			p.start();
			saveThread.add(p);
		}
		// 자식 스레드 작업이 종료될때까지 메인 스레드 대기.
		while (true) {
			boolean threadEnd = true;
			for (int i = 0; i < saveThread.size(); i++) {
				DeadLockPreventionUsingNonPreemption temp = saveThread.get(i);
				// 자식 스레드가 하나라도 작업이 종료되지 않으면 반복문 탈출 안함.
				if (temp.end == false) {
					threadEnd = false;
				}
			}
			// 자식 스레드의 작업이 끝나면 반복문 탈출.
			if (threadEnd) {
				break;
			}
		}
		long afterTime = System.currentTimeMillis(); // 코드 실행 후의 시간 측정
		System.err.println(" 소요시간(ms) : " + (afterTime - beforeTime));
	}
}