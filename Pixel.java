
public class Pixel {
	public int x,y;
	public int red,green,blue, alpha;
	public double energy;
	public Pixel(int x, int y, int rgb)
	{
		this.x = x;
		this.y = y;
		
		this.alpha = ((rgb >> 24) & 0xFF);
		this.green =   ((rgb >> 16) & 0xFF);
		this.blue = ((rgb >>  8) & 0xFF);
		this.red =  ((rgb      ) & 0xFF);
		this.energy = 0;
	}
}
