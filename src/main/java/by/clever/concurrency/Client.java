package by.clever.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;


public class Client {

    private final List<Integer> clientIntegers = new ArrayList<>();
    private int accumulator = 0;
    private final Lock lock = new ReentrantLock();

    public Client(int capacity) {
        fillClientIntegers(capacity);
    }

    public List<Integer> getClientIntegers() {
        try {
            lock.lock();
            return clientIntegers;
        } finally {
            lock.unlock();
        }
    }

    public int getClientIntegersSize() {
        try {
            lock.lock();
            return clientIntegers.size();
        } finally {
            lock.unlock();
        }
    }

    public int getAccumulator() {
        try {
            lock.lock();
            return accumulator;
        } finally {
            lock.unlock();
        }
    }

    public Request sendRequest(int index) {
        Random random = new Random();

        try {
            lock.lock();
            TimeUnit.MILLISECONDS.sleep(random.ints(100, 401)
                    .findFirst()
                    .orElseThrow());
            Integer value = clientIntegers.remove(index);
            Request request = new Request(value);
            return request;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void sumResponseData(Response response) {

        int responseData = response.getData();

        try {
            lock.lock();
            accumulator += responseData;
        } finally {
            lock.unlock();
        }
    }

    private void fillClientIntegers(int capacity) {

        IntStream.range(1, capacity + 1)
                .forEach(clientIntegers::add);
    }
}