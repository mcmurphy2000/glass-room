package com.sln.glassroom.service;

import java.util.List;

import com.sln.glassroom.domain.Rect;

public interface RectService {
	
	List<Rect> findAll();
	
	void saveAll(List<Rect> rectList);
	
	void deleteAll();

}
