
package com.example.productapi.mapper;

import com.example.productapi.entity.Statistics;
import com.example.productapi.model.StatisticsResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatisticsMapper {
    StatisticsResponse toResponse(Statistics statistics);
}
