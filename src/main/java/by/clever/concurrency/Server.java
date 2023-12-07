package by.clever.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {

    private final List<Integer> serverIntegers = new ArrayList<>();
    private final Lock lock = new ReentrantLock();

    public Server() {

    }

    public List<Integer> getServerIntegers() {
        try {
            lock.lock();
            return serverIntegers;
        } finally {
            lock.unlock();
        }
    }

    public Response sendResponse(Request request) {

        Integer requestData;
        Response response;
        Random random = new Random();

        try {
            lock.lock();
            TimeUnit.MILLISECONDS.sleep(random.ints(100, 1001)
                    .findFirst()
                    .orElseThrow());

            requestData = request.getData();
            serverIntegers.add(requestData);
            response = new Response(serverIntegers.size());
            return response;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}