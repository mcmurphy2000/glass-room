package com.sln.glassroom.service;

import java.awt.image.BufferedImage;

public interface BinContainer {

	int getBinCount();
	
	BufferedImage getBinImage(int binIndex);	// zero based index

}
