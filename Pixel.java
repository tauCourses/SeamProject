import java.awt.Color;

public class Pixel {
	public int x,y;
	public int red,green,blue;
	public double energy;
	public Color c;
	public Pixel(int x, int y, int rgb)
	{
		this.x = x;
		this.y = y;
		c = new Color(rgb);
		this.green =   c.getGreen();
		this.blue = c.getBlue();
		this.red =  c.getRed();
		c = new Color(this.red,this.green,this.blue);

		this.energy = 0;
	}
}
