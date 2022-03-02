package isogame;

import java.util.ArrayList;

public class Entity {
	float x,y;
	float width, height;
	ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public Entity(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
	}
	public void draw() {
		
	}
}
