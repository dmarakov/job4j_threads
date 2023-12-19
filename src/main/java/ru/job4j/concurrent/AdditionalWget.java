package ru.job4j.concurrent;

import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.IOException;

public class AdditionalWget implements Runnable {
    private final String url;
    private final int speed;
    private final String file;

    public AdditionalWget(String url, int speed, String file) {
        this.url = url;
        this.speed = speed;
        this.file = file;
    }

    @Override
    public void run() {
        var startAt = System.currentTimeMillis();
        try (var input = new URL(url).openStream();
             var output = new FileOutputStream(file)) {
            System.out.println("Open connection: " + (System.currentTimeMillis() - startAt) + " ms");
            var dataBuffer = new byte[512];
            int bytesRead;
            long bytesCount = 0;
            long downloadAt = System.currentTimeMillis();
            while ((bytesRead = input.read(dataBuffer, 0, dataBuffer.length)) != -1) {
                output.write(dataBuffer, 0, bytesRead);
                bytesCount += bytesRead;
                if (bytesCount >= speed) {
                    long timeDiff = System.currentTimeMillis() - downloadAt;
                    if (1000 > timeDiff) {
                        Thread.sleep(1000 - timeDiff);
                    }
                }
                bytesCount = 0;
                downloadAt = System.currentTimeMillis();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 3) {
            throw new IllegalArgumentException(String.format("3 parameters are required: url, "
                            + "the number of bytes per second and filename separated by a space. You entered %d parameter(s)",
                    args.length));
        }
        String url = args[0];
        int speed = Integer.parseInt(args[1]);
        String file = args[2];
        isValidURL(url);
        isValidSpeed(speed);
        Thread wget = new Thread(new AdditionalWget(url, speed, file));
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
