/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.utils;

import java.util.concurrent.*;

public class ThreadPool {
    private static final ExecutorService pool = Executors.newFixedThreadPool(4);

    public static void runAsync(Runnable task) {
        pool.submit(task);
    }

    public static void shutdown() {
        pool.shutdown();
    }
}

