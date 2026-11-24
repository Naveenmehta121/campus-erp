package com.campuserp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalTime;

/** One class slot in the weekly timetable, e.g. MONDAY 09:00–10:00 DBMS, Room 301. */
@Entity
public class TimetableEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String dayOfWeek; // MONDAY..FRIDAY (plain String keeps the demo simple)

    private LocalTime startTime;
    private LocalTime endTime;

    @NotBlank
    private String subject;
    private String facultyName;
    private String room;
    private String course; // e.g. "BTech CSE"
    private int semester;

    public TimetableEntry() {}

    public TimetableEntry(String dayOfWeek, LocalTime startTime, LocalTime endTime,
                          String subject, String facultyName, String room,
                          String course, int semester) {
        this.dayOfWeek = dayOfWeek; this.startTime = startTime; this.endTime = endTime;
        this.subject = subject; this.facultyName = facultyName; this.room = room;
        this.course = course; this.semester = semester;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
}
