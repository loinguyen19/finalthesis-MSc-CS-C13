package com.nbloi.cqrses.query.service;

import com.nbloi.cqrses.commonapi.event.CustomerProductRatedEvent;
import com.nbloi.cqrses.query.entity.CustomerProductRateView;
import com.nbloi.cqrses.query.repository.CustomerProductRateRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class CustomerProductRateProjectionHandler {

    @Autowired
    private CustomerProductRateRepository customerProductRateRepository;

    @EventHandler
    public double findAvgRatingByCustomerIdAndProductId(CustomerProductRatedEvent event) {
        return customerProductRateRepository.findAverageRatingByCustomerIdAndProductId(event.getCustomerId(), event.getProductId());
    }

    @EventHandler
    public void onAddedNewRating(CustomerProductRatedEvent event) {
        CustomerProductRateView rateView = customerProductRateRepository.findByCustomerIdAndProductId(event.getCustomerId(),
                event.getProductId());
        rateView.setRating(event.getRating());
        rateView.setRatedAt(event.getRatedAt());

        CustomerProductRateView newRateView = new CustomerProductRateView(event.getCustomerId(),event.getProductId(),
                event.getRating(), event.getRatedAt());
        customerProductRateRepository.save(newRateView);
    }

    @Query
    public List<CustomerProductRateView> findAllRatings() {
        return customerProductRateRepository.findAll();
    }

    @Query
    public double findAvgRatingsByCustomerIdAndProductId(String customerId, String productId) {
        return customerProductRateRepository.findAverageRatingByCustomerIdAndProductId(customerId, productId);
    }

    @Query
    public double findAvgRatingsByCustomerId(String customerId) {
        return customerProductRateRepository.findAverageRatingByCustomerId(customerId);
    }

    @Query
    public double findAvgRatingsByProductId(String productId) {
        return customerProductRateRepository.findAverageRatingByProductId(productId);
    }
}
