package com.campuserp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/** One student's attendance for one subject on one day. */
@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Student student;

    private LocalDate date;
    private String subject;

    @Enumerated(EnumType.STRING)
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    public enum AttendanceStatus { PRESENT, ABSENT, LATE }

    public Attendance() {}

    public Attendance(Student student, LocalDate date, String subject, AttendanceStatus status) {
        this.student = student; this.date = date; this.subject = subject; this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
}
