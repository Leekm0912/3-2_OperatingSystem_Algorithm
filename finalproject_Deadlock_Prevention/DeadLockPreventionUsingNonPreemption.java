package finalproject_Deadlock_Prevention;

import java.util.*;
import java.util.concurrent.Semaphore;

public class DeadLockPreventionUsingNonPreemption extends Thread {
	private int id;
	private int numOfResource;
	private int require = 0;
	private Random random = new Random();
	private List<Semaphore> resource;
	private Map<Integer, Semaphore> save = new HashMap<>();
	private boolean[] lock;

	public DeadLockPreventionUsingNonPreemption(int id, int numOfResource, List<Semaphore> resource) {
		this.id = id;
		this.resource = resource;
		this.numOfResource = numOfResource;
		this.lock = new boolean[numOfResource];
	}

	@Override
	public void run() {
		while (true) {
			// System.out.println("start "+ id);
			int rand = random.nextInt(5) + 1; // 몇개의 자원을 사용할지. 1~5개의 자원을 사용함.
			// 고른 자원을 save에 추가
			while (this.save.size() < rand) {
				int temp = random.nextInt(this.numOfResource);
				if (!this.save.containsKey(temp)) {
					this.save.put(temp, this.resource.get(temp));
				}
			}
			System.out.println("id " + this.id + " need : " + Arrays.toString(this.save.keySet().toArray()));

			try {
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
				work(this.save.keySet());
				// 작업 후 초기화
				for (Semaphore s : this.save.values()) {
					s.release();
				}
				this.save = new HashMap<Integer, Semaphore>();
				this.require = 0;
				// System.out.println("초기화 완료 " + id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void work(Set<Integer> need) throws InterruptedException {
		System.out.println("work : " + this.id + " need : " + Arrays.toString(need.toArray()));
		this.save.forEach((k, v) -> {
			this.lock[k] = true;
		});
		sleep(1000);
		this.save.forEach((k, v) -> {
			this.lock[k] = false;
		});
	}

	public static void main(String[] args) {
		int numOfResource = 10;
		int numOfThread = 10;
		
		List<Semaphore> resource = new ArrayList<>();
		for (int i = 0; i < numOfResource; i++) {
			resource.add(new Semaphore(1));
		}

		for (int i = 0; i < numOfThread; i++) {
			DeadLockPreventionUsingNonPreemption p = new DeadLockPreventionUsingNonPreemption(i, numOfResource, resource);
			p.start();
		}
	}
}