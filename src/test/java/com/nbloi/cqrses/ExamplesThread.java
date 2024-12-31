package com.nbloi.cqrses;

import com.nbloi.cqrses.query.service.OrderEventHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;

public class ExamplesThread {
    public static void main(String[] args) {
        RunnableThread runnable = new RunnableThread();
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);

//        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//        public String readData(String key) {
//            lock.readLock().lock();
//            try {
//                return cache.get(key);
//            } finally {
//                lock.readLock().unlock();
//            }
//        }
//
//
//        public void writeData(String key, String value) {
//            lock.writeLock().lock();
//            try {
//                cache.put(key, value);
//            } finally {
//                lock.writeLock().unlock();
//            }
//        }


        thread1.start();
        thread2.start();

        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 10; i++) {
            int taskNo = i;
            executor.submit(() ->
                    System.out.println(Thread.currentThread().getName() + " is executing task " + taskNo));
//            test();
        }
        executor.shutdown();
    }

}