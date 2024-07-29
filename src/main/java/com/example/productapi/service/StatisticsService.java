
package com.example.productapi.service;

import com.example.productapi.model.StatisticsResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StatisticsService {
    Mono<Void> createStatistics(String date, int totalProducts, int totalUpdates, int totalDeletions, String category);
    Flux<StatisticsResponse> getAllStatistics();
}
