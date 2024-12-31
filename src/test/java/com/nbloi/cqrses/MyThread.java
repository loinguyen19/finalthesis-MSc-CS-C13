package com.nbloi.cqrses;

public class MyThread extends Thread {
    @Override
    public void run() {
        // contains all the code you want to execute
        // when the thread starts

        // prints out the name of the thread
        // which is running the process
        System.out.println(Thread.currentThread().getName());
    }
}
