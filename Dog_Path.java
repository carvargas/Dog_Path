/*Carina Vargas*/
/*Dog Walking Challenge
**Checks for segment intersection to find exclusion sets
**If no intersection between any two segments, 
**finds if segments are within range of each other based on each dog walker's leash length
**To implement this, the distance from segment to segment is calculated
**and if it less than the total length of both dog walker's leash lengths, 
**then these segments are an exclusion zone*/

import java.io.*;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.awt.geom.Line2D;
import java.lang.Math;

class Point { 
	float x, y;
 
	//constructor
    Point(float p, float q) {
        x = p;
        y = q;
    }
	
	public boolean equalsPoint(Point p) { 
		if(this.x == p.x && this.y == p.y)
			return true;
		return false;
	}
}

class LineSegment {
	String id; //ex: AB or EF
	Point p1, p2; //p1: start node(ex: A in AB), p2: end node (ex: B in AB)
	
	public LineSegment(String id, Point p1, Point p2) {
		this.id = id;
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public String getId() {
		return id;
	}
	
	public Point getP1() {
		return p1;
	}
	
	public Point getP2() { 
		return p2;
	}
} 
	

class Graphs { 
	float len;
	int node_count;
	int seg_count;
	
	public Graphs() { 
		this.len = 0;
		this.node_count = 0;
		this.seg_count = 0;
	}
	
	public void addLen(float l) {
		this.len = l;
	}
	
	public void addNC(int n) {
		this.node_count = n;
	}
	
	public void addSC(int s) {
		this.seg_count = s;
	}
	
	public float getLen() {
		return len;
	}
	
	public int getNC() {
		return node_count;
	}
	
	public int getSC() {
		return seg_count;
	}
}

class DataSet { 
	String id;
	Point p;
	String edge;

	public DataSet() {
		this.id = null;
		this.edge = null;
		this.p = null;
	}
	
	public void addId(String i) {
		this.id = i;
	}
	
	public void addPoint(Point p) {
		this.p = p;
	}
	
	public void addEdge(String seg) {
		this.edge = seg;
	}
	
	public String getId() { 
		return id;
	}	
	
	public Point getPoint() { 
		return p;
	} 
	
	public String getEdge() { 
		return edge;
	}
}

public class Dog_Path { 
	
	//number of graphs
	static final int NUM = 2;
	
	//returns length of a segment
	public static float segmentLength(Point p1, Point p2) {
		float sLen;

		sLen = (float) Math.pow((p1.x - p2.x), 2) +
				(float) Math.pow((p1.y - p2.y), 2);

		return ((float) Math.sqrt(sLen));
	}
	
	/*find the closest distance from a point to a line segment
	**p1 and p2 are endpoints of a line segment
	**p3 is point we want to know distance to line segment
	**function returns the distance of point to segment*/
	public static float distanceToSegment(Point p1, Point p2, Point p3) {

		float xDelta = p2.x - p1.x;
		float yDelta = p2.y - p1.y;

		if ((xDelta == 0) && (yDelta == 0)) {
			throw new IllegalArgumentException("p1 and p2 cannot be the same point");
		}

		float u = ((p3.x - p1.x) * xDelta + (p3.y - p1.y) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

		Point closestPoint;
		if (u < 0) 
			closestPoint = p1;
		
		else if (u > 1)
			closestPoint = p2;
		
		else
			closestPoint = new Point(p1.x + u * xDelta, p1.y + u * yDelta);		

		return segmentLength(closestPoint, p3);
    }
	
	public static boolean intersection(Point p1, Point q1, Point p2, Point q2) { 
		//Line2D format: x1, y1, x2, y2
		Line2D line1 = new Line2D.Float(p1.x, p1.y, q1.x, q1.y);
		Line2D line2 = new Line2D.Float(p2.x, p2.y, q2.x, q2.y);
		
		boolean result = line2.intersectsLine(line1);
		return result;
	}
	
	public static Point intersectionPoint(Point p1, Point q1, Point p2, Point q2) {	
		//line equation 1
		float A1 = q1.y - p1.y;
		float B1 = p1.x - q1.x;
		float C1 = (A1 * p1.x) + (B1 * p1.y);
		
		//line equation 2
		float A2 = q2.y - p2.y;
		float B2 = p2.x - q2.x;
		float C2 = (A2 * p2.x) + (B2 * p2.y);
		
		//finds intersection point
		float det = (A1 * B2) - (A2 * B1);
		//assumes intersection exists, so parallel lines (det == 0) will not occur
		float x = ((B2 * C1) - (B1 * C2)) / det;
		float y = ((A1 * C2) - (A2 * C1)) / det;
		
		Point result = new Point(x, y);
		
		return result;
	}
	
	//Find minimizing node, a point +/- the intersection point along a line segment
	//start is the intersection point
	public static Point minNode(Point start, Point end, float d) {
		/*if start == end, segment cannot be shortened, therefore return endpoint as 
		**minimizing node*/
		if(segmentLength(start, end) == 0)
			return end;
		/*if the segment length is less than leash length
		**there is no shorter segment that can be made in between start and end points
		**therefore return endpoint as minimizing node*/
		else if(segmentLength(start, end) < d)
			return end;
		
		Point v = new Point((end.x - start.x), (end.y - start.y));
		
		float vNorm = (float)Math.sqrt((float)Math.pow(v.x, 2) + (float)Math.pow(v.y, 2));
		
		Point u = new Point((v.x/vNorm), v.y/vNorm);
		
		Point min = new Point(start.x + d * u.x, start.y + d * u.y);
		
		return min;
	}
	
	public static void main(String[] args) { 
			String line;
			String[] tokens;
			
			//loop counts
			int count, node, seg;
			
			//exclusion set & intersections count
			int ex = 0, i = 0;
			
			FileReader file[] =  new FileReader[2];
			
			//stores graph details: leash length & #vertices/#edges
			ArrayList<Graphs> graph = new ArrayList<Graphs>();
			
			//assume 2 graphs
			graph.add(new Graphs());
			graph.add(new Graphs());
			
			//node->(x,y) & nodes->edges
			ArrayList<ArrayList<DataSet>> set = new ArrayList<ArrayList<DataSet>>();
			set.add(new ArrayList<DataSet>());
			set.add(new ArrayList<DataSet>());
			
			//line segments
			ArrayList<ArrayList<LineSegment>> segment = new ArrayList<ArrayList<LineSegment>>();
			segment.add(new ArrayList<LineSegment>());
			segment.add(new ArrayList<LineSegment>());
			
			//stores exclusion zones
			ArrayList<String> exclusionSet = new ArrayList<String>();
			
			//stores minimizing nodes for segments in each graph
			ArrayList<Point> minimumSet1 = new ArrayList<Point>();
			ArrayList<Point> minimumSet2 = new ArrayList<Point>();
			
			//stores intersecting segments
			ArrayList<String> intersections = new ArrayList<String>();
			
		try {	
	/*--------------------------------------------------------------------------------*/	
			//DATA STORING
			
			for(count = 0; count < NUM; count++) {
				//open input file
				file[count] = new FileReader(args[count]);
				BufferedReader buff = new BufferedReader(file[count]);
				
				//leash length
				line = buff.readLine();
			
				graph.get(count).addLen(Float.parseFloat(line));
				//node count
				line = buff.readLine();
				graph.get(count).addNC(Integer.parseInt(line));

				//stores A->(X,Y)
				for(node = 0; node < graph.get(count).getNC(); node++) {
					line = buff.readLine();
					
					tokens = line.split(",");//removes commas
					
					set.get(count).add(new DataSet());
					set.get(count).get(node).addId(tokens[0]);
					set.get(count).get(node).addPoint(new Point(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
						
				}
			
				//segment count
				line = buff.readLine();
				graph.get(count).addSC(Integer.parseInt(line));
				
				//stores A->B
				for(seg = 0; seg < graph.get(count).getSC(); seg++) {
					line = buff.readLine();
					
					tokens = line.split(",");//removes commas

					//look for node in dataSet and then store its edge
					for(node = 0; node < (graph.get(count)).getNC(); node++) {
						if(Objects.equals((set.get(count).get(node).getId()), (tokens[0]))) {
							set.get(count).get(node).addEdge(tokens[1]);	
							break;
						}
					}
				}
				
			}

	/*--------------------------------------------------------------------------------*/				
			//CREATE LINE SEGMENTS
			
			for(count = 0; count < NUM; count++) {		
				for(node = 0; node < graph.get(count).getNC(); node++) {
					//ex: p1 = 10, 10 (from node A)
					Point p1 = set.get(count).get(node).getPoint();
						
					//now find which node equals A's edge to retrieve its point
					for(seg = 0; seg < graph.get(count).getNC(); seg++) {
						if(Objects.equals((set.get(count).get(node).getEdge()), 
							(set.get(count).get(seg).getId()))) {
							Point p2 = set.get(count).get(seg).getPoint();
							
							//create segment name
							String cat = set.get(count).get(node).getId() + set.get(count).get(seg).getId();
							
							segment.get(count).add(new LineSegment(cat, p1, p2));
							
							break;
						}
					}
				}
			}
			
	/*----------------------------------------------------------------------------------*/
			//DETERMINES LINE INTERSECTION
			
			for(count = 0; count < graph.get(0).getSC(); count++) {
				Point p1, p2, p3, p4, intersect;
				float l1, l2, l3, l4, min;
				
				float radius = (graph.get(0).getLen()) + (graph.get(1).getLen());
				
				/*ex: A checked against segment EF
				**so E and F points need to be known 
				**so need graph1 P1 node, checked against graph2 P1 and P2 node*/
				p1 = segment.get(0).get(count).getP1();
				
				p2 = segment.get(0).get(count).getP2();
				
				//checking each segment in graph 1 to every segment in graph2
				for(node = 0; node < graph.get(1).getSC(); node++) {
					p3 = segment.get(1).get(node).getP1();
					
					p4 = segment.get(1).get(node).getP2();
					
					//if segments intersect add to exclusion set
					if(intersection(p1, p2, p3, p4)) {
						//keeps track of segments that intersect
						intersections.add(segment.get(0).get(count).getId() + segment.get(1).get(node).getId());
						i++;
						
						exclusionSet.add(new String(segment.get(0).get(count).getId()));
						exclusionSet.add(new String(segment.get(1).get(node).getId()));
						ex += 2;
						
						//find minimizing nodes for intersecting segments
						
						//first, find intersection point
						intersect = intersectionPoint(p1, p2, p3, p4);
						
						/*remember: each segment's minimizing nodes must be the opposite 
						**segment's (dogwalker's) leash length away*/
						
						//gets minimizing nodes on left/right side of intersection point
						//segments from graph1
						minimumSet1.add(minNode(intersect, p1, graph.get(1).getLen()));
						minimumSet1.add(minNode(intersect, p2, graph.get(1).getLen()));
						//segments from graph2
						minimumSet2.add(minNode(intersect, p3, graph.get(0).getLen()));
						minimumSet2.add(minNode(intersect, p4, graph.get(0).getLen()));
					}
					
	/*----------------------------------------------------------------------------------*/				
					//CALCULATE DISTANCE BETWEEN LINE SEGMENTS
					
					//checks if segments are within leash length of each other
					else {	
						/*FOUR SEGMENTS: A to EF, B to EF
						**E to AB, F to AB
						**so call to distanceToSegment 4 times
						**and check which is smaller*/
						//E, F, A : (A to EF)
						l1 = distanceToSegment(p3, p4, p1);
						
						//E, F, B : (B to EF)
						l2 = distanceToSegment(p3, p4, p2);
						
						if(l1 > l2)
							min = l2;
						else
							min = l1;
						
						//A, B, E : (E to AB)
						l3 = distanceToSegment(p1, p2, p3);
						
						if(min > l3)
							min = l3;
						
						//A, B, F : (F to AB)
						l4 = distanceToSegment(p1, p2, p4);
						
						if(min > l4) 
							min = l4;

						/*min = shortest distance
						**check if distance against 
						**graph1 + graph2 leash lengths*/
						
						/*if shortest distance is less than leash lengths
						**segments collide so add to exclusion set*/
						if(min < radius) { 
							exclusionSet.add(new String(segment.get(0).get(count).getId()));
							exclusionSet.add(new String(segment.get(1).get(node).getId()));
							ex += 2;
						}
						//otherwise, segments do not collide
					}
				}
			}

	/*----------------------------------------------------------------------------------*/
			//OUTPUT RESULTS
			
			//need if intersecting/nonintersecting segments
			int m1 = 0, m2 = 0; //minimum set indices
			int loop = 0; //for each loop iteration determines if-intersection statement was executed
			
			if(ex == 0) 
				System.out.println("No exclusion zones");

			else {	
				for(count = 0; count < ex; count += 2) {
					if(i > 0) {
						for(int n = 0; n < i; n++) {
							if((exclusionSet.get(count) + exclusionSet.get(count + 1)).equals(intersections.get(n))) {
								System.out.println("Segments " + exclusionSet.get(count) + " and " + exclusionSet.get(count + 1) +
												" form an exclusion set");
								System.out.printf("Minimizing nodes on " + exclusionSet.get(count) + " are located at (" + 
												minimumSet1.get(m1).x + "," + minimumSet1.get(m1++).y + ") and (" + 
												minimumSet1.get(m1).x + "," + minimumSet1.get(m1++).y +")\n");
								System.out.printf("Minimizing nodes on " + exclusionSet.get(count + 1) + " are located at (" + 
												minimumSet2.get(m2).x + "," + minimumSet2.get(m2++).y + ") and (" + 
												minimumSet2.get(m2).x + "," + minimumSet2.get(m2++).y +")\n");
								loop++;
							}
							
							else if(n + 1 == i && loop == 0) {
								System.out.println("Segments " + exclusionSet.get(count) + " and " + exclusionSet.get(count + 1) +
												" form an exclusion set");
							}
						}
						
						loop = 0;
					}
					//no intersections exist
					else {
						System.out.println("Segments " + exclusionSet.get(count) + " and " + exclusionSet.get(count + 1) +
										" form an exclusion set");
					}
				}
			}
		}
		catch (Exception e) {
			System.err.format("exception\n");
		}
	}
}