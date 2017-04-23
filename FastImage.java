
//package SeamProject;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.omg.Messaging.SyncScopeHelper;

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
    public int actualWidth;
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
        actualWidth = width;
        hasAlphaChannel = (image.getAlphaRaster() != null)?1:0;
        pixelLength = 3 + hasAlphaChannel;
      
        energy = new float[width*height];
        energySum = new float[width*height];

    }

	public void save(String path) throws IOException {
		File outputfile = new File(path);
		System.out.println(" ww" + this.width);
		 BufferedImage bufferedImage = new BufferedImage(this.width,
		 this.height, BufferedImage.TYPE_INT_RGB);
		 for (int i = 0; i < this.height; i++)
			 for (int j = 0; j < this.width; j++)
				 bufferedImage.setRGB( j, i, getRGB(i, j));
		 ImageIO.write(bufferedImage, "bmp", outputfile);
		
//		BufferedImage img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_3BYTE_BGR);
//		img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(pixels, pixels.length), new Point()));
//		ImageIO.write(img, "bmp", outputfile);

	}
	

	public void calculateImageEnergy() {
		int gradientWeight = 1;
		int entropyWeight = 0;
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				energy[i * width + j] = ((float)(gradientWeight * calculatePixelGradient(i, j))+ (entropyWeight * calculatePixelEntropy(i, j))) / (gradientWeight + entropyWeight);
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
		return (i >= 0) & (i < this.height) & (j >= 0) & (j < this.actualWidth);
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
        int pos = (x * pixelLength * width) + (y * pixelLength);

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
        return (pixels[(x * pixelLength * width) + (y * pixelLength) + hasAlphaChannel+color.value]&0xff) ; 
    }
      
    
    public int getGrayScale(int x, int y)
    {
        int pos = (x * pixelLength * width) + (y * pixelLength) + hasAlphaChannel;

        int argb = 0;
        
        argb += ((int) pixels[pos++]&0xff); // blue
        argb += ((int) pixels[pos++]&0xff); // green
        argb += ((int) pixels[pos]&0xff); // red
        return argb/3;
    }
    
    public void updateEnergyDynamically()
    {
    	for(int j=0;j<this.actualWidth;j++)
    		this.energySum[(this.width)*(this.height-1) + j] = this.energy[(this.width)*(this.height-1) + j];
    	for(int i = this.height - 2; i>=0; i--)
    	{
    		this.energySum[i * this.width] = this.energy[i * this.width] + 
    				Math.min(this.energySum[(i+1) * this.width], this.energySum[(i+1) * this.width+1]);
    		for(int j=1;j<this.actualWidth-1;j++)
    			this.energySum[i * this.width+j] = this.energy[i * this.width+j] + 
    						Math.min(this.energySum[(i+1) * this.width+j-1],
    						Math.min(this.energySum[(i+1) * this.width+j], this.energySum[(i+1) * this.width + j + 1]));	
    		
    		this.energySum[i * this.width+this.actualWidth-1] =  
    				this.energy[i * this.width + this.actualWidth-1] + 
    				Math.min(this.energySum[(i+1) * this.width + this.actualWidth-1], 
    						this.energySum[(i+1) * this.width+ this.actualWidth-1-1]);  	
    	}
    }
    
    public void substruct(int seams)
    {
    	System.out.println("Starting energy calculation, please hold...");
    	this.calculateImageEnergy();
    	System.out.println("start substructing");
    	//this.printEnergy();
    	
    	for(int i=0;i<seams;i++)
    	{
    		this.updateEnergyDynamically();
    		this.substructLine();
    		//System.out.println("after " + i + "substruct:");
        	//this.printEnergy();
    	}
    	createNewImage();
    	System.out.println("");
    	//System.out.println("after substruct:");
    	//this.printEnergy();
    }
    
    public void substructLine()
    {	
    	System.out.println("line");
    	int[] lowestIndex = new int[this.height]; 
		lowestIndex[0] = findLowestEnergyInLine(0,0,this.actualWidth);
		System.out.println("i " + lowestIndex[0]);
		
    	System.out.println((lowestIndex[0]+1)* this.pixelLength);
    	System.out.println(lowestIndex[0]* this.pixelLength);
    	System.out.println((this.actualWidth - lowestIndex[0]-1) * this.pixelLength);
    	System.out.println(this.pixels.length);
		System.arraycopy(this.energy, lowestIndex[0]+1, this.energy, lowestIndex[0], this.actualWidth - lowestIndex[0] - 1);
		System.arraycopy(this.pixels, 
						(lowestIndex[0]+1)* this.pixelLength, 
						this.pixels, 
						lowestIndex[0]* this.pixelLength, 
						(this.actualWidth - lowestIndex[0]-1) * this.pixelLength );
//    		this.pixels[this.width*0 + lowestIndex[0]*this.pixelLength] = 0;
//			this.pixels[this.width*0 + lowestIndex[0]*this.pixelLength+1] = 0;
//			this.pixels[this.width*0 + lowestIndex[0]*this.pixelLength+2] = 0;
    	
 
    	
    	for(int i=1; i<this.height;i++)
    	{
    		
    		lowestIndex[i] = findLowestEnergyInLine(i,lowestIndex[i-1]-1,lowestIndex[i-1]+1);
    		if(lowestIndex[i]<actualWidth-1)
        	{
    			System.arraycopy(this.energy, this.width*i+lowestIndex[i]+1, this.energy, this.width*i+lowestIndex[i], this.actualWidth - lowestIndex[i] - 1);
    			System.arraycopy(this.pixels, 
    							(this.width*i+lowestIndex[i] +1 )* this.pixelLength, 
    							this.pixels, 
    							(this.width*i+lowestIndex[i])*this.pixelLength , 
    							(this.actualWidth - lowestIndex[i] -1 )*this.pixelLength);
//    			this.pixels[(this.width*i + lowestIndex[i])*this.pixelLength] = 0;
//    			this.pixels[(this.width*i + lowestIndex[i])*this.pixelLength+1] = 0;
//    			this.pixels[(this.width*i + lowestIndex[i])*this.pixelLength+2] = 0;
//    			
    		}
    	}

    	this.actualWidth--;
    	//System.out.println("lines:");
    	for(int i=0;i<this.height;i++)
    	{
    		if(isPixelInBounds(i, lowestIndex[i]))
    			energy[i * width + lowestIndex[i]] = calculatePixelGradient(i, lowestIndex[i]) + 2;
    		
    		if(isPixelInBounds(i, lowestIndex[i]-1))
    			energy[i * width + lowestIndex[i]-1] = calculatePixelGradient(i, lowestIndex[i]-1) + 2;
    		
    	//	System.out.print("" + lowestIndex[i]+ " ");
    	}
    	//System.out.println("");
    	
    }
    public int findLowestEnergyInLine(int line, int start, int end)
    {
    	if(start < 0)
    		start = 0;
    	if(end >= this.actualWidth)
    		end = this.actualWidth-1;
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
    	//if(end - start > 5)
    	//System.out.println(minValue);
    	return index;
    }
    public void printEnergy()
    {
    	for(int i=0;i<this.height;i++)
    	{
    		for(int j=0; j<this.actualWidth; j++)
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
    		for(int j=0; j<this.actualWidth; j++)
    		{
    			System.out.print("" + String.format("%3.1f", this.energySum[i*width+j]) + " ");
    		}
    		System.out.println(" ");
    	}
    }

    public void createNewImage()
    {
    	byte[] actualPixels = new byte[this.height*this.actualWidth*this.pixelLength];
    	float[] actualEnergy = new float[this.height*this.actualWidth];
    	for (int i = 0; i < this.height; i++)
    	{
    		System.arraycopy(this.pixels, i*this.width*this.pixelLength, actualPixels, i*this.actualWidth*this.pixelLength, (this.actualWidth*this.pixelLength));
    		System.arraycopy(this.energy, i*this.width, actualEnergy, i*this.actualWidth, this.actualWidth);
    	}
    	this.pixels = actualPixels;
    	this.energy = actualEnergy;
    	this.width = this.actualWidth;
    	
    	
    }
    public void createNewImage(int newWidth)
    {
    	this.actualWidth = this.width;
    	byte[] actualPixels = new byte[this.height*newWidth*this.pixelLength];
    	float[] actualEnergy = new float[this.height*newWidth];
    	for (int i = 0; i < this.height; i++) 
    	{
    		System.arraycopy(this.pixels, i*this.width*this.pixelLength, actualPixels, i*newWidth*this.pixelLength, (this.actualWidth*this.pixelLength));
    		System.arraycopy(this.energy, i*this.width, actualEnergy, i*newWidth, this.actualWidth);
    	}
    	this.pixels = actualPixels;
    	this.energy = actualEnergy;
    	this.width = newWidth;
    	energySum = new float[this.width*this.height];
    	
    	
    }

    public void add(int number)
    {
    	this.calculateImageEnergy();
    	createNewImage(this.width + number);
    	for(int i=0;i<number;i++)
    	{
    		this.updateEnergyDynamically();
    		this.addLine();
    	}
    }
    public void addLine()
    {
    	int[] lowestIndex = new int[this.height]; 
		lowestIndex[0] = findLowestEnergyInLine(0,0,this.actualWidth);
		
    	
		System.arraycopy(this.energy, lowestIndex[0], this.energy, lowestIndex[0]+1, this.actualWidth - lowestIndex[0]);
		System.arraycopy(this.pixels, 
						lowestIndex[0]* this.pixelLength, 
						this.pixels, 
						(lowestIndex[0]+1)* this.pixelLength, 
						(this.actualWidth - lowestIndex[0]) * this.pixelLength );
//    		this.pixels[this.width*0 + lowestIndex[0]*this.pixelLength] = 0;
//			this.pixels[this.width*0 + lowestIndex[0]*this.pixelLength+1] = 0;
//			this.pixels[this.width*0 + lowestIndex[0]*this.pixelLength+2] = 0;
    	
 
    	
    	for(int i=1; i<this.height;i++)
    	{
    		
    		lowestIndex[i] = findLowestEnergyInLine(i,lowestIndex[i-1]-1,lowestIndex[i-1]+1);
    		if(lowestIndex[i]<actualWidth-1)
        	{
    			System.arraycopy(this.energy, this.width*i+lowestIndex[i], this.energy, this.width*i+lowestIndex[i] + 1, this.actualWidth - lowestIndex[i]);
    			System.arraycopy(this.pixels, 
    							(this.width*i+lowestIndex[i])* this.pixelLength, 
    							this.pixels, 
    							(this.width*i+lowestIndex[i]+1)*this.pixelLength , 
    							(this.actualWidth - lowestIndex[i])*this.pixelLength);
//    			this.pixels[(this.width*i + lowestIndex[i])*this.pixelLength] = 0;
//    			this.pixels[(this.width*i + lowestIndex[i])*this.pixelLength+1] = 0;
//    			this.pixels[(this.width*i + lowestIndex[i])*this.pixelLength+2] = 0;
//    			
    		}
    	}

    	this.actualWidth++;
    	//System.out.println("lines:");
    	for(int i=0;i<this.height;i++)
    	{
    		/*for(int k=0; k<this.pixelLength;k++)
    		{
    			if(lowestIndex[i] == 0)
    				break;
    			int temp = (int)this.pixels[(this.width*i+lowestIndex[i])* this.pixelLength +k];
    			System.out.print("temp: " +temp);
	    		temp +=		(int)this.pixels[(this.width*i+lowestIndex[i]-1)* this.pixelLength +k];
	    		System.out.print("temp2: "  + temp);
	    		System.out.println("last: " +temp/2);
	    		this.pixels[(this.width*i+lowestIndex[i])* this.pixelLength +k] = (byte)(temp/2);
	    	}*/
    		if(isPixelInBounds(i, lowestIndex[i]))
    			energy[i * this.width + lowestIndex[i]] = calculatePixelGradient(i, lowestIndex[i]) + 50;
    		
    		if(isPixelInBounds(i, lowestIndex[i]-1))
    			energy[i * this.width + lowestIndex[i]-1] = calculatePixelGradient(i, lowestIndex[i]-1) +50;
    		if(isPixelInBounds(i, lowestIndex[i]+1))
    			energy[i * this.width + lowestIndex[i]+1] = calculatePixelGradient(i, lowestIndex[i]+1) +50;
    	//	System.out.print("" + lowestIndex[i]+ " ");
    	}
    	//System.out.println("");
    }
}