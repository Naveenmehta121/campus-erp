package com.campuserp.controller;

import com.campuserp.model.Fee;
import com.campuserp.repository.FeeRepository;
import com.campuserp.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Fees API.
 *   GET  /api/fees?studentId=1&status=UNPAID
 *   POST /api/fees   body: {"student":{"id":1},"amount":85000,"paidAmount":50000,
 *                          "status":"PARTIAL","dueDate":"2026-12-15","description":"Sem 6 tuition"}
 *   POST /api/fees/{id}/pay?amount=10000   record a payment (updates paidAmount + status)
 */
@RestController
@RequestMapping("/api/fees")
public class FeeController {

    private final FeeRepository fees;
    private final StudentRepository students;

    public FeeController(FeeRepository fees, StudentRepository students) {
        this.fees = fees; this.students = students;
    }

    @GetMapping
    public List<Fee> all(@RequestParam(required = false) Long studentId,
                         @RequestParam(required = false) Fee.FeeStatus status) {
        if (studentId != null) return fees.findByStudentId(studentId);
        if (status != null) return fees.findByStatus(status);
        return fees.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fee> one(@PathVariable Long id) {
        return fees.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Fee fee) {
        if (fee.getStudent() == null || fee.getStudent().getId() == null)
            return ResponseEntity.badRequest().body("student.id must be an existing student");
        // Load the full student so the response shows their name, not just the id.
        var student = students.findById(fee.getStudent().getId());
        if (student.isEmpty())
            return ResponseEntity.badRequest().body("student.id must be an existing student");
        fee.setStudent(student.get());
        return ResponseEntity.ok(fees.save(fee));
    }

    /** Record a payment against a fee: paidAmount grows, status flips to PAID when fully paid. */
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> pay(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        BigDecimal amount = body.getOrDefault("amount", BigDecimal.ZERO);
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            return ResponseEntity.badRequest().body("amount must be > 0");
        return fees.findById(id).map(f -> {
            f.setPaidAmount(f.getPaidAmount().add(amount));
            int cmp = f.getPaidAmount().compareTo(f.getAmount());
            f.setStatus(cmp >= 0 ? Fee.FeeStatus.PAID
                       : f.getPaidAmount().compareTo(BigDecimal.ZERO) > 0 ? Fee.FeeStatus.PARTIAL
                       : Fee.FeeStatus.UNPAID);
            return ResponseEntity.ok(fees.save(f));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!fees.existsById(id)) return ResponseEntity.notFound().build();
        fees.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
