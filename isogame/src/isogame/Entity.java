package isogame;
public abstract class Entity {
	private float x,y;
	private float width, height;
	private int mapX, mapY;
	public Entity(float x, float y, float width, float height, int mapX, int mapY) {
		this.setX(x);
		this.setY(y);
		this.height = height;
		this.width = width;
		this.mapX = mapX;
		this.mapY = mapY;
	}
	public void draw() {
		
	}
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public int getMapX() {
		return mapX;
	}
	public void setMapX(int mapX) {
		this.mapX = mapX;
	}
	public int getMapY() {
		return mapY;
	}
	public void setMapY(int mapY) {
		this.mapY = mapY;
	}
}
