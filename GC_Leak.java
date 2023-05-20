import java.util.ArrayList;
import java.util.List;
public class GC_Leak {
    private static List<Integer> list = new ArrayList<>();

    public static void main(String[] args) {
        while (true) {
            // 매 반복마다 계속해서 새로운 객체를 리스트에 추가
            List<Integer> newList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                newList.add(i);
            }
            list.addAll(newList);
        }
    }
}
