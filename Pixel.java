
public class Pixel {
	int x,y;
	byte red,green,blue;
	double energy;
	public Pixel(int x, int y, int rgb)
	{
		this.x = x;
		this.y = y;
		
		this.red =   (byte) ((rgb >> 16) & 0xFF);
		this.green = (byte) ((rgb >>  8) & 0xFF);
		this.blue =  (byte) ((rgb      ) & 0xFF);
		this.energy = 0;
	}
}
