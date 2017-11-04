package com.sln.glassroom.validator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.sln.glassroom.domain.Rect;
import com.sln.glassroom.domain.Settings;
import com.sln.glassroom.service.SettingsWrapper;
import com.sln.glassroom.view.RectWrapper;

//http://docs.spring.io/spring/docs/current/spring-framework-reference/html/validation.html#validation-mvc-configuring
@Component
public class RectWrapperValidator implements Validator {
	
	@Autowired
	SettingsWrapper settingsWrapper;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return RectWrapper.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RectWrapper rectWrapper = (RectWrapper) target;
		
		// if some rows were removed in form using JavaScript, corresponding entries will still be present
		// in the list here. They will just be empty (isNew() = true)
		// remove all those lines. The following line modifies list.
		rectWrapper.getRectList().removeIf(Rect::isNew);
		
		Settings settings = settingsWrapper.getSettings();
		
		List<Rect> rects = rectWrapper.getRectList();  
		for (int i = 0; i < rects.size(); i++) { 
			Rect r = rects.get(i);

			if (r.getQuantity() < 1 || r.getQuantity() > 50)   
				errors.rejectValue("rectList[" + i + "].quantity", "RectWrapper.quantity");  
			if (r.getWidth() < 1 || r.getWidth() > (settings.getBedWidth() - settings.getMinDistanceBetweenPieces()))   
				errors.rejectValue("rectList[" + i + "].width", "RectWrapper.width");  
			if (r.getHeight() < 1 || r.getHeight() > (settings.getBedHeight() - settings.getMinDistanceBetweenPieces()))   
				errors.rejectValue("rectList[" + i + "].height", "RectWrapper.height");  
			if (r.getWidth() * r.getHeight() > (settings.getBedWidth() - settings.getMinDistanceBetweenPieces()) * (settings.getBedHeight() - settings.getMinDistanceBetweenPieces())) { 
				errors.rejectValue("rectList[" + i + "].width", "RectWrapper.area");
				errors.rejectValue("rectList[" + i + "].height", "RectWrapper.area");
			}
		}  
	}

}
