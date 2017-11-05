package com.sln.glassroom.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sln.glassroom.domain.Rect;
import com.sln.glassroom.domain.RectHistory;
import com.sln.glassroom.repository.RectHistoryRepository;
import com.sln.glassroom.repository.RectRepository;

@Service
public class RectServiceImpl implements RectService {
	
	@Autowired
	RectRepository rectRepository;

	@Autowired
	RectHistoryRepository rectHistoryRepository;

	@Override
	public List<Rect> findAll() {
		return (List<Rect>) rectRepository.findAll();
	}
	
	@Override
	@Transactional
	public void saveAll(List<Rect> rectList, String clientIp) {
		rectRepository.deleteAllEntries();
		rectRepository.save(rectList);
		List<RectHistory> rectHistoryList = createHistoryList(rectList, clientIp);
		rectHistoryRepository.save(rectHistoryList);
	}
	
	@Override
	@Transactional
	public void deleteAll() {
		//rectRepository.deleteAll();
		rectRepository.deleteAllEntries();	// CrudRepository has deleteAll() but using this to speed up
	}
	
	private List<RectHistory> createHistoryList(List<Rect> rectList, String clientIp) {
		Date now = Calendar.getInstance().getTime();
		List<RectHistory> list = rectList.stream()
			.map(r -> new RectHistory(r, now, clientIp))
			.collect(Collectors.toList());
		return list;
	}

}
