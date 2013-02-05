import java.util.Arrays;
import java.io.*;

/**
 * Main Chem class for the simulation project.
 * 
 * Contains run schedule, heap for reactions, and several helper methods.
 * 
 * Chris Schweinhart (schwein)
 * Nate Kibler (nkibler7)
 */
public class Chem {

	// Integers for starting values
	private static int numSpecies = 0;
	private static int numReactions = 0;
	private static int simulationTime = 0;
	private static int numRuns = 0;
	private static int time = 0;
	private static Reaction current;

	// Data structures for species/reaction info
	private static int[] species = null;
	private static int[] tracks = null;
	private static MinHeap<Reaction> reactions = null;

	/**
	 * Main method to run simulation.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		// Check for proper usage
		if (args.length != 3) {
			System.out.println("Usage:");
			System.out.println("Chemistry INPUT_FILE RUN OUTPUT_FILE");
			System.exit(1);
		}

		// Read in the initial values from the input file
		if (!readFile(args[0])) {
			System.exit(2);
		}

		numRuns = Integer.parseInt(args[1]);

		for (int i = 0; i < numRuns; i++) {
			time = 0;

			while (time < simulationTime) {
				track = false;
				
				// Choose the next reaction
				current = reactions.getMin();
				current.fire();
				time += current.getNextTime();
				
				// Test for simulation end
				if (time > simulationTime) {
					break;
				}
				
				// Decrement reactants
				if (current.getReactants() != null) {
					for (int index : current.getReactants()) {
						species[index]--;
						if (tracked(index)) {
							track = true;
						}
					}
				}
				
				// Increment products
				if (current.getProducts() != null) {
					for (int index : current.getProducts()) {
						species[index]++;
						if (tracked(index)) {
							track = true;
						}
					}
				}
				
				// Recalculate next times for affected reactions
				for (Reaction rxn : current.getTable()) {
					double propensity = rxn.getRate();
					int[] reactants = rxn.getReactants();
					
					if (rxn instanceof RxnTwo) {
						propensity *= species[reactants[0]];
					} else if (rxn instanceof RxnThree) {
						propensity *= species[reactants[0]];
						propensity *= (species[reactants[0]] - 1);
					} else if (rxn instanceof RxnFour) {
						propensity *= species[reactants[0]];
						propensity *= species[reactants[1]];
					}
					
					// Generate random numer and next-time
					double rand = Math.random();
					
					if (rand == 0) {
						rxn.setNextTime(simulationTime + 1);
					} else {
						rxn.setNextTime(Math.log10(rand)/propensity);
					}
					
					// Remove and re-add to re-order heap
					reactions.remove(rxn);
					reactions.add(rxn);
				}
				
				// Output for reaction
				if (numRuns == 1 && track) {
					if (!trackOutput(args[2])) {
						System.exit(3);
					}
				}
			}
			
			// Output summary data for one of many runs
			if (numRuns > 1) {
				if (!runOutput(args[2])) {
					System.exit(4);
				}
			}
		}

		// Output summary data for a single run
		if (numRuns == 1) {
			if (!singleOutput(args[2])) {
				System.exit(5);
			}
		}
		
		// Output summary data for multiple runs
		if (numRuns > 1) {
			if (!finalOutput(args[2])) {
				System.exit(6);
			}
		}
	}

	/**
	 * Reads inputs from the given file to initialize variables.
	 * 
	 * @param fileName
	 *            the file name for reading
	 * @return true if successful, false otherwise
	 */
	private static boolean readFile(String fileName) {
		
		BufferedReader in = null;

		// Attempt to open the file into a buffered reader
		try {
			in = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("The file could not be found.");
			return false;
		}

		try {
			String line = in.readLine();
			String[] tokens = line.split(" ");

			// Use the first three numbers as initial values
			numSpecies = Integer.parseInt(tokens[0]);
			numReactions = Integer.parseInt(tokens[1]);
			simulationTime = Integer.parseInt(tokens[2]);

			// Allocate arrays based on our given parameters
			species = new int[numSpecies];
			reactions = new int[numReactions][];
			rates = new double[numReactions];

			line = in.readLine();
			tokens = line.split(" ");
			// Read in initial species values
			for (int i = 0; i < numSpecies; i++) {
				species[i] = Integer.parseInt(tokens[i]);
			}

			// Read in reaction coefficients
			for (int i = 0; i < numReactions; i++) {
				line = in.readLine();
				tokens = line.split(" ");

				reactions[i] = new int[tokens.length - 1];

				// Input reaction coefficients
				for (int j = 0; j < 2; j++) {
					reactions[i][j] = Integer.parseInt(tokens[j]);
				}

				// Input reaction rate
				rates[i] = Double.parseDouble(tokens[2]);

				// Input product coefficients
				for (int j = 3; j < tokens.length; j++) {
					reactions[i][j - 1] = Integer.parseInt(tokens[j]);
				}
			}

			in.close();
		} catch (IOException e) {
			System.out.println("Error reading from file.");
			return false;
		} catch (Exception e) {
			System.out.println("Incorrect file formatting.");
			return false;
		}

		return true;
	}

	/**
	 * Output tracked species when changed
	 * 
	 * @param fileName
	 *            the file name for reading
	 * @return true if successful, false otherwise
	 */
	private static boolean trackOutput(String fileName) {

	}
	
	/**
	 * Final output for the summmary of multiple runs
	 * 
	 * @param fileName
	 *            the file name for reading
	 * @return true if successful, false otherwise
	 */
	private static boolean finalOutput(String fileName) {

	}
}
