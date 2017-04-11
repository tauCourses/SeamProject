import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Seam {
	
	public static void main(String[] args)
	{
		try 
		{
			BufferedImage image = ImageIO.read(new File(args[0]));
			Image seamImage = new Image(image);
			seamImage.changeSize(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			seamImage.save(args[3]);
			seamImage.calculateImageEnergy();
			System.out.println(seamImage.pixels[0][0]);
			System.out.println("end");
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	     
	}
}
