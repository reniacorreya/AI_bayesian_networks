import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

/********************
 * Starter Code
 * 
 * @author lf28
 * @author 220031271
 * 
 *         run with
 *         java A2main <Pn> <NID>
 * 
 */

public class A2main {
	private static final String MSG = "Format: java A4main <Pn> <NID>";

	public static void main(String[] args) {

		BayesianNetwork bn = null;
		try {						

			        // Validate arguments
					if (args.length != 2) {
						System.out.println(MSG);
						System.exit(-1);
					}
			
					// Get XML file			
					String bnFilePath = args[1];
					File bnFile = new File(bnFilePath);
					if(!bnFile.exists()) {
						System.out.println("Error: Invalid file name.");
						System.exit(-1);
					}
			
					// Parse XML file to get the Bayes net
					bn = BNParser.readBNFromFile(bnFile);
				if(bn.getGraph().checkCycle()){
					System.out.println("Error: The given graph is not a Directed Acyclic Graph.\n");
						System.exit(-1);
				}
				ArrayList<String> invalidVar = bn.validateProbabilities();
				if(!invalidVar.isEmpty()){
					System.out.println("Error: The probability distribution in the given graph is not valid for the following variables: "+invalidVar);
						System.exit(-1);
				}		

		Scanner sc = new Scanner(System.in);
		Inference inference = new Inference();
		switch (args[0]) {
			case "P1": {
				// Part 1
				String[] query = getQueriedNode(sc);
				String variable = query[0];
				String value = query[1];
				ArrayList<String[]> evidence = null;
				String[] order = null;

				// execute query of p(variable=value)				
				double result = inference.findProbability(bn, variable, value, evidence, order);
				printResult(result);
			}
				break;

			case "P2": {
				// Part 2
				String[] query = getQueriedNode(sc);
				String variable = query[0];
				String value = query[1];
				String[] order = getOrder(sc);
				ArrayList<String[]> evidence = null;

				// execute query of p(variable=value) with given order of elimination				
				double result = inference.findProbability(bn, variable, value, evidence, order);
				printResult(result);
			}
				break;

			case "P3": {
				// Part 3
				String[] query = getQueriedNode(sc);
				String variable = query[0];
				String value = query[1];
				String[] order = null;
				ArrayList<String[]> evidence = getEvidence(sc);				
				
				// execute query of p(variable=value|evidence) with an order
				double result = inference.findProbability(bn, variable, value, evidence, order);			
				printResult(result);
			}
				break;
		}
		sc.close();
	} catch (Exception e) {
		System.out.println("Exception Occurred:\n");
		e.printStackTrace();
	}
	}

	// method to obtain the evidence from the user
	private static ArrayList<String[]> getEvidence(Scanner sc) {

		System.out.println("Evidence:");
		ArrayList<String[]> evidence = new ArrayList<String[]>();
		try{
		String[] line = sc.nextLine().split(" ");

		for (String st : line) {
			String[] ev = st.split(":");
			evidence.add(ev);
		}
		return evidence;
	}
		catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Please provide valid evidences separated by space: <Variable1:Value1 Variable2:Value2 Variable3:Value3>");
			System.exit(-1);
		}
		catch(Exception e){
			System.out.println("Please provide valid evidence separated by space: <Variable1:Value1 Variable2:Value2 Variable3:Value3>");
			System.exit(-1);
		}
		return null;
	}

	// method to obtain the order from the user
	private static String[] getOrder(Scanner sc) {

		System.out.println("Order:");
		String[] val = sc.nextLine().split(",");
		return val;
		
	}

	// method to obtain the queried node from the user
	private static String[] getQueriedNode(Scanner sc) {

		try{
			System.out.println("Query:");
			String[] val = sc.nextLine().split(":");
			return val;
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Please provide valid query: <Variable:Value>");
			System.exit(-1);
		}
		catch(Exception e){
			System.out.println("Please provide valid query: <Variable:Value>");
			System.exit(-1);
		}
		return null;

		

	}

	// method to format and print the result
	private static void printResult(double result) {

		DecimalFormat dd = new DecimalFormat("#0.00000");
		System.out.println(dd.format(result));
	}

}