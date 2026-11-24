package com.campuserp.repository;
import com.campuserp.model.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TimetableRepository extends JpaRepository<TimetableEntry, Long> {
    List<TimetableEntry> findByDayOfWeekOrderByStartTimeAsc(String dayOfWeek);
    List<TimetableEntry> findByCourseAndSemester(String course, int semester);
}
