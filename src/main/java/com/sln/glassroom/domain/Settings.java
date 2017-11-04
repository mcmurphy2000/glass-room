package com.sln.glassroom.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Settings {
	
	@Id
	private Integer id;

	@Column(nullable=false)
	private Integer bedWidth;
	
	@Column(nullable=false)
	private Integer bedHeight;
	
	@Column(nullable=false)
	private Integer minDistanceBetweenPieces;
	
	public Settings() {
	}
	
	public Settings(Integer id, Integer bedWidth, Integer bedHeight, Integer minDistanceBetweenPieces) {
		this.id = id;
		this.bedWidth = bedWidth;
		this.bedHeight = bedHeight;
		this.minDistanceBetweenPieces = minDistanceBetweenPieces;
	}

	public Integer getBedWidth() {
		return bedWidth;
	}

	public void setBedWidth(Integer bedWidth) {
		this.bedWidth = bedWidth;
	}

	public Integer getBedHeight() {
		return bedHeight;
	}

	public void setBedHeight(Integer bedHeight) {
		this.bedHeight = bedHeight;
	}

	public Integer getMinDistanceBetweenPieces() {
		return minDistanceBetweenPieces;
	}

	public void setMinDistanceBetweenPieces(Integer minDistanceBetweenPieces) {
		this.minDistanceBetweenPieces = minDistanceBetweenPieces;
	}

	@Override
	public String toString() {
		return "Settings [id=" + id + ", bedWidth=" + bedWidth + ", bedHeight=" + bedHeight
				+ ", minDistanceBetweenPieces=" + minDistanceBetweenPieces + "]";
	}

}
