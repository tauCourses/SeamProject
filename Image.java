import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Image {
	Pixel[][] pixels;
	int sizeX,sizeY;
	public Image(BufferedImage image)
	{
		this.sizeX = image.getWidth();
		this.sizeY = image.getHeight();
		this.pixels = new Pixel[this.sizeX][this.sizeY];
		for(int i=0;i<this.sizeX;i++)
			for(int j=0;j<this.sizeY;j++)
				this.pixels[i][j] = new Pixel(i,j,image.getRGB(i,j));
		
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
	
	public void save(String path) throws IOException
	{
		BufferedImage bufferedImage = new BufferedImage(this.sizeX , this.sizeY, BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < this.sizeX; i++)
			for (int j = 0 ; j < this.sizeY; j++)
				bufferedImage.setRGB( i, j, this.pixels[i][j].c.getRGB() );
		
		File outputfile = new File(path);
		ImageIO.write(bufferedImage, "jpg", outputfile);
	}

	public void changeSize(int newX, int newY)
	{
	}	
			
}
