package MultipleAgents;

/**
 * Created by noa on 10-Oct-16.
 */

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;


public class Graph {
    private static final String NEWLINE = System.getProperty("line.separator");
    private int V;
    private int E;
    private boolean[][] adj;
    Random r;

    // empty graph with V vertices
    public Graph(int V) {
        if (V < 0) throw new RuntimeException("Number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        this.adj = new boolean[V][V];
        r = new Random(100);
    }

    //return subGraph
    public  Graph(int V, List<IntIntPair> edges) {
        this(V);
        for ( IntIntPair pair: edges) {
            addEdge(pair.firstNum,pair.secondNum);
        }
    }

    // random graph with V vertices and E edges
    public Graph(int V, int E) {
        this(V);
        if (E < 0) throw new RuntimeException("Number of edges must be nonnegative");
        if (E > V * (V - 1) + V) throw new RuntimeException("Too many edges");

        // can be inefficient
        for(int v =0; v < V; v++)
        {
            int w = r.nextInt(V);
            while(w == v)
            {
                w = r.nextInt(V);
            }
            addEdge(v, w);
        }
        while (this.E != E) {
            int v = r.nextInt(V);
            int w = r.nextInt(V);
            while(w == v)
            {
                w = r.nextInt(V);
            }
            addEdge(v, w);
        }
    }

    public Graph subGrah( List <Integer> nodes) {
        Graph retGraph = new Graph(nodes.size());
        for (int i = 0; i < retGraph.V-1; i++) {
            for(int j = i+1; j <V; j++)
            {
                if(contains(i,j))
                addEdge(i,j);
            }
        }
        return retGraph;
    }


    // number of vertices and edges
    public int V() {
        return V;
    }

    public int E() {
        return E;
    }


    // add undirected edge v-w
    public void addEdge(int v, int w) {
        if (!adj[v][w])
            E++;
        adj[v][w] = true;
        adj[w][v] = true;
    }

    // does the graph contain the edge v-w?
    public boolean contains(int v, int w) {
        return adj[v][w];
    }

    // return list of neighbors of v
    public Iterable<Integer> adj(int v) {
        return new AdjIterator(v);
    }

    // support iteration over graph vertices
    private class AdjIterator implements Iterator<Integer>, Iterable<Integer> {
        private int v;
        private int w = 0;

        AdjIterator(int v) {
            this.v = v;
        }

        public Iterator<Integer> iterator() {
            return this;
        }

        public boolean hasNext() {
            while (w < V) {
                if (adj[v][w]) return true;
                w++;
            }
            return false;
        }

        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return w++;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    // string representation of Graph - takes quadratic time
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (int w : adj(v)) {
                s.append(w + " ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }
}