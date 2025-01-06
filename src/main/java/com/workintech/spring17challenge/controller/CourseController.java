package com.workintech.spring17challenge.controller;

import com.workintech.spring17challenge.exceptions.ApiException;

import com.workintech.spring17challenge.entity.Course;
import com.workintech.spring17challenge.entity.Grade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/courses")

public class CourseController {
    private final List<Course> courses = new ArrayList<>();

    @PostMapping
    public ResponseEntity<Course> addCourse(@RequestBody Course course) {
        if (course.getName() == null || course.getCredit() == null || course.getGrade() == null) {
            throw new ApiException("Course fields cannot be null", HttpStatus.BAD_REQUEST);
        }
        // Eğer kurs mevcutsa ekleme işlemini atla
        boolean courseExists = courses.stream().anyMatch(c -> c.getName().equalsIgnoreCase(course.getName()));
        if (!courseExists) {
            course.setId(courses.size() + 1);
            courses.add(course);
        }

        // Zaten mevcut olan kurslar için yine de 201 Created döndür
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Course> getCourseByName(@PathVariable String name) {
        Optional<Course> course = courses.stream().filter(c -> c.getName().equalsIgnoreCase(name)).findFirst();
        if (course.isPresent()) {
            return ResponseEntity.ok(course.get());
        } else {
            throw new ApiException("Course not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable int id, @RequestBody Course updatedCourse) {
        Optional<Course> existingCourse = courses.stream().filter(c -> c.getId() == id).findFirst();

        if (existingCourse.isEmpty()) {
            throw new ApiException("Invalid course ID", HttpStatus.NOT_FOUND);  // 404 hatası
        }

        // Mevcut kursu güncelle
        Course courseToUpdate = existingCourse.get();
        courseToUpdate.setName(updatedCourse.getName());
        courseToUpdate.setCredit(updatedCourse.getCredit());
        courseToUpdate.setGrade(updatedCourse.getGrade());

        return ResponseEntity.ok(courseToUpdate);  // 200 OK yanıtı
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable int id) {
        if (id < 0 || id >= courses.size()) {
            throw new ApiException("Invalid course ID", HttpStatus.NOT_FOUND);
        }
        courses.remove(id);
        return ResponseEntity.ok().build();
    }
}
