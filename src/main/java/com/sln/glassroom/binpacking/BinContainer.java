package com.sln.glassroom.binpacking;

import java.awt.image.BufferedImage;

public interface BinContainer {

	int getBinCount();
	
	BufferedImage getBinImage(int binIndex);	// zero based index

}
