package com.sln.glassroom.controller;

// For the sake of example using @ExceptionHandler in MainController instead of just @ResponseStatus
//@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Bin Not Found")
public class BinNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private int index;
	
	public BinNotFoundException(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
}
