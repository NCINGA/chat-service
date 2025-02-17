//package com.ncinga.chatservice.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class UserService<T, ID> {
//
//    protected final CrudRepository<T, ID> repository;
//
//    public UserService(CrudRepository<T, ID> repository) {
//        this.repository = repository;
//    }
//
//
//    public T save(T entity) {
//        return repository.save(entity);
//    }
//
//    /**
//     * Saves multiple entities
//     * @param entities the entities to save
//     * @return the saved entities
//     */
//    public Iterable<T> saveAll(Iterable<T> entities) {
//        return repository.saveAll(entities);
//    }
//
//    /**
//     * Finds an entity by its ID
//     * @param id the entity ID
//     * @return Optional containing the found entity or empty if not found
//     */
//    public Optional<T> findById(ID id) {
//        return repository.findById(id);
//    }
//
//    /**
//     * Checks if an entity exists by ID
//     * @param id the entity ID
//     * @return true if entity exists, false otherwise
//     */
//    public boolean existsById(ID id) {
//        return repository.existsById(id);
//    }
//
//    /**
//     * Retrieves all entities
//     * @return Iterable of all entities
//     */
//    public Iterable<T> findAll() {
//        return repository.findAll();
//    }
//
//    /**
//     * Finds all entities with the given IDs
//     * @param ids the IDs to search for
//     * @return Iterable of found entities
//     */
//    public Iterable<T> findAllById(Iterable<ID> ids) {
//        return repository.findAllById(ids);
//    }
//
//    /**
//     * Counts total number of entities
//     * @return the count of entities
//     */
//    public long count() {
//        return repository.count();
//    }
//
//    /**
//     * Deletes an entity by ID
//     * @param id the ID of entity to delete
//     */
//    public void deleteById(ID id) {
//        repository.deleteById(id);
//    }
//
//    /**
//     * Deletes the given entity
//     * @param entity the entity to delete
//     */
//    public void delete(T entity) {
//        repository.delete(entity);
//    }
//
//    /**
//     * Deletes all entities with the given IDs
//     * @param ids the IDs of entities to delete
//     */
//    public void deleteAllById(Iterable<ID> ids) {
//        repository.deleteAllById(ids);
//    }
//
//    /**
//     * Deletes all given entities
//     * @param entities the entities to delete
//     */
//    public void deleteAll(Iterable<T> entities) {
//        repository.deleteAll(entities);
//    }
//
//    /**
//     * Deletes all entities
//     */
//    public void deleteAll() {
//        repository.deleteAll();
//    }
//
//    /**
//     * Utility method to convert Iterable to List
//     * @param iterable the iterable to convert
//     * @return List containing all elements from the iterable
//     */
//    protected List<T> toList(Iterable<T> iterable) {
//        List<T> list = new ArrayList<>();
//        iterable.forEach(list::add);
//        return list;
//    }
//}

package com.ncinga.chatservice.service;

import com.ncinga.chatservice.MongoModel.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserService extends MongoRepository<User, String> {
    List<User> findAll();

    User save(User user);


}