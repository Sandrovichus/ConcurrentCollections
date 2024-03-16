import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static BlockingQueue<String> stringsA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> stringsB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> stringsC = new ArrayBlockingQueue<>(100);
    public static String outputA;
    public static String outputB;
    public static String outputC;
    public static int textCount = 10_000;                  // кол-во обрабатываемых слов
    public static int textLength = 100_000;                // длина слов
    public static String charsInText = "abc";              // из каких символов должны состоять слова

    public static void main(String[] args) throws InterruptedException {
        Thread put = new Thread(() -> {
            for (int i = 0; i < textCount; i++) {
                String text = generateText(charsInText, textLength);
                try {
                    stringsA.put(text);
                    stringsB.put(text);
                    stringsC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        Thread a = new Thread(() -> {
            try {
                outputA = "Максимальное количество символов 'a' в слове: " + countMaxChar('a', stringsA);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread b = new Thread(() -> {
            try {
                outputB = "Максимальное количество символов 'b' в слове: " + countMaxChar('b', stringsB);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread c = new Thread(() -> {
            try {
                outputC = "Максимальное количество символов 'c' в слове: " + countMaxChar('c', stringsC);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        put.start();
        a.start();
        b.start();
        c.start();

        put.join();
        a.join();
        b.join();
        c.join();

        System.out.println(outputA);
        System.out.println(outputB);
        System.out.println(outputC);
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countMaxChar(char letter, BlockingQueue<String> bQueue) throws InterruptedException {
        int maxCount = 0;
        for (int i = 0; i < textCount; i++) {
            int count = 0;
            String text = bQueue.take();
            for (int j = 0; j < text.length(); j++) {
                if (text.charAt(j) == letter) {
                    count++;
                }
            }
            if (count > maxCount) {
                maxCount = count;
            }
        }
        return maxCount;
    }
}
