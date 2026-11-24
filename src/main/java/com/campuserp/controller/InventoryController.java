package com.campuserp.controller;

import com.campuserp.model.InventoryItem;
import com.campuserp.repository.InventoryRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Inventory API — admin writes, everyone reads. GET /api/inventory?category=LAB */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryRepository repo;

    public InventoryController(InventoryRepository repo) { this.repo = repo; }

    @GetMapping
    public List<InventoryItem> all(@RequestParam(required = false) String category) {
        if (category != null) return repo.findByCategory(category);
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> one(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InventoryItem> create(@Valid @RequestBody InventoryItem item) {
        return ResponseEntity.ok(repo.save(item));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> update(@PathVariable Long id, @Valid @RequestBody InventoryItem in) {
        return repo.findById(id).map(i -> {
            i.setName(in.getName()); i.setCategory(in.getCategory()); i.setQuantity(in.getQuantity());
            i.setLocation(in.getLocation()); i.setCondition(in.getCondition());
            return ResponseEntity.ok(repo.save(i));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
