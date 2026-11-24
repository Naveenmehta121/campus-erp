package com.campuserp.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One fee record for one student.
 * status: UNPAID / PARTIAL / PAID. paidAmount tracks partial payments,
 * so "amount - paidAmount" is always what is still due.
 */
@Entity
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Student student;

    private BigDecimal amount = BigDecimal.ZERO;
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private FeeStatus status = FeeStatus.UNPAID;

    private LocalDate dueDate;
    private String description; // e.g. "Semester 6 tuition"

    public enum FeeStatus { UNPAID, PARTIAL, PAID }

    public Fee() {}

    public Fee(Student student, BigDecimal amount, BigDecimal paidAmount,
               FeeStatus status, LocalDate dueDate, String description) {
        this.student = student; this.amount = amount; this.paidAmount = paidAmount;
        this.status = status; this.dueDate = dueDate; this.description = description;
    }

    /** Money still owed on this record. */
    public BigDecimal getDueAmount() {
        return amount.subtract(paidAmount);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }
    public FeeStatus getStatus() { return status; }
    public void setStatus(FeeStatus status) { this.status = status; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
