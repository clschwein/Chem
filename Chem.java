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

import java.io.*;
import java.util.*;

/**
 * Main Chem class for the simulation project.
 * 
 * Contains run schedule, heap for reactions, and several helper methods.
 * The main method below runs the simulation for the given number of times,
 * reading from the given input file and writing to the given output file.
 * 
 * Helper methods for initialization (file reading) and output (file writing)
 * are below the main method for ease of reading.
 * 
 * @author Chris Schweinhart (schwein)
 * @author Nate Kibler (nkibler7)
 */
public class Chem {

	// Private member fields, with javadoc descriptions
	
	/**
	 * This is used to keep track of the minimum index of all species
	 * for offseting.  For example, the species may start labeling at
	 * S0, S1, or even S100.  Initially set to Integer.MAX_VALUE for
	 * comparison reasons.  Will be determined in initialize() method.
	 */
	private static int minIndex = Integer.MAX_VALUE;
	
	/**
	 * This represents the time for the simulation duration, which is
	 * usually just 10.  Will be determined in initialize() method, and
	 * is used for determining when any run is over.
	 */
	private static int simulationTime = 0;
	
	/**
	 * This is used to determine how many runs the simulation should be.
	 * Also used to choose which output method, whether single run or
	 * aggregated summary over multiple runs.  Will be determined in main()
	 * method as it is given by command-line argument.
	 */
	private static int numRuns = 0;
	
	/**
	 * This represents the current time, which starts at zero and ticks
	 * up to simulationTime.  This is used for all the time calculations
	 * in the main() method, and is reset to zero on each run.
	 */
	private static double time = 0;
	
	/**
	 * This is for writing to the output file.  It is useful to keep it as
	 * a global variable due to how many different times and types of
	 * output for this simulation.  It is much better to deal with opening
	 * and closing only once, which we still do with the reader in the
	 * initialize() method.  Will be determined in the main() method
	 * using the command-line parameter.
	 */
	private static BufferedWriter out = null;

	/**
	 * This is an array of length numSpecies that is used to keep track of
	 * the initial species values.  Species values are reset to these starting
	 * values on each run.  These populations are taken from the input file in
	 * the initialize() method.
	 */
	private static int[] initialSpecies = null;
	
	/**
	 * This is an array of length numSpecies that is used to keep track of
	 * the current species values.  These are reset to the initial species
	 * values on each run in the main() method.  Used to determine propensities
	 * for reactions.
	 */
	private static int[] species = null;
	
	/**
	 * This is an array of length numDisplays that is used to keep track of
	 * which species the user wants tracked and displayed.  Most of the output
	 * of the simulation uses this array, which preserves the order of the user's
	 * input file, to output important statistics about species.  Each entry is
	 * the index of a species that is to be displayed.  Will be determined in
	 * initialize() method.
	 */
	private static int[] displays = null;
	
	/**
	 * This is an array of length numDisplays that keeps the same information
	 * as displays, but in a different format.  The main reason to have this array
	 * is to make checking for whether or not a species is being tracked O(1).
	 * With just the displays array, we would have to do a linear search, because
	 * we have to preserve the user's order instead of sorting as per the spec, which
	 * would give us an O(n) runtime for checking species.  This is just used in
	 * the main() method to check when to output tracked species.  Will be determined
	 * in initialize() method.  Each entry is a boolean value for whether or not
	 * the species with a given index is being tracked.
	 */
	private static boolean[] tracks = null;
	
	/**
	 * This is a two dimensional array for representing the data of multiple runs.
	 * There are as many entries as there are species, and each entry is an array
	 * to keep track of final run populations.  There will be two extra spots at the
	 * end of each array to leave space for mean and variance, which can be seen in
	 * the finalOutput() method.  Will be determined in the main() method at the end
	 * of each run.
	 */
	private static int[][] data;
	
	/**
	 * This is an array of length numReactions that keeps track of all the reactions
	 * outside of the heap.  This is needed for initialization and reforming the
	 * heap every run, as we cannot effectively keep the heap as the only data
	 * structure for our Reactions (at least not without making MinHeap<E> specific to
	 * Reactions, which would be bad).  Reactions are put into the array in the
	 * order that they appear in the input file, which is determined in initialize()
	 * method.
	 */
	private static Reaction[] reactionsArray = null;
	
	/**
	 * This is the MinHeap<Reaction> that keeps our reactions in a neat und tidy
	 * heap.  The heap is formed with Reactions whenever we start a new run, and it
	 * is used to retrieve the next Reaction to occur.  For more information, you
	 * can look at our MinHeap.java and Reaction.java files.  Will be determined
	 * in the main method when the heap is reformed at the start of a run.
	 */
	private static MinHeap<Reaction> reactionsHeap = null;

	/**
	 * Main method to run simulation.  Reads in command-line parameters to
	 * determine input, output, and number of runs.  Continues with the run
	 * order, and handles method calls and members.  Runs output with four
	 * helper methods given.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		// Boolean to determine if track output is needed
		boolean track = false;
		
		Reaction current = null;
		
		// Check for proper usage
		if (args.length != 3) {
			System.out.println("Usage:");
			System.out.println("Chemistry NUM_RUNS INPUT_FILE OUTPUT_FILE");
			System.exit(0);
		}

		numRuns = Integer.parseInt(args[0]);
		
		// Read from file to initialize variables
		initialize(args[1]);
		
		// Set up output file writer
		try {
			out = new BufferedWriter(new FileWriter(args[2]));
		} catch (FileNotFoundException e) {
			System.out.println("The output file could not be found.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Error with output file.");
			System.exit(0);
		}

		// Main run sequence
		for (int i = 0; i < numRuns; i++) {
			time = 0;
			
			// Reset species to initial values
			for (int j = 0; j < species.length; j++) {
				species[j] = initialSpecies[j];
			}
			
			// Initialize the heap and reactions
			reactionsHeap = new MinHeap<Reaction>(new Reaction[reactionsArray.length], 0);
			for (Reaction rxn : reactionsArray) {				
				double propensity = rxn.getRate();
				int[] reactants = rxn.getReactants();
				ReactionType type = rxn.getType();
				
				// Determine propensity
				if (type == ReactionType.RXN_TWO) {
					propensity *= species[reactants[0]];
				} else if (type == ReactionType.RXN_THREE) {
					propensity *= species[reactants[0]];
					propensity *= (species[reactants[0]] - 1);
				} else if (type == ReactionType.RXN_FOUR) {
					propensity *= species[reactants[0]];
					propensity *= species[reactants[1]];
				}
				
				// Generate random number
				double rand = 0;
				while (rand == 0) {
					rand = Math.random();
				}
				
				// Calculate next-time
				if (propensity == 0) {
					rxn.setNextTime(simulationTime + 1);
				} else {
					rxn.setNextTime(Math.log(1/rand)/propensity);
				}
				
				// Add to the heap
				reactionsHeap.insert(rxn);
			}
			
			while (time < simulationTime) {				
				track = false;
				
				// Choose the next reaction
				current = reactionsHeap.getMin();
				time = current.getNextTime();
				
				// Test for simulation end
				if (time > simulationTime) {
					break;
				}
				
				// Fire the reaction
				if (numRuns == 1) {
					current.fire();
				}
				
				// Decrement reactants
				if (current.getReactants() != null) {
					for (int index : current.getReactants()) {
						species[index]--;
						if (tracks[index]) { // Check if species is tracked
							track = true;
						}
					}
				}
				
				// Increment products
				if (current.getProducts() != null) {
					for (int index : current.getProducts()) {
						species[index]++;
						if (tracks[index]) { // Check if species is tracked
							track = true;
						}
					}
				}
				
				// Recalculate next times for effected reactions
				for (Reaction rxn : current.getTable()) {
					double propensity = rxn.getRate();
					int[] reactants = rxn.getReactants();
					ReactionType type = rxn.getType();
					
					// Remove reaction from the heap
					reactionsHeap.remove(rxn);
					
					// Determine propensity
					if (type == ReactionType.RXN_TWO) {
						propensity *= species[reactants[0]];
					} else if (type == ReactionType.RXN_THREE) {
						propensity *= species[reactants[0]];
						propensity *= (species[reactants[0]] - 1);
					} else if (type == ReactionType.RXN_FOUR) {
						propensity *= species[reactants[0]];
						propensity *= species[reactants[1]];
					}
					
					// Generate random number
					double rand = 0;
					while (rand == 0) {
						rand = Math.random();
					}
					
					// Calculate next-time
					if (propensity == 0) {
						rxn.setNextTime(simulationTime + 1);
					} else {
						rxn.setNextTime(time + Math.log(1/rand)/propensity);
					}
					
					// Re-add to re-order heap
					reactionsHeap.insert(rxn);
				}
				
				// Output for tracked species changes
				if (numRuns == 1 && track) {
					trackOutput();
				}
			}
			
			// Output data for one of many runs
			if (numRuns > 1) {
				runOutput(i + 1);
				
				for (int j = 0; j < displays.length; j++) {
					data[j][i] = species[displays[j]];
				}
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
		
		// Clean up after output writer
		try {
			out.close();
		} catch (IOException e) {
			System.out.println("Failure closing output.");
			System.exit(0);
		}
	}

	/**
	 * Reads inputs from the given file to initialize variables.  Takes many
	 * values from the file for input and stores them in various ways for
	 * the member fields.  No error testing done here, since the spec requires
	 * output to conform to a basic format.
	 * 
	 * @param fileName
	 *            the file name for reading
	 */
	private static void initialize(String fileName) {
		
		BufferedReader in = null;

		// Attempt to open the file into a buffered reader
		try {
			in = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("The input file could not be found.");
			System.exit(0);
		}

		// Main input file reading
		try {
			
			// Set-up and tokenize the first line
			String line = in.readLine();
			String[] tokens = line.split(" ");

			// Species links for determining which reactions are effected
			// by which species.  Used to form reaction tables.
			LinkedList<LinkedList<Reaction>> speciesLinks = new LinkedList<LinkedList<Reaction>>();
			
			// Use the first four numbers as initial values
			int numSpecies = Integer.parseInt(tokens[0]);
			int numReactions = Integer.parseInt(tokens[1]);
			int numDisplays = Integer.parseInt(tokens[2]);
			simulationTime = Integer.parseInt(tokens[3]);

			// Allocate arrays based on our given parameters
			initialSpecies = new int[numSpecies];
			species = new int[numSpecies];
			reactionsArray = new Reaction[numReactions];
			displays = new int[numDisplays];
			tracks = new boolean[numSpecies];
			data = new int[numDisplays][numRuns + 2];
			
			// Set-up for the species links
			for (int i = 0; i < species.length; i++) {
				speciesLinks.add(new LinkedList<Reaction>());
			}

			// Set-up and tokenize the second line
			line = in.readLine();
			tokens = line.split(" ");
			
			// Read in initial species values
			for (int i = 0; i < initialSpecies.length; i++) {
				initialSpecies[i] = Integer.parseInt(tokens[i]);
			}

			// Save the displayed species for after we know the min index
			String save = in.readLine();
			
			// Read in reaction coefficients
			for (int i = 0; i < numReactions; i++) {
				
				// Set-up and tokenize the line
				line = in.readLine();
				tokens = line.split(" ");

				// Check for an index which is smaller than minIndex
				String pattern;
				for (int j = 0; j < minIndex; j++) {
					pattern = "S" + j;
					if (line.contains(pattern)) {
						minIndex = j;
						break;
					}
				}
				
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
				} else if (tokens[1].contains("+")) {
					type = ReactionType.RXN_FOUR;
					reactants = new int[2];
					reactants[0] = Integer.parseInt(tokens[0].substring(1)) - minIndex;
					reactants[1] = Integer.parseInt(tokens[2].substring(1)) - minIndex;
					offset = 3;
				} else if (tokens[0].charAt(0) == '2') {
					type = ReactionType.RXN_THREE;
					reactants = new int[2];
					reactants[0] = Integer.parseInt(tokens[0].substring(2)) - minIndex;
					reactants[1] = reactants[0];
					offset = 1;
				} else {
					type = ReactionType.RXN_TWO;
					reactants = new int[1];
					reactants[0] = Integer.parseInt(tokens[0].substring(1)) - minIndex;
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
					products[0] = Integer.parseInt(tokens[offset].substring(2)) - minIndex;
					products[1] = products[0];
				} else if (offset == tokens.length - 1) {
					products = new int[1];
					products[0] = Integer.parseInt(tokens[offset].substring(1)) - minIndex;
				} else {
					if (tokens[offset].contains("2S")) {
						products = new int[3];
						products[0] = Integer.parseInt(tokens[offset].substring(2)) - minIndex;
						products[1] = products[0];
						products[2] = Integer.parseInt(tokens[offset + 2].substring(1)) - minIndex;
					} else {
						products = new int[2];
						products[0] = Integer.parseInt(tokens[offset].substring(1)) - minIndex;
						products[1] = Integer.parseInt(tokens[offset + 2].substring(1)) - minIndex;
					}
				}
				
				// Add reaction to array
				reactionsArray[i] = new Reaction(rate, type, reactants, products);
			}
			
			// Read in display species
			tokens = save.split(" ");
			for (int i = 0; i < displays.length; i++) {
				displays[i] = Integer.parseInt(tokens[i]) - minIndex;
				tracks[Integer.parseInt(tokens[i]) - minIndex] = true;
			}
			
			// Build species links
			for (int i = 0; i < reactionsArray.length; i++) {
				Reaction rxn = reactionsArray[i];
				int[] reactants = rxn.getReactants();
				if (reactants != null) {
					for (int j = 0; j < reactants.length; j++) {
						speciesLinks.get(reactants[j]).add(rxn);
					}
				}
			}
			
			// Build reaction dependent tables from species links
			for (int i = 0; i < reactionsArray.length; i++) {
				LinkedList<Reaction> table = new LinkedList<Reaction>();
				Reaction rxn = reactionsArray[i];
				
				table.add(rxn);
				
				// Check reactants for dependencies
				int[] reactants = rxn.getReactants();
				if (reactants != null) {
					for (int j = 0; j < reactants.length; j++) {
						LinkedList<Reaction> links = speciesLinks.get(reactants[j]);
						for (int k = 0; k < links.size(); k ++) {
							if (!table.contains(links.get(k))) {
								table.add(links.get(k));
							}
						}
					}
				}
				
				// Check products for dependencies
				int[] products = rxn.getProducts();
				if (products != null) {
					for (int j = 0; j < products.length; j++) {
						LinkedList<Reaction> links = speciesLinks.get(products[j]);
						for (int k = 0; k < links.size(); k ++) {
							if (!table.contains(links.get(k))) {
								table.add(links.get(k));
							}
						}
					}
				}
				
				// Set Reaction table for the current Reaction
				rxn.setTable(table.toArray(new Reaction[1]));
			}
			
			// Clean up input file
			in.close();
		} catch (IOException e) {
			System.out.println("Error reading from file.");
			System.exit(0);
		} catch (Exception e) {
			System.out.println("Incorrect file formatting.");
			System.out.println(e);
			System.exit(0);
		}
	}

	/**
	 * Output tracked species when changed.  Used whenever a tracked species
	 * is modified in simulations with a single run.  Outputs all tracked species
	 * in the order given by the user in the input file.
	 */
	private static void trackOutput() {
		
		// Set-up string for output
		String output = "Simulation Time = " + time + ": ";
		
		// Add each of the tracked species
		for (int index : displays) {
			output += "S" + (index + minIndex) + " = " + species[index] + "; ";
		}
		
		output = output.substring(0, output.length() - 2) + "\n";
		
		// Attempt writing
		try {
			out.write(output);
	    } catch (IOException e) {
			System.out.println("Failure writing to output file.");
			System.exit(0);
		}
	}
	
	/**
	 * Output for one run of many.  Used to display the tracked species
	 * at the end of a run when multiple runs are simulated.  Outputs all
	 * tracked species in the order given by the user in the input file.
	 * 
	 * @param  run
	 * 				the run number
	 */
	private static void runOutput(int run) {

		// Set-up string for output
		String output = "Summary for Run #" + run + ": ";
		
		// Add each of the tracked species
		for (int index : displays) {
			output += "S" + (index + minIndex) + " = " + species[index] + "; ";
		}
		
		output = output.substring(0, output.length() - 2) + "\n";
		
		// Attempt write
		try {
			out.write(output);
	    } catch (IOException e) {
			System.out.println("Failure writing to output file.");
			System.exit(0);
		}
	}
	
	/**
	 * Output for the summary of a single run.  Used for summary statistics
	 * when only a single run is simulated.  Outputs how often each reaction
	 * fired in the order given by the user in the input file.
	 */
	private static void singleOutput() {

		// Set-up string for output
		String output = "\nSimulation Summary:\n";
		
		// Add each reaction's fire times
		for (int i = 0; i < reactionsArray.length; i++) {
			Reaction rxn = reactionsArray[i];
			output += "Reaction " + (i + 1) + " fired " + rxn.getFired() + " time(s)\n";
		}
		
		output = output.substring(0, output.length() - 1);
		
		// Attempt write
		try {
			out.write(output);
	    } catch (IOException e) {
			System.out.println("Failure writing to output file.");
			System.exit(0);
		}
	}
	
	/**
	 * Final output for the summary of multiple runs.  Used for summary
	 * statistics when multiple runs are simulated.  Outputs means and
	 * variances for each tracked species in the order given by the user in
	 * the input file.
	 */
	private static void finalOutput() {

		// Calculate tracked species' means
		for (int i = 0; i < displays.length; i++) {
			data[i][numRuns] = 0;
			for (int j = 0; j < numRuns; j++) {
				data[i][numRuns] += data[i][j];
			}
			data[i][numRuns] /= numRuns;
		}
		
		// Calculate tracked species' variances
		for (int i = 0; i < displays.length; i++) {
			data[i][numRuns + 1] = 0;
			for (int j = 0; j < numRuns; j++) {
				data[i][numRuns + 1] += Math.pow(data[i][numRuns] - data[i][j], 2);
			}
			data[i][numRuns + 1] /= numRuns;
		}
		
		// Set-up string for output
		String output = "\nSimulation Summary:\n";
		
		// Add the tracked species' means
		output += "Means: ";
		for (int i = 0; i < displays.length; i++) {
			output += "S" + (displays[i] + minIndex) + " = " + data[i][numRuns] + "; ";
		}
		output = output.substring(0, output.length() - 2) + "\n";
		
		// Add the tracked species' variances
		output += "Variances: ";
		for (int i = 0; i < displays.length; i++) {
			output += "S" + (displays[i] + minIndex) + " = " + data[i][numRuns + 1] + "; ";
		}
		output = output.substring(0, output.length() - 2);
		
		// Attempt write
		try {
			out.write(output);
			System.out.println(output);
	    } catch (IOException e) {
			System.out.println("Failure writing to output file.");
			System.exit(0);
		}
	}
}
