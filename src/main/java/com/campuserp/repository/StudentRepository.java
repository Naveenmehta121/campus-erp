package com.campuserp.repository;
import com.campuserp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
/** Spring Data JPA: method names become queries automatically. No SQL written by hand. */
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRollNo(String rollNo);
    List<Student> findByCourseAndSemester(String course, int semester);
}
