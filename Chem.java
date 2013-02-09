// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction.

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
	private static int numDisplay = 0;
	private static int simulationTime = 0;
	private static int numRuns = 0;
	private static int time = 0;
	private static boolean track = false;
	private static Reaction current = null;
	private static BufferedWriter out = null;

	// Data structures for species/reaction info
	private static int[] species = null;
	private static int[] displays = null;
	private static boolean[] tracks = null;
	private static Reaction[] reactionsArray = null;
	private static MinHeap<Reaction> reactionsHeap = null;

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

		// Read from file to initialize variables
		if (!initialize(args[1])) {
			System.exit(2);
		}

		try {
			out = new BufferedWriter(new FileWriter(args[2]));
		} catch (FileNotFoundException e) {
			System.out.println("The output file could not be found.");
			System.exit(3);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		numRuns = Integer.parseInt(args[0]);

		for (int i = 0; i < numRuns; i++) {
			time = 0;
			
			// TODO: set propensities, next time, and add to heap for all reactions
			
			while (time < simulationTime) {
				track = false;
				
				// Choose the next reaction
				current = reactionsHeap.removeMin();
				time += current.getNextTime();
				
				// Test for simulation end
				if (time > simulationTime) {
					break;
				}
				
				// Decrement reactants
				if (current.getReactants() != null) {
					for (int index : current.getReactants()) {
						species[index]--;
						if (tracks[index]) {
							track = true;
						}
					}
				}
				
				// Increment products
				if (current.getProducts() != null) {
					for (int index : current.getProducts()) {
						species[index]++;
						if (tracks[index]) {
							track = true;
						}
					}
				}
				
				// Recalculate next times for affected reactions
				for (Reaction rxn : current.getTable()) {
					double propensity = rxn.getRate();
					int[] reactants = rxn.getReactants();
					ReactionType type = rxn.getType();
					
					if (type == ReactionType.RXN_TWO) {
						propensity *= species[reactants[0]];
					} else if (type == ReactionType.RXN_THREE) {
						propensity *= species[reactants[0]];
						propensity *= (species[reactants[0]] - 1);
					} else if (type == ReactionType.RXN_FOUR) {
						propensity *= species[reactants[0]];
						propensity *= species[reactants[1]];
					}
					
					// Generate random number and next-time
					double rand = Math.random();
					
					if (rand == 0) {
						rxn.setNextTime(simulationTime + 1);
					} else {
						rxn.setNextTime(Math.log10(rand)/propensity);
					}
					
					// Remove and re-add to re-order heap
					reactionsHeap.insert(rxn);
				}
				
				// Output for reaction
				if (numRuns == 1 && track) {
					trackOutput();
				}
			}
			
			// Output summary data for one of many runs
			if (numRuns > 1) {
				runOutput();
			}
		}

		// Output summary data for a single run
		if (numRuns == 1) {
			singleOutput();
		}
		
		// Output summary data for multiple runs
		if (numRuns > 1) {
			finalOutput();
		}
	}

	/**
	 * Reads inputs from the given file to initialize variables.
	 * 
	 * @param fileName
	 *            the file name for reading
	 * @return true if successful, false otherwise
	 */
	private static boolean initialize(String fileName) {
		
		BufferedReader in = null;

		// Attempt to open the file into a buffered reader
		try {
			in = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("The input file could not be found.");
			return false;
		}

		try {
			String line = in.readLine();
			String[] tokens = line.split(" ");

			// Use the first four numbers as initial values
			numSpecies = Integer.parseInt(tokens[0]);
			numReactions = Integer.parseInt(tokens[1]);
			numDisplay = Integer.parseInt(tokens[2]);
			simulationTime = Integer.parseInt(tokens[3]);

			// Allocate arrays based on our given parameters
			species = new int[numSpecies];
			reactionsArray = new Reaction[numReactions];
			displays = new int[numDisplay];

			line = in.readLine();
			tokens = line.split(" ");
			// Read in initial species values
			for (int i = 0; i < numSpecies; i++) {
				species[i] = Integer.parseInt(tokens[i]);
			}

			line = in.readLine();
			tokens = line.split(" ");
			// Read in display species
			for (int i = 0; i < numDisplay; i++) {
				displays[i] = Integer.parseInt(tokens[i]);
				tracks[Integer.parseInt(tokens[i])] = true;
			}
			
			// Read in reaction coefficients
			for (int i = 0; i < numReactions; i++) {
				line = in.readLine();
				tokens = line.split(" ");

				int offset = 0;
				double rate = 0;
				ReactionType type = null;
				int[] reactants = null;
				int[] products = null;

				// Input reactant coefficients
				if (tokens[0].startsWith("->")) {
					type = ReactionType.RXN_ONE;
					reactants = null;
					offset = 0;
				} else if (tokens[1] == "+") {
					type = ReactionType.RXN_FOUR;
					reactants = new int[2];
					reactants[0] = Integer.parseInt(tokens[0].substring(1));
					reactants[1] = Integer.parseInt(tokens[2].substring(1));
					offset = 3;
				} else if (tokens[0].charAt(0) == '2') {
					type = ReactionType.RXN_THREE;
					reactants = new int[2];
					reactants[0] = Integer.parseInt(tokens[0].substring(2));
					reactants[1] = reactants[0];
					offset = 1;
				} else {
					type = ReactionType.RXN_TWO;
					reactants = new int[1];
					reactants[0] = Integer.parseInt(tokens[0].substring(1));
					offset = 1;
				}

				// Input reaction rate
				rate = Double.parseDouble(tokens[offset].substring(2));
				offset ++;

				// Input product coefficients
				if (offset == tokens.length) {
					products = null;
				} else if ((offset == tokens.length - 1) && tokens[offset].charAt(0) == '2') {
					products = new int[2];
					products[0] = Integer.parseInt(tokens[offset].substring(2));
					products[1] = products[0];
				} else if (offset == tokens.length - 1) {
					products = new int[1];
					products[0] = Integer.parseInt(tokens[offset].substring(1));
				} else {
					products = new int[2];
					products[0] = Integer.parseInt(tokens[offset].substring(1));
					products[1] = Integer.parseInt(tokens[offset + 2].substring(1));
				}
				
				// Add reaction to array
				reactionsArray[i] = new Reaction(rate, type, reactants, products);
			}

			// TODO: build reaction dependent tables from reaction array
			
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
	 * @return true if successful, false otherwise
	 */
	private static void trackOutput() {

		// TODO: implement
	}
	
	/**
	 * Output for one run of many
	 * 
	 * @return true if successful, false otherwise
	 */
	private static void runOutput() {

		// TODO: implement
	}
	
	/**
	 * Output for the summary of a single run
	 * 
	 * @return true if successful, false otherwise
	 */
	private static void singleOutput() {

		// TODO: implement
	}
	
	/**
	 * Final output for the summary of multiple runs
	 * 
	 * @return true if successful, false otherwise
	 */
	private static void finalOutput() {

		// TODO: implement
	}
}
