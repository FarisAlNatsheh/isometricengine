package isogame;
import org.lwjgl.glfw.GLFWCursorPosCallback;
public class MouseInput extends GLFWCursorPosCallback{
	public static double x,y;
	public void invoke(long window, double x, double y) {
		MouseInput.x = x;
		MouseInput.y = y;
	}

}
