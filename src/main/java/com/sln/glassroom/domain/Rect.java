package com.sln.glassroom.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

@Entity
public class Rect {
	@Id
	@Column(nullable = false, updatable = false)
	private Integer id;
	
	@Column(length=15, columnDefinition = "char(15)")
	private String label;
	
	@Column(nullable=false)
	private Integer width;
	
	@Column(nullable=false)
	private Integer height;
	
	@Column(nullable=false)
	private Integer quantity;
	
	@Column(nullable=false)
	private String color;
	
	public Rect() {
	}
	
	public Rect(Integer id, String label, Integer width, Integer height, Integer quantity, String color) {
		this.id = id;
		this.label = label;
		this.width = width;
		this.height = height;
		this.quantity = quantity;
		this.color = color;
	}
	
	@Transient
	public boolean isNew() {
		return (this.id == null);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "Rect [id=" + id + ", label=" + label + ", width=" + width + ", height=" + height + ", quantity="
				+ quantity + ", color=" + color + "]";
	}
}
