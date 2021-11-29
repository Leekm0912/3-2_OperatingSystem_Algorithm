package chap05_project.timecheck;


public class DijkstraAlgorithm extends Thread {
   public int threadNum; // 자신의 스레드 번호
   public Share share; // 상호 배제에 필요한 공유 정보들

   public DijkstraAlgorithm(int threadNum, Share share) {
      this.threadNum = threadNum;
      this.share = share;
   }

   @Override
   public void run() {
      System.out.println("thread" + threadNum + " start");
      int j;
      // 프로세스i의 진입 영역
      // 임계구역 진입시도 1단계
      do {
         // 자신이 1단계 진입시도를 한다고 알림.
         this.share.flag[threadNum] = 1;
         // 자신의 차례가 될 때까지 대기를 함.
         while (this.share.turn != threadNum) {
            // 임계구역에 프로세스가 없다면,
            System.out.println(threadNum + "번 스레드 1단계 진입시도");
            if (this.share.flag[this.share.turn] == 0) {
               // 자신의 차례로 변경함.
               this.share.turn = threadNum;
               System.out.println(threadNum + "번 스레드 1단계 진입성공");

            }
         }
         // 임계구역 진입시도 2단계
         // 임계구역에 진입하겠다고 알림.
         this.share.flag[threadNum] = 2;
         j = 0;
         // 자신을 제외한 in-CS 상태의 프로세스가 있는지 검사 함.
         // j가 전체 스레드 수보다 작고 j가 자신의 스레드 번호거나 다른 스레드 번호의 flag값이 2가 아니면 true.
         while ((j < this.share.totalThreadCount) && (j == threadNum || this.share.flag[j] != 2)) {
            j++;
            // System.out.println("thread" + threadNum + " j : " + j);
         }
         // 자신 외에 2단계 진입을 시도하는 프로세스가 있다면 j가 최대 스레드 수보다 작을것이므로 다시 1단계로 돌아감.
      } while (j < this.share.totalThreadCount);

      System.out.println(" Enter Critical Section." + "\tThread" + threadNum);
      /*
       * 임계영역 코드는 여기에 작성
       */
      System.out.println(" Exit Critical Section." + "\t\tThread" + threadNum);

      // 임계구역 사용완료를 알림.
      --this.share.end;
      this.share.flag[threadNum] = 0;
   }

   public static void main(String[] args) {
      System.out.println("다익스트라 알고리즘 시작");
//      Scanner sc = new Scanner(System.in);
//      System.out.print("프로그램에 참여 할 스레드의 개수 입력 : ");
//      int n = sc.nextInt();
//      sc.close();
      int n = 100;
      Share share = Share.getInstance(n);
      long beforeTime = System.currentTimeMillis(); // 시작시간 측정
      for (int i = n - 1; i >= 0; --i) {
         Thread th = new DijkstraAlgorithm(i, share);
         th.start();
      }
      while(share.end > 0) {
         System.out.println("main 대기중");
      }
      long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
      long secDiffTime = (afterTime - beforeTime); // 두 시간에 차 계산
      System.err.println(" 소요시간(ms) : " + secDiffTime);
   }
}