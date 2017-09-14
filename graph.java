import java.io.*;
import java.util.*;

class vertex implements Comparable<vertex>{
	private int id;
	private String name;
	private ArrayList<edge> edgeList;
	private vertex psor;
	public vertex(String name){
		this.name=name;
		this.id=0;
		edgeList = new ArrayList<>();
		psor=null;
	}
	public void setId(int i){this.id = i ;}
	public int getId(){ return id; }
	public String getName(){ return name; }
	public int getDegree(){ return edgeList.size();}
	public void addEdge(edge e){edgeList.add(e);}
	public ArrayList<edge> getEdgeList(){return edgeList;}
	public vertex getPsor(){
		return psor;
	}
	public void setPsor(vertex v){
		psor=v;
	}
	public void resetPsor(){ psor = null;}
	public int compareTo(vertex v){
		if(name.equals(v.getName())) return 0;
		if(name.compareTo(v.getName())>0) return 1;
		else return -1;
	}
}

class edge{
	private int weight;
	private vertex v1;
	private vertex v2;
	private ArrayList<vertex> endPoints;
	public edge(vertex v1,vertex v2,int weight){
		this.v1=v1;
		this.v2=v2;
		this.weight=weight;
		endPoints = new ArrayList<>(2);
		endPoints.add(v1); endPoints.add(v2);
	}	
	public int getCost(){
		return weight;
	}
	public ArrayList<vertex> getEndPoints(){
		return endPoints;
	}
}

class position{
	private vertex v1;
	private vertex v2;
	private int inter;
	public position(vertex v1,vertex v2,int inter)
	{
		this.v1=v1;
		this.v2=v2;
		this.inter=inter;
	}
	public vertex getV1(){ return v1;}
	public vertex getV2(){ return v2;}
	public int getInter() { return inter;}

}

public class graph{
	private ArrayList<vertex> adjList;
	private int numOfVertices;
	private int numOfEdges;
	public graph(){
		adjList = new ArrayList<>();
		numOfEdges=0;
		numOfVertices=0;
	}
	public int numVertices(){ return numOfVertices;}
	public int numEdges(){ return numOfEdges;}
	public edge getEdge(vertex u,vertex v) throws Exception
	{
		vertex small = u.getDegree() > v.getDegree() ? v :u;
		vertex big = u.getDegree() > v.getDegree() ? u :v;
		Iterator<edge> it = small.getEdgeList().listIterator();
		edge tempEdge=null;
		while(it.hasNext()){
			tempEdge = it.next();
			if(tempEdge.getEndPoints().contains(big)){
				return tempEdge;
			}
		}
		if(tempEdge==null) throw new Exception("Edge not present in graph");
		return tempEdge;
	}
	public int getDegreeOfVertex(vertex u){ return u.getDegree();}
	public ArrayList<vertex> getEndPointsOfEdge(edge e){
		return e.getEndPoints();
	}
	public ArrayList<edge> getAdjListOfVertex(vertex v){
		return v.getEdgeList();
	}
	public vertex insertVertex(String str){
		Iterator<vertex> it = adjList.listIterator();
		boolean flag=false;
		vertex temp=null;
		while(it.hasNext()){
			temp=it.next();
			if(temp.getName().equals(str)){
			 	flag=true;
			 	break;
			}
		}
		if(flag==true) return temp;
		vertex v = new vertex(str);
		adjList.add(v);
		Collections.sort(adjList);
		for(vertex vv: adjList){
			vv.setId(adjList.indexOf(vv));
		}
		numOfVertices++;
		return v;
	}
	public vertex getVertexFromName(String str){
		Iterator<vertex> it = adjList.listIterator();
		vertex temp=null;
		while(it.hasNext()){
			temp=it.next();
			if(temp.getName().equals(str)){
			 	return temp;
			}
		}
		return null;
	}
	public void insertEdge(vertex v,vertex u,int cost){
	 	edge e = new edge(v,u,cost);
		v.addEdge(e); u.addEdge(e);
		numOfEdges++;
	}
	public ArrayList<vertex> getNeighbourVertices(vertex v){
		ArrayList<vertex> listOfNeighbours = new ArrayList<>();
		for(edge e : v.getEdgeList()){
			Iterator<vertex> it = e.getEndPoints().listIterator();
			while(it.hasNext()){
				vertex temp = it.next();
				if(temp != v){
					listOfNeighbours.add(temp);
					break;
				}
			}
		}
		return listOfNeighbours;
	}
	public vertex getVertexFromId(int j){
		return adjList.get(j);
	}
	public ArrayList<vertex> getListOfVertices(){
		return adjList;
	}
}

class dijkstra{
	private graph g;
	private int[] distList;
	private boolean[] isSettledList;
	private vertex source;
	public dijkstra(graph g,vertex source){
		this.g=g;
		this.source=source;
		distList = new int[g.numVertices()];
		isSettledList = new boolean[g.numVertices()];
		int i;
		for(i=0;i<g.numVertices();i++){
			distList[i]=Integer.MAX_VALUE;
			isSettledList[i]=false;
		}
		distList[source.getId()]=0;
		int count;
		for(count=0;count<g.numVertices()-1;count++){
			int current = minDistance();
			isSettledList[current]=true;
			ArrayList<vertex> neighbours = g.getNeighbourVertices(g.getVertexFromId(current));
			Iterator<vertex> it = neighbours.listIterator();
			vertex tempVertex;
			while(it.hasNext()){
				tempVertex = it.next();
				try{
					if(!isSettledList[tempVertex.getId()] && distList[current]!= Integer.MAX_VALUE
				    && distList[current]+g.getEdge(g.getVertexFromId(current),tempVertex).getCost() < distList[tempVertex.getId()])
					{
						distList[tempVertex.getId()]=distList[current]+ g.getEdge(g.getVertexFromId(current),tempVertex).getCost();
						tempVertex.setPsor(g.getVertexFromId(current));
					}
				} catch(Exception e){}
			}
		}
	}
	public int getMinDistance(vertex destination){
		return distList[destination.getId()];
	}
	private int minDistance(){
		int min = Integer.MAX_VALUE;
		int minIndex=-1;
		int i;
		for(i=0;i<g.numVertices();i++){
			if(isSettledList[i]==false && distList[i] <= min){
				min = distList[i];
				minIndex=i;
			}
		}
		return minIndex;
	}
	public LinkedList<vertex> getPath(vertex destination) throws Exception
	{
		LinkedList<vertex> path = new LinkedList<>();
		if(source==destination){
			path.add(source);
			return path;
		}
		if(destination.getPsor() == null) throw new Exception("Destination is not connected");
		vertex temp=destination;
		path.add(destination);
		while(temp.getPsor() != null){
			temp=temp.getPsor();
			path.add(temp);
		}
		Collections.reverse(path);
		return path;
	}
}