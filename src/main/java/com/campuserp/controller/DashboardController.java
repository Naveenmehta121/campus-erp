package com.campuserp.controller;

import com.campuserp.model.Fee;
import com.campuserp.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Dashboard counts + who-is-logged-in.
 *   GET /api/dashboard  -> {students, feesDue, attendanceRecords, timetableEntries, inventoryItems}
 *   GET /api/me         -> {username, roles}
 */
@RestController
public class DashboardController {

    private final StudentRepository students;
    private final FeeRepository fees;
    private final AttendanceRepository attendance;
    private final TimetableRepository timetable;
    private final InventoryRepository inventory;

    public DashboardController(StudentRepository students, FeeRepository fees,
                               AttendanceRepository attendance, TimetableRepository timetable,
                               InventoryRepository inventory) {
        this.students = students; this.fees = fees; this.attendance = attendance;
        this.timetable = timetable; this.inventory = inventory;
    }

    @GetMapping("/api/dashboard")
    public Map<String, Object> dashboard() {
        BigDecimal due = fees.findAll().stream()
                .map(Fee::getDueAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of(
                "students", students.count(),
                "feesDue", due,
                "unpaidFees", fees.findByStatus(Fee.FeeStatus.UNPAID).size(),
                "attendanceRecords", attendance.count(),
                "timetableEntries", timetable.count(),
                "inventoryItems", inventory.count());
    }

    @GetMapping("/api/me")
    public Map<String, Object> me(Authentication auth) {
        return Map.of("username", auth.getName(),
                "roles", auth.getAuthorities().stream().map(Object::toString).toList());
    }
}
