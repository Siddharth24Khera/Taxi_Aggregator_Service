import java.io.*;
import java.util.*;

public class TaxiService{
	graph cityGraph;
	LinkedList<taxi> taxiList;

	public TaxiService() {
		cityGraph= new graph();
		taxiList = new LinkedList<>();
	}

	public void performAction(String actionMessage) {
		//System.out.println();
		//System.out.println("action to be performed: " + actionMessage);
		String[] tokens = actionMessage.split("\\s");
		
		if(tokens[0].equals("edge")){
			vertex v1 =cityGraph.insertVertex(tokens[1]);
			vertex v2=cityGraph.insertVertex(tokens[2]);
			cityGraph.insertEdge(v1,v2,Integer.parseInt(tokens[3]));
			return;
		}
		
		if(tokens[0].equals("taxi")){
			vertex v= cityGraph.getVertexFromName(tokens[2]);
			if(v==null){
				System.out.println("Taxi location does not exist.");
				return;
			}
			ArrayList<vertex> list = cityGraph.getNeighbourVertices(v);
			if(list.size()==0){
				System.out.println("Taxi cannot be present at isolated location");
				return;
			}
			position p = new position(v,list.get(0),0);
			taxi t = new taxi(tokens[1],p);
			taxiList.add(t);
			position dest = getDestPosForTaxi(p);
			bestRouteTime bb = getBestTimeAndRoute(p,dest);
			t.updateRoute(bb,0);
			System.out.println("At time 0 taxi "+t.getTaxiName()+" chose a new destination vertex "+dest.getV1().getName());
			return;
		}

		if(tokens[0].equals("customer")){
			vertex source1 = cityGraph.getVertexFromName(tokens[1]);
			vertex source2 = cityGraph.getVertexFromName(tokens[2]);
			int interSource = Integer.parseInt(tokens[3]);
			edge sConEdge =null;
			if(source1==null || source2 ==null){
				System.out.println("Invalid source");
				return;
			}
			try{sConEdge = cityGraph.getEdge(source1,source2);}
			catch(Exception e){System.out.println("Ends of source not connected");}
			if(interSource > sConEdge.getCost()){
				System.out.println("Invalid intermediate distance for source");
				return;
			}
			vertex destination1 = cityGraph.getVertexFromName(tokens[4]);
			vertex destination2 = cityGraph.getVertexFromName(tokens[5]);
			int interDestination = Integer.parseInt(tokens[6]);
			edge dConEdge =null;
			try{dConEdge = cityGraph.getEdge(destination1,destination2);}
			catch(Exception e){System.out.println("Ends of destination not connected");}
			if(destination1==null || destination2==null){
				System.out.println("Invalid destination");
				return;
			}
			if(interDestination > dConEdge.getCost()){
				System.out.println("Invalid intermediate distance for destination");
				return;
			}
			int time = Integer.parseInt(tokens[7]);
			position sourcePosition = new position(source1,source2,interSource);
			position destinationPosition = new position(destination1,destination2,interDestination);
			vertex bestSource=null;
			taxi bestTaxi=null;
			int bestTime=Integer.MAX_VALUE;
			vertex bestStart=null;
			boolean flag = false;

			updateAllTaxiPositions(time);
			System.out.println("Available taxis:");
			for(taxi t : taxiList){	
				if(!t.isAvailable(time)) continue;	
				flag=true;
				vertex bestSourceForThisTaxi=null;
				vertex bestStartForThisTaxi=null;
				dijkstra dj1,dj2;
				position p =t.getPosition();
				bestRouteTime brt = getBestTimeAndRoute(p,sourcePosition);
				System.out.print("Path of "+t.getTaxiName()+": ");	
				LinkedList<vertex> path = null;
				for(vertex vex : cityGraph.getListOfVertices()){
						vex.resetPsor();
					}
				if(brt.getBestSource() ==p.getV1() && brt.getBestDestination() == source1){					
					dj1=new dijkstra(cityGraph,source1);
					try{path = dj1.getPath(p.getV1());}catch(Exception e){}
					Collections.reverse(path);
					Iterator<vertex> it = path.listIterator();
					vertex v;
					while(it.hasNext()){
						v=it.next();
						if(!it.hasNext()){
							System.out.print(v.getName()+". ");
							break;
						}
						System.out.print(v.getName()+", ");
					}	
					bestSourceForThisTaxi = source1;
					bestStartForThisTaxi = p.getV1();
					System.out.println("time taken is "+brt.getBestTime()+" units");
				}
				else if(brt.getBestSource() ==p.getV2() && brt.getBestDestination() == source1){
					dj1=new dijkstra(cityGraph,source1);
					try{path = dj1.getPath(p.getV2());}catch(Exception e){}
					Collections.reverse(path);
					Iterator<vertex> it = path.listIterator();
					vertex v;
					while(it.hasNext()){
						v=it.next();
						if(!it.hasNext()){
							System.out.print(v.getName()+". ");
							break;
						}
						System.out.print(v.getName()+", ");
					}	
					bestSourceForThisTaxi = source1;
					bestStartForThisTaxi = p.getV2();
					System.out.println("time taken is "+brt.getBestTime()+" units");
				}
				else if(brt.getBestSource() ==p.getV1() && brt.getBestDestination() == source2){
					dj2=new dijkstra(cityGraph,source2);
					try{path = dj2.getPath(p.getV1());}catch(Exception e){}
					Collections.reverse(path);
					Iterator<vertex> it = path.listIterator();
					vertex v;
					while(it.hasNext()){
						v=it.next();
						if(!it.hasNext()){
							System.out.print(v.getName()+". ");
							break;
						}
						System.out.print(v.getName()+", ");
					}	
					bestSourceForThisTaxi = source2;
					bestStartForThisTaxi = p.getV1();
					System.out.println("time taken is "+brt.getBestTime()+" units");
				}
				else if(brt.getBestSource() ==p.getV2() && brt.getBestDestination() == source2){
					dj2=new dijkstra(cityGraph,source2);
					try{path = dj2.getPath(p.getV2());}catch(Exception e){}
					Collections.reverse(path);
					Iterator<vertex> it = path.listIterator();
					vertex v;
					while(it.hasNext()){
						v=it.next();
						if(!it.hasNext()){
							System.out.print(v.getName()+". ");
							break;
						}
						System.out.print(v.getName()+", ");
					}	
					bestSourceForThisTaxi = source2;
					bestStartForThisTaxi = p.getV2();
					System.out.println("time taken is "+brt.getBestTime()+" units");
				}				

				if(brt.getBestTime()<bestTime){
					bestTaxi = t;
					bestTime = brt.getBestTime();
					bestStart = bestStartForThisTaxi;
					bestSource = bestSourceForThisTaxi;
				}
			}
			if(flag==false){
				System.out.println("Sorry! No taxi available at this time");
				return;
			}
			System.out.println("** Chose "+bestTaxi.getTaxiName()+" to service the customer request ***");
			bestRouteTime brtForCust = getBestTimeAndRoute(sourcePosition,destinationPosition);

			System.out.print("Path of customer: ");
			LinkedList<vertex> cusPath=null;
			for(vertex vex : cityGraph.getListOfVertices()){
						vex.resetPsor();
					}
			dijkstra d = new dijkstra(cityGraph,brtForCust.getBestSource());
			try{cusPath=d.getPath(brtForCust.getBestDestination());}catch (Exception e) {}
			Iterator<vertex> ite = cusPath.listIterator();
			vertex v;
			while(ite.hasNext()){
				v=ite.next();
				if(!ite.hasNext()){
					System.out.print(v.getName()+". ");
					break;
				}
				System.out.print(v.getName()+", ");
			}
			bestTaxi.updatePosition(destinationPosition);			
			System.out.println("time taken is "+brtForCust.getBestTime()+" units");			
			bestTaxi.updateAvailability(time+bestTime+brtForCust.getBestTime());			
			for(vertex vex : cityGraph.getListOfVertices()){
				vex.resetPsor();
			}
			position dp = getDestPosForTaxi(destinationPosition);
			bestRouteTime bbb = getBestTimeAndRoute(destinationPosition,dp);
			bestTaxi.updateRoute(bbb,time+bestTime+brtForCust.getBestTime());
			return;
		}

		if(tokens[0].equals("printTaxiPosition")){
			int time = Integer.parseInt(tokens[1]);
			updateAllTaxiPositions(time);
			boolean flag=true;
			for(taxi t: taxiList){
				if(t.isAvailable(time)){
					flag=false;
					System.out.println(t.getTaxiName()+": "+t.getPosition().getV1().getName()+" "+t.getPosition().getV2().getName()+" "+t.getPosition().getInter());
				}
			}
			if(flag==true){
				System.out.println("No taxi is available at this time");
				return;
			}
			return;
		}
	}

	public bestRouteTime getBestTimeAndRoute(position p1,position p2){
		vertex source1 = p1.getV1();
		vertex source2 = p2.getV2();
		vertex destination1 = p2.getV1();
		vertex destination2 = p2.getV2();
		dijkstra dj1 = new dijkstra(cityGraph,source1);
		dijkstra dj2 = new dijkstra(cityGraph,source2);
		int interSource = p1.getInter();
		edge sConEdge = null;
		try{sConEdge = cityGraph.getEdge(source1,source2);}catch(Exception e){}
		int t11 = dj1.getMinDistance(p2.getV1()) + p2.getInter() + interSource;				
		int t12 = dj2.getMinDistance(p2.getV1()) + p2.getInter() + sConEdge.getCost() - interSource;
		int t22=0,t21=0;
		try{t22 = dj2.getMinDistance(p2.getV2()) + cityGraph.getEdge(p2.getV1(),p2.getV2()).getCost() - p2.getInter() + sConEdge.getCost() - interSource;
			t21 = dj1.getMinDistance(p2.getV2()) + cityGraph.getEdge(p2.getV1(),p2.getV2()).getCost() - p2.getInter() + interSource;}
		catch(Exception e){}
		int min=t11;
		if(t12<min) min=t12;
		if(t21<min) min=t21;
		if(t22<min) min=t22;
		//System.out.println(p1.getV1().getName()+p1.getV2().getName()+p1.getInter());
		//System.out.println(p2.getV1().getName()+p2.getV2().getName()+p2.getInter());
		if(min==t11){
			return new bestRouteTime(source1,destination1,min);
		}
		else if(min==t12){
			return new bestRouteTime(source2,destination1,min);	
		}
		else if(min==t21){
			return new bestRouteTime(source1,destination2,min);	
		}
		else{
			return new bestRouteTime(source2,destination2,min);	
		}
	}

	public position getDestPosForTaxi(position p){
		vertex v=null;
		if(p.getInter()==0) v = p.getV1(); 
		else{
			edge ee = null;
			try{ee=cityGraph.getEdge(p.getV1(),p.getV2());}catch(Exception e){}
			boolean isEven = ee.getCost()%2 == 0 ? true : false;
			if(p.getInter() == (ee.getCost() +1)/2 && !isEven)
			{
				if(p.getV1().getId()<p.getV2().getId())
					v = p.getV1();
				else v =p.getV2();
			}
			else if(p.getInter()<=ee.getCost()/2){
				v=p.getV1();
			}
			else if(p.getInter()>ee.getCost()/2){
				v=p.getV2();
			}
		}
		vertex nextVertex = cityGraph.getVertexFromId((v.getId()+1)%cityGraph.numVertices());
		position dP= new position(nextVertex,cityGraph.getNeighbourVertices(nextVertex).get(0),0);
		return dP;
	}

	public void updateAllTaxiPositions(int currentTime){
		for(taxi t : taxiList){
			if(!t.isAvailable(currentTime)) continue;		
			while(currentTime >= t.getLastUpdated()+t.getRoute().getBestTime()){
				position spos = new position(t.getRoute().getBestDestination(),cityGraph.getNeighbourVertices(t.getRoute().getBestDestination()).get(0),0);
				position dpos = getDestPosForTaxi(spos);
				bestRouteTime b = getBestTimeAndRoute(spos,dpos);
				t.updateRoute(b,t.getLastUpdated() + t.getRoute().getBestTime());
				System.out.println("At time "+t.getLastUpdated()+" taxi "+t.getTaxiName()+" chose a new destination vertex "+t.getRoute().getBestDestination().getName());
				t.updatePosition(spos);
			}
			vertex sou = t.getRoute().getBestSource();
			vertex desti = t.getRoute().getBestDestination();
			int excessTime = currentTime - t.getLastUpdated();
			position posAtTime = getPositionAtTime(t,excessTime);
			t.updatePosition(posAtTime);	
			bestRouteTime boom = getBestTimeAndRoute(posAtTime,new position(desti,cityGraph.getNeighbourVertices(desti).get(0),0));
			t.updateRoute(boom,currentTime);		
		}
	}

	public position getPositionAtTime(taxi t,int tIme){
		position currentPos = t.getPosition();
		position posiAtTime =null;
		vertex bSource = t.getRoute().getBestSource();
		vertex bDestination = t.getRoute().getBestDestination();
		int totalTimeOfTravel = t.getRoute().getBestTime();
		boolean near1 =false;
		edge abcd = null; 
		if(currentPos.getV1()==bSource){
			tIme = tIme- currentPos.getInter();
			near1 =true;		
		}
		else{
			try{abcd = cityGraph.getEdge(currentPos.getV1(),currentPos.getV2());}catch(Exception e){}
			tIme = tIme - (abcd.getCost() - currentPos.getInter());
		}
		if(tIme <=0 && near1){
			return new position(currentPos.getV1(),currentPos.getV2(),(-1)*tIme);
		}
		if(tIme <=0 && !near1){
			return new position(currentPos.getV1(),currentPos.getV2(),abcd.getCost()+tIme);
		}
		// at this point, i have source vertex,destination vertex and time
		for(vertex vex : cityGraph.getListOfVertices()){
			vex.resetPsor();
		}
		dijkstra dij = new dijkstra(cityGraph,bSource);
		LinkedList<vertex> llist=null;
		try{llist = dij.getPath(bDestination);}catch(Exception e){}
		Iterator<vertex> iter1 = llist.listIterator();
		Iterator<vertex> iter2 = llist.listIterator();
		int cost = 0;
		vertex nvertex =null,n2vertex=null;
		n2vertex = iter2.next();
		while(tIme > 0){
			nvertex= iter1.next();
			n2vertex = iter2.next();
			try{cost = cityGraph.getEdge(nvertex,n2vertex).getCost();}catch(Exception e){}
			tIme = tIme - cost;
		}
		tIme = tIme + cost;
		return new position(nvertex,n2vertex,tIme);
	}
}

class taxi{
	private String taxiName;
	private position currentPosition;
	private int availabileAfter;
	private bestRouteTime brt;
	private int lastUpdated;
	public taxi(String taxiName,position currentPosition){
		this.taxiName = taxiName;
		this.currentPosition = currentPosition;
		availabileAfter=-1;
	}
	public String getTaxiName(){
		return taxiName;
	}
	public position getPosition(){
		return currentPosition;
	}
	public void updatePosition(position v){
		currentPosition = v;
	}
	public boolean isAvailable(int time){
		return availabileAfter <=time;
	}
	public void updateAvailability(int time){
		availabileAfter = time;
	}
	public bestRouteTime getRoute(){
		return brt;
	}
	public void updateRoute(bestRouteTime brt,int lastUpdated){
		this.lastUpdated = lastUpdated;
		this.brt=brt;
	}
	public int getLastUpdated(){
		return lastUpdated;
	}
}

class bestRouteTime{
	private vertex source;
	private vertex destination;
	private int time;
	public bestRouteTime(vertex source,vertex destination,int time){
		this.source = source;
		this.destination = destination;
		this.time = time;
	}
	public int getBestTime(){
		return time;
	}
	public vertex getBestSource(){
		return source;
	}
	public vertex getBestDestination(){
		return destination;
	}
}