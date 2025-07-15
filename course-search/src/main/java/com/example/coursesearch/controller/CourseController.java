package com.example.coursesearch.controller;

import com.example.coursesearch.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CourseController {

        private final CourseService courseService;

        @GetMapping("/search")
        public ResponseEntity<Map<String, Object>> searchCourses(
                        @RequestParam(required = false) String q,
                        @RequestParam(required = false) Integer minAge,
                        @RequestParam(required = false) Integer maxAge,
                        @RequestParam(required = false) String category,
                        @RequestParam(required = false) String type,
                        @RequestParam(required = false) Double minPrice,
                        @RequestParam(required = false) Double maxPrice,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                        @RequestParam(required = false, defaultValue = "upcoming") String sort,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size) {

                CourseService.SearchResult result = courseService.searchCourses(
                                q, minAge, maxAge, category, type, minPrice, maxPrice,
                                startDate, sort, page, size);

                return ResponseEntity.ok(Map.of(
                                "total", result.getTotal(),
                                "courses", result.getCourses()));
        }
}