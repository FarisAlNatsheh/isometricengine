package isogame;
//Use glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

public class Main extends GLFW{
	public final static int WINDOW_HEIGHT = 720;
	public final static int WINDOW_WIDTH = 1280;
	public final static int MAP_SIZE = 70;
	static int gridWidth = 50;
	static int gridHeight = 50;
	static float tileWidth = 0.2f, tileHeight = 0.1f;
	volatile static int[][] mapE= new int[MAP_SIZE][MAP_SIZE];
	static int[][] mapSol= new int[MAP_SIZE][MAP_SIZE];
	static int[][] map= new int[MAP_SIZE][MAP_SIZE];
	static int mapX = 0, mapY = -9;
	static float camX = 0, camY = 0;
	static float speed = 0.009f;
	static double FPS = 60000.0;
	static double targetFPMS = 1000000000/FPS;
	static double TPS = 60.0;
	static float mouseX, mouseY;
	static int mouseMapX=0, mouseMapY=0;
	static int charXMap=0, charYMap=0;
	static double animateChar;
	static Texture grass,house,dirt, cursor, blue, out;
	static Texture[][] charTextures;
	static boolean up, down, left, right;
	static int charMode;
	static float charX, charY, charCamX, charCamY;
	static float charHitbox = 0.001f;
	static boolean topLeft, topRight, bottomLeft, bottomRight;
	static Pathfinder[] pathfinder = new Pathfinder[6];
	static int desX=6, desY=6;

	public static float distance(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2));
	}
	public static int rand(int min, int max) {
		return (int) Math.floor(Math.random()*(max-min+1)+min);
	}
	public static <T> void mirror(T[][] arr) {
		for(int i = 0; i < arr.length; i++) {
			for(int j = 0; j < arr[i].length/2; j++) {
				T temp = arr[i][j];
				arr[i][j] = arr[i][arr[i].length-1-j];
				arr[i][arr[i].length-1-j] = temp;
			}
		}
	}
	public static void initializeTextures() {
		int scale = 200;
		BufferedImage sheet = null;
		try {
			sheet = ImageIO.read(new File("Punk1.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int sheetHeight = sheet.getHeight(null)/200;
		int sheetWidth = sheet.getWidth(null)/scale;
		charTextures = new Texture[sheetWidth][sheetHeight];
		for(int i = 0; i < sheetWidth; i++) {
			for(int i2 = 0; i2 < sheetHeight; i2++) {
				charTextures[i][i2] = new Texture(sheet.getSubimage(i * scale,  i2 * 200, scale,200 ));
			}
		}



	}
	public static void animate(double animSpeed, double animLength) {
		if(animateChar < animLength-animSpeed)
			animateChar+=animSpeed;
		else
			animateChar = 0;
	}
	public static void pathfind() {

		Thread t = new Thread() {
			public void run() {
				pathfinder[0] = new Pathfinder(mapE,5,5,charYMap, charXMap);
				pathfinder[0].run();
				mapSol = pathfinder[0].getPath();
			}
		};
		t.setDaemon(true);
		t.start();

	}
	public static void createWindow() {
		@SuppressWarnings("unused")
		GLFWCursorPosCallback cursorInput;
		@SuppressWarnings("unused")
		GLFWScrollCallback scrollInput;
		double start = System.nanoTime();
		System.out.println("Intializing window");
		glfwInit();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		GLFWVidMode monitor = glfwGetVideoMode(glfwGetPrimaryMonitor());
		long window = glfwCreateWindow(WINDOW_WIDTH,WINDOW_HEIGHT, "Isometric game",0,0);
		glfwSetWindowPos(window, monitor.width()/2-WINDOW_WIDTH/2,monitor.height()/2-WINDOW_HEIGHT/2);
		glfwShowWindow(window);
		System.out.println("Setting up graphics");
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
		glClearColor(0,255,255,0);
		System.out.println("Loading textures");
		grass = new Texture("Floor_Lower_1.png");
		dirt = new Texture("dirt.png");
		house = new Texture("Amp 9.png");//housee
		cursor = new Texture("Cursor1.png");
		blue = new Texture("Blue 2.png");
		out = new Texture("out.png");
		for(int i =0; i < MAP_SIZE; i+=1) {
			for(int j =0; j < MAP_SIZE; j+=1) {
				//mapE[i][j] = rand(0,1);
			}
		}
		for(int i =0; i < MAP_SIZE; i+=1) {
			mapE[i][0] = 1;
			mapE[0][i] = 1;
			mapE[i][MAP_SIZE-1] = 1;
			mapE[MAP_SIZE-1][i] = 1;
		}
		initializeTextures();
		System.out.println("Done!\n"+ (System.nanoTime()-start)*0.000001+" ms");

		double timeFPS = System.nanoTime();
		double currentFPS = (System.nanoTime()-timeFPS);
		double timeTPS = System.nanoTime();
		double currentTPS = (System.nanoTime()-timeTPS);
		int oldStateLeft = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1);
		@SuppressWarnings("unused")
		int oldStateRight = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2);
		@SuppressWarnings("unused")
		double oldStateScroll = 0;

		while(!glfwWindowShouldClose(window)) {
			currentFPS = (System.nanoTime()-timeFPS);
			currentTPS = (System.nanoTime()-timeTPS);		
			//Game loop
			if(currentTPS >= 1000000000/TPS) {




				map = new int[MAP_SIZE][MAP_SIZE];
				//				int mapDiffX = charXMap-desX;
				//				int mapDiffY = charYMap-desY;

				//				for(int i = 0; i < mapSol.length; i++) {
				//					for(int j = 0; j < mapSol[0].length; j++) {
				//						if(mapSol[i][j] == 5)
				//							if(mapDiffY > 0)
				//								if(mapDiffX > 0)
				//									map[i+desX][j+desY]= 1;
				//								else
				//									map[i+desX+mapDiffX][j+desY]= 1;
				//							else								
				//								if(mapDiffX > 0)
				//									map[i+desX][j+desY+mapDiffY]= 1;
				//								else
				//									map[i+desX+mapDiffX][j+desY+mapDiffY]= 1;
				//
				//					}
				//				}
				for(int i = 0; i < mapSol.length; i++) {
					for(int j = 0; j < mapSol[0].length; j++) {
						if(mapSol[i][j] == 5)
							map[i][j] = 1;

					}
				}
				glfwSetCursorPosCallback(window, cursorInput = new MouseInput());
				glfwSetScrollCallback(window, scrollInput = new Scroll());
				if(glfwGetKey(window, GLFW_KEY_W ) == GL_TRUE ||
						glfwGetKey(window, GLFW_KEY_A ) == GL_TRUE ||
						glfwGetKey(window, GLFW_KEY_S ) == GL_TRUE ||
						glfwGetKey(window, GLFW_KEY_D) == GL_TRUE) {
					charMode = 10;
					animate(0.2,6);	
				}
				else {
					//standing anim
					charMode = 7;
					animate(0.06,4);
				}
				if(glfwGetKey(window, GLFW_KEY_D) == GL_TRUE  &&
						!(mapE[charXMap+1][charYMap] == 1 && topRight)&& 
						!(mapE[charXMap][charYMap+1] == 1 && bottomRight)) {
					glTranslatef(-speed,0,0);
					camX-=speed;
					right = true;
					left = false;
					up = false;
					down = false;
				}
				if(glfwGetKey(window, GLFW_KEY_A ) == GL_TRUE &&
						!(mapE[charXMap][charYMap-1] == 1 &&
						topLeft) &&
						!(mapE[charXMap-1][charYMap] == 1 && bottomLeft)
						) {
					glTranslatef(speed,0,0);
					camX+=speed;
					left = true;
					right = false;
					up = false;
					down = false;
				}
				if(glfwGetKey(window, GLFW_KEY_W) == GL_TRUE 
						&& !(mapE[charXMap][charYMap-1] == 1 && topLeft) 
						&& !(mapE[charXMap+1][charYMap] == 1 && topRight)) {
					glTranslatef(0,-speed,0);
					camY-=speed;
					up = true;
					down = false;
					left = false;
					right = false;
				}
				if(glfwGetKey(window, GLFW_KEY_S) == GL_TRUE &&
						!(mapE[charXMap-1][charYMap] == 1 && bottomLeft)&& !(mapE[charXMap][charYMap+1] == 1 && bottomRight)) {
					glTranslatef(0,speed,0);
					camY+=speed;
					down = true;
					up = false;
					left = false;
					right = false;

				}

				if(glfwGetKey(window, GLFW_KEY_W ) == GL_TRUE &&
						glfwGetKey(window, GLFW_KEY_A ) == GL_TRUE && !(mapE[charXMap][charYMap-1] == 1 && topLeft)) {
					up = true;
					left = true;
					down = false;
					right = false;
				}
				if(glfwGetKey(window, GLFW_KEY_W ) == GL_TRUE &&
						glfwGetKey(window, GLFW_KEY_D ) == GL_TRUE &&
						!(mapE[charXMap+1][charYMap] == 1 && topRight)) {

					up = true;
					right = true;
					down = false;
					left = false;
				}
				if(glfwGetKey(window, GLFW_KEY_S) == GL_TRUE &&
						glfwGetKey(window, GLFW_KEY_A ) == GL_TRUE&&
						!(mapE[charXMap-1][charYMap] == 1 && bottomLeft)) {

					down = true;
					left = true;
					up = false;
					right = false;
				}
				if(glfwGetKey(window, GLFW_KEY_S) == GL_TRUE &&
						glfwGetKey(window, GLFW_KEY_D ) == GL_TRUE && !(mapE[charXMap][charYMap+1] == 1 && bottomRight)) {

					down = true;
					right = true;
					up = false;
					left = false;
				}


				if(glfwGetKey(window, GLFW_KEY_EQUAL) == GL_TRUE)  {
					tileWidth+= 0.001;
					tileHeight+= 0.0005;
				}
				if(glfwGetKey(window, GLFW_KEY_MINUS) == GL_TRUE)  {
					if(tileWidth > 0.3) {
						tileWidth-= 0.001;
						tileHeight-= 0.0005;
					}
				}
				if(mouseMapX >= 0  && mouseMapY >= 0 && mouseMapX <= MAP_SIZE && mouseMapY <= MAP_SIZE) {
					if(glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GL_TRUE && oldStateLeft != 1) {
						mapE[mouseMapX][mouseMapY]=1;
					}
					if(glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2) == GL_TRUE && oldStateRight != 1) {
						map[mouseMapX][mouseMapY]=1;
					}
				}
				if(glfwGetKey(window, GLFW_KEY_SPACE) == GL_TRUE) {
					//speed += 0.00025f;
				}

				//glfwWaitEventsTimeout(0.7);
				if(camX < -tileWidth) {
					glTranslatef(-camX,0,0);
					camX = 0;
					mapX++;
					mapY++;
					pathfind();
				}
				if(camX > tileWidth) {
					glTranslatef(-camX,0,0);
					camX = 0;
					mapX--;
					mapY--;
					pathfind();
				}

				if(camY < -tileHeight) {
					glTranslatef(0,-camY,0);
					camY = 0;
					mapX++;
					mapY--;
					pathfind();
				}

				if(camY > tileHeight) {
					glTranslatef(0,-camY,0);
					camY = 0;
					mapY++;
					mapX--;
					for(int i = 0; i < 10; i++) {
						pathfind();
					}
				}

				mouseX = (float)(MouseInput.x/WINDOW_WIDTH)*2-1-camX;
				mouseY = (float)(MouseInput.y/WINDOW_HEIGHT)*-2+1-camY;

				charX = 0-camX;
				charY = -tileHeight/2-camY;
				//System.out.println("X: "+ charXMap+"\tY: "+charYMap);
				oldStateLeft = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1);
				oldStateRight = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2);
				oldStateScroll =Scroll.scrollVal;
				timeTPS = System.nanoTime();

			}




			//Graphics loop
			if(currentFPS >= targetFPMS) {
				timeFPS = System.nanoTime();
				render();
				glfwSwapBuffers(window);
			}
		}	


		glfwTerminate();
	}
	public static void render() {
		glColor3f(255,255,255);
		glfwPollEvents();
		glClear(GL_COLOR_BUFFER_BIT);
		//Drawing tiles
		for(int i =0; i < gridHeight; i++) {
			for(int j =gridWidth; j >0; j--) {
				float x=(-(gridWidth*tileWidth)/2+tileWidth/2) +i*tileWidth/2+j*tileWidth/2;
				float y=tileHeight/2-i*tileHeight/2+j*tileHeight/2;
				if(mapX+j <= MAP_SIZE-1 && mapY+i <= MAP_SIZE-1 && mapX+j >= 0 && mapY+i >= 0) {

					if(map[mapX+j][mapY+i] == 0) {
						fillBlock(x,y,tileWidth,tileHeight,tileHeight,out);
						//fillDiamond(x,y,tileWidth,tileHeight,grass);
					}
					else if(map[mapX+j][mapY+i] == 1) {
						//fillBlock(x,y,tileWidth,tileHeight,tileHeight,out);
						fillDiamond(x,y,tileWidth,tileHeight,grass);
					}

				}
				//else
				//fillBlock(x,y,tileWidth,tileHeight,tileHeight,out);
			}
		}

		//Drawing mouse

		for(int i =0; i < gridHeight; i++) {
			for(int j =gridWidth; j > 0; j--) {
				float x1=(-(gridWidth*tileWidth)/2+tileWidth/2) +i*tileWidth/2+j*tileWidth/2;
				float y1=tileHeight/2-i*tileHeight/2+j*tileHeight/2-tileHeight;

				if(mapX+j <= MAP_SIZE-1 && mapY+i <= MAP_SIZE-1 && mapX+j >= 0 && mapY+i >= 0) 
					if(mouseY > y1 && mouseY< y1+tileHeight) {
						if(mouseX > x1 -tileWidth/2 && mouseX< x1+tileWidth/2) {
							mouseMapX = mapX+j;
							mouseMapY = mapY+i;
							if(isInside(x1,y1+tileHeight, x1+tileWidth/2,y1+tileHeight, x1+tileWidth/2,y1+tileHeight-tileHeight/2,mouseX, mouseY)) {
								x1+=tileWidth/2;
								y1+= tileHeight/2; //top right
								mouseMapX += 1;
							}
							else if(isInside(x1-tileWidth/2,y1+tileHeight, x1,y1+tileHeight, x1-tileWidth/2,y1+tileHeight-tileHeight/2,mouseX, mouseY)) {
								x1-=tileWidth/2;
								y1+= tileHeight/2; //top left
								mouseMapY -= 1;
							}
							else if(isInside(x1,y1,x1-tileWidth/2,y1,x1-tileWidth/2,y1+tileHeight/2,mouseX,mouseY)) {
								x1-=tileWidth/2;
								y1-= tileHeight/2; //bottom left
								mouseMapX -= 1;

							}
							else if(isInside(x1,y1,x1+tileWidth/2,y1,x1+tileWidth/2,y1+tileHeight/2,mouseX,mouseY)) {
								x1+=tileWidth/2;
								y1-= tileHeight/2; //bottom right
								mouseMapY += 1;
							}
							glColor3f(255,0,0); 
							if(mouseMapY >= 0 && mouseMapY <= MAP_SIZE && mouseMapX >=0 && mouseMapX <= MAP_SIZE)
								drawDiamond(x1,y1+tileHeight,tileWidth,tileHeight);
							glColor3f(255,255,0); 
							glColor3f(255,255,255); 
						}
					}
				float x2=(-(gridWidth*tileWidth)/2+tileWidth/2) +i*tileWidth/2+j*tileWidth/2;
				float y2=tileHeight/2-i*tileHeight/2+j*tileHeight/2-tileHeight;

				if(charY > y2 && charY< y2+tileHeight) {
					if(charX > x2 -tileWidth/2 && charX< x2+tileWidth/2) {
						charXMap = mapX+j;
						charYMap = mapY+i;
						if(isInside(x2,y2+tileHeight, x2+tileWidth/2,y2+tileHeight, x2+tileWidth/2,y2+tileHeight-tileHeight/2,charX, charY)) {
							x2+=tileWidth/2;
							y2+= tileHeight/2; //top right
							charXMap += 1;
						}
						else if(isInside(x2-tileWidth/2,y2+tileHeight, x2,y2+tileHeight, x2-tileWidth/2,y2+tileHeight-tileHeight/2,charX, charY)) {
							x2-=tileWidth/2;
							y2+= tileHeight/2; //top left
							charYMap -= 1;
						}
						else if(isInside(x2,y2,x2-tileWidth/2,y2,x2-tileWidth/2,y2+tileHeight/2,charX,charY)) {
							x2-=tileWidth/2;
							y2-= tileHeight/2; //bottom left
							charXMap -= 1;

						}
						else if(isInside(x2,y2,x2+tileWidth/2,y2,x2+tileWidth/2,y2+tileHeight/2,charX,charY)) {
							x2+=tileWidth/2;
							y2-= tileHeight/2; //bottom right
							charYMap += 1;
						}
						glColor3f(255,255,0); 
						//if(charYMap >= 0 && charYMap <= MAP_SIZE && charXMap >=0 && charXMap <= MAP_SIZE)
						//drawDiamond(x1,y1+tileHeight,tileWidth,tileHeight);
						charCamX = ((1-(x2+camX+tileWidth/2)/tileWidth)-0.5f );
						charCamY = ((y2+camY+tileHeight+tileHeight/2)/tileHeight-0.5f);

						//System.out.println("X: " + charCamX+ "\tY:" + charCamY );
						/*
						glColor3f(255,255,0); 
						drawTriangle(charX, charY,x2+tileWidth/2,y2+tileHeight/2,x2,y2);
						glColor3f(0,255,0); 
						drawTriangle(charX, charY,x2-tileWidth/2,y2+tileHeight/2,x2,y2);
						glColor3f(0,0,255); 
						drawTriangle(charX, charY,x2,y2+tileHeight,x2+tileWidth/2,y2+tileHeight/2);
						glColor3f(255,0,0); 
						drawTriangle(charX, charY,x2,y2+tileHeight,x2-tileWidth/2,y2+tileHeight/2);
						 */

						topLeft = (area(charX, charY,x2,y2+tileHeight,x2-tileWidth/2,y2+tileHeight/2)< charHitbox);
						topRight = (area(charX, charY,x2,y2+tileHeight,x2+tileWidth/2,y2+tileHeight/2)< charHitbox);
						bottomLeft = (area(charX, charY,x2-tileWidth/2,y2+tileHeight/2,x2,y2)< charHitbox);
						bottomRight = (area(charX, charY,x2+tileWidth/2,y2+tileHeight/2,x2,y2)< charHitbox);
						glColor3f(255,255,255); 
					}
				}

			}
		}

		//Drawing entities
		for(int i =0; i < gridHeight; i++) {
			for(int j =gridWidth; j > 0; j--) {
				float x = (-(gridWidth*tileWidth)/2+tileWidth/2) +i*tileWidth/2+j*tileWidth/2-tileWidth/2;
				float y =tileHeight/2-i*tileHeight/2+j*tileHeight/2+tileHeight;
				if(mapX+j <= MAP_SIZE-1 && mapY+i <= MAP_SIZE-1 && mapX+j >= 0 && mapY+i >= 0) {
					if(mapE[mapX+j][mapY+i] == 1) {
						if((mapX+j+1 == charXMap && mapY+i-1 == charYMap)||
								(mapX+j+1 == charXMap && mapY+i == charYMap)||
								(mapX+j == charXMap && mapY+i-1 == charYMap)||
								(mapX+j+2 == charXMap && mapY+i-2 == charYMap) ||
								(mapX+j+2 == charXMap && mapY+i-1 == charYMap) ||
								(mapX+j+1 == charXMap && mapY+i-2== charYMap)
								)
							glColor4f(255,255,255,0.3f);
						fillRect(x,y,tileWidth,tileHeight*2,house);
						fillRect(x,y+tileHeight,tileWidth,tileHeight*2,house);
						glColor4f(255,255,255,1f);
					}

				}
				if(!(mapX+j <= charXMap+1 && mapX+j >= charXMap-1 && mapY+i-2 >= charYMap && mapY+i-4 <= charYMap)) {
					glColor4f(0,0,0,0.4f); 
					//fillDiamond2(x-tileWidth,y+tileHeight/2,tileWidth,tileHeight);
					glColor4f(255,255,255,1f);
				}
			}
		}
		//Drawing character on screen
		//drawRect(0-tileWidth/8-camX,0+tileHeight/2-camY,tileWidth/4,tileHeight);

		drawChar(0-tileWidth/8-camX,0+tileHeight/2-camY,tileWidth/4,tileHeight, charTextures[(int)animateChar][charMode]);

		for(int i =0; i < gridHeight; i++) {
			for(int j =gridWidth; j > 0; j--) {
				float x = (-(gridWidth*tileWidth)/2+tileWidth/2) +i*tileWidth/2+j*tileWidth/2-tileWidth/2;
				float y =tileHeight/2-i*tileHeight/2+j*tileHeight/2+tileHeight;
				if(mapX+j <= MAP_SIZE-1 && mapY+i <= MAP_SIZE-1 && mapX+j >= 0 && mapY+i >= 0) {
					if(mapE[mapX+j][mapY+i] == 1 && mapY+i >= charYMap && mapX+j <= charXMap) {
						if((mapX+j+1 == charXMap && mapY+i-1 == charYMap)||
								(mapX+j+1 == charXMap && mapY+i == charYMap)||
								(mapX+j == charXMap && mapY+i-1 == charYMap)||
								(mapX+j+2 == charXMap && mapY+i-2 == charYMap) ||
								(mapX+j+2 == charXMap && mapY+i-1 == charYMap) ||
								(mapX+j+1 == charXMap && mapY+i-2== charYMap)
								)
							glColor4f(255,255,255,0.3f);
						fillRect(x,y,tileWidth,tileHeight*2,house);
						fillRect(x,y+tileHeight,tileWidth,tileHeight*2,house);
						glColor4f(255,255,255,1f);
					}


				}
				//Lighting


			}
		}

		//drawRect(0,1,0,2);
		//drawRect(-1,0,2,0);
		//tileHeight+= 0.00001f;
		//fillDiamond(0,0,0.5f,0.25f,house);
		//fillRect(0, 0,tileWidth,tileHeight,reference);
		//glRotatef(0.01f, 1,1, 0);
		//glTranslatef(-0.0001f,0.0001f,0);
		//fillDiamond(mouseX,mouseY,tileWidth,tileHeight,dirt);

		fillRect(mouseX,mouseY,0.035f,0.1f, cursor);

	}
	public static void fillRect(float x, float y, float width, float height, Texture texture) {
		texture.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0,0);
		glVertex2f(x,y);

		glTexCoord2f(1,0);
		glVertex2f(x+width, y);

		glTexCoord2f(1,1);
		glVertex2f(x+width, y-height);

		glTexCoord2f(0,1);
		glVertex2f(x, y-height);
		glEnd();
	}
	public static void drawChar(float x, float y, float width, float height, Texture texture) {
		float cut = 0.6f;
		if(right || (up && !right && !left)) {
			texture.bind();
			glBegin(GL_QUADS);
			glTexCoord2f(0,0);
			glVertex2f(x,y);

			glTexCoord2f(cut,0);
			glVertex2f(x+width, y);

			glTexCoord2f(cut,1);
			glVertex2f(x+width, y-height);

			glTexCoord2f(0,1);
			glVertex2f(x, y-height);
			glEnd();
		}
		else {
			texture.bind();
			glBegin(GL_QUADS);
			glTexCoord2f(cut,0);
			glVertex2f(x,y);

			glTexCoord2f(0,0);
			glVertex2f(x+width, y);

			glTexCoord2f(0,1);
			glVertex2f(x+width, y-height);

			glTexCoord2f(cut,1);
			glVertex2f(x, y-height);
			glEnd();
		}
	}
	public static void fillBlock(float x, float y, float width, float length, float height ,Texture texture) {

		texture.bind();
		glBegin(GL_POLYGON);
		glTexCoord2f(0.5f,0);
		glVertex2f(x,y);

		glTexCoord2f(1,0.25f);
		glVertex2f(x+width/2, y-length/2);

		glTexCoord2f(1,0.75f);
		glVertex2f(x+width/2, y-length/2-height);

		glTexCoord2f(0.5f,1);
		glVertex2f(x, y-length-height);

		glTexCoord2f(0,0.75f);
		glVertex2f(x-width/2, y-length/2-height);
		glTexCoord2f(0,0.25f);

		glVertex2f(x-width/2, y-length/2);

		glEnd();

	}
	public static void fillDiamond2(float x, float y, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, 0);
		glBegin(GL_POLYGON);
		glVertex2f(x,y);
		glVertex2f(x+width/2, y-height/2);
		glVertex2f(x, y-height);
		glVertex2f(x-width/2, y-height/2);
		glEnd();
	}
	public static void fillDiamond(float x, float y, float width, float height, Texture texture) {
		texture.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0.5f,0);
		glVertex2f(x,y);

		glTexCoord2f(1,0.5f);
		glVertex2f(x+width/2, y-height/2);

		glTexCoord2f(0.5f,1);
		glVertex2f(x, y-height);

		glTexCoord2f(0,0.5f);
		glVertex2f(x-width/2, y-height/2);
		glEnd();
	}
	public static void drawDiamond(float x, float y, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, 0);

		glBegin(GL_LINE_STRIP);
		glVertex2f(x,y);
		glVertex2f(x+width/2, y-height/2);
		glVertex2f(x, y-height);
		glVertex2f(x-width/2, y-height/2);
		glVertex2f(x,y);
		glEnd();
		glColor3f(255,255,255);
	}
	public static void drawTriangle(float x1, float y1, float x2,float y2, float x3, float y3) {
		glBindTexture(GL_TEXTURE_2D, 0);

		glBegin(GL_TRIANGLES);
		glVertex2f(x1,y1);
		glVertex2f(x2, y2);
		glVertex2f(x3,y3);
		glEnd();
		glColor3f(255,255,255);
	}
	public static void drawRect(float x, float y, float width, float height) {
		glBindTexture(GL_TEXTURE_2D, 0);
		glBegin(GL_LINE_STRIP);
		glVertex2f(x,y);

		glVertex2f(x+width, y);

		glVertex2f(x+width, y-height);

		glVertex2f(x, y-height);
		glVertex2f(x,y);
		glEnd();
	}
	public static void main(String[] args) {
		createWindow();
	}
	/* A utility function to calculate area of triangle formed by (x1, y1),
	   (x2, y2) and (x3, y3) */
	static double area(float x1, float y1, float x2, float y2, float x3, float y3)
	{
		//System.out.println((float)Math.abs((x1*(y2-y3) + x2*(y3-y1)+ x3*(y1-y2))/2.0));
		double answer =  (x1*(y2-y3) + x2*(y3-y1)+ x3*(y1-y2))/2f;
		return Math.abs(answer);
	}

	/* A function to check whether point P(x, y) lies inside the triangle formed
	   by A(x1, y1), B(x2, y2) and C(x3, y3) */
	static boolean isInside(float x1, float y1, float x2, float y2, float x3, float y3, float x, float y)
	{  
		/* Calculate area of triangle ABC */
		double A = area (x1, y1, x2, y2, x3, y3);

		/* Calculate area of triangle PBC */ 
		double A1 = area (x, y, x2, y2, x3, y3);

		/* Calculate area of triangle PAC */ 
		double A2 = area (x1, y1, x, y, x3, y3);

		/* Calculate area of triangle PAB */  
		double A3 = area (x1, y1, x2, y2, x, y);
		/* Check if sum of A1, A2 and A3 is same as A */
		//System.out.println( round2(round(A)-(round(A1) + round(A2) + round(A3)) ));
		return round2(round(A) - (round(A1) + round(A2) + round(A3)) )==0.0;

	}
	public static double round(double num) {
		DecimalFormat df = new DecimalFormat("#.#########");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return Double.parseDouble((df.format(num)));
	}
	public static double round2(double num) {
		DecimalFormat df = new DecimalFormat("#.########");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return Math.abs(Double.parseDouble((df.format(num))));
	}

}
