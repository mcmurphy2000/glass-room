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
	
	private static final int MAX_QUANTITY = 50;
	
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
			
			int binBiggerSide = Math.max(settings.getBedWidth(), settings.getBedHeight());
			int binSmallerSide = Math.min(settings.getBedWidth(), settings.getBedHeight());
			int rectBiggerSide, rectSmallerSide;
			String biggerField, smallerField; 
			if (r.getWidth() < r.getHeight()) {
				rectBiggerSide = r.getHeight() + settings.getMinDistanceBetweenPieces();
				rectSmallerSide = r.getWidth() + settings.getMinDistanceBetweenPieces();
				biggerField = "rectList[" + i + "].height";
				smallerField = "rectList[" + i + "].width";
			} else {
				rectBiggerSide = r.getWidth() + settings.getMinDistanceBetweenPieces();
				rectSmallerSide = r.getHeight() + settings.getMinDistanceBetweenPieces();
				biggerField = "rectList[" + i + "].width";
				smallerField = "rectList[" + i + "].height";
			}
			
			if (rectBiggerSide > binBiggerSide)   
				errors.rejectValue(biggerField, "RectWrapper.WillNotFit");
			if (rectSmallerSide > binSmallerSide)   
				errors.rejectValue(smallerField, "RectWrapper.WillNotFit");  
			if (r.getQuantity() < 1 || r.getQuantity() > MAX_QUANTITY)   
				errors.rejectValue("rectList[" + i + "].quantity", "RectWrapper.quantity");
			if (r.getWidth() < 1)   
				errors.rejectValue("rectList[" + i + "].width", "RectWrapper.MinWidth");  
			if (r.getHeight() < 1)   
				errors.rejectValue("rectList[" + i + "].height", "RectWrapper.MinHeight");
		}  
	}

}
