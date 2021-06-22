package ru.study.delivery.entities;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"orderId", "customerId", "products", "orderedTime", "delivered"})
public class Order {
    @JsonProperty("orderId")
    private Long id;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("customerId")
    private Long customerId;

    @JsonProperty(value = "products")
    private List<Product> productList = new ArrayList<>();

    private LocalDateTime orderedTime;

    private boolean delivered;
}
