package finalproject_Deadlock_Prevention;

import java.util.*;
import java.util.concurrent.Semaphore;

public class DeadLockUsingNonPreemption extends Thread {
	int id;
	Random r = new Random();
	int require;
	List<Semaphore> resource;
	Map<Integer, Semaphore> save = new HashMap<>();
	int numOfResource;
	boolean[] lock;

	public DeadLockUsingNonPreemption(int id, int n, List<Semaphore> resource) {
		this.id = id;
		this.resource = resource;
		this.numOfResource = n;
		this.lock = new boolean[n];
	}

	@Override
	public void run() {
		while(true) {
			// System.out.println("start "+ id);
			int rand = r.nextInt(5) + 1; // 몇개의 자원을 사용할지. 1~5개의 자원을 사용함.
			// 고른 자원을 save에 추가
			while (save.size() < rand) {
				int temp = r.nextInt(this.numOfResource);
				if (!save.containsKey(temp)) {
					save.put(temp, this.resource.get(temp));
				}
			}
			System.out.println("id " + this.id + " need : " + Arrays.toString(save.keySet().toArray()));

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
							// 잠겨있으면 확보했던 자원들을 놓고 기다림.
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
				work(save.keySet());
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
		this.save.forEach((k, v)->{
			this.lock[k] = true;
		});
		sleep(1000);
		this.save.forEach((k, v)->{
			this.lock[k] = false;
		});
	}

	public static void main(String[] args) {
		int n = 10;
		List<Semaphore> resource = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			resource.add(new Semaphore(1));
		}
//        Semaphore ls = new Semaphore(1);
		for (int i = 0; i < n; i++) {
			DeadLockUsingNonPreemption p = new DeadLockUsingNonPreemption(i, n, resource);
			p.start();
		}
	}
}