package DVProtocol;

/**
 * The class represent a shortest path from source node(host node) to
 * destination
 */
public class Path {
	public long destination; // destination node ID
	public long predecessor; // predecessor node ID from source node
	public int hops; // total path cost to destination

	/**
	 * A constructor.
	 * 
	 * @param destination
	 * @param predecessor
	 * @param cost
	 */
	public Path(long destination, long predecessor, int hops) {
		this.destination = destination;
		this.predecessor = predecessor;
		this.hops = hops;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(destination);
	}

	@Override
	public boolean equals(Object o) {
		return this.hashCode() == o.hashCode();
	}

	@Override
	public String toString() {
		return "[" + destination + "->" + predecessor + ", " + hops + "]";
	}
}
