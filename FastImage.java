import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FastImage
{

    public int width;
    public int height;
    public boolean hasAlphaChannel;
    public int pixelLength;
    public byte[] pixels;
    public float[] energy;

    public FastImage(BufferedImage image)
    {

        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        width = image.getWidth();
        height = image.getHeight();
        hasAlphaChannel = image.getAlphaRaster() != null;
        pixelLength = 3;
        if (hasAlphaChannel)
        {
            pixelLength = 4;
        }
        energy = new float[width*height];

    }
    
	public void save(String path) throws IOException
	{
		BufferedImage bufferedImage = new BufferedImage(this.width , this.height, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < this.width; i++)
			for (int j = 0 ; j < this.height; j++)
				bufferedImage.setRGB( i, j, getRGB(i,j) );
		File outputfile = new File(path);
		ImageIO.write(bufferedImage, "jpg", outputfile);
	}
	
	public void calculateImageEnergy()
	{
		int gradientWeight = 99; 
		int entropyWeight = 1 ;
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				energy[j*width+i] = (gradientWeight*calculatePixelGradient(i,j)+entropyWeight*calculatePixelEntropy(i,j))/(gradientWeight+entropyWeight);//weight is equal if (1*gradient+1*entropy)/2
			}
		}
	}
	
	public float calculatePixelGradient(int x, int y)
	{
		int gradient = 0;
		int numOfNeighbors = 0;
		for (int i = -1; i < 2; i++)
		{
			for (int j = -1; j < 2; j++)
			{
				if (isPixelInBounds(x+i,y+j)&(!(x==i&y==j))) //if neighbor not out of bounds and not pixels[x][y] itself
				{
					numOfNeighbors++;
					gradient += Math.abs(getRed(x,y) - getRed(x+i,y+j));
					gradient += Math.abs(getGreen(x,y) - getGreen(x+i,y+j));
					gradient += Math.abs(getBlue(x,y) - getBlue(x+i,y+j));
				}
			}
		}
		
		
		return gradient/numOfNeighbors;
		
	}
	
	public boolean isPixelInBounds(int i, int j)
	{
		return (i >= 0)&(i<width)&(j >= 0)&(j<height);
	}
	
	public float calculatePixelEntropy(int x, int y)
	{
		float entropy = 0;
		int graySacleSum = 0;
		for (int i = -4; i < 5; i++)
		{
			for (int j = -4; j < 5; j++)
			{
				if (isPixelInBounds(x+i,y+j)&(!(x==i&y==j))) //if neighbor not out of bounds and not pixels[x][y] itself
				{
					graySacleSum += getGraySacle(x+i,y+j);
				}
			}
		}
		float funcP;
		for (int i = -4; i < 5; i++)
		{
			for (int j = -4; j < 5; j++)
			{
				if (isPixelInBounds(x+i,y+j)&(!(x==i&y==j))) //if neighbor not out of bounds and not pixels[x][y] itself
				{
					funcP = (getGraySacle(x+i,y+j)/graySacleSum);
					if (funcP!=0)
						entropy += funcP*Math.log(funcP);
				}
			}
		}
		
		return (-entropy);
	}
    
	// getters
    public int getRGB(int x, int y)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength);

        int argb = -16777216; // 255 alpha
        if (hasAlphaChannel)
        {
            argb = (((int) pixels[pos++] & 0xff) << 24); // alpha
        }

        argb += ((int) pixels[pos++] & 0xff); // blue
        argb += (((int) pixels[pos++] & 0xff) << 8); // green
        argb += (((int) pixels[pos++] & 0xff) << 16); // red
        return argb;
    }
    
    public int getRed(int x, int y)
    {
    	int pos = (y * pixelLength * width) + (x * pixelLength);
        if (hasAlphaChannel)
        {
        	pos++;
        }
        return (pixels[pos+2]&0xff) ; 
    }
    
    public int getGreen(int x, int y)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength);
        if (hasAlphaChannel)
        {
        	pos++;
        }
        return (pixels[pos+1]&0xff); 
    }
    
    public int getBlue(int x, int y)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength);
        if (hasAlphaChannel)
        {
        	pos++;
        }
        return (pixels[pos]&0xff);
    }
    
    
    public int getGraySacle(int x, int y)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength);

        int argb = 0;
        if (hasAlphaChannel)
        {
            pos++; // alpha
        }

        argb += ((int) pixels[pos++]&0xff); // blue
        argb += ((int) pixels[pos++]&0xff); // green
        argb += ((int) pixels[pos]&0xff); // red
        return argb/3;
    }
    
    //setters
    public void setRed(int x, int y, int newRed)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength);
        if (hasAlphaChannel)
        {
        	pos++;
        }
        pixels[pos+2] = (byte)newRed; 
    }
    
    public void setGreen(int x, int y, int newGreen)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength);
        if (hasAlphaChannel)
        {
        	pos++;
        }
        pixels[pos+1] = (byte)newGreen; 
    }
    
    public void setBlue(int x, int y, int newBlue)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength);
        if (hasAlphaChannel)
        {
        	pos++;
        }
        pixels[pos] = (byte)newBlue; 
    }
    
}