package isogame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Enemy extends Entity{

	static Texture[][] texture;
	private Pathfinder pathfinder;
	private float offsetX, offsetY, speed = 0.007f;
	private int[][] path;
	private int mode;
	boolean right;
	double anim;
	public Enemy(float width, float height, int mapX, int mapY) {
		super(0, 0, width, height, mapX, mapY);
		pathfind();
		
	}
	public static void initializeTextures() {
		int scale = 200;
		BufferedImage sheet = null;
		try {
			sheet = ImageIO.read(new File("Cyborg1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int sheetHeight = sheet.getHeight(null)/200;
		int sheetWidth = sheet.getWidth(null)/scale;
		texture = new Texture[sheetWidth][sheetHeight];
		for(int i = 0; i < sheetWidth; i++) {
			for(int i2 = 0; i2 < sheetHeight; i2++) {
				texture[i][i2] = new Texture(sheet.getSubimage(i * scale,  i2 * 200, scale,200 ));
			}
		}



	}
	public void pathfind() {

		Thread t = new Thread() {
			public void run() {
				pathfinder = new Pathfinder(Main.mapE,getMapX(),getMapY(),Main.charYMap, Main.charXMap);
				pathfinder.run();
				path = pathfinder.getPath();
			}
		};
		//t.setDaemon(true);
		t.start();

	}
	public void draw() {
		Main.drawEnemy(getX(), getY(), getWidth(), getHeight(),  texture[(int)anim][mode], right);
		//Main.drawChar(0-Main.tileWidth/8-Main.camX,0+Main.tileHeight/2-Main.camY,Main.tileWidth/4,Main.tileHeight, Main.charTextures[(int)Main.animateChar][Main.charMode]);

	}
	public void move() {
		if(mode == 10)
			animate(0.2,6);
		else if(mode == 7)
			animate(0.06,4);
		//pathfind();

		//		System.out.println(path[getMapY()-1][getMapX()] );
		//		System.out.println(path[getMapY()+1][getMapX()] );
		//		System.out.println(path[getMapY()][getMapX()-2] );
		//		System.out.println(path[getMapY()][getMapX()-1] );

	
		//		System.out.println();
		if(path[getMapX()][getMapY()+1] == 5) {
			if(Math.sqrt(getOffsetX()*getOffsetX()+getOffsetY()*getOffsetY()) <= Main.tileSide) {
				moveY(1);
			}
			else {
				offsetX=0;
				offsetY=0;
				setMapY(getMapY()+1);
			}
			right = true;
			mode = 10;
		}	
		else if(path[getMapX()][getMapY()-1] == 5){
			if(Math.sqrt(getOffsetX()*getOffsetX()+getOffsetY()*getOffsetY()) <= Main.tileSide) {
				moveY(-1);
			}
			else {
				offsetX=0;
				offsetY=0;
				setMapY(getMapY()-1);
			}
			right = false;
			mode = 10;
		}	
		else if(path[getMapX()+1][getMapY()] == 5){
			if(Math.sqrt(getOffsetX()*getOffsetX()+getOffsetY()*getOffsetY()) <= Main.tileSide) {
				moveX(1);
			}
			else {
				offsetX=0;
				offsetY=0;
				setMapX(getMapX()+1);
			}
			right = true;
			mode = 10;
		}	
		else if(path[getMapX()-1][getMapY()] == 5){
			if(Math.sqrt(getOffsetX()*getOffsetX()+getOffsetY()*getOffsetY()) <= Main.tileSide) {
				moveX(-1);
			}
			else {
				offsetX=0;
				offsetY=0;
				setMapX(getMapX()-1);
			}
			right = false;
			mode = 10;
		}
		else {
			mode = 7;
			//System.out.println(123);
		}
		//System.out.println(Main.mouseMapX + " , "+ Main.mouseMapY);
		//System.out.println(getMapX() + " "+ getMapY());

		//System.out.println(Arrays.deepToString(path).replace("], ", "]\n").replace("[[", "[").replace("]]","]"));
		//		for(int i =0; i < path.length; i++) {
		//			for(int j =0; j < path[0].length; j++) {
		//				System.out.print(path[j][i]+" ");
		//			}
		//			System.out.println();
		//		}
		//			System.out.println();

	}
	public void moveX(int dir) {
		if(dir > 0) {
				setOffsetX(getOffsetX()+speed);
				setOffsetY(getOffsetY()+speed/2);
			
		}
		else {
			setOffsetX(getOffsetX()-speed);
			setOffsetY(getOffsetY()-speed/2);
		}
	}
	public void moveY(int dir) {
		if(dir > 0) {
			setOffsetX(getOffsetX()+speed);
			setOffsetY(getOffsetY()-speed/2);
		}
		else {
			setOffsetX(getOffsetX()-speed);
			setOffsetY(getOffsetY()+speed/2);
		}
	}
	public void animate(double animSpeed, double animLength) {
		if(anim < animLength-animSpeed)
			anim+=animSpeed;
		else
			anim = 0;
	}
	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
	}
	public float getOffsetY() {
		return offsetY;
	}
	public float getOffsetX() {
		return offsetX;
	}
}
