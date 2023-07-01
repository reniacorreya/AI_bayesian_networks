import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class parse XML file and generate the Bayesian Network 
 * @author 220031271
 */
public class BNParser {

    /**
     * This method parse XML file and generate the Bayesian Network 
     *
     * @param xmlFile XML File.
     * @return BayesianNetwork object generated from the XML file.
     */
    public static BayesianNetwork readBNFromFile(File xmlFile) {

        BayesianNetwork bayesianNetwork = new BayesianNetwork();

        try {

            // Parse the XML file
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document xmlFileDoc = documentBuilder.parse(xmlFile);
            xmlFileDoc.getDocumentElement().normalize();

            // Get variables and the probability definitions.
            NodeList xmlFileVariables = xmlFileDoc.getElementsByTagName("VARIABLE");
            NodeList xmlFileDefinitions = xmlFileDoc.getElementsByTagName("DEFINITION");

            //Initialize graph 
            Graph graph = new Graph(xmlFileVariables.getLength());

            // iterate and retrieve variable name and outcomes from the file
            for (int i = 0; i < xmlFileVariables.getLength(); i++) {
                Node currNode = xmlFileVariables.item(i);

                if (currNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) currNode;

                    // Get variable name
                    String varName = element.getElementsByTagName("NAME").item(0).getTextContent();

                    // Get variable outcomes
                    ArrayList<String> varOutcomes = new ArrayList<>();
                    NodeList outcomes = element.getElementsByTagName("OUTCOME");
                    for (int j = 0; j < outcomes.getLength(); j++) {
                        varOutcomes.add(outcomes.item(j).getTextContent());
                    }

                    // Create variable object and add to the Bayesian Network 
                    bayesianNetwork.addNode(varName, varOutcomes, i);
                }
            }

            // iterate and retrieve parent variables and probability defenitions from the file
            for (int i = 0; i < xmlFileDefinitions.getLength(); i++){

                Node def = xmlFileDefinitions.item(i);

                if (def.getNodeType() == Node.ELEMENT_NODE) {

                    Element defElement = (Element) def;

                    String varString = defElement.getElementsByTagName("FOR").item(0).getTextContent();
                    BNVariable variable = bayesianNetwork.getVariable(varString);

                    String probTableStr = defElement.getElementsByTagName("TABLE").item(0).getTextContent();
                    ArrayList<String> probTable = new ArrayList<>(Arrays.asList(probTableStr.split(" ")));
                    variable.setProbTable(probTable);

                    // Get parent variables
                    NodeList parentList = defElement.getElementsByTagName("GIVEN");                    

                    for (int j = 0; j < parentList.getLength(); j++) {

                        String parentStr = parentList.item(j).getTextContent();
                        variable.addParent(parentStr);
                        BNVariable currGivenVar = bayesianNetwork.getVariable(parentStr);
                        int givenVarIndex = currGivenVar.getIndex();
                        graph.addEdge(givenVarIndex, i);
                    }

                }

            }
            bayesianNetwork.setGraph(graph);

        } catch (Exception e) { 
            System.out.println("Exception while reading the file");
            System.exit(-1);
        }

        return bayesianNetwork;

    } 

}