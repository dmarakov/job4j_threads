package ru.job4j.concurrent;

public class ConsoleProgress implements Runnable {


    public static void main(String[] args) {
        Thread progress = new Thread(new ConsoleProgress());
        progress.start();
        try {
            Thread.sleep(5000); /* симулируем выполнение параллельной задачи в течение 5 секунд. */
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        progress.interrupt();
    }

    @Override
    public void run() {
        var process = new char[]{'-', '\\', '|', '/'};
        int currentIndex = 0;
        while (!Thread.currentThread().isInterrupted()) {
            System.out.print("\r load: " + process[currentIndex]);
            currentIndex = (currentIndex + 1) % process.length;
        }
    }
}
