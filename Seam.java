import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Seam {
	
	public static void main(String[] args)
	{
		try 
		{
			BufferedImage image = ImageIO.read(new File(args[0]));

			int columsSeams = Integer.parseInt(args[1]);
			int rowsSeams = Integer.parseInt(args[2]);
			int energyType = Integer.parseInt(args[3]);
			FastImage fastSeamImage = new FastImage(image, energyType);

			
			if(columsSeams != fastSeamImage.width)
			{
				if (columsSeams < fastSeamImage.width)
						fastSeamImage.substruct(fastSeamImage.actualWidth - columsSeams);
				if (columsSeams > fastSeamImage.width)
					fastSeamImage.add(columsSeams - fastSeamImage.actualWidth);
			}
//			BufferedImage bufferedImage = new BufferedImage(fastSeamImage.width,fastSeamImage.height, BufferedImage.TYPE_INT_RGB);
//			for (int i = 0; i < fastSeamImage.height; i++)
//				for (int j = 0; j < fastSeamImage.width; j++)
//					bufferedImage.setRGB( j, i, fastSeamImage.getRGB(i, j));
			if(rowsSeams != fastSeamImage.height)
			{
				System.out.println("rotate...");
				
				BufferedImage bufferedImage = new BufferedImage(fastSeamImage.width, fastSeamImage.height, BufferedImage.TYPE_3BYTE_BGR);
				bufferedImage.setData(Raster.createRaster(bufferedImage.getSampleModel(), new DataBufferByte(fastSeamImage.pixels, fastSeamImage.pixels.length), new Point()));
			       
				AffineTransform tx = new AffineTransform();//.getScaleInstance(1, 1);
				tx.translate(fastSeamImage.height / 2,fastSeamImage.width / 2);
				tx.rotate(Math.PI / 2);
				tx.translate(-fastSeamImage.width / 2,-fastSeamImage.height / 2);
				AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				bufferedImage = op.filter(bufferedImage, null);
				
				
				fastSeamImage = new FastImage(bufferedImage,energyType);
				
				if (rowsSeams < fastSeamImage.width)
					fastSeamImage.substruct(fastSeamImage.width - rowsSeams );
				if (rowsSeams > fastSeamImage.width)
					fastSeamImage.add(rowsSeams - fastSeamImage.width);
				
				System.out.println("rotate back...");
				
				bufferedImage = new BufferedImage(fastSeamImage.width, fastSeamImage.height, BufferedImage.TYPE_3BYTE_BGR);
				bufferedImage.setData(Raster.createRaster(bufferedImage.getSampleModel(), new DataBufferByte(fastSeamImage.pixels, fastSeamImage.pixels.length), new Point()));
			    
				tx = new AffineTransform();//.getScaleInstance(1, 1);
				tx.translate(fastSeamImage.height / 2,fastSeamImage.width / 2);
				tx.rotate(3*Math.PI  / 2);
				
				tx.translate(-fastSeamImage.width / 2,-fastSeamImage.height / 2);
				op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
				bufferedImage = op.filter(bufferedImage, null);
				fastSeamImage = new FastImage(bufferedImage,energyType);
				//fix problem in the first line: 
				System.arraycopy(fastSeamImage.pixels, fastSeamImage.width*2*fastSeamImage.pixelLength, fastSeamImage.pixels, 0, fastSeamImage.width*fastSeamImage.pixelLength);
				
			}
				
			
			fastSeamImage.save(args[4]);
			System.out.println("end");
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	     
	}
}
