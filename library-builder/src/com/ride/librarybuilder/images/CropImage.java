package com.ride.librarybuilder.images;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CropImage {
	
	public static boolean cropImage(File imgFile) throws IOException{
		return cropImage(imgFile, null);
	}
	public static boolean cropImage(File imgFile, String watermarkImage) throws IOException{
		
		//BufferedImage image = ImageIO.read(new File(imgFile));
		BufferedImage image = ImageIO.read(imgFile);
		//BufferedImage out = image.getSubimage(x, y, w, h);
		int w = image.getWidth();
		int h = image.getHeight();
		int w2 = (int) (w * 0.9);
		int h2 = (int) (h * 0.9);
		int x2 = w-w2;
		int y2 = h-h2;
		BufferedImage out = image.getSubimage(x2, y2, w2, h2);
		
		if(watermarkImage!=null && "".equals(watermarkImage)==false){
			File watermarkImageFile = new File(watermarkImage); 
			out = addImageWatermark(watermarkImageFile, out);
		}
				
		//String outFile = imgFile.substring(0, imgFile.lastIndexOf(".")) + "_cropped.jpg";
		String outFile = imgFile.getAbsolutePath();
		return ImageIO.write(out, "jpg", new File(outFile));
		
		
	}
	
	private static BufferedImage addImageWatermark(File watermarkImageFile, BufferedImage sourceImage) {
	    try {
	        //BufferedImage sourceImage = ImageIO.read(sourceImageFile);
	        BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);
	 
	        // initializes necessary graphic properties
	        Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();
	        AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
	        g2d.setComposite(alphaChannel);
	 
	        // calculates the coordinate where the image is painted
	        int topLeftX = 15;
	        int topLeftY = 15;
	 
	        // paints the image watermark
	        g2d.drawImage(watermarkImage, topLeftX, topLeftY, null);
	 	        
	        g2d.dispose();
	        
	        return sourceImage;	 	        
	 
	    } catch (IOException ex) {
	        System.err.println(ex);
	        return null;
	    }
	}
}
