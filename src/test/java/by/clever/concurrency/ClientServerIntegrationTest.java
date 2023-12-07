package by.clever.concurrency;

import org.junit.jupiter.api.RepeatedTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientServerIntegrationTest {

    @RepeatedTest(20)
    public void should_CorrectlyAccumulateResponses() throws InterruptedException, ExecutionException {
        //given
        int clientCapacity = 100;
        Client client = new Client(clientCapacity);
        Server server = new Server();
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        Callable<Response> responseCallable = () -> {
            Random random = new Random();
            int clientIntegersSize = client.getClientIntegersSize();
            int randomInt = random.nextInt(clientIntegersSize);
            Request request = client.sendRequest(randomInt);
            return server.sendResponse(request);
        };

        List<Callable<Response>> callableList = new ArrayList<>();
        for (int i = 0; i < clientCapacity; i++) {
            callableList.add(responseCallable);
        }
        int expected = (1 + clientCapacity) * (clientCapacity / 2);

        //when
        List<Future<Response>> futureList = executorService.invokeAll(callableList);

        for (Future<Response> future : futureList) {
            client.sumResponseData(future.get());
        }
        executorService.shutdown();
        int actual = client.getAccumulator();

        //then
        assertEquals(expected, actual);
    }
}