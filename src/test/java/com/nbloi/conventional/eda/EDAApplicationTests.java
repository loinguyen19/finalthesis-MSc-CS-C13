package com.nbloi.conventional.eda;

import com.nbloi.conventional.eda.entity.Order;
import com.nbloi.conventional.eda.entity.OrderItem;
import com.nbloi.conventional.eda.entity.Product;
import com.nbloi.conventional.eda.service.OrderEventHandler;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@SpringBootTest
class EDAApplicationTests {

	@Autowired
	private OrderEventHandler orderEventHandler;

	@Test
	void contextLoads() {
	}

//	private FixtureConfiguration<OrderAggregate> fixture;

	private final LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime updatedAt = LocalDateTime.now();

	@Test
	@Before("Should produce and return the expected OrderCreatedEvent")
	// test case: should return the OrderCreatedEvent when producing the command successfully
    public void setUpTestCase1() {
//		fixture = new AggregateTestFixture<>(OrderAggregate.class);
		// assign the necessary properties
		String orderItemId = UUID.randomUUID().toString();
		String orderItemId2 = UUID.randomUUID().toString();

		String productId = "UUID-10";
		String name = "Towel";
		BigDecimal price = BigDecimal.valueOf(30);
		int quantity1 = 10;
		BigDecimal totalPrice1 = price.multiply(BigDecimal.valueOf(quantity1));
		int stock = 1000;
		String currency = "VND";

		String productId2 = "UUID-8";
		String name2 = "Desk";
		BigDecimal price2 = BigDecimal.valueOf(300);
		int quantity2 = 5;
		BigDecimal totalPrice2 = price2.multiply(BigDecimal.valueOf(quantity2));
		int stock2 = 780;

		Product product = new Product(productId, name, price, stock, currency);
		Product product2 = new Product(productId2, name2, price2, stock2, currency);

		OrderItem orderItem = new OrderItem(orderItemId, quantity1, price, totalPrice1, currency,  null, product);
		OrderItem orderItem2 = new OrderItem(orderItemId2, quantity2, price2, totalPrice2, currency,  null, product2);

		BigDecimal totalAmount = orderItem.getTotalPrice().add(orderItem2.getTotalPrice());

		List<OrderItem> orderItems = new ArrayList<>();
		orderItems.add(orderItem);
		orderItems.add(orderItem2);

		String orderId = UUID.randomUUID().toString();
		String customerId = "UUID-C-5";
		String paymentId = UUID.randomUUID().toString();
//		String currency = "VND";

		// then: should return the OrderCreatedEvent when producing the command successfully
//		ResultValidator<OrderAggregate> resultValidator = fixture.givenNoPriorActivity()
//				.when(new CreateOrderCommand(orderId, orderItems, totalAmount, currency, customerId, paymentId))
//				.expectEvents(new OrderCreatedEvent(orderId, orderItems, OrderStatus.CREATED.toString(), totalAmount, currency, customerId, paymentId));
//
//		OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(orderId, orderItems, OrderStatus.CREATED.toString(), totalAmount,
//				currency, customerId, paymentId);
//		orderEventHandler.on(orderCreatedEvent);
//		Order orderFindById = orderEventHandler.handle(new FindOrderByIdQuery(orderId));
//		System.out.println(orderFindById);
	}

	@Test
	@AfterEach
	// test case: should return the exception when shipping the unconfirmed order
	public void setUpTestCase2() {
//		fixture = new AggregateTestFixture<>(OrderAggregate.class);

		String orderItemId = UUID.randomUUID().toString();
		String productId = UUID.randomUUID().toString();
		String name = "Sneaker shoes";
		BigDecimal price = BigDecimal.valueOf(106.99);
		int quantity = 10;
		BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));
		int stock = 650;
		String currency = "VND";

		Product product = new Product(productId, name, price, stock, currency);

		OrderItem orderItem = new OrderItem(orderItemId, quantity, price, totalPrice, currency,  null, product);
		List<OrderItem> orderItems = new ArrayList<>();
		orderItems.add(orderItem);

		BigDecimal totalAmount = orderItem.getTotalPrice();

		String orderId = UUID.randomUUID().toString();
		String customerId = UUID.randomUUID().toString();
		String paymentId = UUID.randomUUID().toString();

//		fixture.given(new OrderCreatedEvent(orderId, orderItems, OrderStatus.CREATED.toString(), totalAmount,currency, customerId, paymentId))
//				.when(new ShipOrderCommand(orderId))
//				.expectException(UnconfirmedOrderException.class);
	}

	@Test
	public void setUpTestCase3() {
//		fixture = new AggregateTestFixture<>(OrderAggregate.class);
		String orderItemId = UUID.randomUUID().toString();
		String productId = UUID.randomUUID().toString();
		String name = "Laptop Dell";
		BigDecimal price = BigDecimal.valueOf(1250.6);
		int quantity = 56;
		BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(quantity));
		int stock = 2253;
		String currency = "VND";

		Product product = new Product(productId, name, price, stock, currency);

		OrderItem orderItem = new OrderItem(orderItemId, quantity, price, totalPrice, currency, null, product);
		List<OrderItem> orderItems = new ArrayList<>();
		orderItems.add(orderItem);

		BigDecimal totalAmount = orderItem.getTotalPrice();

		String orderId = UUID.randomUUID().toString();
		String customerId = UUID.randomUUID().toString();
		String paymentId = UUID.randomUUID().toString();
//		fixture.given(new OrderCreatedEvent(orderId, orderItems, OrderStatus.CREATED.toString(), totalAmount, currency, customerId, paymentId),
//						new OrderConfirmedEvent(orderId))
//				.when(new ShipOrderCommand(orderId))
//				.expectEvents(new OrderShippedEvent(orderId));
	}
}