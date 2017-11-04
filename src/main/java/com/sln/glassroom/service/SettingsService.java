package com.sln.glassroom.service;

import com.sln.glassroom.domain.Settings;
import com.sln.glassroom.view.FormSettings;

public interface SettingsService {
	
	Settings findOne(Integer id);
	
	Settings save(Settings settings);
	
	Settings assignFromFormSettings(Settings settings, FormSettings formSettings);
	
	FormSettings assignFromSettings(FormSettings formSettings, Settings settings);
	
}
