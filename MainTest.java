import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.function.Consumer;
import java.util.stream.Stream;

class MainTest {

    static final int WARMUP = 2;   // прогревочные прогоны, не считаем
    static final int RUNS = 5;     // прогоны, по которым берём среднее

    // лучший случай: уже отсортирован
    static int[] best(int N) {
        int[] arr = new int[N];
        for (int i = 0; i < N; i++) arr[i] = i;
        return arr;
    }

    // худший случай: обратный порядок
    static int[] worst(int N) {
        int[] arr = new int[N];
        for (int i = 0; i < N; i++) arr[i] = N - i + 1;
        return arr;
    }

    // средний случай: случайный массив, seed фиксированный, чтобы список был один и тот же
    static int[] random(int N) {
        java.util.Random rnd = new java.util.Random(42);
        int[] arr = new int[N];
        for (int i = 0; i < N; i++) arr[i] = rnd.nextInt();
        return arr;
    }

    // среднее время сортировки; каждый прогон получает свежую копию одного и того же массива
    static long average(int[] source, Consumer<int[]> sort) {
        long total = 0;
        for (int r = 0; r < WARMUP + RUNS; r++) {
            int[] arr = source.clone();
            long start = System.nanoTime();
            sort.accept(arr);
            long diff = System.nanoTime() - start;
            if (r >= WARMUP) total += diff;

            for (int i = 0; i < arr.length - 1; i++) {
                Assertions.assertTrue(arr[i] <= arr[i + 1]);
            }
        }
        return total / RUNS;
    }

    static void compare(String label, int N, int[] source) {
        long ins = average(source, Main::insertionSort);
        long bub = average(source, Main::bubbleSort);
        long jdk = average(source, java.util.Arrays::sort);
        System.out.println(label + " " + N + " | " + ins + " | " + bub + " | " + jdk);
    }

    @ParameterizedTest
    @MethodSource("testSortData")
    void testBest(int N) {
        compare("BEST", N, best(N));
    }

    @ParameterizedTest
    @MethodSource("testSortData")
    void testWorst(int N) {
        compare("WORST", N, worst(N));
    }

    @ParameterizedTest
    @MethodSource("testSortData")
    void testRandom(int N) {
        compare("RANDOM", N, random(N));
    }

    static Stream<Arguments> testSortData() {
        return Stream.of(
                Arguments.of(100),
                Arguments.of(200),
                Arguments.of(400),
                Arguments.of(800),
                Arguments.of(1600),
                Arguments.of(3200),
                Arguments.of(6400),
                Arguments.of(12800),
                Arguments.of(25600),
                Arguments.of(51200),
                Arguments.of(102400)
        );
    }
}
