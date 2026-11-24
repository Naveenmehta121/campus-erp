package com.campuserp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/** One stock item — lab equipment, library books, furniture, etc. */
@Entity
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String category; // LAB, LIBRARY, FURNITURE, IT, SPORTS ...
    @Min(0)
    private int quantity;
    private String location; // e.g. "Lab 3"
    private String condition; // GOOD, DAMAGED, UNDER_REPAIR

    public InventoryItem() {}

    public InventoryItem(String name, String category, int quantity, String location, String condition) {
        this.name = name; this.category = category; this.quantity = quantity;
        this.location = location; this.condition = condition;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}
