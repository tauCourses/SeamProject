import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Seam {
	
	public static void main(String[] args)
	{
		try 
		{
			BufferedImage image = ImageIO.read(new File(args[0]));
		
//			Image seamImage = new Image(image);
//			seamImage.changeSize(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
//			seamImage.save(args[3]);
//			seamImage.calculateImageEnergy();
//			System.out.println(seamImage.pixels[0][0]);
//			System.out.println("end");
			
			//testing for fastImage
			FastImage fastSeamImage = new FastImage(image);
			fastSeamImage.save(args[3]);
			System.out.println("Starting energy calculation, please hold...");
			fastSeamImage.calculateImageEnergy();
			System.out.println("First pixle's red,green,blue and grayscale values: "+fastSeamImage.getRed(0,0)+" "+fastSeamImage.getGreen(0,0)+" "+fastSeamImage.getBlue(0,0)+" "+fastSeamImage.getGraySacle(0,0));
			System.out.println("First pixle's energy: "+fastSeamImage.energy[0]);
			System.out.println("end");
			//end of testing of fastImage
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	     
	}
}
