package com.sln.glassroom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sln.glassroom.domain.Settings;
import com.sln.glassroom.repository.SettingsRepository;
import com.sln.glassroom.view.FormSettings;

@Service
public class SettingsServiceImpl implements SettingsService {
	
	@Autowired
	SettingsRepository settingsRepository;

	@Override
	public Settings findOne(Integer id) {
		return settingsRepository.findOne(id);
	}
	
	@Override
	public Settings save(Settings settings) {
		return settingsRepository.save(settings);
	}

	@Override
	public Settings assignFromFormSettings(Settings settings, FormSettings formSettings) {
		settings.setBedWidth(formSettings.getWidth());
		settings.setBedHeight(formSettings.getHeight());
		settings.setMinDistanceBetweenPieces(formSettings.getMinDistance());
		return settings;
	}

	@Override
	public FormSettings assignFromSettings(FormSettings formSettings, Settings settings) {
		formSettings.setWidth(settings.getBedWidth());
		formSettings.setHeight(settings.getBedHeight());
		formSettings.setMinDistance(settings.getMinDistanceBetweenPieces());
		return formSettings;
	}

}
