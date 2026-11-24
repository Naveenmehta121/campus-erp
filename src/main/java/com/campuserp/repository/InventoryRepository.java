package com.campuserp.repository;
import com.campuserp.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByCategory(String category);
}
