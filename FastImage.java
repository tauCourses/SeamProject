
//package SeamProject;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class FastImage {

	public int width;
	public int height;
	public boolean hasAlphaChannel;
	public int pixelLength;
	public byte[] pixels;
	public float[] energy;

	public FastImage(BufferedImage image) {

		pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		width = image.getWidth();
		height = image.getHeight();
		hasAlphaChannel = image.getAlphaRaster() != null;
		pixelLength = 3;
		if (hasAlphaChannel) {
			pixelLength = 4;
		}
		energy = new float[width * height];

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
		int entropyWeight = 1;
		for (int i = 0; i < this.height; i++) {
			for (int j = 0; j < this.width; j++) {
				energy[i * width + j] = (gradientWeight * calculatePixelGradient(i, j)
						+ entropyWeight * calculatePixelEntropy(i, j)) / (gradientWeight + entropyWeight);// weight
																											// is
																											// equal
																											// if
																											// (1*gradient+1*entropy)/2
			}
		}
	}

	public float calculatePixelGradient(int x, int y) {
		int gradient = 0;
		int numOfNeighbors = 0;
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				if (isPixelInBounds(x + i, y + j) & (!(i == 0 & j == 0))) // if
																			// neighbor
																			// not
																			// out
																			// of
																			// bounds
																			// and
																			// not
																			// pixels[x][y]
																			// itself
				{
					if ((x == 0) && (y == 2))
						System.out.println("this neighbor: "+(x+i)+" "+(y+j));
					numOfNeighbors++;
					gradient += Math.abs(getRed(x, y) - getRed(x + i, y + j));
					gradient += Math.abs(getGreen(x, y) - getGreen(x + i, y + j));
					gradient += Math.abs(getBlue(x, y) - getBlue(x + i, y + j));
				}
			}
		}
		System.out.println("This is pixel "+x+", "+y+" and his numOfNeighbors: "+numOfNeighbors+" and his RGB: "+getRGB(x, y));
		return ((float) gradient) / numOfNeighbors;

	}

	public boolean isPixelInBounds(int i, int j) {
		return (i >= 0) & (i < this.height) & (j >= 0) & (j < this.width);
	}

	public float calculatePixelEntropy(int x, int y) {
		float entropy = 0;
		int grayScaleSum = 0;
		for (int i = -4; i < 5; i++) {
			for (int j = -4; j < 5; j++) {
				if (isPixelInBounds(x + i, y + j) & (!(i == 0 & j == 0))) // if
																			// neighbor
																			// not
																			// out
																			// of
																			// bounds
																			// and
																			// not
																			// pixels[x][y]
																			// itself
				{
					grayScaleSum += getGrayScale(x + i, y + j);
				}
			}
		}
		float funcP;
		for (int i = -4; i < 5; i++) {
			for (int j = -4; j < 5; j++) {
				if (isPixelInBounds(x + i, y + j) & (!(i == 0 & j == 0))) // if
																			// neighbor
																			// not
																			// out
																			// of
																			// bounds
																			// and
																			// not
																			// pixels[x][y]
																			// itself
				{
					funcP = ((float)getGrayScale(x + i, y + j) /grayScaleSum);
					System.out.println("This is pixel "+(x+i)+", "+(y+j)+" and his grayscale: "+getGrayScale(x+i, y+j)+" and sum: "+grayScaleSum);
					if (funcP != 0)
						entropy += funcP * Math.log(funcP);
				}
			}
		}

		return (-entropy);
	}

	// getters
	public int getRGB(int x, int y) {
		int pos = (x * pixelLength * width) + (y * pixelLength);
		int argb = -16777216; // 255 alpha
		if (hasAlphaChannel) {
			argb = (((int) pixels[pos++] & 0xff) << 24); // alpha
		}

		argb += ((int) pixels[pos++] & 0xff); // blue
		argb += (((int) pixels[pos++] & 0xff) << 8); // green
		argb += (((int) pixels[pos++] & 0xff) << 16); // red
		return argb;
	}

	public int getRed(int x, int y) {
		int pos = (x * pixelLength * width) + (y * pixelLength);
		if (hasAlphaChannel) {
			pos++;
		}
		return (pixels[pos + 2] & 0xff);
	}

	public int getGreen(int x, int y) {
		int pos = (x * pixelLength * width) + (y * pixelLength);
		if (hasAlphaChannel) {
			pos++;
		}
		return (pixels[pos + 1] & 0xff);
	}

	public int getBlue(int x, int y) {
		int pos = (x * pixelLength * width) + (y * pixelLength);
		if (hasAlphaChannel) {
			pos++;
		}
		return (pixels[pos] & 0xff);
	}

	public int getGrayScale(int x, int y) {
		int pos = (x * pixelLength * width) + (y * pixelLength);

		int argb = 0;
		if (hasAlphaChannel) {
			pos++; // alpha
		}

		argb += ((int) pixels[pos++] & 0xff); // blue
		argb += ((int) pixels[pos++] & 0xff); // green
		argb += ((int) pixels[pos] & 0xff); // red
		return argb / 3;
	}

	// setters
	public void setRed(int x, int y, int newRed) {
		int pos = (x * pixelLength * width) + (y * pixelLength);
		if (hasAlphaChannel) {
			pos++;
		}
		pixels[pos + 2] = (byte) newRed;
	}

	public void setGreen(int x, int y, int newGreen) {
		int pos = (x * pixelLength * width) + (y * pixelLength);
		if (hasAlphaChannel) {
			pos++;
		}
		pixels[pos + 1] = (byte) newGreen;
	}

	public void setBlue(int x, int y, int newBlue) {
		int pos = (x * pixelLength * width) + (y * pixelLength);
		if (this.hasAlphaChannel) {
			pos++;
		}
		this.pixels[pos] = (byte) newBlue;
	}

	public void updateEnergyDynamically() {
		for (int i = this.height - 2; i >= 0; i--) {
			this.energy[i * this.width] += Math.min(this.energy[(i + 1) * this.width],
					this.energy[(i + 1) * this.width] + 1);
			for (int j = 1; j < this.width - 1; j++)
				this.energy[i * this.width + j] += Math.min(this.energy[(i + 1) * this.width + j - 1],
						Math.min(this.energy[(i + 1) * this.width + j], this.energy[(i + 1) * this.width] + j + 1));

			this.energy[i * this.width] += Math.min(this.energy[(i + 1) * this.width],
					this.energy[(i + 1) * this.width] + 1);
		}
	}

}