/**
 * Enumerated type to represent various reaction types.
 * 
 * Used for making conditional branching easier when determining
 * propensities.  The four types are as follows:
 * 1. No reactants
 * 2. One reactant
 * 3. Two reactants w/ repeat
 * 4. Two distinct reactants
 * 
 * @author Chris Schweinhart (schwein)
 * @author Nate Kibler (nkibler7)
 */
public enum ReactionType {
	RXN_ONE, RXN_TWO, RXN_THREE, RXN_FOUR;
}
