import java.util.*;

public class Map {
	//initialize variables
	private Building[] buildings;
	private int numBuildings;
	private int maxBuildings;
	PriorityQueue<Road> dPQ; // use a priorityQ implementation of a heap from java
	Boolean[] isFinalized; //keeps track of which buildings have been finalized 
	int[] dCostEstimates; //keeps track of cost estimates to arrive at each building
	
	//map constructor
	public Map(int maxSize) { //Create a map that can hold maxSize amount of buildings
		buildings = new Building[maxSize];
		numBuildings = 0;
		maxBuildings = maxSize;
		dPQ = new PriorityQueue<Road>();
		dCostEstimates = new int[maxSize];
		isFinalized = new Boolean[maxSize];
	}

	//map methods from assignments
	public final boolean addBuilding(String name) {//add a building with a name

		if (findBuilding(name) >= 0) {
			throw new IllegalArgumentException("Building Name Already Exists");
		}
		buildings[numBuildings] = new Building(name);
		numBuildings++;
		checkMapSize(); //make sure there is enough room to add a future building
		return true;
	}
	public final boolean addRoad(String fromBuilding, String toBuilding, int length) {//add road between 2 buildings

		int indexFrom = findBuilding(fromBuilding);
		int indexTo = findBuilding(toBuilding);

		if (indexFrom == indexTo) 
			return false;

		if (indexFrom < 0) {// if source DNE
			addBuilding(fromBuilding); // add source
		}
		if (indexTo < 0) {// if des DNE
			addBuilding(toBuilding);// add des
		}

		indexFrom = findBuilding(fromBuilding);
		indexTo = findBuilding(toBuilding);

		LinkedList<Road> possibleDesList = buildings[indexFrom].roads;

		for (int k = 0; k < possibleDesList.size(); k++) { //make sure road DNE already
			Road travRoad = possibleDesList.get(k);
			if (travRoad.pointerIndex == indexTo)
				return false;
		}
		
		buildings[indexFrom].roads.add(new Road(indexTo, length));
		buildings[indexTo].roads.add(new Road(indexFrom, length));
		return true;
	}

	public final boolean addRoads(String fromBuilding, Collection<String> toBuildings, int length) {//add roads from a source to a list of destinations with the same road cost

		int indexFrom = findBuilding(fromBuilding);
		if (indexFrom < 0) {
			addBuilding(fromBuilding);
		}

		Iterator<String> itr = toBuildings.iterator();
		while (itr.hasNext()) {
			String building = itr.next();
			addRoad(fromBuilding, building, length);
		}
		return true;
	}

	public final boolean removeBuilding(String name) {//if building exists, remove it
		int indexOfToRemove = findBuilding(name);
		if (indexOfToRemove < 0) {
			return false;
		}

		for (int i = indexOfToRemove; i < numBuildings; i++) {
			buildings[i] = buildings[i + 1];
		}
		numBuildings--;

		for (int j = 0; j < numBuildings; j++) {
			Iterator<Road> itr = buildings[j].roads.iterator();
			while (itr.hasNext()) {
				Road travR = itr.next();

				if (travR.pointerIndex > indexOfToRemove) // ASK ROHAN
					travR.pointerIndex--;

				if (travR.pointerIndex == indexOfToRemove)
					itr.remove();
			}
		}
		return true;
	}

	public final boolean removeRoad(String fromBuilding, String toBuilding) {//given road exists, remove from both adjacency lists(fromBuilding and toBuildings's)
		int sourceIndex = findBuilding(fromBuilding);
		int destIndex = findBuilding(toBuilding);

		if (sourceIndex < 0 || destIndex < 0) {
			return false;
		}

		ListIterator<Road> itr = buildings[sourceIndex].roads.listIterator();
		while (itr.hasNext()) {
			Road travR = itr.next();
			if (travR.pointerIndex == destIndex) {
				itr.remove();
				break;
			}
		}

		itr = buildings[destIndex].roads.listIterator();
		while (itr.hasNext()) {
			Road travR = itr.next();
			if (travR.pointerIndex == sourceIndex) {
				itr.remove();
				return true;
			}
		}
		return false;
	}

	public final int shortestLength(String source, String destination) {//dijkstra to find shortest total length from a source to destination
		int sourceIndex = findBuilding(source);
		int destIndex = findBuilding(destination);
		if (sourceIndex < 0 || destIndex < 0) {
			System.out.println("Source or Destination DNE");
			return -999;
		}
		shortestSpanningTree(sourceIndex);
		return dCostEstimates[destIndex];
	}

	public final List<String> shortestPath(String source, String destination) {//use dijkstra to find the shortest path between a source and a destination
		int start = findBuilding(source);
		int end = findBuilding(destination);

		if (start == -1 || end == -1 || start == end)
			return null;

		shortestLength(source, destination);

		LinkedList<String> reverseP = new LinkedList<String>();
		LinkedList<String> returnP = new LinkedList<String>();

		Building endBuilding = buildings[end];

		Building curr = endBuilding;

		while (curr != null) {
			reverseP.add(curr.id);
			curr = curr.parent;
		}

		ListIterator<String> reverseIter = reverseP.listIterator();

		do {
			reverseIter.next();
		} while (reverseIter.hasNext());

		while (reverseIter.hasPrevious()) {
			returnP.add(reverseIter.previous());
		}
		return returnP;
	}

	public final int minimumTotalLength() {//uses prim's algo to return the length of the MST
		boolean[] inMST = new boolean[numBuildings];
		int[] rCost = new int[numBuildings];
		int countMST = 0;

		for (int i = 0; i < numBuildings; i++) {
			inMST[i] = false;
			rCost[i] = 99999;
		}

		PriorityQueue<Road> pPQ = new PriorityQueue<Road>();
		pPQ.add(new Road(0, 0));
		rCost[0] = 0;
		buildings[0].parent = null;
		countMST++;

		// Will remain inside the loop till the number of finalized buildings is less
		// than the number of buildings
		while (countMST < numBuildings) {
			if (pPQ.isEmpty())
				break;
			int i = pPQ.poll().pointerIndex;
			if (inMST[i])
				continue;
			inMST[i] = true;
			countMST++;

			Building currB = buildings[i];
			Iterator<Road> itr;

			// Processing the neighbors of Building b
			for (itr = currB.roads.iterator(); itr.hasNext();) {
				Road travRoad = itr.next();
				int curr = travRoad.pointerIndex;

				if (!inMST[curr]) {

					if (travRoad.cost < rCost[curr]) {
						rCost[curr] = travRoad.cost;
						buildings[curr].parent = buildings[i];
					}
					pPQ.add(new Road(curr, travRoad.cost));
				}
			}
		}

		int shortestLength = 0;
		for (int i = 0; i < rCost.length; i++)
			shortestLength += rCost[i];
		return shortestLength;
	}

	public final int secondShortestPath(String source, String destination) {//use dijstrka algo with a little change(handicap)
		// find the shortest path, and manipulate each road path just by a little bit
		// keep track of all these almost shortest path, find the shortest out of them
		
		//we handicap our method by not allowing it to use a specific road in the og dijkstra path

		if (source.equals(destination))
			return -99999;

		if (findBuilding(source) < 0 || findBuilding(destination) < 0)
			throw new IllegalArgumentException("one or both buildings DNE");

		List<String> sPath = shortestPath(source, destination); // Getting the list of the shortest path

		String[] sPathBuildings = new String[sPath.size()];

		Iterator<String> itr = sPath.iterator();
		for (int i = 0; i < sPathBuildings.length; i++)
			sPathBuildings[i] = itr.next();

		int secondShortestPathLength = 99999;

		for (int j = 0; j < sPathBuildings.length - 1; j++) {
			String sourceB = sPathBuildings[j];
			String destB = sPathBuildings[j + 1];

			int rCost = returnRoadCost(sourceB, destB);
			removeRoad(sourceB, destB);//handicapping our algorithm. Find the shortest path but without using THIS road
			int possibleSecondShortest = shortestLength(source, destination);
			if (possibleSecondShortest < secondShortestPathLength)//we want the shortest of the handicapped paths = secondshortest
				secondShortestPathLength = possibleSecondShortest;
			addRoad(sourceB, destB, rCost);
		}
		return secondShortestPathLength;
	}

	// helper methods
	
	//helper methods
	private void checkMapSize() {//make sure mapSize can fit another building, if not, grows map by 5
		if (numBuildings < maxBuildings) {
			return;//mapSize is okay
		}
		//if not, increase map size and corresponding arrays by 5
		int oldMaxSize = maxBuildings;
		Building[] oldMap = buildings;
		int[] oldDSP = dCostEstimates;
		Boolean[] oldE = isFinalized;
		maxBuildings += 5;

		buildings = new Building[maxBuildings];
		dCostEstimates = new int[maxBuildings];
		isFinalized = new Boolean[maxBuildings];

		for (int i = 0; i < oldMaxSize; i++) {
			buildings[i] = oldMap[i];
			dCostEstimates[i] = oldDSP[i];
			isFinalized[i] = oldE[i];
		}

	}

	private int findBuilding(String name) {//searches for a building, returns index of it in column list
		for (int i = 0; i < numBuildings; i++) {
			if (buildings[i] != null && buildings[i].id.toLowerCase().equals(name.toLowerCase()))
				return i;
		}
		return -999;
	}

	private void shortestSpanningTree(int head) {//constructs the MST from the start head
		int finalEstimates = 0;

		for (int i = 0; i < numBuildings; i++) {
			dCostEstimates[i] = 99999;
			isFinalized[i] = false;
		}

		dPQ.add(new Road(head, 0));
		dCostEstimates[head] = 0;
		buildings[head].parent = null;
		finalEstimates++;

		while (finalEstimates < numBuildings) {
			if (dPQ.isEmpty())
				return;

			int i = dPQ.remove().pointerIndex;
			if (isFinalized[i])
				continue;

			isFinalized[i] = true;
			finalEstimates++;

			int cost = -1;
			int pathEstimate = -1;
			Building curr = buildings[i];
			Iterator<Road> itr;

			for (itr = curr.roads.iterator(); itr.hasNext();) {
				Road road = itr.next();

				int neighbor = road.pointerIndex;

				if (!isFinalized[neighbor]) {
					cost = road.cost;
					pathEstimate = dCostEstimates[i] + cost;
					if (pathEstimate < dCostEstimates[neighbor]) {
						dCostEstimates[neighbor] = pathEstimate;
						buildings[neighbor].parent = buildings[i];
					}
					dPQ.add(new Road(neighbor, dCostEstimates[neighbor]));
				}
			}
		}

	}

	private int returnRoadCost(String b1, String b2) {
		int start = findBuilding(b1);
		int end = findBuilding(b2);

		if (start < 0 || end < 0)
			throw new IllegalArgumentException("one or both buildings DNE");

		LinkedList<Road> adjR = buildings[start].roads; // Getting the list of roads from the building at
														// the source
		Iterator<Road> itr;
		for (itr = adjR.iterator(); itr.hasNext();) {
			Road travR = itr.next();
			if (buildings[travR.pointerIndex].id.toLowerCase().equals(b2.toLowerCase()))
				return travR.cost;
		}
		return 0;
	}

	public void printGraph() { //print out the graph - adjaceny list presentation
		for (int i = 0; i < numBuildings; i++) {
			String name = "";
			if (buildings[i] != null) {
				name += buildings[i].id; // Adding the id of the building to the String
				System.out.println(name + " to:");
				if (!buildings[i].roads.isEmpty()) {
					String s = "\t";
					for (Road r : buildings[i].roads) { // Traversing through the Linked List of roads
						if (buildings[r.pointerIndex] != null) {
							s += buildings[r.pointerIndex].id; // Adding the id of the destination building to
							s += " ("+String.valueOf(r.cost) + ")";											// the String
							s += ", ";
						}
					}
					s = s.substring(0, s.length() - 2);
					System.out.println(s);
				}
				
			}
		}
	}
	public final void pathprint(List<String> path) {//print the path of a list

		if (path.size() == 0) {
			System.out.println("no path");
			return;
		}

		String printStr = "";
		for (String s : path)
			printStr += s + " to ";
		printStr = printStr.substring(0, printStr.length() - 3);

		System.out.println(printStr);
	}
	
	//nested classes
	private class Building {//building class, buildings have a name, adjancent link list, and a parent
		private String id;// name of buidling
		private LinkedList<Road> roads;// adjacent linked list
		private Building parent;// parent of building

		public Building(String name) {
			id = name;
			roads = new LinkedList<Road>();
		}
	}

	private class Road implements Comparator<Road>, Comparable<Road> {//roads connect buildings, and costs should be compared! - implements comparable
		private int pointerIndex;//where the road eads to
		private int cost;

		private Road(int endNode, int cost) {//create a road!
			pointerIndex = endNode;
			this.cost = cost;
		}

		@Override
		public int compare(Road r1, Road r2) {
			if (r1.cost < r2.cost) {
				return -1;
			} else if (r1.cost > r2.cost) {
				return 1;
			} else {
				return 0;
			}
		}

		@Override
		public int compareTo(Road r) {
			if (this.cost < r.cost) {
				return -1;
			} else if (this.cost > r.cost) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	//main - create map of NRV at CWRU!
	public static void main(String args[]) {
		//USING MAP STRUCTURE FROM LECTURE
		Map nRVILLAGE = new Map(2);//North Residential Village
		nRVILLAGE.addBuilding("Village");
		nRVILLAGE.addBuilding("Dennys");
		nRVILLAGE.addBuilding("Plum");
		nRVILLAGE.addBuilding("CoffeeHouse");
		nRVILLAGE.addBuilding("Smith");
		nRVILLAGE.addBuilding("Taft");
		nRVILLAGE.addBuilding("Taplin");
		nRVILLAGE.addBuilding("CIM");
		nRVILLAGE.addBuilding("Cutter");
		nRVILLAGE.addBuilding("MINI CUTTER");
		//Adding roads
		nRVILLAGE.addRoad("Village", "Dennys", 9);
		nRVILLAGE.addRoad("Village", "Plum", 10);
		nRVILLAGE.addRoad("Dennys", "Taft", 4);
		nRVILLAGE.addRoad("Dennys", "CoffeeHouse", 5);
		nRVILLAGE.addRoad("Dennys", "Plum", 5);
		nRVILLAGE.addRoad("Plum", "CoffeeHouse", 8);
		nRVILLAGE.addRoad("CoffeeHouse", "Smith", 3);
		nRVILLAGE.addRoad("Smith", "Taft", 9);
		nRVILLAGE.addRoad("Smith", "Cutter", 3);
		nRVILLAGE.addRoad("Taft", "Taplin", 2);
		nRVILLAGE.addRoad("Taft", "CIM", 12);
		nRVILLAGE.addRoad("Taplin","CIM",14);
		nRVILLAGE.addRoad("CIM", "Cutter", 9);
		nRVILLAGE.addRoad("MINI CUTTER", "Cutter", 20);

		
		ArrayList<String> extraPaths = new ArrayList<String>();
		extraPaths.add("Plum");
		extraPaths.add("Dennys");
		extraPaths.add("Village");
		nRVILLAGE.addRoads("CIM", extraPaths, 30);
		
		//Printing the graph
		//testing add buildings and roads
		nRVILLAGE.printGraph();

		
		nRVILLAGE.removeBuilding("MINI CUTTER");
		nRVILLAGE.removeRoad("CIM", "Taplin");
		//testing remove road and building
		nRVILLAGE.printGraph();
		System.out.println("\n");
		
		
		int testOne = nRVILLAGE.shortestLength("Village", "Taft");
		System.out.println("The shortest length from the Village to Taft is ");
		System.out.println(testOne);
		System.out.println();
		
		List<String> shortestPathVtoT = nRVILLAGE.shortestPath("Village", "Taft");
		System.out.println("The shortest path from the Village to Taft looks like: ");
		nRVILLAGE.pathprint(shortestPathVtoT);
		System.out.println();
		
		int mstNRVlength = nRVILLAGE.minimumTotalLength();
		System.out.println("The minimum total length of the spanning tree of the NRV is: " + String.valueOf(mstNRVlength));
		
		int ssDDtoV = nRVILLAGE.secondShortestPath("Dennys", "Village");
		System.out.println("The second shortest from Dennys to the Village is length: " + String.valueOf(ssDDtoV));
	}

}
