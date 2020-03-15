package DVProtocol;

import java.util.TreeMap;

import DVProtocol.Path;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

//Control class to observe DVProtocol

public class DVObserver implements Control {

	public static final String PAR_PROT = "protocol";

	private final int pid;

	public DVObserver(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
	}

	@Override
	public boolean execute() {
		for (int i = 0; i < Network.size(); i++) {

			Node node = Network.get(i);
			DVProtocol protocol = (DVProtocol) node.getProtocol(pid);
			TreeMap<Long, Path> paths = protocol.getNetMap();

			if (paths == null)
				continue;

			System.out.printf("ND#%3d->| ", Network.get(i).getID());
			for (Path p : paths.values())
				System.out.printf("%3d,", p.destination);
			System.out.printf(" |%s\n    hops| ", (protocol.hasUpdates()) ? "UPDATE" : "STOPPED");
			for (Path p : paths.values())
				if (p.hops == Integer.MAX_VALUE)
					System.out.printf(" X ,");
				else
					System.out.printf("%3d,", p.hops);
			System.out.printf(" |\n     via| ");
			for (Path p : paths.values())
				System.out.printf("%3d,", p.predecessor);
			System.out.printf(" |\n----------------------------------------------------------------------------\n");
		}
		return false;
	}
}
