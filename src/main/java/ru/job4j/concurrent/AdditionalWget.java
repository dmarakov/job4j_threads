package ru.job4j.concurrent;


import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.IOException;


public class AdditionalWget implements Runnable {
    private final String url;
    private final int speed;

    public AdditionalWget(String url, int speed) {
        this.url = url;
        this.speed = speed;
    }

    @Override
    public void run() {
        var startAt = System.currentTimeMillis();
        var file = new File("tmp.xml");
        try (var input = new URL(url).openStream();
             var output = new FileOutputStream(file)) {
            System.out.println("Open connection: " + (System.currentTimeMillis() - startAt) + " ms");
            var dataBuffer = new byte[512];
            int bytesRead;
            while ((bytesRead = input.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                var downloadAt = System.nanoTime();
                output.write(dataBuffer, 0, bytesRead);
                long downloadSpeed = Math.round((dataBuffer.length * 1.0 / (System.nanoTime() - downloadAt)) * 1000000);
                if (downloadSpeed > speed) {
                    Thread.sleep(Math.round(downloadSpeed * 1.0 / speed));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 2) {
            throw new IllegalArgumentException(String.format("2 parameters are required: url and "
                            + "the number of bytes per millisecond separated by a space. You entered %d parameter(s)",
                    args.length));
        }
        String url = args[0];
        int speed = Integer.parseInt(args[1]);
        isValidURL(url);
        isValidSpeed(speed);
        Thread wget = new Thread(new AdditionalWget(url, speed));
        wget.start();
        wget.join();
    }

    public static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void isValidSpeed(int speed) {
        if (speed < 0) {
            throw new ArithmeticException("It's not possible to have speed less then zero");
        }
    }
}
