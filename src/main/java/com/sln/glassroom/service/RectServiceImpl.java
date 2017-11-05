package com.sln.glassroom.service;

import java.util.List;

import javax.transaction.Transactional;

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
	@Transactional
	public void saveAll(List<Rect> rectList, String clientIp) {
		rectRepository.deleteAllEntries();
		rectRepository.save(rectList);
	}
	
	@Override
	@Transactional
	public void deleteAll() {
		//rectRepository.deleteAll();
		rectRepository.deleteAllEntries();	// CrudRepository has deleteAll() but using this to speed up
	}

}
