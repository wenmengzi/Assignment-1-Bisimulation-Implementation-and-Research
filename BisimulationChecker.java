import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class BisimulationChecker {

	private List<Triple> lp;
	private List<Triple> lq;
	private Map<Integer, HashMap<String, Set<Integer>>> stateTrainsitions;
	private Set<Integer> ps;
	private Set<Integer> qs;
	private Set<Integer> states;
	private Set<String> simbols;

	private Set<String> simp;
	private Set<String> simq;

	private List<Set<Integer>> q;
	// Set<Set<Integer>> q;

	// Set<Set<Integer>> q1;

	private Set<Set<Integer>> waiting;

	private int pstart;
	private int qstart;
	private boolean hasRead = false;
	private Scanner console = new Scanner(System.in);

	public BisimulationChecker() {
		states = new HashSet<Integer>();
		ps = new HashSet<Integer>();
		qs = new HashSet<Integer>();
		q = new LinkedList<Set<Integer>>();
		// q1=new HashSet<Set<Integer>>();
		waiting = new HashSet<Set<Integer>>();
		simbols = new HashSet<String>();
		simp = new HashSet<String>();
		simq = new HashSet<String>();
		lp = new ArrayList<BisimulationChecker.Triple>();
		lq = new ArrayList<BisimulationChecker.Triple>();
		stateTrainsitions = new HashMap<Integer, HashMap<String, Set<Integer>>>();
	}

	public void readInput(String fileP, String fileQ) {

		if (fileP == null) {
			fileP = queryForFileName("P");

		}
		if (fileQ == null) {
			fileQ = queryForFileName("Q");
		}

		try {
			readFile(fileP, 0);
			readFile(fileQ, 1);
			hasRead = true;

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

	}

	public void performBisimulation() {
		if (!hasRead) {
			System.out.println("Error, the input files have not been read yet");
			return;
		}
		setUp();
		initial();
		compute();
	}

	public void writeOutput(String filename) {
		if (filename == null) {
			System.out.println("please input the output file name");

			filename = console.nextLine();
			// in.close();
		}

		try {
			writeFile(filename);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}

	private static class Triple {
		int x;
		String a;
		int y;

		public Triple(int x, String a, int y) {
			super();
			this.x = x;
			this.a = a;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + x + "," + a + "," + y + ")";
		}

	}

	private void setUp() {
		for (Triple t : lp) {
			ps.add(t.x);
			ps.add(t.y);
			simp.add(t.a);

		}

		for (Triple t : lq) {
			qs.add(t.x);
			qs.add(t.y);
			simq.add(t.a);

		}

		simbols.addAll(simp);
		simbols.addAll(simq);
		states.addAll(ps);
		states.addAll(qs);

		for (Integer e : states) {
			HashMap<String, Set<Integer>> m = new HashMap<String, Set<Integer>>();

			for (String a : simbols) {
				m.put(a, new HashSet<Integer>());
			}

			stateTrainsitions.put(e, m);

		}

		for (Triple t : lp) {
			stateTrainsitions.get(t.x).get(t.a).add(t.y);

		}

		for (Triple t : lq) {
			stateTrainsitions.get(t.x).get(t.a).add(t.y);

		}

	}

	private void initial() {
		// q1.add(states);
		q.add(states);
		waiting.add(states);
	}

	private void compute() {
		while (!waiting.isEmpty()) {
			Set<Integer> p1 = waiting.iterator().next();
			waiting.remove(p1);
			for (String a : simbols) {
				splitAll(a, p1);
			}
		}
	}

	private static void printSt(PrintWriter p, List<Triple> ts) {
		for (int i = 0; i < ts.size(); i++) {
			p.print(ts.get(i));
			if (i != ts.size() - 1) {
				p.print(",");
			}
		}
	}


	private boolean isBisimilar() {

		for (Set<Integer> s : q) {
			if (s.contains(pstart) && s.contains(qstart)) {
				return true;
			}
		}

		return false;
	}

	private static void writeProcess(PrintWriter pw, String pn, Set<Integer> state,
			Set<String> simbol,List<Triple> triples) {
		pw.println("Process " + pn);
		pw.print("S = ");

		int count = 0;
		for (Integer e : state) {
			pw.print(e);
			if (count != state.size() - 1) {
				pw.print(",");
			}
			count += 1;
		}
		pw.println();

		pw.print("A = ");

		count = 0;
		for (String e : simbol) {
			pw.print(e);
			if (count != simbol.size() - 1) {
				pw.print(",");
			}
			count += 1;
		}
		pw.println();
		pw.print("T = ");
        printSt(pw,triples);
        pw.println();

	}

	private void writeFile(String fn) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(fn));

		writeProcess(pw, "P", ps, simp,lp);
		writeProcess(pw, "Q", qs, simq,lq);

		pw.println("Bisimulation Results");

		for (Set<Integer> s : q) {
			int count = 0;
			for (Integer e : s) {
				pw.print(e);
				if (count != s.size() - 1) {
					pw.print(",");
				}
				count += 1;
			}
			pw.println();

		}

		pw.println("Bisimulation Answer");
		if (isBisimilar()) {
			pw.println("Yes");
		} else {
			pw.println("No");
		}

		pw.close();
	}


	private void readFile(String fn, int t) throws FileNotFoundException {
		Scanner in = new Scanner(new File(fn));
		String line = in.nextLine();
		int count = 0;
		while (line.charAt(0) != '!') {
			int d = line.indexOf(",");
			int m = line.indexOf(":");

			int u = Integer.parseInt(line.substring(0, d));
			if (count == 0) {
				if (t == 0) {
					pstart = u;
				} else {
					qstart = u;
				}

				count++;
			}

			if (line.charAt(d + 1) == ' ') {
				d++;
			}

			String a = line.substring(d + 1, m);

			if (line.charAt(m + 1) == ' ') {
				m++;
			}
			int v = Integer.parseInt(line.substring(m + 1));

			if (t == 0) {
				lp.add(new Triple(u, a, v));
			} else {
				lq.add(new Triple(u, a, v));
			}

			line = in.nextLine();
		}
		in.close();
	}

	private void split(Set<Integer> p, Set<Integer> p1, String a, List<Set<Integer>> t) {
		Set<Integer> x = new HashSet<Integer>();
		Set<Integer> y = new HashSet<Integer>();
		for (Integer s : p) {
			HashMap<String, Set<Integer>> m = stateTrainsitions.get(s);
			boolean found = false;
			if (m.containsKey(a)) {
				for (Integer s1 : m.get(a)) {
					if (p1.contains(s1)) {
						found = true;
						break;
					}
				}
			}
			if (found) {
				x.add(s);
			} else {
				y.add(s);
			}

		}

		if (x.size() != 0 && x.size() != p.size()) {
			// q.remove(p);
			// q.add(x);
			// q.add(y);
			// System.out.println("&&& "+p+" "+p1+" "+a+" "+x);
			t.add(x);
			t.add(y);

			waiting.remove(p);
			waiting.add(x);
			waiting.add(y);

		} else {
			t.add(p);
		}

		// else{
		// return null;
		// }
		//

	}

	private void splitAll(String a, Set<Integer> p1) {
		// Set<Set<Integer>> qc=new HashSet<Set<Integer>>(q);
		List<Set<Integer>> t = new LinkedList<Set<Integer>>();
		for (Set<Integer> p : q) {
			split(p, p1, a, t);
		}

		q = t;

		// System.out.println(q);
	}

	

	private String queryForFileName(String type) {

		System.out.println("please input file name for " + type);
		boolean valid = false;
		String f = null;
		while (!valid) {

			f = console.nextLine();
			try {

				Scanner in = new Scanner(new File(f));
				valid = true;
				in.close();
			} catch (Exception e) {
				System.out.println("invalid file name, try again");
			}

		}

		return f;
	}

	public static void main(String[] args) throws FileNotFoundException {
		BisimulationChecker bc = new BisimulationChecker();
		String f1 = "input1";
		String f2 = "input2";
		String out = "out";
		f1 = null;
		f2 = null;
		out = null;
		bc.readInput(f1, f2);
		bc.performBisimulation();
		bc.writeOutput(out);
	}
}
