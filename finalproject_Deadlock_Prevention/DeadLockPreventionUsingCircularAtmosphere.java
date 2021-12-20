package finalproject_Deadlock_Prevention;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class DeadLockPreventionUsingCircularAtmosphere extends Thread {
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
	public int run = 0;

	public DeadLockPreventionUsingCircularAtmosphere(int id, int numOfResource, List<Semaphore> resource, int maxResource, int sleepTime) {
		this.id = id;
		this.resource = resource;
		this.numOfResource = numOfResource;
		this.maxResource = maxResource;
		this.sleepTime = sleepTime;
	}

	public DeadLockPreventionUsingCircularAtmosphere(int id, int numOfResource, List<Semaphore> resource, int maxResource, int sleepTime,
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
				// System.out.println("id " + this.id + " need : " + rand + "resource");
				
				while (save.size() < rand) {
					int temp = random.nextInt(this.numOfResource);
					if (!save.containsKey(temp)) {
						save.put(temp, this.resource.get(temp));
					}
				}

				List<Map.Entry<Integer, Semaphore>> entries = save.entrySet().stream()
					.sorted(Map.Entry.comparingByKey())
					.collect(Collectors.toList());
				

				while (require < rand) {
					for (Map.Entry<Integer, Semaphore> entry : entries) {
						entry.getValue().acquire();
						require++;
					}
				}
				work(save.keySet());
				// 작업 후 초기화
				save.forEach((key, value) -> {
					value.release();
				});
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
		this.run++;
		sleep(this.sleepTime);
		this.run--;
	}

	public static void main(String[] args) throws InterruptedException, IOException{
		// 리소스의 개수
		int numOfResource = 100;
		// 스레드의 개수
		int numOfThread = 150;
		// 스레드가 필요로 하는 최대 리소스 개수
		int maxResource = 5;
		// 반복 횟수. -1이면 무한반복
		int loop = 50;
		// work의 sleep 시간
		int sleepTime = 100;

		
		// 현재 날짜 구하기
		Date time = new Date();
		SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd_HH_mm_ss");
		// 기록을 위한 파일 객체 생성.
		File file = new File("./"+format1.format(time)+"DeadLockPreventionUsingCircularAtmosphereLog.txt");
		FileWriter fw = null;
		BufferedWriter writer = null;
		// 파일이 없으면 생성.
		if (!file.exists()) {
			try {
				file.createNewFile();
				// Buffer를 사용해서 File에 write할 수 있는 BufferedWriter 생성
				fw = new FileWriter(file);
				writer = new BufferedWriter(fw);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		List<Semaphore> resource = new ArrayList<>();
		for (int i = 0; i < numOfResource; i++) {
			resource.add(new Semaphore(1, true));
		}

		long beforeTime = System.currentTimeMillis(); // 시작시간 측정

		List<DeadLockPreventionUsingCircularAtmosphere> saveThread = new ArrayList<>();
		for (int i = 0; i < numOfThread; i++) {
			DeadLockPreventionUsingCircularAtmosphere p = new DeadLockPreventionUsingCircularAtmosphere(i, // Thread id
					numOfResource, resource, maxResource, sleepTime, loop);
			p.start();
			saveThread.add(p);
		}

		// thread가 끝날때까지 대기.
		long[] threadEndTime = new long[numOfThread];
		boolean[] threadEndCheck = new boolean[numOfThread];
		while (true) {
			boolean allthreadEnd = true;
			int runningWork = 0;
			for (int i = 0; i < saveThread.size(); i++) {
				DeadLockPreventionUsingCircularAtmosphere temp = saveThread.get(i);
				if (temp.end == false) {
					allthreadEnd = false;
					runningWork += temp.run;
				}else {
					if(threadEndCheck[i] == false) {
						long afterTime = System.currentTimeMillis(); // 코드 실행 후의 시간 측정
						threadEndTime[i] = (afterTime - beforeTime);
						threadEndCheck[i] = true;
					}
				}
			}
			sleep(100);
			long afterTime = System.currentTimeMillis();
			System.out.println("runningWork : "+runningWork + "\ttime : " + (afterTime - beforeTime));
			// 외부 파일로 기록.
			try {
				writer.write((afterTime - beforeTime) + "," + runningWork+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (allthreadEnd) {
				break;
			}
		}
		long afterTime = System.currentTimeMillis(); // 코드 실행 후의 시간 측정
		for(int i = 0; i < numOfThread; i++) {
			System.err.println(i + "번 스레드 종료시간(ms) : " + threadEndTime[i]);
		}
		System.err.println(" 총 소요시간(ms) : " + (afterTime - beforeTime));
		long max = Arrays.stream(threadEndTime).max().getAsLong();
		long min = Arrays.stream(threadEndTime).min().getAsLong();
		System.err.println(" 스레드 별 차이(ms) : " + (max - min));

		writer.close();

	}
}