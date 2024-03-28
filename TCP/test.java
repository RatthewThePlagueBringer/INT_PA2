import java.util.ArrayList;
import java.util.Collections;

public class test {
    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        long test = 0;

        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i=1; i<11; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        System.out.println("Time to complete: " + time + " ms");
        for (int num : numbers) {
            System.out.println(num + " ");
        }
    }
}