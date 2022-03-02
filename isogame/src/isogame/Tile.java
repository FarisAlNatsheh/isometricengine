package isogame;

public class Tile {
	private float x,y;
	private float width,height;
	private boolean hasDepth;
	private Texture texture;
	private int type;
	public Tile(float x, float y, float width, float height, boolean hasDepth, Texture texture, int type) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.hasDepth = hasDepth;
		this.texture = texture;
		this.type = type;
	}
	public void draw() {
		if(!hasDepth)
			Main.fillDiamond(x, y, width, height,texture);
		else
			Main.fillBlock(x,y,width,height,height,texture);
	}
	public int getType() {
		return type;
	}
	public void setType(int n) {
		type = n;
	}
}
