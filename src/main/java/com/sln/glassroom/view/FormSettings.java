package com.sln.glassroom.view;

import org.hibernate.validator.constraints.Range;

public class FormSettings {
	
	@Range(min = 100, max = 6000, message="Min=100 Max=6000")
	private Integer width;
	
	@Range(min = 100, max = 6000, message="Min=100 Max=6000")
	private Integer height;
	
	@Range(min = 0, max = 500, message="Min=0 Max=500")
	private Integer minDistance;

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(Integer minDistance) {
		this.minDistance = minDistance;
	}

}
