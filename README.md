# AI_bayesian_networks
Program to construct Bayesian networks based on a given BN specification. 

Various methods for inference in Bayesian networks were implemented. The program accepts an XML file and parses it into Bayesian Network based on the specification in the file. It then prompts the user for the query and additional parameters (depending on the part of execution) and calculates and returns the output based on the inputs.


This code:
- Construct a network based on a given BN’s specification in an XML file (E.g., invoke the system with input argument ‘BNA.xml’)
- Read the query as user input: a queried variable name followed by its truth value (T/F) separated by a colon (E.g., ‘D:T’)
- Calculate the output (E.g., P(D = true) = 0.57050)
- Print the output without further information. Outputs should be formatted as numbers with 5 decimal places. (E.g., print ‘0.57050’ only)

  
Compiling and running Instructions
- The src/ directory contains all the required files and classes. A2main.java is the class with the main() method and can be compiled using the below command from the src/ directory.
  javac A2main.java
- Once compiled the code can be executed using the following command.
  java A2main <P1|P2|P3> <NID>
  <P1|P2|P3> represents the part of the assignment that needs to be executed. <NID> is the path of the XML file that needs to be parsed. Both arguments are mandatory.

