
package com.example.productapi.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("statistics")
public class Statistics {
    @Id
    private Integer id;
    private String date;
    private int totalProducts;
    private int totalUpdates;
    private int totalDeletions;
    private String category;

    // Getters and Setters
}
