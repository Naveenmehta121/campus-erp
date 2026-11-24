package com.campuserp.controller;

import com.campuserp.model.Attendance;
import com.campuserp.repository.AttendanceRepository;
import com.campuserp.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Attendance API. Faculty + admin can mark attendance; everyone can read.
 *   GET  /api/attendance?studentId=1&subject=DBMS
 *   POST /api/attendance  body: {"student":{"id":1},"date":"2026-11-24","subject":"DBMS","status":"PRESENT"}
 *   GET  /api/attendance/summary/{studentId}  -> {present, absent, late, total, percentage}
 */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceRepository repo;
    private final StudentRepository students;

    public AttendanceController(AttendanceRepository repo, StudentRepository students) {
        this.repo = repo; this.students = students;
    }

    @GetMapping
    public List<Attendance> all(@RequestParam(required = false) Long studentId,
                                @RequestParam(required = false) String subject) {
        if (studentId != null) return repo.findByStudentId(studentId);
        if (subject != null) return repo.findBySubject(subject);
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<?> mark(@Valid @RequestBody Attendance a) {
        if (a.getStudent() == null || a.getStudent().getId() == null)
            return ResponseEntity.badRequest().body("student.id must be an existing student");
        // Load the full student so the response shows their name, not just the id.
        var student = students.findById(a.getStudent().getId());
        if (student.isEmpty())
            return ResponseEntity.badRequest().body("student.id must be an existing student");
        a.setStudent(student.get());
        return ResponseEntity.ok(repo.save(a));
    }

    @GetMapping("/summary/{studentId}")
    public ResponseEntity<?> summary(@PathVariable Long studentId) {
        if (!students.existsById(studentId)) return ResponseEntity.notFound().build();
        List<Attendance> rows = repo.findByStudentId(studentId);
        long present = rows.stream().filter(r -> r.getStatus() == Attendance.AttendanceStatus.PRESENT).count();
        long late = rows.stream().filter(r -> r.getStatus() == Attendance.AttendanceStatus.LATE).count();
        long absent = rows.stream().filter(r -> r.getStatus() == Attendance.AttendanceStatus.ABSENT).count();
        double pct = rows.isEmpty() ? 0 : (present + late) * 100.0 / rows.size();
        return ResponseEntity.ok(Map.of(
                "studentId", studentId, "total", rows.size(),
                "present", present, "late", late, "absent", absent,
                "percentage", Math.round(pct * 10) / 10.0));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
