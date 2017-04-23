
//package SeamProject;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FastImage
{
	public enum RGBcolor {
		RED(0),GREEN(1),BLUE(2);
		public int value;
		private RGBcolor(int v)
		{
			this.value = v;
		}
	}
    public int width;
    public int acutualWidth;
    public int height;
    public int hasAlphaChannel;
    public int pixelLength;
    public byte[] pixels;
    public float[] energy;
    public float[] energySum;

	public FastImage(BufferedImage image)
    {

        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        width = image.getWidth();
        height = image.getHeight();
        acutualWidth = width;
        hasAlphaChannel = (image.getAlphaRaster() != null)?1:0;
        pixelLength = 3 + hasAlphaChannel;
      
        energy = new float[width*height];
        energySum = new float[width*height];

    }

	public void save(String path) throws IOException {
		File outputfile = new File(path);
//		 BufferedImage bufferedImage = new BufferedImage(this.width,
//		 this.height, BufferedImage.TYPE_INT_RGB);
//		 for (int i = 0; i < this.height; i++)
//		 for (int j = 0; j < this.width; j++)
//		 bufferedImage.setRGB( j, i, getRGB(i, j));
//		 ImageIO.write(bufferedImage, "bmp", outputfile);
		
		BufferedImage img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_3BYTE_BGR);
		img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(pixels, pixels.length), new Point()));
		ImageIO.write(img, "bmp", outputfile);

	}
	

	public void calculateImageEnergy() {
		int gradientWeight = 1;
		int entropyWeight = 0;
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				energy[i * width + j] = (gradientWeight * calculatePixelGradient(i, j)
						+ entropyWeight * calculatePixelEntropy(i, j)) / (gradientWeight + entropyWeight);
			}
		}
	}

	public float calculatePixelGradient(int x, int y) {
		int gradient = 0;
		int numOfNeighbors = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (isPixelInBounds(x + i, y + j) & (!(i == 0 & j == 0))) 
				{
					numOfNeighbors++;
					gradient += Math.abs(getPixelColor(x,y,RGBcolor.RED) - getPixelColor(x+i,y+j,RGBcolor.RED));
					gradient += Math.abs(getPixelColor(x,y,RGBcolor.GREEN) - getPixelColor(x+i,y+j,RGBcolor.GREEN));
					gradient += Math.abs(getPixelColor(x,y,RGBcolor.BLUE) - getPixelColor(x+i,y+j,RGBcolor.BLUE));
				}
			}
		}
		
		
		return gradient/(float)numOfNeighbors;
	}
	
	public boolean isPixelInBounds(int i, int j) {
		return (i >= 0) & (i < this.height) & (j >= 0) & (j < this.width);
	}

	public float calculatePixelEntropy(int x, int y) {
		float entropy = 0;
		int grayScaleSum = 0;
		for (int i = -4; i < 5; i++) {
			for (int j = -4; j < 5; j++) {
				if (isPixelInBounds(x + i, y + j) & (!(i == 0 & j == 0))) 
				{
					grayScaleSum += getGrayScale(x + i, y + j);
				}
			}
		}
		float funcP;
		for (int i = -4; i < 5; i++) {
			for (int j = -4; j < 5; j++) {
				if (isPixelInBounds(x + i, y + j) & (!(i == 0 & j == 0))) 
				{
					funcP = ((float)getGrayScale(x + i, y + j) /grayScaleSum);
					if (funcP != 0)
						entropy += funcP * Math.log(funcP);
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
        if (hasAlphaChannel == 1)
            argb = (((int) pixels[pos++] & 0xff) << 24); // alpha
        

        argb += ((int) pixels[pos++] & 0xff); // blue
        argb += (((int) pixels[pos++] & 0xff) << 8); // green
        argb += (((int) pixels[pos++] & 0xff) << 16); // red
        return argb;
    }
    
    public int getPixelColor(int x, int y, RGBcolor color)
    {   
        return (pixels[(y * pixelLength * width) + (x * pixelLength) + hasAlphaChannel+color.value]&0xff) ; 
    }
      
    
    public int getGrayScale(int x, int y)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength) + hasAlphaChannel;

        int argb = 0;
        
        argb += ((int) pixels[pos++]&0xff); // blue
        argb += ((int) pixels[pos++]&0xff); // green
        argb += ((int) pixels[pos]&0xff); // red
        return argb/3;
    }
    
    //setters
    public void setRed(int x, int y, int newRed)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength) + hasAlphaChannel;   
        pixels[pos+2] = (byte)newRed; 
    }
    
    public void setGreen(int x, int y, int newGreen)
    {
        int pos = (y * pixelLength * width) + (x * pixelLength) + hasAlphaChannel;
        pixels[pos+1] = (byte)newGreen; 
    }
    
    public void setBlue(int x, int y, int newBlue)
    {
        int pos = (y * this.pixelLength * this.width) + (x * this.pixelLength) + hasAlphaChannel;
        this.pixels[pos] = (byte)newBlue; 
    }
    
    public void updateEnergyDynamically()
    {
    	for(int i = this.height - 2; i>=0; i--)
    	{
    		this.energySum[i * this.acutualWidth] = this.energy[i * this.acutualWidth] + 
    				Math.min(this.energySum[(i+1) * this.width], this.energySum[(i+1) * this.width+1]);
    		for(int j=1;j<this.acutualWidth-1;j++)
    			this.energySum[i * this.width+j] = this.energy[i * this.width+j] + 
    						Math.min(this.energySum[(i+1) * this.width+j-1],
    						Math.min(this.energySum[(i+1) * this.width+j], this.energySum[(i+1) * this.width + j + 1]));	
    		
    		this.energySum[i * this.width+this.acutualWidth-1] = 
    				this.energy[i * this.width + this.acutualWidth-1] + 
    				Math.min(this.energySum[(i+1) * this.width + this.acutualWidth-1], 
    						this.energySum[(i+1) * this.width+ this.acutualWidth-1-1]);  	
    	}
    }
    public void substruct()
    {	
    	int[] lowestIndex = new int[this.height]; 
		lowestIndex[0] = findLowestEnergyInLine(0,0,this.acutualWidth);
    	if(lowestIndex[0]<acutualWidth-1)
    	{
    		System.arraycopy(this.energy, lowestIndex[0], this.energy, lowestIndex[0] + 1, this.acutualWidth - lowestIndex[0] - 1);
    		System.arraycopy(this.pixels, lowestIndex[0], this.pixels, lowestIndex[0] + 3 + this.hasAlphaChannel, (this.acutualWidth - lowestIndex[0])*3-1);
    	}
    	
    	for(int i=1; i<this.height;i++)
    	{
    		lowestIndex[i] = findLowestEnergyInLine(i,lowestIndex[i-1]-1,lowestIndex[i-1]+1);
    		if(lowestIndex[i]<acutualWidth-1)
        	{
    			System.arraycopy(this.energy, this.width*i+lowestIndex[i], this.energy, this.width*i+lowestIndex[i] + 1, this.acutualWidth - lowestIndex[i] - 1);
    			System.arraycopy(this.pixels, this.width*i+lowestIndex[i], this.pixels, this.width*i+lowestIndex[i] + this.pixelLength, (this.acutualWidth - lowestIndex[i])*this.pixelLength-1);
        	}
    	}
    	this.acutualWidth--;
    	
    }
    public int findLowestEnergyInLine(int line, int start, int end)
    {
    	if(start < 0)
    		start = 0;
    	if(end >= this.acutualWidth)
    		end = this.acutualWidth-1;
    	double minValue = this.energySum[line * this.width + start];
    	int index = start;
    	for(int j=start+1;j<=end;j++)
    	{
    		if(this.energySum[line * this.width + j] < minValue)
    		{
    			index = j;
    			minValue = this.energySum[line * this.width + j];
    		}
    	}
    	return index;
    }
    public void printEnergy()
    {
    	for(int i=0;i<this.height;i++)
    	{
    		for(int j=0; j<this.acutualWidth; j++)
    		{
    			System.out.print("" + String.format("%.1f", this.energy[i*width+j]) + " ");
    		}
    		System.out.println(" ");
    	}
    }
    public void printSumEnergy()
    {
    	for(int i=0;i<this.height;i++)
    	{
    		for(int j=0; j<this.acutualWidth; j++)
    		{
    			System.out.print("" + String.format("%3.1f", this.energySum[i*width+j]) + " ");
    		}
    		System.out.println(" ");
    	}
    }
    public void createNewImage()
    {
    	
    }
}