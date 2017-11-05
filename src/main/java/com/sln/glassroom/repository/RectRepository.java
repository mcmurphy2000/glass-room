package com.sln.glassroom.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.sln.glassroom.domain.Rect;

public interface RectRepository extends CrudRepository<Rect, Integer> {
	
	@Modifying
	@Query("delete from Rect")
	void deleteAllEntries();	// CrudRepository has deleteAll() but using this to speed up

}
