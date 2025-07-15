package com.example.coursesearch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public void saveAll(List<CourseDocument> courses) {
        courseRepository.saveAll(courses);
    }

    public SearchResult searchCourses(String q, Integer minAge, Integer maxAge,
            String category, String type, Double minPrice,
            Double maxPrice, LocalDateTime startDate,
            String sort, int page, int size) {
        Criteria criteria = new Criteria();

        if (q != null && !q.isBlank()) {
            criteria = criteria.or(new Criteria("title").matches(q))
                    .or(new Criteria("description").matches(q));
        }
        if (category != null && !category.isBlank()) {
            criteria = criteria.and(new Criteria("category").is(category));
        }
        if (type != null && !type.isBlank()) {
            criteria = criteria.and(new Criteria("type").is(type));
        }
        if (minAge != null) {
            criteria = criteria.and(new Criteria("minAge").greaterThanEqual(minAge));
        }
        if (maxAge != null) {
            criteria = criteria.and(new Criteria("maxAge").lessThanEqual(maxAge));
        }
        if (minPrice != null) {
            criteria = criteria.and(new Criteria("price").greaterThanEqual(minPrice));
        }
        if (maxPrice != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(maxPrice));
        }
        if (startDate != null) {
            criteria = criteria.and(new Criteria("nextSessionDate").greaterThanEqual(startDate));
        }

        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        criteriaQuery.setPageable(PageRequest.of(page, size));
        // Sorting logic can be added here if needed

        if (sort != null) {
        switch (sort) {
            case "priceAsc":
                criteriaQuery.addSort(Sort.by(Sort.Direction.ASC, "price"));
                break;
            case "priceDesc":
                criteriaQuery.addSort(Sort.by(Sort.Direction.DESC, "price"));
                break;
            case "upcoming":
                criteriaQuery.addSort(Sort.by(Sort.Direction.ASC, "nextSessionDate"));
                break;
            default:
        }
    }

        SearchHits<CourseDocument> searchHits = elasticsearchOperations.search(criteriaQuery, CourseDocument.class);

        List<CourseDocument> courses = searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

        return new SearchResult(searchHits.getTotalHits(), courses);
    }

    public static class SearchResult {
        private final long total;
        private final List<CourseDocument> courses;

        public SearchResult(long total, List<CourseDocument> courses) {
            this.total = total;
            this.courses = courses;
        }

        public long getTotal() {
            return total;
        }

        public List<CourseDocument> getCourses() {
            return courses;
        }
    }
}