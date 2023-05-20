public class GC_java {
    public static void main(String[] args) {
        // 객체 생성
        GC obj1 = new GC();
        GC obj2 = new GC();

        // obj1이 더 이상 참조되지 않도록 null로 설정
        obj1 = null;

        // GC 호출
        System.gc();

        // 잠시 대기
        try {
            Thread.sleep(1000); // 1초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 메모리 상태 출력
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used Memory: " + usedMemory + " bytes");
    }
}

class GC {
    // finalize() 메소드 재정의
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Finalize method called");
        super.finalize();
    }
}