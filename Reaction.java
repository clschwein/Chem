/**
 * This Reaction class allows for the creation of a separate Reaction object for
 * every reaction that is read in from the input file. The data is stored and retrieved
 * from this class that is relevant for Reaction calculations.
 * 
 * @author Nate Kibler (nkibler7)
 * @author Chris Schweinhart (schwein)
 */
public class Reaction implements Comparable <Reaction> {
	
	/**
	 * This double value represents the reaction rate given by the user.
	 * It is used for calculating propensities.
	 */
	private double rate;
	
	/**
	 * This value represents the type of the reaction, which is described
	 * in the ReactionType.java file.
	 */
	private ReactionType type;
	
	/**
	 * These integer arrays represent the reactants and products respectively.
	 * Each entry is the index of a species that is used in the chemical reaction,
	 * whether a reactant or a product.
	 */
	private int[] reactants, products;
	
	/**
	 * This double value represents the next firing time for the reaction.  It
	 * is an absolute time, based on an offset from the current simulation time.
	 * Used for comparing to other Reactions with sorting.
	 */
	private double nextTime = 0.0;
	
	/**
	 * This integer value is a counter to keep track of how often a Reaction is
	 * fired.  Used for summary output for single run simulations.  Starts at zero
	 * and is incremented every time the Reaction is fired.
	 */
	private int fired = 0;
	
	/**
	 * This array of Reactions is the dependency table that is used for updating
	 * propensities and next-times whenever a Reaction fired.  Each entry is a
	 * Reaction whose reactants are effected by this Reaction's reactants or products.
	 */
	private Reaction[] affectedReactions = null;
	
	/**
	 * Creates a new Reaction object.
	 * 
	 * @param rRate
	 * 			the reaction rate of this reaction
	 * @param rType
	 * 			the type of reaction this simulates (one, two, three, or four)
	 * @param reactants
	 * 			array of index positions for reactants
	 * @param products
	 * 			array of index positions for products
	 */
	public Reaction(double rRate, ReactionType rType, int[] reactants, int[] products) {
		rate = rRate;
		type = rType;
		this.reactants = reactants;
		this.products = products;
	}
	
	/**
	 * Sets the new reaction time to the given newNextTime.
	 * 
	 * @param newNextTime
	 * 			the new relative reaction time
	 */
	public void setNextTime(double newNextTime) {
		nextTime = newNextTime;
	}
	
	/**
	 * Returns the reaction next-time.
	 * 
	 * @return
	 * 			the reaction's next-time
	 */
	public double getNextTime() {
		return nextTime;
	}
	
	/**
	 * Returns the array of index positions for the reactants.
	 * 
	 * @return
	 * 			an array containing index positions for reactants
	 */
	public int[] getReactants() {
		return reactants;
	}
	
	/**
	 * Returns the array of index positions for the products.
	 * 
	 * @return
	 * 			an array containing index positions for products
	 */
	public int[] getProducts() {
		return products;
	}
	
	/**
	 * Returns the type of reaction (one, two, three, or four).
	 * 
	 * @return
	 * 			the ReactionType enumeration value that determines the type of reaction
	 */
	public ReactionType getType() {
		return type;
	}
	
	/**
	 * Returns the reaction rate.
	 * 
	 * @return
	 * 			the reaction rate as a double value
	 */
	public double getRate() {
		return rate;
	}
	
	/**
	 * Returns an array of the reactions that need to be updated from the firing
	 * event of this specific reaction.
	 * 
	 * @return
	 * 			an array of Reaction objects that need their times updated
	 */
	public Reaction[] getTable() {
		return affectedReactions;
	}
	
	/**
	 * Sets the reaction dependent table to the given new table
	 * 
	 * @param newAffectedReactions
	 * 			the new reaction dependency table
	 */
	public void setTable(Reaction[] newAffectedReactions) {
		affectedReactions = newAffectedReactions;
	}
	
	/**
	 * Returns an integer value for the number of times this reaction has
	 * fired in the simulation
	 * 
	 * @return
	 * 			an int for the number of firings
	 */
	public int getFired() {
		return fired;
	}
	
	/**
	 * Fires the reaction, incrementing the fired counter
	 */
	public void fire() {
		fired++;
	}
	
	/**
	 * Returns negative, 0, or positive based upon the difference in reaction times.
	 * 
	 * @param r
	 * 			the Reaction object to compare with
	 * @return
	 * 			-1 if time of r is > this, 0 if equal, or 1 if time of r is < this
	 */
	@Override
	public int compareTo(Reaction r) {
		if (r.getNextTime() > getNextTime())
			return -1;
		if (r.getNextTime() < getNextTime())
			return 1;
		return 0;
	}
	
	/**
	 * Determines if this Reaction object and given r are equal.
	 * 
	 * @param r
	 * 			the Reaction object to compare with this 
	 * @return
	 * 			true if they are equal, false otherwise
	 */
	public boolean equals(Reaction r) {
		return r.getType().equals(getType()) &&
				r.getRate() == getRate() &&
				compareTo(r) == 0;
	}
}