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
		
			FastImage fastSeamImage = new FastImage(image);
			fastSeamImage.substruct(10);
			
			System.out.println("end");
			fastSeamImage.createNewImage();
			fastSeamImage.save(args[3]);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	     
	}
}
