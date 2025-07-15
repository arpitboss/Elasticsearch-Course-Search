package com.example.coursesearch.config;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.service.CourseService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {
    
    private final CourseService courseService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void run(String... args) throws Exception {
        loadSampleData();
    }
    
    private void loadSampleData() {
        try {
            objectMapper.registerModule(new JavaTimeModule());
            
            ClassPathResource resource = new ClassPathResource("sample-courses.json");
            List<Map<String, Object>> rawCourses = objectMapper.readValue(
                    resource.getInputStream(), 
                    new TypeReference<List<Map<String, Object>>>() {}
            );
            
            List<CourseDocument> courses = rawCourses.stream()
                    .map(this::mapToCourseDocument)
                    .toList();
            
            courseService.saveAll(courses);
            log.info("Successfully loaded {} courses into Elasticsearch", courses.size());
            
        } catch (IOException e) {
            log.error("Failed to load sample data", e);
        }
    }
    
    private CourseDocument mapToCourseDocument(Map<String, Object> rawCourse) {
        CourseDocument course = new CourseDocument();
        course.setId((String) rawCourse.get("id"));
        course.setTitle((String) rawCourse.get("title"));
        course.setDescription((String) rawCourse.get("description"));
        course.setCategory((String) rawCourse.get("category"));
        course.setType((String) rawCourse.get("type"));
        course.setGradeRange((String) rawCourse.get("gradeRange"));
        course.setMinAge((Integer) rawCourse.get("minAge"));
        course.setMaxAge((Integer) rawCourse.get("maxAge"));
        course.setPrice((Double) rawCourse.get("price"));
        
        String dateString = (String) rawCourse.get("nextSessionDate");
        LocalDateTime dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
        course.setNextSessionDate(dateTime);
        
        return course;
    }
}