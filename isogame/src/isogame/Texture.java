package isogame;
import static org.lwjgl.opengl.GL11.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class Texture {
	private int id, width, height;
	BufferedImage bi;
	public Texture(String filename) {
		try {
			bi = ImageIO.read(new File(filename));
			width = bi.getWidth();
			height = bi.getHeight();
			int[] rawPixels = new int[width*height*4];
			rawPixels = bi.getRGB(0,0,width,height,null,0,width);
			ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*4);
			for(int i =0; i < height; i++) {
				for(int j =0; j < width; j++) {
					int pixel = rawPixels[i*width+j];
					pixels.put((byte)(pixel >> 16 & 0xFF)); 	//red
					pixels.put((byte)(pixel >> 8 & 0xFF));		//green
					pixels.put((byte)(pixel & 0xFF));			//blue
					pixels.put((byte)(pixel >> 24 & 0xFF));	//alpha
				}
			}
			
			pixels.flip();
			id = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
			
		}catch(IOException e) {e.printStackTrace();}
	}
	public Texture(BufferedImage bi) {
			this.bi = bi;
			width = bi.getWidth();
			height = bi.getHeight();
			int[] rawPixels = new int[width*height*4];
			rawPixels = bi.getRGB(0,0,width,height,null,0,width);
			ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*4);
			for(int i =0; i < height; i++) {
				for(int j =0; j < width; j++) {
					int pixel = rawPixels[i*width+j];
					pixels.put((byte)(pixel >> 16 & 0xFF)); 	//red
					pixels.put((byte)(pixel >> 8 & 0xFF));		//green
					pixels.put((byte)(pixel & 0xFF));			//blue
					pixels.put((byte)(pixel >> 24 & 0xFF));	//alpha
				}
			}
			
			pixels.flip();
			id = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, id);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
			
	}
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, id);
	}
}
