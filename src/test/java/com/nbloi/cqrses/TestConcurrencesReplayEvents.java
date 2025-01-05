package com.nbloi.cqrses;

import com.nbloi.cqrses.query.repository.CustomerOrderRepository;
import com.nbloi.cqrses.query.service.EventReplayService;
import com.nbloi.cqrses.query.service.KafkaManage;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.EventProcessor;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RunWith(SpringRunner.class)
@EmbeddedKafka(partitions = 1, topics = {"events"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "axon.eventhandling.processors.replayProcessor.mode=tracking",
        "axon.kafka.consumer.group-id=order_group, payment_group, product_group, customer_group, test_group",
        "spring.kafka.bootstrap-servers=172.24.236.211:9092"})
class TestConcurrencesReplayEvents {

    @Autowired
    private TestRestTemplate restTemplate;

    // Implement the thread-safe executor
    private final ExecutorService executorService = Executors.newFixedThreadPool(30);
    @Autowired
    private DataSource dataSource;

//    @Autowired
//    private EventProcessor eventProcessor;
    @Autowired
    private EventReplayService eventReplayService;

    @Test
    public void contextLoads() {}

//    @DisplayName("Should create order created event, tehn shut down the kafka server. After a time, restart the kafka again." +
//            "Apply Replay Event technique to continue to handle")
    @Test
    public void testOrderCreatedEventWithKafkaFailureReplayEvents() throws IOException, InterruptedException {
        // Step 1: Start Kafka to ensure it's running
        KafkaManage.startKafka();
        System.out.println("Kafka started!");

        // Step 2: Trigger an OrderCreatedEvent
        // Simulate your application sending an OrderCreatedEvent to Kafka.
//        simulateOrderCreatedEvent();

        // Step 3: Stop Kafka to simulate a broker failure
        KafkaManage.stopKafka();
        System.out.println("Kafka stopped!");

        // Step 4: Assert retry mechanisms or behavior during the outage
        // Check logs, application retry mechanisms, or database entries.
        Thread.sleep(10000); // Give some time for retries to occur



        // Step 5: Start Kafka again
        KafkaManage.startKafka();
        System.out.println("Kafka restarted!");

//        eventReplayService.replayEvents("orderProcessor");
        String processorName = "orderProcessor";
        ResponseEntity<String> responseReplayResult = restTemplate.postForEntity("/api/v1/replay", processorName ,String.class);

        // Step 6: Assert that the event was successfully published after Kafka restart
//        verifyEventPublishedSuccessfully();
    }

    private void simulateOrderCreatedEvent() {
        // Your logic to trigger an OrderCreatedEvent
        System.out.println("OrderCreatedEvent triggered.");
    }

    private void verifyEventPublishedSuccessfully() {
        // Your logic to verify event was published after Kafka restart
        System.out.println("Event successfully published after Kafka restart.");
    }


}
