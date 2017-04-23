import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
			FastImage fastSeamImage = new FastImage(image);
			/*
			if (columsSeams < fastSeamImage.actualWidth)
					fastSeamImage.substruct(fastSeamImage.actualWidth - columsSeams, energyType);
			if (columsSeams > fastSeamImage.actualWidth)
				fastSeamImage.add(columsSeams - fastSeamImage.actualWidth, energyType);
			*/
			fastSeamImage.createNewImage();
			BufferedImage bufferedImage = new BufferedImage(fastSeamImage.width,fastSeamImage.height, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < fastSeamImage.height; i++)
				for (int j = 0; j < fastSeamImage.width; j++)
					bufferedImage.setRGB( j, i, fastSeamImage.getRGB(i, j));
			       
			AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
			tx.translate(0, -bufferedImage.getHeight(null));
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			bufferedImage = op.filter(bufferedImage, null);
			
			fastSeamImage = new FastImage(bufferedImage);
			/*
			if (rowsSeams < fastSeamImage.actualWidth)
				fastSeamImage.substruct(fastSeamImage.actualWidth - columsSeams, energyType );
			if (rowsSeams > fastSeamImage.actualWidth)
				fastSeamImage.add(columsSeams - fastSeamImage.actualWidth, energyType);
			*/
//			fastSeamImage.substruct(150);
//			System.out.println("end");
			fastSeamImage.createNewImage();
			fastSeamImage.save(args[4]);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	     
	}
}
