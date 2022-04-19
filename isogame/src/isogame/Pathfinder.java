package isogame;


import java.util.ArrayList;
import java.util.Arrays;

public class Pathfinder implements Runnable {
	Node[][] map;
	int[][] mapFinal;
	private int startx;
	private int starty;
	boolean flipped;
	double timeI = System.nanoTime();
	int mapI[][];
	int startX,startY,endX,endY;
	public void run(int map[][], int startX, int startY, int endX, int endY) {
		//[HEIGHT][WIDTH]
		//System.out.println(Math.abs(startX-endX)+1);
		//System.out.println(Math.abs(startY-endY)+1);
//		if(startX > endX || startY > endY) {
//			int temp=startY;
//			int temp2=startX;
//			startY = endY;
//			startX = endX;
//			endX = temp2;
//			endY = temp;
//			flipped = true;
//		}
		//this.map = new Node[Math.abs(startY-endY)+1][Math.abs(startX-endX)+1];
		//mapFinal = new int[Math.abs(startY-endY)+1][Math.abs(startX-endX)+1];
		this.map = new Node[map.length][map[0].length];
		this.mapFinal = new int[map.length][map[0].length];

		

		for(int i = 0; i < this.map.length; i++) {
			for(int j = 0; j < this.map[0].length; j++) {
				if(map[i][j] == 0)
					this.map[i][j] = new Node(3,i,j);
				else
					this.map[i][j] = new Node(2,i,j);
			}
		}
		this.map[endY][endX].setType(1); 
		this.map[startY][startX].setType(0);
		//System.out.println("Loaded map\n"+ (System.nanoTime()-timeI)*0.000001+" ms");
		//timeI = System.nanoTime();
		/*for(int i = startY; i < this.map.length; i++) {
	for(int j = startX; j < this.map[0].length; j++) {
		//System.out.print(this.map[i][j].getType()+" ");
	}
	//System.out.println();
}*/

		new Algorithm().Dijkstra();
		//System.out.println("Ran algorithm\n"+ (System.nanoTime()-timeI)*0.000001+" ms");
		//timeI = System.nanoTime();
	}
	
	public Pathfinder(int map[][], int startX, int startY, int endX, int endY) {
		mapI = map;
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		run();
	}
	public int[][] getPath() {
		return mapFinal;
	}
	class Algorithm {	//ALGORITHM CLASS

		//DIJKSTRA WORKS BY PROPAGATING OUTWARDS UNTIL IT FINDS THE FINISH AND THEN WORKING ITS WAY BACK TO GET THE PATH
		//IT USES A PRIORITY QUE TO KEEP TRACK OF NODES THAT IT NEEDS TO EXPLORE
		//EACH NODE IN THE PRIORITY QUE IS EXPLORED AND ALL OF ITS NEIGHBORS ARE ADDED TO THE QUE
		//ONCE A NODE IS EXLPORED IT IS DELETED FROM THE QUE
		//AN ARRAYLIST IS USED TO REPRESENT THE PRIORITY QUE
		//A SEPERATE ARRAYLIST IS RETURNED FROM A METHOD THAT EXPLORES A NODES NEIGHBORS
		//THIS ARRAYLIST CONTAINS ALL THE NODES THAT WERE EXPLORED, IT IS THEN ADDED TO THE QUE
		//A HOPS VARIABLE IN EACH NODE REPRESENTS THE NUMBER OF NODES TRAVELED FROM THE START
		public void Dijkstra() {
			ArrayList<Node> priority = new ArrayList<Node>();	//CREATE A PRIORITY QUE
			priority.add(map[startx][starty]);	//ADD THE START TO THE QUE
			while(true) {
				if(priority.size() <= 0) {	//IF THE QUE IS 0 THEN NO PATH CAN BE FOUND
					break;
				}
				int hops = priority.get(0).getHops()+1;	//INCREMENT THE HOPS VARIABLE
				ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops);	//CREATE AN ARRAYLIST OF NODES THAT WERE EXPLORED
				if(explored.size() > 0) {
					priority.remove(0);	//REMOVE THE NODE FROM THE QUE
					priority.addAll(explored);	//ADD ALL THE NEW NODES TO THE QUE
					for(int i = 0;i < map.length; i++) {
						for(int j = 0;j < map[0].length; j++) {
							//System.out.print(map[i][j].getType()+" ");
							mapFinal[i][j] = map[i][j].getType();
						}
						//System.out.println();
					}
					//System.out.println();
				} else {	//IF NO NODES WERE EXPLORED THEN JUST REMOVE THE NODE FROM THE QUE
					priority.remove(0);
				}
			}
		}

		//A STAR WORKS ESSENTIALLY THE SAME AS DIJKSTRA CREATING A PRIORITY QUE AND PROPAGATING OUTWARDS UNTIL IT FINDS THE END
		//HOWEVER ASTAR BUILDS IN A HEURISTIC OF DISTANCE FROM ANY NODE TO THE FINISH
		//THIS MEANS THAT NODES THAT ARE CLOSER TO THE FINISH WILL BE EXPLORED FIRST
		//THIS HEURISTIC IS BUILT IN BY SORTING THE QUE ACCORDING TO HOPS PLUS DISTANCE UNTIL THE FINISH

		public ArrayList<Node> exploreNeighbors(Node current, int hops) {	//EXPLORE NEIGHBORS
			ArrayList<Node> explored = new ArrayList<Node>();	//LIST OF NODES THAT HAVE BEEN EXPLORED
			for(int a = -1; a <= 1; a++) {
				for(int b = -1; b <= 1; b++) {
					int xbound = current.getX()+a;
					int ybound = current.getY()+b;
					if((xbound > -1 && xbound < map.length) && (ybound > -1 && ybound < map[0].length)) {	//MAKES SURE THE NODE IS NOT OUTSIDE THE GRID
						Node neighbor = map[xbound][ybound];
						if((neighbor.getHops()==-1 || neighbor.getHops() > hops) && neighbor.getType()!=2) {	//CHECKS IF THE NODE IS NOT A WALL AND THAT IT HAS NOT BEEN EXPLORED
							explore(neighbor, current.getX(), current.getY(), hops);	//EXPLORE THE NODE
							explored.add(neighbor);	//ADD THE NODE TO THE LIST
						}
					}
				}
			}
			return explored;
		}

		public void explore(Node current, int lastx, int lasty, int hops) {	//EXPLORE A NODE
			if(current.getType()!=0 && current.getType() != 1)	//CHECK THAT THE NODE IS NOT THE START OR FINISH
				current.setType(4);	//SET IT TO EXPLORED
			current.setLastNode(lastx, lasty);	//KEEP TRACK OF THE NODE THAT THIS NODE IS EXPLORED FROM
			current.setHops(hops);	//SET THE HOPS FROM THE START
			if(current.getType() == 1) {	//IF THE NODE IS THE FINISH THEN BACKTRACK TO GET THE PATH
				backtrack(current.getLastX(), current.getLastY(),hops);
			}
		}

		public void backtrack(int lx, int ly, int hops) {	//BACKTRACK
			while(hops > 0) {	//BACKTRACK FROM THE END OF THE PATH TO THE START
				Node current = map[lx][ly];
				current.setType(5);
				lx = current.getLastX();
				ly = current.getLastY();
				hops--;
			}
		}
	}

	class Node {

		// 0 = start, 1 = finish, 2 = wall, 3 = empty, 4 = checked, 5 = finalpath
		private int cellType = 0;
		private int hops;
		private int x;
		private int y;
		private int lastX;
		private int lastY;

		public Node(int type, int x, int y) {	//CONSTRUCTOR
			cellType = type;
			this.x = x;
			this.y = y;
			hops = -1;
		}

		public int getX() {return x;}		//GET METHODS
		public int getY() {return y;}
		public int getLastX() {return lastX;}
		public int getLastY() {return lastY;}
		public int getType() {return cellType;}
		public int getHops() {return hops;}

		public void setType(int type) {cellType = type;}		//SET METHODS
		public void setLastNode(int x, int y) {lastX = x; lastY = y;}
		public void setHops(int hops) {this.hops = hops;}
	}
	public static void main(String[] args) {
		int[][] map = {
				{0,0,0,0,0},
				{0,0,0,0,0},
				{0,0,0,0,0},
				{0,0,0,0,0},
				{0,0,0,0,0}
		};
		System.out.println(Arrays.deepToString(new Pathfinder(map,3,3,4,4).getPath()).replace("], ", "]\n").replace("[[", "[").replace("]]","]"));
	}

	public void run() {
		//run(mapI,startX,startY,endX,endY);
		this.map = new Node[mapI.length][mapI[0].length];
		this.mapFinal = new int[mapI.length][mapI[0].length];

		

		for(int i = 0; i < this.map.length; i++) {
			for(int j = 0; j < this.map[0].length; j++) {
				if(mapI[i][j] == 0)
					this.map[i][j] = new Node(3,i,j);
				else
					this.map[i][j] = new Node(2,i,j);
			}
		}
		startx = startX;
		starty= startY;
		this.map[endY][endX].setType(1); 
		this.map[startY][startX].setType(0);
		
		new Algorithm().Dijkstra();
	}
}



