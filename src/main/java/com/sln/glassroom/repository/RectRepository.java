package com.sln.glassroom.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.sln.glassroom.domain.Rect;

public interface RectRepository extends CrudRepository<Rect, Integer> {
	
	// CrudRepository has deleteAll() method, but using this to speed up and for example's sake
	// Spring's default is clearAutomatically=false (was true before): https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.modifying-queries
	// Using =true because it doesn't seem that EntityManager is able to track changes made by this query
	@Modifying(clearAutomatically = true)
	@Query("delete from Rect")
	void deleteAllEntries();

}
