package com.lhb.nowcoder;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueTests {
    public static void main(String[] args) {
        ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        new Thread(new producer(queue)).start();
        new Thread(new customer(queue)).start();
        new Thread(new customer(queue)).start();
        new Thread(new customer(queue)).start();

    }
}

class producer implements Runnable {
    private BlockingQueue<Integer> blockingQueue;

    public producer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(20);
                blockingQueue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产:" + blockingQueue.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

class customer implements Runnable{
    private BlockingQueue<Integer> blockingQueue;

    public customer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(new Random().nextInt(1000));
                blockingQueue.take();
                System.out.println(Thread.currentThread().getName() + "消费:" + blockingQueue.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
