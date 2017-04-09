import java.awt.Color;
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
	
	public int getSizeX()
	{
		return this.sizeX;
	}
	
	public int getSizeY()
	{
		return this.sizeY;
	}
	
	public Pixel[][] getPixels()
	{
		return this.pixels;
	}
	
	public void saveBufferedImageFromImage(String path)
	{
		BufferedImage bufferedImage = new BufferedImage(this.sizeX , this.sizeY, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < sizeX; i++)
		{
			for (int j = 0 ; j < sizeY; j++)
			{
				bufferedImage.setRGB(i, j, (new Color(this.pixels[i][j].red, this.pixels[i][j].green, this.pixels[i][j].blue)).getRGB());
			}
		}
		File outputfile = new File(path);
		ImageIO.write(bufferedImage, "jpg", outputfile);
	}
			
}
