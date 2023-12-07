package by.clever.concurrency;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ClientTest {

    @Nested
    class SendRequestMethodTest {

        @Test
        public void should_ReturnRequest_when_IndexCorrect() {
            //given
            Client client = new Client(20);
            int index = 10;
            Integer expected = client.getClientIntegers().get(10);

            //when
            Integer actual = client.sendRequest(10).getData();

            //then
            assertEquals(expected, actual);
        }

        @RepeatedTest(20)
        public void should_CorrectlySendRequest_when_InvokeConcurrently() throws InterruptedException {
            //given
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            Client client = new Client(20);
            Callable<Request> callable = () -> {
                Random random = new Random();
                int randomInt = random.nextInt(client.getClientIntegersSize());
                Request request = client.sendRequest(randomInt);
                TimeUnit.MILLISECONDS.sleep(400);
                return request;
            };
            List<Callable<Request>> callableList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                callableList.add(callable);
            }

            int expected = 0;

            //when
            executorService.invokeAll(callableList);
            int actual = client.getClientIntegersSize();
            executorService.shutdown();

            //then
            assertEquals(expected, actual);
        }
    }

    @Nested
    public class SumResponseDataMethodTest {

        @RepeatedTest(20)
        public void should_CorrectlySumResponses_when_InvokeConcurrently() throws InterruptedException {
            //given
            int size = 20;
            List<Response> responseList = fillResponseList();
            Client client = new Client(size);
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            int expected = (1 + size) * (size / 2);

            //when
            for (Response response : responseList) {

                Runnable runnable = () -> {
                    client.sumResponseData(response);
                };
                executorService.execute(runnable);
            }
            TimeUnit.SECONDS.sleep(1);
            int actual = client.getAccumulator();
            executorService.shutdown();

            //then
            assertEquals(expected, actual);
        }
    }


    private List<Response> fillResponseList() {
        List<Response> responseList = new ArrayList<>();
        List<Integer> integerList = new ArrayList<>();

        IntStream.range(0, 21).forEach(integerList::add);

        for (Integer i : integerList) {
            responseList.add(new Response(i));
        }
        return responseList;
    }
}