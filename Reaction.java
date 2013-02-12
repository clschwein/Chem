/**
 * This Reaction class allows for the creation of a separate Reaction object for
 * every reaction that is read in from the input file. The data is stored and retrieved
 * from this class that is relevant for Reaction calculations.
 * 
 * @author Nate Kibler (nkibler7)
 * @author Chris Schweinhart (schwein)
 */
public class Reaction implements Comparable <Reaction> {
	private double rate;
	private ReactionType type;
	private int[] reactants, products;
	private double nextTime = 0.0;
	private int fired = 0;
	private Reaction[] affectedReactions = null;
	
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
	 * @return an array of Reaction objects that need their times updated
	 */
	public Reaction[] getTable() {
		return affectedReactions;
	}
	
	/**
	 * Sets the reaction dependent table to the given new table
	 * 
	 * @param newAffectedReactions - the new reaction dependency table
	 */
	public void setTable(Reaction[] newAffectedReactions) {
		affectedReactions = newAffectedReactions;
	}
	
	/**
	 * Returns an integer value for the number of times this
	 * reaction has occurred in the simulation
	 * @return an int for the number of firings
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
	
	@Override
	public int compareTo(Reaction r) {
		if (r.getNextTime() > getNextTime())
			return -1;
		if (r.getNextTime() < getNextTime())
			return 1;
		return 0;
	}
}