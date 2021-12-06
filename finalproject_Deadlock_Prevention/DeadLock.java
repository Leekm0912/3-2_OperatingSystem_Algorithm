package finalproject_Deadlock_Prevention;

import java.util.*;
import java.util.concurrent.Semaphore;

public class DeadLock extends Thread {
	int id;
	Random r = new Random();
	int require;
	List<Semaphore> resource;
	Map<Integer, Semaphore> save = new HashMap<>();
	int numOfResource;

	public DeadLock(int id, int n, List<Semaphore> resource) {
		this.id = id;
		this.resource = resource;
		this.numOfResource = n;
	}

	@Override
	public void run() {
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
			while (true) {
				while (require < rand) {
					this.save.forEach((k, v) -> {
						if (v.tryAcquire()) {
							require++;
						}else {
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
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void work(Set<Integer> need) throws InterruptedException {
		System.out.println("work : " + this.id + " need : " + Arrays.toString(need.toArray()));
		sleep(1000);
	}

	public static void main(String[] args) {
		int n = 10;
		List<Semaphore> stick = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			stick.add(new Semaphore(1));
		}
//        Semaphore ls = new Semaphore(1);
		for (int i = 0; i < n; i++) {
			DeadLock p = new DeadLock(i, n, stick);
			p.start();
		}
	}
}