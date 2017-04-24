
//package SeamProject;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
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
	public enum Direction {
		LEFT(0),UP(1),Right(2);
		public int value;
		private Direction(int v)
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
    public float avgEnergy;
    public int gradientWeight = 1;
	public int entropyWeight = 1;
	public int energyType;
	

	public FastImage(BufferedImage image, int energyType)
    {
		this.energyType = energyType;
        pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        width = image.getWidth();
        height = image.getHeight();
        actualWidth = width;
        hasAlphaChannel = (image.getAlphaRaster() != null)?1:0;
        pixelLength = 3 + hasAlphaChannel;
      
        energy = new float[width*height];
        energySum = new float[width*height];
        
    }
	public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int extensionPos = filename.lastIndexOf('.');
        int lastUnixPos = filename.lastIndexOf('/');
        int lastWindowsPos = filename.lastIndexOf('\\');
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);

        int index = lastSeparator > extensionPos ? -1 : extensionPos;
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }
	public void save(String path) throws IOException {
		
		File outputfile = new File(path);
/*		 BufferedImage bufferedImage = new BufferedImage(this.width,
		 this.height, BufferedImage.TYPE_INT_RGB);
		 for (int i = 0; i < this.height; i++)
			 for (int j = 0; j < this.width; j++)
				 bufferedImage.setRGB( j, i, getRGB(i, j));
		 ImageIO.write(bufferedImage, "bmp", outputfile);*/
		
		BufferedImage img = new BufferedImage(this.width, this.height, BufferedImage.TYPE_3BYTE_BGR);
		img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(pixels, pixels.length), new Point()));
		ImageIO.write(img,  getExtension(path), outputfile);

	}
	

	public void calculateImageEnergy() {
		System.out.println("Starting energy calculation, please hold...");
    	
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) 
				this.energy[i * width + j] = calcEnergy(i,j);
		}
		
		float sum = 0;
		float max = 0;
		for(int k=0;k<this.energy.length;k++)
		{
			sum += this.energy[k];
			if(this.energy[k]>max)
				max = this.energy[k];
		}
		avgEnergy = 2*sum/this.energy.length;
		System.out.println("avg : " + avgEnergy + " max: " + max);
	}
	
	public float calcEnergy(int i, int j)
	{
		switch(this.energyType)
		{
			case 0:
				return ((float)calculatePixelGradient(i, j));	
			case 1:
				return ((float)(gradientWeight * calculatePixelGradient(i, j))+ (entropyWeight * calculatePixelEntropy(i, j))) / (gradientWeight + entropyWeight);
			case 2:
				return calculateForwardEnergy(i,j);
				// this.energy[i*this.width+j];
		}
		return 0;
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
	
	public float calculateGradientForTwoPixels(int x1, int y1, int x2, int y2)
	{
		float gradient = 0;
		gradient += Math.abs(getPixelColor(x1,y1,RGBcolor.RED) - getPixelColor(x2,y2,RGBcolor.RED));
		gradient += Math.abs(getPixelColor(x1,y1,RGBcolor.GREEN) - getPixelColor(x2,y2,RGBcolor.GREEN));
		gradient += Math.abs(getPixelColor(x1,y1,RGBcolor.BLUE) - getPixelColor(x1,y2,RGBcolor.BLUE));
		return gradient;
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
						entropy += funcP * Math.log(funcP+0.1);
				}
			}
		}

		return (-entropy);
	}
	
	public float calculateForwardEnergy(int i, int j)
	{

		float energy = (float)calculatePixelGradient(i, j);
	//	this.energy[i * this.width + j] = 
		if ((i > 0)&&(j < this.width-1)&&(j>0))
			energy += Math.min(
					Math.min(this.energy[(i-1)* this.width + j-1] + cost(Direction.LEFT,i,j	), 
					this.energy[(i-1)* this.width + j] + cost(Direction.UP,i,j				)), 
					this.energy[(i-1)* this.width + j+1] + cost(Direction.Right,i,j))/2;
		if(i>0 && j==0)// || j==this.width-1))
			energy += (this.energy[(i-1)* this.width +1] + 700)/2;
		
//		System.out.println(i + " " + j);
		if(i>0 && j== this.actualWidth-1)
			energy += (this.energy[(i-1)* this.width + this.actualWidth -2] + 700)/2;
					
		return energy;
	}
	
	public float cost(Direction direction, int i, int j)
	{
		switch (direction.value)
		{
			case 0 :
				if (isPixelInBounds(i, j+1) && isPixelInBounds(i, j-1) && isPixelInBounds(i-1, j))
					return (calculateGradientForTwoPixels(i, j+1, i, j-1) + calculateGradientForTwoPixels(i-1, j, i, j-1));
					//return (Math.abs(calculatePixelGradient(i, j+1) - calculatePixelGradient(i, j-1)) + Math.abs(calculatePixelGradient(i-1, j) - calculatePixelGradient(i, j-1)));	
				break;
			case 1:
				if (isPixelInBounds(i, j+1) && (isPixelInBounds(i, j-1)))
					return 2*(calculateGradientForTwoPixels(i, j+1, i, j-1));	
				break;
			case 2:
				if (isPixelInBounds(i, j+1) && (isPixelInBounds(i, j-1)) && (isPixelInBounds(i-1, j)))
					return (calculateGradientForTwoPixels(i, j+1, i, j-1) + calculateGradientForTwoPixels(i-1, j, i, j-1));	
				break;
		}
		
		return 0;
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
    	this.calculateImageEnergy();
    	System.out.println("start substructing " + seams + " seams");
    	//this.printEnergy();
    	
    	for(int i=0;i<seams;i++)
    	{
    		this.updateEnergyDynamically();
    		this.substructLine();
    		//System.out.println("after " + i + "substruct:");
        	//this.printEnergy();
    	}
    	createNewImage();
    	//System.out.println("");
    	//System.out.println("after substruct:");
    	//this.printEnergy();
    }
    
    public void substructLine()
    {	
    	int[] lowestIndex = new int[this.height]; 
		lowestIndex[0] = findLowestEnergyInLine(0,0,this.actualWidth);
		
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
    			energy[i * width + lowestIndex[i]] = calcEnergy(i, lowestIndex[i]) ;//this.avgEnergy;
    		
    		if(isPixelInBounds(i, lowestIndex[i]-1))
    			energy[i * width + lowestIndex[i]-1] = calcEnergy(i, lowestIndex[i]-1) ;// this.avgEnergy;
    		
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

    public void add(int seams)
    {
    	this.calculateImageEnergy();
    	System.out.println("start addition " + seams + " seams");
    	createNewImage(this.width + seams);
    	for(int i=0;i<seams;i++)
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
    		for(int k=0; k<this.pixelLength;k++)
    		{
    			if(lowestIndex[i] == 0)	
    				break;
    			int temp = (int)(this.pixels[(this.width*i+lowestIndex[i])* this.pixelLength +k]&0xff);
	    		temp +=		(int)(this.pixels[(this.width*i+lowestIndex[i]-1)* this.pixelLength +k]&0xff);
	    		
	    		this.pixels[(this.width*i+lowestIndex[i])* this.pixelLength +k] = (byte)(temp/2);
	    	}
    		if(isPixelInBounds(i, lowestIndex[i]))
    			energy[i * this.width + lowestIndex[i]] = calcEnergy(i, lowestIndex[i])  + avgEnergy/4;
    		if(isPixelInBounds(i, lowestIndex[i]-1))
    			energy[i * this.width + lowestIndex[i]-1] = calcEnergy(i, lowestIndex[i]-1)  + avgEnergy/2;
    		if(isPixelInBounds(i, lowestIndex[i]+1))
    			energy[i * this.width + lowestIndex[i]+1] = calcEnergy(i, lowestIndex[i]+1)  + avgEnergy;
    	//	System.out.print("" + lowestIndex[i]+ " ");
    	}
    	//System.out.println("");
    }
}