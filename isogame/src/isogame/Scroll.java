package isogame;

import org.lwjgl.glfw.GLFWScrollCallback;

public class Scroll extends GLFWScrollCallback{
	static double scrollVal;
	public void invoke(final long window, final double xOffset, final double yOffset) {
		scrollVal = yOffset;
	}

}
