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

import java.util.ArrayList;

/**
 * This Reaction class allows for the creation of a separate Reaction object for
 * every reaction that is read in from the input file. The data is stored and retrieved
 * from this class that is relevant for Reaction calculations.
 * 
 * @author Nate Kibler (PID: nkibler7)
 * @author Chris Schweinhart (PID: schwein)
 */
public class Reaction {
	private double rate;
	private ReactionType type;
	private int[] reactants, products;
	private double nextTime = 0.0;
	private ArrayList<Reaction> affectedReactions = null;
	
	/**
	 * Creates a new Reaction object.
	 * @param rRate - the reaction rate of this reaction
	 * @param rType - the type of reaction this simulates (one, two, three, or four)
	 * @param reactants - array of index positions for reactants
	 * @param products - array of index positions for products
	 */
	public Reaction(double rRate, ReactionType rType, int[] reactants, int[] products) {
		rate = rRate;
		type = rType;
		this.reactants = reactants;
		this.products = products;
	}
	
	/**
	 * Sets the new reaction time to the given newNextTime.
	 * @param newNextTime - the new relative reaction time
	 */
	public void setNextTime(double newNextTime) {
		nextTime = newNextTime;
	}
	
	/**
	 * Returns the reaction time.
	 * @return the relative reaction time
	 */
	public double getNextTime() {
		return nextTime;
	}
	
	/**
	 * Returns the array of index positions for the reactants.
	 * @return an array containing index positions for reactants
	 */
	public int[] getReactants() {
		return reactants;
	}
	
	/**
	 * Returns the array of index positions for the products.
	 * @return an array containing index positions for products
	 */
	public int[] getProducts() {
		return products;
	}
	
	/**
	 * Returns the type of reaction (one, two, three, or four).
	 * @return the ReactionType enumeration value that determines the type of reaction
	 */
	public ReactionType getType() {
		return type;
	}
	
	/**
	 * Returns the reaction rate.
	 * @return the reaction rate as a double value
	 */
	public double getRate() {
		return rate;
	}
	
	/**
	 * Returns an ArrayList of the reactions that need to be updated from the firing
	 * event of this specific reaction.
	 * @return an ArrayList of Reaction objects that need their times updated
	 */
	public ArrayList<Reaction> getTable() {
		return affectedReactions;
	}
}