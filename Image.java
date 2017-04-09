import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Image {
	Pixel[][] pixels;
	int sizeX,sizeY;
	public Image(int x, int y, BufferedImage image)
	{
		this.sizeX = x;
		this.sizeY = y;
		this.pixels = new Pixel[this.sizeX][this.sizeY];
		for(int i=0;i<this.sizeX;i++)
		{
			this.pixels[0] = new Pixel[this.sizeY];
			for(int j=0;j<this.sizeY;j++)
			{
				this.pixels[i][j] = new Pixel(i,j,image.getRGB(i,j));
			}
		}
	}
	
			
}
