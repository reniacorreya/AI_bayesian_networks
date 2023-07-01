import java.util.ArrayList;

/**
 * Class that store and manage Bayesian Network Variable
 * @author 220031271
 */
public class BNVariable {


    private String name; // variable name
    private ArrayList<String> outcomes; // variable outcomes
    private ArrayList<String> probTable; // list of probability distribution in string
    private ArrayList<Double> probabilities; // list of probability distributions    
    private int index; // index of the variable
    private ArrayList<String> parents; // list of parents


    public ArrayList<Double> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(ArrayList<Double> probabilities) {
        this.probabilities = probabilities;
    }

    public BNVariable(String name, ArrayList<String> outcomes, int index) {

        this.name = name;
        this.outcomes = outcomes;
        this.index = index;
        this.parents = new ArrayList<>();

    } 
    
    public BNVariable(String name, ArrayList<String> outcomes, int index, ArrayList<String> parents,
                      ArrayList<String> probTable) {

        this.name = name;
        this.outcomes = outcomes;
        this.index = index;
        this.parents = parents;
        this.probTable = probTable;
        this.probabilities = new ArrayList<>();
        this.probTable.stream().forEach(str -> this.probabilities.add(Double.parseDouble(str)));
    } 

    /**
     * method to add a parent
     * @param parent parent node
     */
    public void addParent(String parent) {
        this.parents.add(parent);
    }   

    /**
     * method to validate probability 
     * @param isValid true if valid or false if invalid
     */
    public boolean validateProbabilty(){
        boolean isValid = true;
        // each set of outcomes for a given parent value appears adjacent to each other. Getting each set of probabilities and validating
        for(int i=0; i<=this.probabilities.size()-this.getNoOfOutcomes(); i=i+this.getNoOfOutcomes()){
            Double sum = 0.0;
            for(int j=0; j<this.getNoOfOutcomes(); j++){
                //return false if value is not within 0 to 1
                if(!(this.probabilities.get(i+j) >= 0 && this.probabilities.get(i+j) <= 1)){
                    isValid = false;
                    break;
                } 
                sum+=this.probabilities.get(i+j);
            }  
            //return false if sum is not 1
            if(sum!=1){
                isValid = false;
                break;
            }       
        }
        return isValid;
    }

   // Getters and setters methods
    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public ArrayList<String> getOutcomes() {
        return outcomes;
    }

    
    public void setOutcomes(ArrayList<String> outcomes) {
        this.outcomes = outcomes;
    }

    
    public int getIndex() {
        return index;
    }

   
    public void setIndex(int index) {
        this.index = index;
    }

    
    public ArrayList<String> getParents() {
        return parents;
    }

    
    public void setParents(ArrayList<String> parents) {
        this.parents = parents;
    }

    
    public ArrayList<String> getProbTable() {
        return probTable;
    }

    
    public void setProbTable(ArrayList<String> probTable) {
        this.probTable = probTable;
        this.probabilities = new ArrayList<>();
        probTable.stream().forEach(str -> probabilities.add(Double.parseDouble(str)));
    }

    public int getNoOfOutcomes(){
        int count = this.outcomes.size();
        return count;
    }

    public int getOutcomeIndex(String value){
        int index = this.outcomes.indexOf(value);
        return index;
    }

} 