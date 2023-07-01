import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class that stores operation related to inference
 * @author 220031271
 */
public class Inference
{
    /**
     * method to find the probability based on the input
     * @param bn bayesian network object
     * @param variable query variable
     * @param value value of the query variable
     * @param evidence list of evidences (null for part (P1, P2))
     * @param order order of elimination (null for part (P1, P3))
     * @return result of the evaluation
     */
    public double
    findProbability (BayesianNetwork bn, String variable, String value,
                     ArrayList<String[]> evidence, String[] order)
    {

        //initialise nuisance variables
        HashSet<String> nuisanceVariables = null;
        nuisanceVariables = bn.getVariableNames();
        nuisanceVariables.remove (variable);
        if (evidence != null)
            {
                for (String[] e : evidence)
                    {
                        String evidenceVariable = e[0];
                        nuisanceVariables.remove(evidenceVariable);
                    }
            }

        // prune non evidence leaf nodes
        ArrayList<String> prunedList
            = pruneNuisanceList (bn, nuisanceVariables, variable, evidence);

        // initialise factor (will not include factors of pruned variables)
        ArrayList<Factor> factors = initialiseFactors (bn, prunedList);

        
        ArrayList<String> nuisanceVariablesOrdered = new ArrayList<> ();
        
        // apply the order of elimination (applicable for part2)
        if (order != null)
            {
                for (int i = 0; i < order.length; i++)
                    {
                        if (nuisanceVariables.contains (order[i]))
                            {
                                nuisanceVariables.remove (order[i]);
                                nuisanceVariablesOrdered.add (order[i]);
                            }
                    }
            }
        else
            {
                //if order is not given, reverse of topological order is used for elimination
                ArrayList<Integer> topologicalSortIndexes
                    = bn.getGraph ().topologicalSort ();
                for (int i = topologicalSortIndexes.size () - 1; i >= 0; i--)
                    {
                        String varName = bn.getVariableByIndex (
                                               topologicalSortIndexes.get (i))
                                             .getName ();
                        if (nuisanceVariables.contains (varName))
                            {
                                nuisanceVariablesOrdered.add (varName);
                                nuisanceVariables.remove (varName);
                            }
                    }
            }

        if (nuisanceVariables.size () > 0)
            {
                nuisanceVariablesOrdered.addAll (nuisanceVariables);
            }

        // assign evidences
        if (evidence != null)
            {
                for (String[] e : evidence)
                    {
                        String evidenceVariable = e[0];
                        String evidenceValue = e[1];

                        for (Factor factor : factors)
                            {
                                if (factor.getFactorVariableNames ().contains (
                                        evidenceVariable))
                                    {
                                        factor.assign (evidenceVariable,
                                                       evidenceValue, bn);
                                    }
                            }
                    }
            }

        // elimination based on the order
        for (String nuisanceVar : nuisanceVariablesOrdered)
            {
                //join operation

                //get factors with the nuisance variable
                ArrayList<Factor> factorsToJoin = new ArrayList<> ();
                for (Factor f : factors)
                    {
                        if (f.getFactorVariableNames ().contains (nuisanceVar))
                            {
                                factorsToJoin.add (f);
                            }
                    }

                Factor joinedFactor = factorsToJoin.get (0);
                
                //perform join if there are more than one factors for the variable
                if (factorsToJoin.size () > 1)
                    {

                        Factor intermediateJoin = null;
                        for (int i = 1; i < factorsToJoin.size (); i++)
                            {
                                intermediateJoin = joinedFactor.joinFactor (
                                    factorsToJoin.get (i), bn);
                                joinedFactor = intermediateJoin;
                            }
                    }


                // Sum operation on joined factor for the variable
                Factor eliminatedFactor
                    = joinedFactor.sumFactor (nuisanceVar, bn);
                
                //replace all the involved factor with the new factor
                factors.removeAll (factorsToJoin);
                factors.add (eliminatedFactor);
            }

        // join all factors until there is only one factor in the list
        Factor finalFactor = factors.get (0);
        if (factors.size () > 1)
            {
                for (int i = 1; i < factors.size (); i++)
                    {
                        Factor intermediateJoin
                            = finalFactor.joinFactor (factors.get (i), bn);
                        finalFactor = intermediateJoin;
                    }
            }

        // normalise the values in the factor
        finalFactor.normalise ();

        //get the result based on the query variable and value
        ArrayList<Double> values = finalFactor.getFactorValues ();
        ArrayList<String> varName = finalFactor.getFactorVariableNames ();
        int valueIndex
            = bn.getVariable (varName.get (0)).getOutcomeIndex (value);
        Double result = values.get (valueIndex);

        // return final result
        return result;
    }

    /**
     * method to initialise all the factors in the network 
     * @param bn bayesian network object
     * @param prunedList pruned list of variables 
     * @return return the list of all factors
     */
    private ArrayList<Factor>
    initialiseFactors (BayesianNetwork bn, ArrayList<String> prunedList)
    {
        ArrayList<Factor> initialFactors = new ArrayList<Factor> ();
        HashSet<BNVariable> variables = bn.getVariables ();
        //iterate for each variable in the network
        for (BNVariable bnVariable : variables)
            {
                //reject if the variable is in pruned list
                if (prunedList.contains (bnVariable.getName ()))
                    {
                        continue;
                    }
                
                //initialise factor based on the variable and parents
                Factor factor = new Factor ();
                ArrayList<String> factorVariableNames
                    = bnVariable.getParents ();
                factorVariableNames.add (bnVariable.getName ());
                factor.setFactorValues (bnVariable.getProbabilities ());
                factor.setFactorVariableNames (factorVariableNames);
                initialFactors.add (factor);
            }

        return initialFactors;
    }

    /**
     * method to prune the elimination variable list by removing non evidence leaf nodes 
     * @param bn bayesian network object
     * @param nuisanceVariables set of nuisance variables
     * @param variable query variable 
     * @param evidence list of evidences
     * @return return the list pruned variables
     */
    private ArrayList<String>
    pruneNuisanceList (BayesianNetwork bn, HashSet<String> nuisanceVariables,
                       String variable, ArrayList<String[]> evidence)
    {
        ArrayList<String> prunedList = new ArrayList<> ();

        //get index of query variabel and evidence variable
        ArrayList<Integer> evidenceAndQueryVarIndex = new ArrayList<> ();
        evidenceAndQueryVarIndex.add (bn.getVariable (variable).getIndex ());
        if (evidence != null)
            {
                for (String[] e : evidence)
                    {
                        evidenceAndQueryVarIndex.add (
                            bn.getVariable (e[0]).getIndex ());
                    }
            }

        //get graph represented as adjacency matrix
        Graph g = bn.getGraph ();
        boolean[][] matrix = g.getAdjMatrix ().clone ();
        int numNodes = matrix.length;

        //list of pruned nodes
        ArrayList<Integer> removedLeafNodes = new ArrayList<Integer> ();

        //iterate until there are no leaf nodes or no nuisance variable
        while (!nuisanceVariables.isEmpty ())
            {

                ArrayList<Integer> leafNodes = new ArrayList<Integer> ();

                for (int i = 0; i < numNodes; i++)
                    {
                        //skip if the node is a query variable or evidence variable
                        if (evidenceAndQueryVarIndex.contains (i))
                            {
                                continue;
                            }
                        boolean isLeaf = true;

                        //get variables without any edges directed from it
                        for (int j = 0; j < numNodes; j++)
                            {
                                if (matrix[i][j] == true)
                                    {
                                        isLeaf = false;
                                        break;
                                    }
                            }
                        if (isLeaf)
                            {
                                if (!removedLeafNodes.contains (i))
                                    {
                                        leafNodes.add (i);
                                        removedLeafNodes.add (i);
                                    }
                            }
                    }

                if (leafNodes.isEmpty ())
                    {
                        break;
                    }

                //remove leaf node from the list of nuisance variables and add to pruned list
                for (Integer leafNode : leafNodes)
                    {
                        if (nuisanceVariables.contains (
                                bn.getVariableByIndex (leafNode).getName ()))
                            {
                                nuisanceVariables.remove (
                                    bn.getVariableByIndex (leafNode)
                                        .getName ());
                                prunedList.add (bn.getVariableByIndex (leafNode)
                                                    .getName ());
                                //remove edges directed to the node
                                for (int i = 0; i < numNodes; i++)
                                    {
                                        matrix[i][leafNode] = false;
                                    }
                            }
                    }
            }
        return prunedList;
    }
}