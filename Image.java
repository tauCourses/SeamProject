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
	
	public void calculateImageEnergy()
	{
		int gradientWeight = 1; 
		int entropyWeight = 1 ;
		for (int i = 0; i < sizeX; i++)
		{
			for (int j = 0; j < sizeY; j++)
			{
				pixels[i][j].energy = (gradientWeight*calculatePixelGradient(i,j)+entropyWeight*calculatePixelEntropy(i,j))/(gradientWeight+entropyWeight);//weight is equal if (1*gradient+1*entropy)/2
			}
		}
	}
	
	public int calculatePixelGradient(int x, int y)
	{
		int gradient = 0;
		for (int i = -1; i < 2; i++)
		{
			for (int j = -1; j < 2; j++)
			{
				if (isPixelInBounds(x+i,y+j)&(!(x==i&y==j))) //if neighbor not out of bounds and not pixels[x][y] itself
				{
					gradient += Math.abs(pixels[x][y].red - pixels[x+i][y+j].red);
					gradient += Math.abs(pixels[x][y].green - pixels[x+i][y+j].green);
					gradient += Math.abs(pixels[x][y].blue - pixels[x+i][y+j].blue);
				}
			}
		}
		return gradient;
	}
	
	public boolean isPixelInBounds(int i, int j)
	{
		return (i >= 0)&(i<sizeX)&(j >= 0)&(j<sizeY);
	}
	
	public int calculatePixelEntropy(int x, int y)
	{
		double entropy = 0;
		for (int i = -4; i < 5; i++)
		{
			for (int j = -4; j < 5; j++)
			{
				if (isPixelInBounds(x+i,y+j)&(!(x==i&y==j))) //if neighbor not out of bounds and not pixels[x][y] itself
				{
					entropy += funcP(i,j)*(int)Math.log(funcP(i,j));
				}
			}
		}
		
		return (int)entropy;
	}
	
	public int funcP(int x, int y) //calculate Pmn function per pixel using Pixel.getGrayScale
	{
		int pValue = 0;
		
		
		return pValue;
	}

			
}
