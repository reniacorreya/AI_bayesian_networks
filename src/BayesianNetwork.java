import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class that stores Bayesian Network *
 * @author 220031271
 */
public class BayesianNetwork {

    //Map of all nodes
    private final HashMap<String, BNVariable> nodeList; 
    private Graph graph;

    public BayesianNetwork() {
        nodeList = new HashMap<>();
    }
    
    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    

    /**
     * method to add a node to the network     
     * @param name variable name
     * @param outcomes List of outcomes of the variable
     * @param index index of the variable
     */
    public void addNode(String name, ArrayList<String> outcomes, int index) {
        this.nodeList.put(name, new BNVariable(name, outcomes, index));
    } 

    
     /**
     * method to add a node to the network     
     * @param name variable name
     * @param outcomes List of outcomes of the variable
     * @param index index of the variable
     * @param parents list of parents
     * @param probTable probability table
     */
    public void addNode(String name, ArrayList<String> outcomes, int index, ArrayList<String> parents,
                            ArrayList<String> probTable) {
        this.nodeList.put(name, new BNVariable(name, outcomes, index, parents, probTable));
    } 

    /**
     * Get variable object by name
     * @param name Name of the variable to get
     * @return variable object, null if not exist
     */
    public BNVariable getVariable(String name) {
        return nodeList.get(name);
    } 

    /**
     * method to get set of variable names in the network    
     * @return Hash set of variable names
     */
    public HashSet<String> getVariableNames() {
        return new HashSet<>(nodeList.keySet());
    } 

    /**
     * method to get set of variable in the network    
     * @return Hash set of variable objects
     */
    public HashSet<BNVariable> getVariables() {
        return new HashSet<>(nodeList.values());
    }

    /**
     * Get variable object by index
     * @param name index of the variable to get
     * @return variable object, null if not exist
     */
    public BNVariable getVariableByIndex(int index) {
        for (BNVariable var : nodeList.values()) {
            if(index == var.getIndex()){
                return var;
            }
        } 
        return null;
    }

    /**
     * Method to validate probabilities
     * @return list of variables with invalid probability distribution
     */
    public ArrayList<String> validateProbabilities(){
        ArrayList<String> invalidVariables = new ArrayList<>();
        for (BNVariable var : nodeList.values()) {
            if(!var.validateProbabilty()){
                invalidVariables.add(var.getName());
            }
        }
        return invalidVariables;
    }    
} 
