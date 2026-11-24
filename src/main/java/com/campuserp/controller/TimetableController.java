package com.campuserp.controller;

import com.campuserp.model.TimetableEntry;
import com.campuserp.repository.TimetableRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Timetable API. Faculty + admin can add slots; everyone can read.
 *   GET /api/timetable?dayOfWeek=MONDAY
 *   GET /api/timetable?course=BTech CSE&semester=6
 *   POST /api/timetable  body: {"dayOfWeek":"MONDAY","startTime":"09:00","endTime":"10:00",
 *                               "subject":"DBMS","facultyName":"Dr. Sharma","room":"301",
 *                               "course":"BTech CSE","semester":6}
 */
@RestController
@RequestMapping("/api/timetable")
public class TimetableController {

    private final TimetableRepository repo;

    public TimetableController(TimetableRepository repo) { this.repo = repo; }

    @GetMapping
    public List<TimetableEntry> all(@RequestParam(required = false) String dayOfWeek,
                                    @RequestParam(required = false) String course,
                                    @RequestParam(required = false) Integer semester) {
        if (dayOfWeek != null) return repo.findByDayOfWeekOrderByStartTimeAsc(dayOfWeek.toUpperCase());
        if (course != null && semester != null) return repo.findByCourseAndSemester(course, semester);
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimetableEntry> one(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TimetableEntry> create(@Valid @RequestBody TimetableEntry e) {
        e.setDayOfWeek(e.getDayOfWeek().toUpperCase());
        return ResponseEntity.ok(repo.save(e));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
