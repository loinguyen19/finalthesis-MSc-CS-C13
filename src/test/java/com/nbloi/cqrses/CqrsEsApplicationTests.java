package com.nbloi.cqrses;

import com.nbloi.cqrses.command.aggregate.OrderAggregate;
import com.nbloi.cqrses.commonapi.command.CreateOrderCommand;
import com.nbloi.cqrses.commonapi.command.ShipOrderCommand;
import com.nbloi.cqrses.commonapi.event.OrderConfirmedEvent;
import com.nbloi.cqrses.commonapi.event.OrderCreatedEvent;
import com.nbloi.cqrses.commonapi.event.OrderShippedEvent;
import com.nbloi.cqrses.commonapi.exception.UnconfirmedOrderException;
import org.aspectj.lang.annotation.Before;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class CqrsEsApplicationTests {

	@Test
	void contextLoads() {
	}

	private FixtureConfiguration<OrderAggregate> fixture;

	@Test
	@Before("Should produce and return the expexted OrderCreatedEvent")
	// test case: should return the OrderCreatedEvent when producing the command successfully
    public void setUpTestCase1() {
		fixture = new AggregateTestFixture<>(OrderAggregate.class);
		String orderId = UUID.randomUUID().toString();
		String productId = "Deluxe Chair";
		fixture.givenNoPriorActivity()
				.when(new CreateOrderCommand(orderId, productId))
				.expectEvents(new OrderCreatedEvent(orderId, productId));

	}

	@Test
	@AfterEach
	// test case: should return the exception when shipping the unconfirmed order
	public void setUpTestCase2() {
		fixture = new AggregateTestFixture<>(OrderAggregate.class);
		String orderId = UUID.randomUUID().toString();
		String productId = "Deluxe Chair";
		fixture.given(new OrderCreatedEvent(orderId, productId))
				.when(new ShipOrderCommand(orderId))
				.expectException(UnconfirmedOrderException.class);
	}

	@Test
	public void setUpTestCase3() {
		fixture = new AggregateTestFixture<>(OrderAggregate.class);
		String orderId = UUID.randomUUID().toString();
		String productId = "Deluxe Chair";
		fixture.given(new OrderCreatedEvent(orderId, productId), new OrderConfirmedEvent(orderId))
				.when(new ShipOrderCommand(orderId))
				.expectEvents(new OrderShippedEvent(orderId));
	}
}
