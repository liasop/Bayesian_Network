package bn;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;


import tui.Query;
import tui.Reader;
import util.BitVector;
import util.WeightedSet;

/**
 * Represents a generic Bayesian Network of boolean random variables
 *
 * @author Lia Chin-Purcell
 * @version 3/20/19
 *
 */
public class BayesianNetwork {
	private Node[] nodes;


	/**
	 * Constructs a new Bayesian network with the given nodes.
	 *
	 * @param nodes The nodes in the Bayesian network
	 *
	 * @pre The nodes are listed in topological order
	 */
	public BayesianNetwork(Node[] nodes) {
	this.nodes = nodes;
	}


	/**
	 * Returns the nodes in the Bayesian network
	 * @return The nodes in the Bayesian network
	 */
	public Node[] getNodes() {
		return this.nodes;
	}


	/**
	 * Approximates the query using direct sampling
	 *
	 * @param q
	 * 			The query
	 *
	 * @param numSamples
	 * 			The number of samples for direct sampling
	 * @return
	 * 			A probability distribution over the query variables
	 */
	public WeightedSet directSample(Query q, int numSamples) {
		Set<String> ev = q.evidenceVariables;
		Set<String> qv = q.queryVariables;

		WeightedSet ws = new WeightedSet(qv.size()); //not sure

		for(int i = 0; i < numSamples; i++){
			//sample ← {}
			BitVector sample = new BitVector(qv.size());
			int sampleCounter = 0; //keeps track of where we are in sample

			for(int j = 0; j < nodes.length; j++){
				//value ← randomly sample from p(Xi | parents(Xi))
				boolean value = nodes[j].sampleAndSet();
				// Xi is a query variable'
				if(qv.contains(nodes[j].getName())){
					sample.set(sampleCounter, value);
					sampleCounter++;
				}
			}
			//Sample now contains the sampled values for all of the query variables
			 ws.increment(sample, 1);
		}
		ws.normalizeWeights();
		return ws;
	}


	/**
	 * Approximates the query using rejection sampling
	 *
	 * @param q
	 * 			The query
	 *
	 * @param numSamples
	 * 			The number of samples for rejection sampling
	 * @return
	 * 			A probability distribution over the query variables
	 */

	public WeightedSet rejectionSampling(Query q, int numSamples) {
		Set<String> ev = q.evidenceVariables;
		Set<String> qv = q.queryVariables;
		HashMap<String, Boolean> eVal = q.evidenceValues;

		WeightedSet ws = new WeightedSet(qv.size()); //not sure

		for(int i = 0; i < numSamples; i++){
			//sample ← {}
			BitVector sample = new BitVector(qv.size());
			int sampleCounter = 0;

			for(int j = 0; j < nodes.length; j++){
				//BitVector value = new BitVector(2);
				boolean value = nodes[j].sampleAndSet();
				//Xi is an evidence variable
				if(ev.contains(nodes[j].getName())){
					boolean evidence = eVal.get(nodes[j].getName());

					if(value != evidence){
					//sampled value does not match evidence
						break;  // Abandon the sample and start over
					}
				}
				// Xi is a query variable'
				if(qv.contains(nodes[j].getName())){
					sample.set(sampleCounter, value);
					sampleCounter++;
				}
			}

			//Sample now contains the sampled values for all of the query variables
			//The sampled values are guaranteed to be consistent with the evidence
			 ws.increment(sample, 1);
		}
		ws.normalizeWeights();
		return ws;

	}


	/**
	 * Approximates the query using likelihood weighting
	 *
	 * @param q
	 * 			The query
	 *
	 * @param numSamples
	 * 			The number of samples for likelihood weighting
	 * @return
	 * 			A probability distribution over the query variables
	 */

	public WeightedSet likelihoodWeighting(Query q, int numSamples) {
		Set<String> ev = q.evidenceVariables;
		Set<String> qv = q.queryVariables;
		HashMap<String, Boolean> eVal = q.evidenceValues;

		WeightedSet ws = new WeightedSet(qv.size()); //not sure

		for(int i = 0; i < numSamples; i++){
			// weight ← 1
			double weight = 1;
			//sample ← {}
			BitVector sample = new BitVector(qv.size());
			int sampleCounter = 0;

			for(int j = 0; j < nodes.length; j++){
				//Xi is an evidence variable
				if(ev.contains(nodes[j].getName())){
					boolean evidence = eVal.get(nodes[j].getName());
					nodes[j].setValue(evidence); // clamp
					//How likely is the evidence given what we've sampled so far?
					weight = weight * nodes[j].getProbability();
				}
				else{
					boolean value = nodes[j].sampleAndSet();
					// Xi is a query variable'
					if(qv.contains(nodes[j].getName())){
						sample.set(sampleCounter, value);
						sampleCounter++;
					}
				}
			}
			//Sample now contains the sampled values for all of the query variables
    	//The sampled values are guaranteed to be consistent with the evidence
			 ws.increment(sample, weight);
		}
		ws.normalizeWeights();
		return ws;
	}
}
