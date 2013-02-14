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
 * 
 * @author Chris Schweinhart (schwein)
 * @author Nate Kibler (nkibler7)
 */
public class Chem {

	// Integers for starting values
	private static int minIndex = 100;
	private static int simulationTime = 0;
	private static int numRuns = 0;
	private static double time = 0;
	private static BufferedWriter out = null;

	// Data structures for species/reaction info
	private static int[] initialSpecies = null;
	private static int[] species = null;
	private static int[] displays = null;
	private static boolean[] tracks = null;
	private static int[][] data;
	private static Reaction[] reactionsArray = null;
	private static MinHeap<Reaction> reactionsHeap = null;

	/**
	 * Main method to run simulation.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		boolean track = false;
		Reaction current = null;
		
		// Check for proper usage
		if (args.length != 3) {
			System.out.println("Usage:");
			System.out.println("Chemistry INPUT_FILE RUN OUTPUT_FILE");
			System.exit(0);
		}

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
		
		numRuns = Integer.parseInt(args[0]);

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
				
				// Generate random number and next-time
				double rand = Math.random();
				
				if (rand < 0.00001 || propensity < 0.00001) {
					rxn.setNextTime(simulationTime + 1);
				} else {
					rxn.setNextTime(Math.log10(1/rand)/propensity);
				}
				
				// Add to the heap
				reactionsHeap.insert(rxn);
			}
			
			//System.out.println(reactionsHeap);
			
			while (time < simulationTime) {
				track = false;
				
				// Choose the next reaction
				current = reactionsHeap.getMin();
				time += current.getNextTime();
				
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
				
				// Recalculate next times for effected reactions
				for (Reaction rxn : current.getTable()) {
					double propensity = rxn.getRate();
					int[] reactants = rxn.getReactants();
					ReactionType type = rxn.getType();
					
					// Remove reaction from the heap
					reactionsHeap.remove(rxn);
					
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
					
					if (rand < 0.0001 || propensity < 0.00001) {
						rxn.setNextTime(simulationTime + 1);
					} else {
						rxn.setNextTime(time + Math.log10(1/rand)/propensity);
					}
					
					// Re-add to re-order heap
					reactionsHeap.insert(rxn);
				}
				
				// Output for reaction
				if (numRuns == 1 && track) {
					trackOutput();
				}
			}
			
			// Output summary data for one of many runs
			if (numRuns > 1) {
				runOutput(i);
				
				for (int j = 0; j < displays.length; j++) {
					data[i][j] = species[displays[j]];
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
		
		try {
			out.close();
		} catch (IOException e) {
			System.out.println("Failure closing output.");
			System.exit(0);
		}
	}

	/**
	 * Reads inputs from the given file to initialize variables.
	 * 
	 * @param fileName
	 *            the file name for reading
	 * @return true if successful, false otherwise
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

		try {
			String line = in.readLine();
			String[] tokens = line.split(" ");

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
			data = new int[numDisplays][numRuns];
			
			for (int i = 0; i < species.length; i++) {
				speciesLinks.add(new LinkedList<Reaction>());
			}

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
				line = in.readLine();
				tokens = line.split(" ");

				String pattern;
				for (int j = 0; j < 100; j++) {
					pattern = "S" + j;
					if (line.contains(pattern) && j < minIndex) {
						minIndex = j;
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
				} else if (tokens[1] == "+") {
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
					products = new int[2];
					products[0] = Integer.parseInt(tokens[offset].substring(1)) - minIndex;
					products[1] = Integer.parseInt(tokens[offset + 2].substring(1)) - minIndex;
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
			
			// Build reaction dependent tables
			for (int i = 0; i < reactionsArray.length; i++) {
				LinkedList<Reaction> table = new LinkedList<Reaction>();
				Reaction rxn = reactionsArray[i];
				
				table.add(rxn);
				
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
				
				rxn.setTable(table.toArray(new Reaction[1]));
			}
			
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
	 * Output tracked species when changed
	 * 
	 * @return true if successful, false otherwise
	 */
	private static void trackOutput() {
		
		String output = "";
		
		output += "Simulation Time = " + simulationTime + " :\n";
		for (int index : displays) {
			output += "S" + index + " = " + species[index] + "; ";
		}
		output = output.substring(0, output.length() - 3);
		
		try {
			out.write(output);
	    } catch (IOException e) {
			System.out.println("Failure writing to output file.");
			System.exit(0);
		}
	}
	
	/**
	 * Output for one run of many
	 * 
	 * @param  run  the run number
	 * @return true if successful, false otherwise
	 */
	private static void runOutput(int run) {

		String output = "";
		
		output += "Simulation summary for run #" + run + " :\n";
		for (int index : displays) {
			output += "S" + index + " = " + species[index] + "; ";
		}
		output = output.substring(0, output.length() - 3);
		
		try {
			out.write(output);
	    } catch (IOException e) {
			System.out.println("Failure writing to output file.");
			System.exit(0);
		}
	}
	
	/**
	 * Output for the summary of a single run
	 * 
	 * @return true if successful, false otherwise
	 */
	private static void singleOutput() {

		String output = "";
		
		output += "\nSimulation Summary:\n";
		for (int i = 0; i < reactionsArray.length; i++) {
			Reaction rxn = reactionsArray[i];
			output += "Reaction " + i + " fired " + rxn.getFired() + " time(s)\n";
		}
		output = output.substring(0, output.length() - 2);
		
		try {
			out.write(output);
	    } catch (IOException e) {
			System.out.println("Failure writing to output file.");
			System.exit(0);
		}
	}
	
	/**
	 * Final output for the summary of multiple runs
	 * 
	 * @return true if successful, false otherwise
	 */
	private static void finalOutput() {

		for (int i = 0; i < displays.length; i++) {
			data[i][displays.length] = 0;
			for (int j = 0; j < numRuns; j++) {
				data[i][displays.length] += data[i][j];
			}
			data[i][displays.length] /= numRuns;
		}
		
		for (int i = 0; i < displays.length; i++) {
			data[i][displays.length + 1] = 0;
			for (int j = 0; j < numRuns; j++) {
				data[i][displays.length + 1] += Math.pow(data[i][displays.length] - data[i][j], 2);
			}
			data[i][displays.length + 1] /= numRuns;
		}
		
		String output = "";
		
		output += "\nSimulation Summary:\n";
		
		output += "Means\n";
		for (int i = 0; i < displays.length; i++) {
			output += "S" + i + " = " + data[i][displays.length] + "; ";
		}
		output = output.substring(0, output.length() - 3);
		
		output += "\nVariances\n";
		for (int i = 0; i < displays.length; i++) {
			output += "S" + i + " = " + data[i][displays.length + 1] + "; ";
		}
		output = output.substring(0, output.length() - 3);
		
		try {
			out.write(output);
	    } catch (IOException e) {
			System.out.println("Failure writing to output file.");
			System.exit(0);
		}
	}
}
