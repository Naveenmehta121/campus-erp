package com.campuserp.controller;

import com.campuserp.model.Student;
import com.campuserp.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Students API. Admin-only writes (enforced in SecurityConfig);
 * every logged-in role can read.
 *   GET    /api/students            list all
 *   GET    /api/students/{id}       one student (404 if missing)
 *   POST   /api/students            create  (rollNo must be unique)
 *   PUT    /api/students/{id}       update
 *   DELETE /api/students/{id}       delete
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository repo;

    public StudentController(StudentRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Student> all(@RequestParam(required = false) String course,
                             @RequestParam(required = false) Integer semester) {
        if (course != null && semester != null) return repo.findByCourseAndSemester(course, semester);
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> one(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Student s) {
        if (repo.findByRollNo(s.getRollNo()).isPresent())
            return ResponseEntity.badRequest().body("rollNo already exists: " + s.getRollNo());
        return ResponseEntity.ok(repo.save(s));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> update(@PathVariable Long id, @Valid @RequestBody Student in) {
        return repo.findById(id).map(s -> {
            s.setName(in.getName()); s.setRollNo(in.getRollNo()); s.setEmail(in.getEmail());
            s.setCourse(in.getCourse()); s.setSemester(in.getSemester()); s.setPhone(in.getPhone());
            return ResponseEntity.ok(repo.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
