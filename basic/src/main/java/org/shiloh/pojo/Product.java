package org.shiloh.pojo;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

/**
 * Product
 *
 * @author shiloh
 * @date 2024/12/18 15:34
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Product implements Serializable {
    private static final long serialVersionUID = -2467067651106263376L;

    /**
     * ID
     */
    private String id;

    /**
     * Name
     */
    private String name;

    /**
     * Price
     */
    private Double price;

    public Product(String name, Double price) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
    }
}
