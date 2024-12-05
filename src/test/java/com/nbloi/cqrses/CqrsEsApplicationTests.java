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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.List;

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
		String productId = "Office Desk";
		double amount = 50;
		String currency = "VND";
		fixture.givenNoPriorActivity()
				.when(new CreateOrderCommand(orderId, productId, 2, amount, currency))
				.expectEvents(new OrderCreatedEvent(orderId, productId, 2, amount, currency));

	}

	@Test
	@AfterEach
	// test case: should return the exception when shipping the unconfirmed order
	public void setUpTestCase2() {
		fixture = new AggregateTestFixture<>(OrderAggregate.class);
		String orderId = UUID.randomUUID().toString();
		String productId = "Sneaker shoes";
		double amount = 860;
		String currency = "VND";
		fixture.given(new OrderCreatedEvent(orderId, productId, 5, amount, currency))
				.when(new ShipOrderCommand(orderId))
				.expectException(UnconfirmedOrderException.class);
	}

	@Test
	public void setUpTestCase3() {
		fixture = new AggregateTestFixture<>(OrderAggregate.class);
		String orderId = UUID.randomUUID().toString();
		String productId = "Laptop Dell";
		double amount = 2550;
		String currency = "VND";
		fixture.given(new OrderCreatedEvent(orderId, productId, 5, amount, currency), new OrderConfirmedEvent(orderId))
				.when(new ShipOrderCommand(orderId))
				.expectEvents(new OrderShippedEvent(orderId));
	}
}
