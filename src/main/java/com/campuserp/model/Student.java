package com.campuserp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** One student record. rollNo is the college roll number — unique per student. */
@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    @Column(unique = true)
    private String rollNo;

    @Email
    private String email;

    private String course;    // e.g. "BTech CSE"
    private int semester;     // e.g. 6
    private String phone;

    public Student() {} // JPA needs a no-arg constructor

    public Student(String name, String rollNo, String email, String course, int semester, String phone) {
        this.name = name; this.rollNo = rollNo; this.email = email;
        this.course = course; this.semester = semester; this.phone = phone;
    }

    // Getters/setters (Jackson + JPA use these; kept explicit for readability)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
