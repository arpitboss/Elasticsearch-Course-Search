# Course Search - Spring Boot Elasticsearch Application

A Spring Boot application that provides course search functionality with Elasticsearch backend.

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven 3.6+

## Setup Instructions

### 1. Start Elasticsearch

```bash
docker-compose up -d
```

Verify Elasticsearch is running:

```bash
curl http://localhost:9200
```

### 2. Build and Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080` and automatically load sample course data into Elasticsearch.

### 3. Verify Data Loading

Check if courses are indexed:

```bash
curl "http://localhost:9200/courses/_count"
```

## API Usage

### Search Courses

**Endpoint:** `GET /api/search`

**Parameters:**

- `q` - Search keyword (searches title and description)
- `minAge` - Minimum age filter
- `maxAge` - Maximum age filter
- `category` - Course category filter
- `type` - Course type filter (`ONE_TIME`, `COURSE`, `CLUB`)
- `minPrice` - Minimum price filter
- `maxPrice` - Maximum price filter
- `startDate` - Start date filter (ISO-8601 format)
- `sort` - Sort order (`upcoming`, `priceAsc`, `priceDesc`)
- `page` - Page number (default: 0)
- `size` - Page size (default: 10)

### Example API Calls

1. **Basic search:**

```bash
curl "http://localhost:8080/api/search?q=math"
```

2. **Filter by category and age:**

```bash
curl "http://localhost:8080/api/search?category=Science&minAge=10&maxAge=15"
```

3. **Price range search:**

```bash
curl "http://localhost:8080/api/search?minPrice=50&maxPrice=200&sort=priceAsc"
```

4. **Search with date filter:**

```bash
curl "http://localhost:8080/api/search?startDate=2025-08-01T00:00:00Z&sort=upcoming"
```

5. **Complex search:**

```bash
curl "http://localhost:8080/api/search?q=science&category=Science&type=COURSE&minAge=12&maxAge=16&minPrice=100&maxPrice=300&sort=priceDesc&page=0&size=5"
```

6. **Pagination:**

```bash
curl "http://localhost:8080/api/search?page=1&size=5"
```

## Response Format

```json
{
  "total": 15,
  "courses": [
    {
      "id": "1",
      "title": "Basic Mathematics",
      "description": "Introduction to basic math concepts...",
      "category": "Math",
      "type": "COURSE",
      "gradeRange": "1st-3rd",
      "minAge": 6,
      "maxAge": 9,
      "price": 99.99,
      "nextSessionDate": "2025-08-15T10:00:00"
    }
  ]
}
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/coursesearch/
│   │   ├── CourseSearchApplication.java
│   │   ├── config/
│   │   │   └── DataLoader.java
│   │   ├── controller/
│   │   │   └── CourseController.java
│   │   ├── document/
│   │   │   └── CourseDocument.java
│   │   ├── repository/
│   │   │   └── CourseRepository.java
│   │   └── service/
│   │       └── CourseService.java
│   └── resources/
│       ├── application.properties
│       └── sample-courses.json
├── docker-compose.yml
└── pom.xml
```

## Stopping the Application

To stop the application and Elasticsearch:

```bash
# Stop Spring Boot application (Ctrl+C)
# Stop Elasticsearch
docker-compose down
```

## Troubleshooting

1. **Elasticsearch connection issues:**

   - Ensure Elasticsearch is running: `docker-compose ps`
   - Check logs: `docker-compose logs elasticsearch`

2. **Data not loading:**

   - Check application logs for any errors
   - Verify sample-courses.json is in src/main/resources

3. **Search returning no results:**
   - Verify data is indexed: `curl "http://localhost:9200/courses/_count"`
   - Check query parameters are correctly formatted
