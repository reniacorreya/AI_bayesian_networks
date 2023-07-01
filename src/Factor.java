import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Class that perform factor operations
 * @author 220031271
 */
public class Factor {
    
    private ArrayList<String> factorVariableNames;  //variables involved in the factor
    ArrayList<Double> factorValues;                 //probabilities stored as a list

     /**
     * method to perform assign operation on factor
     * @param variable variable to which assignment is to be done
     * @param value value to be assigned
     * @param bn Bayesian Network object
     */
    public void assign(String variable, String value, BayesianNetwork bn){
        // get stride value of the variable
        int stride = getStride(variable, bn);
        //get index of the value in outcomes list of the variable
        int valueIndex  = bn.getVariable(variable).getOutcomeIndex(value);
        //get no of outcomes
        int outcomeCount = bn.getVariable(variable).getNoOfOutcomes();
        //initialize new value list
        ArrayList<Double> assignedValues = new ArrayList<Double>();
        for(int i=0;; i++){
            int startIndex = stride * ((outcomeCount * i) + valueIndex); //get the initial index in the stride
            if(this.getFactorValues().size()<=startIndex)
                break;
            for(int j=0;j<stride;j++){
                assignedValues.add(this.getFactorValues().get(startIndex + j)); //get the values in the same step(stride)
            }

        }
        this.factorValues = assignedValues; //assign new values to the new factor
        this.factorVariableNames.remove(variable); //remove assigned variable name from the new factor
    }


    /**
     * method to perform join operation on factor
     * this method accepts more than one comon variable within 2 factors
     * @param f factor with which join operation should be performed
     * @param bn Bayesian Network object
     * @return return joined factor
     */
    public Factor joinFactor(Factor f, BayesianNetwork bn){

        Factor joinedFactor = new Factor();
        
        ArrayList<String> f1Var = this.getFactorVariableNames(); //variables of first factor
        ArrayList<String> f2Var = f.getFactorVariableNames();    //variables of second factor  
        ArrayList<String> joinedVar = new ArrayList<>();        //variables in the result
        joinedVar.addAll(f1Var);
        for (String f2V : f2Var) {
            if(!joinedVar.contains(f2V)){
                joinedVar.add(f2V);
            }
        }

        joinedFactor.setFactorVariableNames(joinedVar); //set variable names in the new factor

        //get size of new probability values of the joined factor
        int sizeOfJoinedFactorValueArray = 1;
        if(!joinedVar.isEmpty()){
            String firstVarInJoinedFactor = joinedVar.get(0);
            BNVariable firstBnVar = bn.getVariable(firstVarInJoinedFactor);
            int noOfOutComesForFirstVar = firstBnVar.getNoOfOutcomes();
            int strideOfFirstVarInJoinedFactor = joinedFactor.getStride(firstVarInJoinedFactor, bn);
            sizeOfJoinedFactorValueArray = strideOfFirstVarInJoinedFactor * noOfOutComesForFirstVar;
        }

        //create and initialize new value list with 0
        ArrayList<Double> joinedFactorValues = new ArrayList<>(Collections.nCopies(sizeOfJoinedFactorValueArray, 0.0));
        joinedFactor.setFactorValues(joinedFactorValues);

        HashMap<String,Integer> f1Assignment = new HashMap<String, Integer>();
        HashMap<String,Integer> f2Assignment = new HashMap<String, Integer>();

        for(int i=0; i< joinedFactorValues.size();i++){
            //get assignment for each row index in the new value list
            HashMap<String,Integer> assignment = joinedFactor.getAssignmentForIndex(i, bn);

            //split the assignment and get corresponding value to be multiplied in both the factors
            for (String f1Variable : f1Var) {
                f1Assignment.put(f1Variable, assignment.get(f1Variable));
            }
            int indexInFirstFactor = this.getIndexForAssignment(f1Assignment, bn);
            Double factorValue1 = this.getFactorValues().get(indexInFirstFactor);


            for (String f2Variable : f2Var) {
                f2Assignment.put(f2Variable, assignment.get(f2Variable));
            }
            int indexInSecondFactor = f.getIndexForAssignment(f2Assignment, bn);
            Double factorValue2 = f.getFactorValues().get(indexInSecondFactor);

            //add the product in the new map
            joinedFactorValues.set(i, factorValue1*factorValue2);
        }

        joinedFactor.setFactorValues(joinedFactorValues);
        return joinedFactor;
    }

    /**
     * method to perform sum operation on factor
     * @param nuisanceVar nuisance variable on which the sum operation is performed
     * @param bn Bayesian Network object
     * @return return sum factor
     */
    public Factor sumFactor(String nuisanceVar, BayesianNetwork bn){

        //create new factor
        Factor eliminatedFactor = new Factor();    
        //get variables involved in the initial factor    
        ArrayList<String> f1Var = this.getFactorVariableNames();   

        //set the variable names in the new factor - nuisance variable will be removed from the new factor
        ArrayList<String> varInNewFactor = new ArrayList<String>();
        varInNewFactor.addAll(f1Var);
        varInNewFactor.remove(nuisanceVar);
        eliminatedFactor.setFactorVariableNames(varInNewFactor);

        //calculate the size of values in the new factor and initialise the new factor values list with all 0
        BNVariable nuisanceBnVar = bn.getVariable(nuisanceVar);
        int noOfOutComesinNuisanceVar = nuisanceBnVar.getNoOfOutcomes();
        //size of new factor value list will be reduced to (initial size / no of outcomes in nuisance variable)
        int sizeOfEliminatedFactorValueArray = this.getFactorValues().size() / noOfOutComesinNuisanceVar;
        ArrayList<Double> eliminatedFactorValues = new ArrayList<>(Collections.nCopies(sizeOfEliminatedFactorValueArray, 0.0));
        eliminatedFactor.setFactorValues(eliminatedFactorValues);

        HashMap<String,Integer> assignmentInFactor = new HashMap<String, Integer>();

        for(int i=0; i< eliminatedFactorValues.size();i++){
            HashMap<String,Integer> assignment = eliminatedFactor.getAssignmentForIndex(i, bn);
            Double sum = 0.0;
            //iterate over each row of the new value list and partially initialise the assignment based on the variables in the new factor
            for (String variable : varInNewFactor) {
                assignmentInFactor.put(variable, assignment.get(variable));
            }
            //iterate over the outcomes of the nuisance variable by keeping the partial assignments fixed 
            //find the sum corresponding to each assignment in the new factor
            for(int j=0; j<noOfOutComesinNuisanceVar; j++) {
                assignmentInFactor.put(nuisanceVar, j);
                int indexInFactor = this.getIndexForAssignment(assignmentInFactor, bn);
                sum += this.getFactorValues().get(indexInFactor);
            }
            //assign the sum to corresponding location in the list
            eliminatedFactorValues.set(i, sum);
        }
        //set the final value list to the new factor
        eliminatedFactor.setFactorValues(eliminatedFactorValues);
        return eliminatedFactor;        
    }

    /**
     * method to perform normalisation on factor
     */
    public void normalise(){
        ArrayList<Double> values = this.getFactorValues();
        ArrayList<Double> normalisedValues = new ArrayList<Double>();
        Double totalSumAlpha = 0.0; 

        //find the total sum by adding all values
        for (Double val : values) { 
            totalSumAlpha+=val;
        }

        //divide each value in the list with the total sum
        for (Double val : values) {
            normalisedValues.add(val / totalSumAlpha); 
        }

        //set the normalised value list to the factor
        this.setFactorValues(normalisedValues);
    } 

    /**
     * method to find stride value of a variable in the list of values in the factor 
     * @param variable variable in the factor for which the stride needs to be calculated 
     * @param bn Bayesian Network object
     * @return return the stride of the variable in the factor
     */
    private int getStride(String variable, BayesianNetwork bn){
        int stride = 1;
        //get variable index in the factor
        int varIndex = this.factorVariableNames.indexOf(variable);
        //return 1 if the variable is the last variable in the list
        if(varIndex==factorVariableNames.size()-1)
            return stride;
        //stride =  product of no of outcomes of variables followed by the current variable in the list 
        for (int i = varIndex+1; i<factorVariableNames.size(); i++) {
            BNVariable bnV = bn.getVariable(factorVariableNames.get(i));
            int outcomeCount = bnV.getOutcomes().size();
            stride *= outcomeCount;
        }
        
        return stride;
    }

    /**
     * method to get index of a particular assignment in the factor 
     * @param assign assignment with the variable name and value 
     * @param bn Bayesian Network object
     * @return return the assignment index
     */
    private int getIndexForAssignment(HashMap<String,Integer> assign, BayesianNetwork bn){
        int assignmentIndex = 0;
        //iterate over each value in the assignment list and sum the value of (assignment(i) * stride)
        for (int i = 0; i<assign.size(); i++) {
            String varName = this.getFactorVariableNames().get(i);
            int stride = getStride(varName, bn);
            assignmentIndex += stride * assign.get(varName);
        }
        return assignmentIndex;
    }

    /**
     * method to get assignment for a particular index in the factor 
     * @param index index value
     * @param bn Bayesian Network object
     * @return return the assignment 
     */
    private HashMap<String,Integer> getAssignmentForIndex(int index, BayesianNetwork bn){

        HashMap<String,Integer> assign = new HashMap<String, Integer>();
        ArrayList<String> varNames = this.getFactorVariableNames();
        int tempIndex = index;
        //assign(i) = index/stride , for each iteration index will be reduced to the remainder of the division operation
        for (String varName : varNames) {
            int stride = getStride(varName, bn);
            assign.put(varName, tempIndex/stride);
            tempIndex %=stride;
        }
        return assign;
        
    }

    //getters and setters methods

    public ArrayList<String> getFactorVariableNames() {
        return factorVariableNames;
    }

    public void setFactorVariableNames(ArrayList<String> factorVariableNames) {
        this.factorVariableNames = factorVariableNames;
    }

    public ArrayList<Double> getFactorValues() {
        return factorValues;
    }

    public void setFactorValues(ArrayList<Double> factorValues) {
        this.factorValues = factorValues;
    }
    
}