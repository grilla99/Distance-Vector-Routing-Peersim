package DVProtocol;

import java.util.TreeMap;

import javax.swing.JOptionPane;

import DVProtocol.Path;
import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;
import peersim.vector.SingleValueHolder;

public class DVProtocol extends SingleValueHolder implements CDProtocol {

	//initialize variables
	public static final int INIT = 0;
	public static final int BCST = 1;
	public static final int STOP = 2;
	private int stage;
	private int  hits;

	private boolean updates = true;
	private TreeMap<Long, Path> netMap; 
										
	//default constructor
	public DVProtocol(String prefix) {
		super(prefix);
		stage = INIT;
		hits = 0;
	}

	//@Override
	public void nextCycle(Node host, int protocolID) {
		
		
		//Initialize the linkable protocol and get host node
		Linkable lnk = (Linkable) host.getProtocol(FastConfig.getLinkable(protocolID));
		Node neighbor = null;
		
		//
		switch (stage) {
		
		
		case INIT:
			// Initialize network map at the first cycle
			netMap = new TreeMap<Long, Path>();
			netMap.put(host.getID(), new Path(host.getID(), host.getID(), 0));
			
			// Add neighbour nodes to the network map 
			for (int i = 0; i < lnk.degree(); i++) {
				neighbor = lnk.getNeighbor(i);
				netMap.put(neighbor.getID(), new Path(neighbor.getID(), neighbor.getID(), 1));
			}
			
			//set stage to binary search stage
			stage = BCST;
			break;

		case BCST:
			// BCST the local graph 
			// loop through network
			for (int i = 0; i < lnk.degree(); i++) {
				// Get details of neighbour nodes
				neighbor = lnk.getNeighbor(i);
				DVProtocol protocol = (DVProtocol) neighbor.getProtocol(protocolID);
				
				// Create a list of predecessor nodes ( a treemap )
				TreeMap<Long, Path> msg = new TreeMap<Long, Path>();
				for (Path p : netMap.values()) {
					// add this as predecessor node
					msg.put(p.destination, new Path(p.destination, host.getID(), p.hops + 1));
				}
				// Call update method and pass message
				protocol.update(msg);
				msg = null;
				
			}
			
			// Stop code if it halts
			if (updates == false){
				hits ++;
			}else{
				hits = 0;
			}
			updates = false;
			
			
			break;

		case STOP:
			
			break;
		}
	}


	public void update(TreeMap<Long, Path> msg) {
		updates = false;
		for (Path p : msg.values()) {
			// destination node is not in local map, add it, use sender node as
			// predecessor
			if (!netMap.containsKey(p.destination)) {
				netMap.put(p.destination, new Path(p.destination, p.predecessor, p.hops));
				updates = true;
			}
		}
		
		// Loop through netmap and update the number of hops
		// Number of hops is updated when host 2 destination > neighbour 2 destination
		for (Path dst : netMap.values()) {
			int ths2dst = dst.hops;
			int nbr2dst = Integer.MAX_VALUE;
			if (msg.containsKey(dst.destination))
				nbr2dst = msg.get(dst.destination).hops;
				
			if (nbr2dst < ths2dst) {
				dst.hops = nbr2dst;
				dst.predecessor = msg.get(dst.destination).predecessor;
				//update
				updates = true;
			}}
	}

	public TreeMap<Long, Path> getNetMap() {
		return netMap;
	}

	public boolean hasUpdates() {
		return hits < 3;
	}
}