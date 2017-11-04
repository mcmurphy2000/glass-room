package com.sln.glassroom.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sln.glassroom.domain.Rect;
import com.sln.glassroom.repository.RectRepository;

@Service
public class RectServiceImpl implements RectService {
	
	@Autowired
	RectRepository rectRepository;

	@Override
	public List<Rect> findAll() {
		return (List<Rect>) rectRepository.findAll();
	}
	
	@Override
	public void saveAll(List<Rect> rectList) {
		rectRepository.save(rectList);
	}
	
	@Override
	public void deleteAll() {
		rectRepository.deleteAll();
	}

}
