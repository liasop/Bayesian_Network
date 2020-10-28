package bn;

import util.*;
import util.WeightedSet;
import java.util.Random;

/**
 * Represents a boolean random variable in a Bayesian Network
 *
 * @author Lia Chin-Purcell
 * @version 3/13/19
 *
 */
public class Node {
		private Random rng = new Random();
		private String name;
		private Node[] parents;
		private WeightedSet cpt;
		private boolean value;

	/**
	 * Creates a node in a Bayesian network representing a boolean random variable.
	 * The value of the node is initially set to false.
	 *
	 * @param name
	 * 			The name of the random variable
	 * @param parents
	 * 			The parents of the node in the Bayesian network
	 * @param cpt
	 * 			The conditional probability table
	 *
	 * @pre The cpt contains an entry for every possible configuration of the parents
	 * @pre The cpt specifies the probability that the node is true given a configuration of the parents
	 *
	 */
	public Node(String name, Node[] parents, WeightedSet cpt) {
		this.name = name;
		this.parents = parents;
		this.cpt = cpt;
		this.value = false;
	}

	/**
	 * Creates a node in a Bayesian network representing a boolean random variable with no parents.
	 * The value of the node is initially set to false.
	 *
	 * @param name
	 * 			The name of the random variable
	 * @param cpt
	 * 			The conditional probability table
	 *
	 * @pre The cpt contains exactly 1 entry which is the probability of the random variable being true
	 */
	public Node(String name, WeightedSet cpt) {
		this.name = name;
		this.cpt = cpt;
		this.value = false;
		parents = new Node[0];
	}

	/**
	 * Returns the value of the random variable
	 * @return The value of the random variable
	 */
	public boolean getValue() {
		return this.value;
	}

	/**
	 * Returns the parents of the random variable
	 * @return The parents of the random variable
	 */
	public Node[] getParents() {
		return this.parents;
	}

	/**
	 * Returns the name of the random variable
	 * @return The name of the random variable
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns p(X = true | parents(X))
	 * @return The probability of the random variable being true given the values of its parents
	 */
	public double getProbability() {
		if(parents.length == 0){
			return cpt.getWeight(BitVector.TRUE);
		}
		BitVector bv = new BitVector(parents.length);
		for(int i = 0; i < parents.length; i++){
			if(parents[i].getValue() == false){
				bv.set(i, false);
			}
		}
		return cpt.getWeight(bv);
	}

	/**
	 * Sets the value of the random variable to the specified value
	 * @param newValue
	 * 				The new value of the random variable
	 */
	public void setValue(boolean newValue) {
		this.value = newValue;
	}

	/**
	 * Samples a value for the random variable conditioned on the configuration of the parents (if parents exist).
	 * If there are no parents, this method samples a value for the random variable from the prior distribution.
	 *
	 * Sets the value of the node to the outcome of the sample.
	 *
	 * @pre
	 * 			sampleAndSet() has already been called on the node's parents (if any exist)
	 *
	 * @return
	 * 			The sampled value
	 */
	public boolean sampleAndSet() {
		double randDouble = rng.nextDouble();
		boolean value = true;
		
		if(randDouble > getProbability()){
			value = false;
		}
		setValue(value);
		return value;
	}
}
