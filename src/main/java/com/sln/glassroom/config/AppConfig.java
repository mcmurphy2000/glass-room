package com.sln.glassroom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import com.sln.glassroom.domain.Settings;
import com.sln.glassroom.domain.SettingsWrapper;
import com.sln.glassroom.service.SettingsService;

@Configuration
public class AppConfig {
	
	@Autowired
	SettingsService settingsService;

	/**
	 * SettingsWrapper bean will be loaded on each new session and will stay until session ends
	 * I'm using SettingsWrapper instead of just Settings because otherwise, when Settings is @Autowired it's impossible to save it
	 * via settingsService.save(settings) (because it would be a Spring proxy, not an entity)
	 *
	 * The proxyMode attribute is necessary because at the moment of the instantiation of the web application context, there is no active session.
	 * Spring will create a proxy to be injected as a dependency, and instantiate the target bean when it is needed in a request.
	 * @see <a href="http://www.baeldung.com/spring-bean-scopes">http://www.baeldung.com/spring-bean-scopes</a>
	 * 
	 * @return a bean that contains current settings entity
	 */
	@Bean
	@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public SettingsWrapper getSettingsWrapper() {
		final int ID = 1;
		final int DEFAULT_WIDTH = 2500;
		final int DEFAULT_HEIGHT = 3600;
		final int MIN_DISTANCE = 50;
		
		Settings settings = settingsService.findOne(ID);
		// if it's not in repository then create one with default values 
		if (settings == null) {
			settings = new Settings(ID, DEFAULT_WIDTH, DEFAULT_HEIGHT, MIN_DISTANCE);
			settings = settingsService.save(settings);
		}
		SettingsWrapper settingsWrapper = new SettingsWrapper();
		settingsWrapper.setSettings(settings);
		return settingsWrapper;
	}
	
}
