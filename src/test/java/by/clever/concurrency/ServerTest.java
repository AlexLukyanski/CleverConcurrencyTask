package by.clever.concurrency;

import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServerTest {

    @RepeatedTest(20)
    public void should_FillServerIntegersToSameSizeAsAmountOfRequests_when_InvokeConcurrently() throws InterruptedException {
        //given
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Server server = new Server();
        List<Request> requestList = fillResponseList();
        List<Callable<Response>> callableList = new ArrayList<>();
        for (Request request : requestList) {
            Callable<Response> callable = () -> {
                Response response = server.sendResponse(request);
                return response;
            };
            callableList.add(callable);
        }
        int expected = requestList.size();

        //when
        executorService.invokeAll(callableList);
        TimeUnit.SECONDS.sleep(13);
        int actual = server.getServerIntegers().size();
        executorService.shutdown();

        //then
        assertEquals(expected, actual);
    }

    @RepeatedTest(20)
    public void should_ContainsSameDataInServerIntegersAsDataInRequests_when_InvokeConcurrently() throws InterruptedException {
        //given
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Server server = new Server();
        List<Request> requestList = fillResponseList();
        List<Callable<Response>> callableList = new ArrayList<>();
        for (Request request : requestList) {
            Callable<Response> callable = () -> {
                Response response = server.sendResponse(request);
                return response;
            };
            callableList.add(callable);
        }
        List<Integer> expected = requestList.stream()
                .map(Request::getData)
                .toList();

        //when
        executorService.invokeAll(callableList);
        TimeUnit.SECONDS.sleep(13);
        Collections.sort(server.getServerIntegers());
        List<Integer> actual = server.getServerIntegers();
        executorService.shutdown();
        //then
        assertEquals(expected, actual);
    }

    private List<Request> fillResponseList() {
        List<Request> requestList = new ArrayList<>();
        List<Integer> integerList = new ArrayList<>();

        IntStream.range(0, 21).forEach(integerList::add);

        for (Integer i : integerList) {
            requestList.add(new Request(i));
        }
        return requestList;
    }
}