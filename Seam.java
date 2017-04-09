import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Seam {
	
	public static void main(String[] args)
	{
		 try
	    {
	      // the line that reads the image file
	      BufferedImage image = ImageIO.read(new File("/home/matan/Pictures/nepalFBpics/CIMG2836.JPG"));
	      
	      System.out.println(image.getHeight());
	      // work with the image here ...
	    } 
	    catch (IOException e)
	    {
	      // log the exception
	      // re-throw if desired
	    }
	}
}
