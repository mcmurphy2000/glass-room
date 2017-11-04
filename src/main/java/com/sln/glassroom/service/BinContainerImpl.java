package com.sln.glassroom.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

import com.sln.glassroom.domain.Rect;

public class BinContainerImpl implements BinContainer {

	public BinContainerImpl(List<Rect> rectList, int margin, int binWidth, int binHeight) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getBinCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public BufferedImage getBinImage(int binIndex) {
		// TODO Auto-generated method stub
	    //BufferedImage originalImage = ImageIO.read(new File("c:\\image\\mypic.jpg"));
		int w = 500, h = 750;
		BufferedImage img = new BufferedImage(w + 20, h + 20, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.BLUE);
		g2d.fillRect(0, 0, w + 20, h + 20);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(10, 10, w, h);
		
		return img;
	}

}
