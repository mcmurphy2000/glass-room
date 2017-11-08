package com.sln.glassroom.binpacking;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.packing.core.Bin;
import org.packing.core.BinPacking;
import org.packing.primitives.MArea;

import com.sln.glassroom.domain.Rect;

/*
 * Can also check another 2d-bin-packing implementation: http://codeincomplete.com/posts/bin-packing/
 */
public class BinContainerImpl implements BinContainer {
	
	private class ColorLabel {
		Color color;
		String label;
		
		public ColorLabel(String hexColor, String label) {
			this.color = Color.decode(hexColor);
			this.label = label;
		}
	}
	
	private static final int DEFAULT_VIEW_WIDTH = 800;	// view height will be set based on binDimension's aspect ratio
	
	private Dimension binDimension;
	private Dimension viewPortDimension;
	private int margin;
	private Map<Integer, ColorLabel> colorLabelMap;
	private Bin[] bins;
	
	public BinContainerImpl(List<Rect> rectList, int margin, int binWidth, int binHeight) {
		binDimension = new Dimension(binWidth, binHeight);
		// in viewDimension preserve aspect ratio of binDimension
		int x, y;
		if (binWidth > binHeight) {
			x = DEFAULT_VIEW_WIDTH;
			y = (int) (DEFAULT_VIEW_WIDTH / ((double)binWidth / binHeight));
		} else {
			x = (int) (DEFAULT_VIEW_WIDTH / ((double)binHeight / binWidth));
			y = DEFAULT_VIEW_WIDTH;
		}
		viewPortDimension = new Dimension(x, y);
		this.margin = margin;
		
		colorLabelMap = new HashMap<>();
		List<MArea> mAreaList = new ArrayList<>();
		int id = 1;
		for (Rect r : rectList) {
			for (int i = 0; i < r.getQuantity(); i++) {
				// each Rect should have a margin around it so each MArea will be bigger that Rect
				// this will be accounted for later when drawing in drawInViewPort()
				mAreaList.add(new MArea(new Rectangle2D.Double(0.0, 0.0, r.getWidth() + margin * 2, r.getHeight() + margin * 2), id));
				colorLabelMap.put(id, new ColorLabel(r.getColor(), r.getLabel()));
				id++;
			}
		}
		MArea[] pieces = mAreaList.toArray(new MArea[0]);
		
		// generate bins
		bins = BinPacking.BinPackingStrategy(pieces, binDimension, viewPortDimension);
	}

	@Override
	public int getBinCount() {
		return bins.length;
	}

	@Override
	public BufferedImage getBinImage(int binIndex) {
		if (binIndex < 0 || binIndex >= bins.length)
			throw new IndexOutOfBoundsException();
		MArea[] areasInThisbin = bins[binIndex].getPlacedPieces();
		// create image
		BufferedImage img = new BufferedImage(viewPortDimension.width + 20, viewPortDimension.height + 20, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = img.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.BLUE);
		g2d.fillRect(0, 0, viewPortDimension.width + 20, viewPortDimension.height + 20);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(10, 10, viewPortDimension.width, viewPortDimension.height);
		for (MArea ma : areasInThisbin) {
			int id = ma.getID();
			ColorLabel cl = colorLabelMap.get(id);
			drawInViewPort(ma, g2d, cl.color, cl.label);
		}
		//img = Utils.flipAroundX(img);
		return img;
	}
	
	private void drawInViewPort(MArea mArea, Graphics g, Color areaColor, String areaLabel) {
        Rectangle2D r = mArea.getBounds2D();
        
        // Need to scale down the rect because it contains margin
        double newX = r.getX() + margin;	// determine left upper coords of where the new scaled-down rectangle will be  
        double newY = r.getY() + margin;	// this will be the coords of final position
        double newWidth = r.getWidth() - 2 * margin;	// the width of rect without margin
        double newHeight = r.getHeight() - 2 * margin;	// the height of rect without margin
        double scaleXFactor = newWidth / r.getWidth(); 	// scale factor due to margin 
        double scaleYFactor = newHeight / r.getHeight(); 	// scale factor due to margin
		
        AffineTransform at1MoveToZero = new AffineTransform();
        AffineTransform at2ScaleDown = new AffineTransform();
        AffineTransform at3MoveToPosition = new AffineTransform();
		
        at1MoveToZero.translate(-r.getX(), -r.getY());
        
        at2ScaleDown.scale(scaleXFactor, scaleYFactor);
        at2ScaleDown.concatenate(at1MoveToZero);
		
        at3MoveToPosition.translate(newX, newY);
        at3MoveToPosition.concatenate(at2ScaleDown);
		
        // now zoom to viewPort
		double xFactor = viewPortDimension.getWidth() / binDimension.getWidth();
		double yFactor = viewPortDimension.getHeight() / binDimension.getHeight();
		AffineTransform at4FinalScale = new AffineTransform();
		at4FinalScale.translate(10, 10);	// border?
		at4FinalScale.scale(xFactor, yFactor);
		at4FinalScale.concatenate(at3MoveToPosition);

		Area newArea = mArea.createTransformedArea(at4FinalScale);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(newArea);
		g2d.setColor(areaColor);
		g2d.fill(newArea);
		
		/* Print a label */
		// get coords of current area
		Rectangle rect = newArea.getBounds();
		// Font
		Font font = new Font("Dialog", Font.PLAIN, 20);
		//Font font = new Font("SansSerif", Font.PLAIN, 70);
		// Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    int strWidth = metrics.stringWidth(areaLabel);
	    int fontHeight = metrics.getHeight();
	    int fontAscent = metrics.getAscent();
	    
	    int x, y;
	    // we will rotate text if height of rect is 20% bigger than width and text does not fit into width
	    if (!(1.2 * rect.width < rect.height && strWidth * 1.1 > rect.width)) {		// horizontal layout of rectangle
		    // Determine the X coordinate for the text
		    x = rect.x + (rect.width - strWidth) / 2;
		    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
		    y = rect.y + ((rect.height - fontHeight) / 2) + fontAscent;
	    } else {							// vertical layout of rectangle
		    x = rect.x + ((rect.width - fontHeight) / 2) + fontAscent; 
		    y = rect.y + rect.height / 2 + strWidth / 2; 
		    // Rotate the text -90 degrees
		    AffineTransform atFont = new AffineTransform();
		    atFont.rotate(Math.toRadians(-90), 0, 0);
		    font = font.deriveFont(atFont);
		}
	    // Set the font
	    g2d.setFont(font);
	    // Draw the String
	    g2d.setColor(Color.BLACK);
	    g2d.drawString(areaLabel, x, y);
	}

}
