package ru.study.delivery.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private Long id;

    private String productName;
    private String productType;
    private String material;
    private String manufacturer;
    private String description;
    private Long price;
}
