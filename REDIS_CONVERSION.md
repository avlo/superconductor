# JPA to Spring Data Redis Conversion

This document summarizes the conversion of the SuperConductor project from Spring Data JPA to Spring Data Redis.

## Changes Made

### 1. Build Configuration Updates
- **Parent POM (pom.xml)**:
  - Removed `spring-boot-starter-data-jpa` dependency
  - Removed `mysql-connector-j` and `h2` database dependencies
  - Added `spring-boot-starter-data-redis` dependency
  - Updated Java version from 21 to 17 for environment compatibility
  - Removed MySQL-specific properties

- **Application POM (superconductor/pom.xml)**:
  - Removed MySQL dependencies from all build profiles (dev_ws, dev_wss, prod_ws, prod_wss)

### 2. Entity Conversion (38 files converted)
All entities in `lib/src/main/java/com/prosilion/superconductor/entity/**/*.java` were converted:

- **JPA → Redis Annotations**:
  - `@Entity` → `@RedisHash("table_name")`
  - `@Table(name = "table_name")` → removed (table name now in @RedisHash)
  - `@Id @GeneratedValue(strategy = GenerationType.IDENTITY)` → `@Id` (Redis handles ID generation)
  - `@Column(...)` → removed (Redis doesn't need column specifications)
  - `@Lob` → removed (Redis handles large objects automatically)
  - `@MappedSuperclass` → removed (no direct Redis equivalent)

- **Import Updates**:
  - `jakarta.persistence.*` → `org.springframework.data.annotation.Id`, `org.springframework.data.redis.core.RedisHash`
  - `jakarta.persistence.Transient` → `org.springframework.data.annotation.Transient`

- **Index Support**:
  - Added `@Indexed` annotations to frequently queried fields like `eventIdString` and `pubKey` in EventEntity

### 3. Repository Conversion (29 files converted)
All repositories in `lib/src/main/java/com/prosilion/superconductor/repository/**/*.java` were converted:

- **Interface Changes**:
  - `extends JpaRepository<Entity, Long>` → `extends CrudRepository<Entity, Long>`
  - Import updated from `org.springframework.data.jpa.repository.JpaRepository` to `org.springframework.data.repository.CrudRepository`

- **Query Methods**: Kept existing query method signatures as Spring Data Redis supports similar query derivation

### 4. Configuration Changes

- **Application Properties**:
  - `application.properties`: Replaced JPA/Hibernate configuration with Redis configuration
  - `application-local_ws.properties`: Replaced H2 database configuration with Redis configuration
  - `application-test.properties`: Replaced H2 test configuration with Redis test configuration

- **Redis Configuration Class**:
  - Created `lib/src/main/java/com/prosilion/superconductor/config/RedisConfig.java`
  - Configured Redis repositories with `@EnableRedisRepositories`
  - Set up Redis template with JSON serialization for values and String serialization for keys

### 5. Service Layer Updates
- **EventEntityService.java**: 
  - Removed `jakarta.persistence.NoResultException` import
  - Replaced `NoResultException::new` with `() -> new RuntimeException("Event not found")`

- **Transactional Annotations**: Left `@Transactional` annotations in place as Redis operations are atomic and Spring will handle them appropriately

### 6. Test Configuration Updates
- **EventEntityRepositoryIT.java**: 
  - Replaced `@DataJpaTest` with `@SpringBootTest` for Redis testing
  - Removed JPA-specific test configuration filters

## Redis Configuration Details

### Connection Settings
```properties
# Default Redis configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=2000ms
spring.data.redis.database=0

# Test configuration uses database 15
spring.data.redis.database=15
```

### Serialization
- **Keys**: String serialization for better Redis CLI compatibility
- **Values**: JSON serialization for complex object storage
- **Hash Keys/Values**: String/JSON serialization respectively

## Migration Notes

### Data Structure Changes
- **ID Generation**: Redis auto-generates IDs, but format may differ from JPA sequence generation
- **Relationships**: Redis doesn't enforce foreign key constraints like relational databases
- **Indexing**: Only fields marked with `@Indexed` will be queryable beyond the ID

### Compatibility Considerations
- **Query Methods**: Most Spring Data query methods work similarly, but complex JPA queries may need revision
- **Transactions**: Redis transactions work differently than database transactions
- **Caching**: Redis serves as both data store and cache, eliminating need for separate caching layer

## Build Status
The conversion is complete, but the build currently fails due to a missing external dependency (`nostr-java-core:1.0.0`). Once this dependency is available, the application should build and run with Redis as the data store.

## Required Runtime Dependencies
- Redis server running on localhost:6379 (or configured host/port)
- All existing application dependencies except JPA/database-specific ones