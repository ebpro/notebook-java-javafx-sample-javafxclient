package fr.univtln.bruno.samples.jfx.fxapp2.model;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

/**
 * Repository for Person entities.
 * Simulates a database repository with query methods and artificial delays
 * Simulates a database repository with query methods and artificial delays
 * to mimic network latency and database behavior.
 */
@Log
@NoArgsConstructor(staticName = "newInstance")
public class PersonRepository {
    // Database simulation constants
    private static final long DATA_SIZE = 1000;
    private static final int SIMULATED_QUERY_DELAY_MS = 150; // Simulate database query time
    
    // In-memory storage of persons, using UUID as key
    private static final Map<UUID, Person> members = new ConcurrentHashMap<>();

    // Static initializer to populate the data with random persons
    static {
        initializeDatabase();
    }
    
    /**
     * Initializes the in-memory database with sample data.
     */
    private static void initializeDatabase() {
        log.info("Initializing person database with " + DATA_SIZE + " records");
        
        // Create a Faker instance with French locale for generating realistic data
        Faker faker = new Faker(Locale.FRENCH);
        
        // Generate DATA_SIZE number of persons
        LongStream.range(0, DATA_SIZE)
            .mapToObj(id -> {
                Name name = faker.name();
                Address address = faker.address();
                return Person.of(
                    name.lastName() + ", " + name.firstName(),
                    address.streetAddress() + " " + address.zipCode() + " " + address.cityName()
                );
            })
            .forEach(p -> members.put(p.getUuid(), p));
        
        log.info("Database initialization complete");
    }
    
    /**
     * Simulates network delay for database operations.
     */
    private void simulateQueryDelay() {
        try {
            TimeUnit.MILLISECONDS.sleep(SIMULATED_QUERY_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Retrieves a person record by ID.
     * 
     * @param id the person's UUID
     * @return the person record, or empty if not found
     */
    public Optional<Person> findById(UUID id) {
        simulateQueryDelay();
        log.info("Finding person by ID: " + id);
        return Optional.ofNullable(members.get(id));
    }
    
    /**
     * Creates or updates a person record.
     * 
     * @param person the person to save
     * @return the saved person
     */
    public Person save(Person person) {
        simulateQueryDelay();
        log.info("Saving person: " + person.getUuid());
        members.put(person.getUuid(), person);
        return person;
    }
    
    /**
     * Deletes a person record by ID.
     * 
     * @param id the person's UUID
     * @return true if deleted, false if not found
     */
    public boolean delete(UUID id) {
        simulateQueryDelay();
        log.info("Deleting person: " + id);
        Person removed = members.remove(id);
        return removed != null;
    }
    
    /**
     * Returns the total number of records in the database.
     * 
     * @return total record count
     */
    public long count() {
        simulateQueryDelay();
        return members.size();
    }

    /**
     * Retrieves all persons with default pagination (page size 10, first page).
     * 
     * @return a page of person data
     */
    public Page<Person> findAll() {
        return findAll(10, 1);
    }
    
    /**
     * Retrieves all persons with custom pagination.
     * 
     * @param pageSize number of items per page
     * @param pageNumber page number (1-based index)
     * @return a page of person data
     */
    public Page<Person> findAll(int pageSize, int pageNumber) {
        return findAll(pageSize, pageNumber, Comparator.comparing(Person::getName));
    }
    
    /**
     * Retrieves all persons with custom pagination and sorting.
     * 
     * @param pageSize number of items per page
     * @param pageNumber page number (1-based index)
     * @param comparator comparator to sort the results
     * @return a page of person data
     */
    public Page<Person> findAll(int pageSize, int pageNumber, Comparator<? super Person> comparator) {
        simulateQueryDelay();
        log.info(String.format("Finding all persons (page %d, size %d)", pageNumber, pageSize));
        
        // Calculate actual skip amount (adjust for 0-based paging in database)
        long skip = (long) (pageNumber) * pageSize;
        
        List<Person> pageContent = members.values()
            .stream()
            .sorted(comparator)
            .skip(skip)
            .limit(pageSize)
            .toList();
            
        int totalPages = (int) Math.ceil((double) members.size() / pageSize);
            
        return new Page<>(
            members.size(),  // Total data size
            pageSize,        // Page size
            pageNumber,      // Current page (1-based)
            pageContent,     // Content for this page
            totalPages       // Total page count
        );
    }

    /**
     * Searches persons by name with default pagination (page size 10, first page).
     * 
     * @param criteria text to search for in person names
     * @return a page of matching person data
     */
    public Page<Person> search(String criteria) {
        return search(10, 1, criteria);
    }
    
    /**
     * Searches persons by name with custom pagination.
     * 
     * @param pageSize number of items per page
     * @param pageNumber page number (1-based index)
     * @param criteria text to search for in person names
     * @return a page of matching person data
     */
    public Page<Person> search(int pageSize, int pageNumber, String criteria) {
        return search(pageSize, pageNumber, Comparator.comparing(Person::getName), criteria);
    }
    
    /**
     * Searches persons by name with custom pagination and sorting.
     * 
     * @param pageSize number of items per page
     * @param pageNumber page number (1-based index)
     * @param comparator comparator to sort the results
     * @param criteria text to search for in person names
     * @return a page of matching person data
     */
    public Page<Person> search(int pageSize, int pageNumber, Comparator<? super Person> comparator, String criteria) {
        simulateQueryDelay();
        log.info(String.format("Searching persons by '%s' (page %d, size %d)", criteria, pageNumber, pageSize));
        
        // First collect all matching items to calculate total size
        List<Person> allMatching = members.values()
            .stream()
            .filter(p -> p.getName().toLowerCase().contains(criteria.toLowerCase()))
            .toList();
            
        long totalResults = allMatching.size();
        int totalPages = (int) Math.ceil((double) totalResults / pageSize);
        
        // Calculate actual skip amount (adjust for 0-based paging in database)
        long skip = (long) (pageNumber - 1) * pageSize;
        
        // Get just the requested page
        List<Person> pageContent = allMatching.stream()
            .sorted(comparator)
            .skip(skip)
            .limit(pageSize)
            .toList();
            
        return new Page<>(
            totalResults,    // Total data size matching the criteria
            pageSize,        // Page size
            pageNumber,      // Current page (1-based)
            pageContent,     // Content for this page
            totalPages       // Total page count
        );
    }
}