package com.campuserp.seed;

import com.campuserp.model.*;
import com.campuserp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Seed data — runs once on first startup (skips if students already exist,
 * so restarts never duplicate rows).
 * 8 students, 8 fee records, 16 attendance rows, 15 timetable slots, 8 inventory items.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final StudentRepository students;
    private final FeeRepository fees;
    private final AttendanceRepository attendance;
    private final TimetableRepository timetable;
    private final InventoryRepository inventory;

    public DataSeeder(StudentRepository students, FeeRepository fees,
                      AttendanceRepository attendance, TimetableRepository timetable,
                      InventoryRepository inventory) {
        this.students = students; this.fees = fees; this.attendance = attendance;
        this.timetable = timetable; this.inventory = inventory;
    }

    @Override
    public void run(String... args) {
        if (students.count() > 0) return; // already seeded

        List<Student> s = students.saveAll(List.of(
                new Student("Naveen Kumar", "CSE-2023-001", "naveen@college.edu", "BTech CSE", 6, "9876543210"),
                new Student("Aarav Sharma", "CSE-2023-002", "aarav@college.edu", "BTech CSE", 6, "9876543211"),
                new Student("Priya Patel", "CSE-2023-003", "priya@college.edu", "BTech CSE", 6, "9876543212"),
                new Student("Rohan Gupta", "CSE-2023-004", "rohan@college.edu", "BTech CSE", 6, "9876543213"),
                new Student("Ananya Singh", "ECE-2023-011", "ananya@college.edu", "BTech ECE", 6, "9876543214"),
                new Student("Vikram Mehta", "ECE-2023-012", "vikram@college.edu", "BTech ECE", 6, "9876543215"),
                new Student("Sneha Reddy", "CSE-2024-021", "sneha@college.edu", "BTech CSE", 4, "9876543216"),
                new Student("Arjun Nair", "CSE-2024-022", "arjun@college.edu", "BTech CSE", 4, "9876543217")
        ));

        fees.saveAll(List.of(
                new Fee(s.get(0), new BigDecimal("85000"), new BigDecimal("85000"), Fee.FeeStatus.PAID, LocalDate.of(2026, 1, 15), "Semester 6 tuition"),
                new Fee(s.get(1), new BigDecimal("85000"), new BigDecimal("50000"), Fee.FeeStatus.PARTIAL, LocalDate.of(2026, 1, 15), "Semester 6 tuition"),
                new Fee(s.get(2), new BigDecimal("85000"), BigDecimal.ZERO, Fee.FeeStatus.UNPAID, LocalDate.of(2026, 1, 15), "Semester 6 tuition"),
                new Fee(s.get(3), new BigDecimal("85000"), new BigDecimal("85000"), Fee.FeeStatus.PAID, LocalDate.of(2026, 1, 15), "Semester 6 tuition"),
                new Fee(s.get(4), new BigDecimal("78000"), new BigDecimal("40000"), Fee.FeeStatus.PARTIAL, LocalDate.of(2026, 1, 15), "Semester 6 tuition"),
                new Fee(s.get(5), new BigDecimal("78000"), BigDecimal.ZERO, Fee.FeeStatus.UNPAID, LocalDate.of(2026, 1, 15), "Semester 6 tuition"),
                new Fee(s.get(6), new BigDecimal("85000"), new BigDecimal("85000"), Fee.FeeStatus.PAID, LocalDate.of(2026, 1, 15), "Semester 4 tuition"),
                new Fee(s.get(7), new BigDecimal("85000"), new BigDecimal("30000"), Fee.FeeStatus.PARTIAL, LocalDate.of(2026, 1, 15), "Semester 4 tuition")
        ));

        // Two days of attendance for the first 4 CSE students (DBMS + Compiler Design)
        for (int i = 0; i < 4; i++) {
            attendance.save(new Attendance(s.get(i), LocalDate.of(2026, 11, 23), "DBMS",
                    i == 2 ? Attendance.AttendanceStatus.ABSENT : Attendance.AttendanceStatus.PRESENT));
            attendance.save(new Attendance(s.get(i), LocalDate.of(2026, 11, 23), "Compiler Design",
                    Attendance.AttendanceStatus.PRESENT));
            attendance.save(new Attendance(s.get(i), LocalDate.of(2026, 11, 24), "DBMS",
                    i == 3 ? Attendance.AttendanceStatus.LATE : Attendance.AttendanceStatus.PRESENT));
            attendance.save(new Attendance(s.get(i), LocalDate.of(2026, 11, 24), "Web Technology",
                    Attendance.AttendanceStatus.PRESENT));
        }

        // Weekly timetable, BTech CSE Sem 6 (Mon–Fri, 3 slots a day)
        String[][] slots = {
                {"MONDAY", "DBMS", "Dr. Sharma"}, {"MONDAY", "Compiler Design", "Prof. Iyer"}, {"MONDAY", "Web Technology", "Dr. Rao"},
                {"TUESDAY", "Software Engineering", "Prof. Nair"}, {"TUESDAY", "DBMS Lab", "Dr. Sharma"}, {"TUESDAY", "Mobile App Dev", "Dr. Rao"},
                {"WEDNESDAY", "Compiler Design", "Prof. Iyer"}, {"WEDNESDAY", "DBMS", "Dr. Sharma"}, {"WEDNESDAY", "Web Technology", "Dr. Rao"},
                {"THURSDAY", "Software Engineering", "Prof. Nair"}, {"THURSDAY", "Mobile App Dev", "Dr. Rao"}, {"THURSDAY", "DBMS Lab", "Dr. Sharma"},
                {"FRIDAY", "Compiler Design Lab", "Prof. Iyer"}, {"FRIDAY", "Web Technology", "Dr. Rao"}, {"FRIDAY", "Software Engineering", "Prof. Nair"}
        };
        LocalTime[] starts = {LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 15)};
        for (int i = 0; i < slots.length; i++) {
            timetable.save(new TimetableEntry(slots[i][0], starts[i % 3], starts[i % 3].plusMinutes(50),
                    slots[i][1], slots[i][2], "Room 30" + (i % 3 + 1), "BTech CSE", 6));
        }

        inventory.saveAll(List.of(
                new InventoryItem("Dell OptiPlex Desktop", "IT", 30, "Computer Lab 1", "GOOD"),
                new InventoryItem("Projector (Epson)", "IT", 8, "Lecture Hall A", "GOOD"),
                new InventoryItem("Oscilloscope", "LAB", 12, "Electronics Lab", "GOOD"),
                new InventoryItem("DBMS Textbook (Silberschatz)", "LIBRARY", 45, "Central Library", "GOOD"),
                new InventoryItem("Chemistry Glassware Set", "LAB", 20, "Chemistry Lab", "UNDER_REPAIR"),
                new InventoryItem("Classroom Desk", "FURNITURE", 120, "Block B", "GOOD"),
                new InventoryItem("Basketball", "SPORTS", 15, "Sports Room", "GOOD"),
                new InventoryItem("Arduino Uno Kit", "LAB", 25, "Robotics Lab", "GOOD")
        ));
    }
}
