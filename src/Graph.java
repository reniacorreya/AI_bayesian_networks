/**
 * Class that stores graph as adjacency matrix and other operations like cycle check, sorting 
 * @author 220031271
 */
import java.util.*;
public class Graph {
    private boolean[][] adjMatrix;
    private int numVertices;

    public boolean[][] getAdjMatrix() {
        return adjMatrix;
    }

    public void setAdjMatrix(boolean[][] adjMatrix) {
        this.adjMatrix = adjMatrix;
    }
  
    public Graph(int numVertices) {
      this.numVertices = numVertices;
      adjMatrix = new boolean[numVertices][numVertices];
    }
  
    
    /**
     * method to add edges
     * @param i index from which edge is directed from
     * @param j index to which the edge is directed to
     */
    public void addEdge(int i, int j) {
      adjMatrix[i][j] = true;
    }
    
    
    /**
     * method to check if the graph has cyclic paths
     * @return true if cyclic path is found, else return false
     */
    public boolean checkCycle() {
        boolean[][] graph = this.adjMatrix;
        int n = graph.length;
        int[] in = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (graph[i][j] == true) {
                    in[j]++;
                }
            }
        }
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (in[i] == 0) {
                q.offer(i);
            }
        }
        int count = 0;
        while (!q.isEmpty()) {
            int node = q.poll();
            count++;
            for (int i = 0; i < n; i++) {
                if (graph[node][i] == true) {
                    in[i]--;
                    if (in[i] == 0) {
                        q.offer(i);
                    }
                }
            }
        }

        if(count != n)
            return true;
        else 
            return false;
    }

    /**
     * method to sort the graph nodes in topological order
     * @return sorted list
     */
    public ArrayList<Integer> topologicalSort() {
        boolean[][] graph = this.adjMatrix;
        int n = graph.length;
        int[] in = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (graph[i][j] == true) {
                    in[j]++;
                }
            }
        }
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (in[i] == 0) {
                q.offer(i);
            }
        }
        ArrayList<Integer> topologicalSortedList = new ArrayList<>();
        while (!q.isEmpty()) {
            int node = q.poll();
            topologicalSortedList.add(node);
            for (int i = 0; i < n; i++) {
                if (graph[node][i] == true) {
                    in[i]--;
                    if (in[i] == 0) {
                        q.offer(i);
                    }
                }
            }
        }
        return topologicalSortedList;
    }
  }