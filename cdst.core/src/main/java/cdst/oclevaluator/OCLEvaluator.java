/**
 * 
 */
package cdst.oclevaluator;

import java.util.ArrayList;

import cdst.image.utils.AircraftData;
import snt.oclsolver.IEvaluatorWrapper;
import snt.oclsolver.datatypes.IntegerValueTuple;
import snt.oclsolver.distance.ClassDiagramTestData;
import snt.oclsolver.distance.Problem;
import snt.oclsolver.distance.SimpleProblem;
import snt.oclsolver.experiment.ExperimentUtil;
import snt.oclsolver.search.IndividualFactory;
import snt.oclsolver.search.Search;
import snt.oclsolver.tuples.ClassifierTuple;
import snt.oclsolver.tuples.ClassifierValueTuple;
import snt.oclsolver.tuples.ValueTuple;

/**
 * A class to setup OCL evaluator, manipulate instance model, and evaluate OCL constraints. 
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class OCLEvaluator {
	private static ExperimentUtil expUtil = new ExperimentUtil();

	/**
	 * A method that loads UML model from the file to setup OCL evaluator 
	 * 
	 * @param filePath
	 */
	public static void setupOCLEvalutor(String filePath) {
		expUtil.loadModel(filePath);
		Problem.useRangeReduction = false;
		Problem.reinitializeEOCL = false;
	}

	/**
	 * A method that retrieves instance model from environment and returns in the form of classifier tuples 
	 * 
	 * @param modelPath
	 * @param constraint
	 * @return instanceModel: ArrayList<ClassifierTuple>
	 */
	public static ArrayList<ClassifierTuple> getInstanceModel(String modelPath, String constraint) {
		boolean useInstanceOptimization = false;
		expUtil.initializeEOCL(modelPath, constraint, useInstanceOptimization);
		Problem prob = new SimpleProblem(constraint,modelPath, null);
		//create random individual
		Search searchAlgo = new snt.oclsolver.search.AVM();
		IndividualFactory iFactory = searchAlgo.getiFactory();
		//set problem type to simple - just for evaluation
		iFactory.setType("simple");
		return prob.getQueryVariables();
	}

	/**
	 * A method that updates instance model in environment according to the given classifier tuples and the values
	 * 
	 * @param tuples
	 * @param data
	 */
	public static void updateInstanceModel(ArrayList<ClassifierTuple> tuples, ArrayList<AircraftData> data) {
		for(AircraftData aData : data) {
			for(ClassifierTuple ct: tuples) {
				if(ct.getClassName().equals(aData.getCdsElementName())) {
					for(ClassifierValueTuple cvt: ct.getObjectTuples()) {
						for(ValueTuple vt: cvt.getAttributeValues()) {
							if(vt.getRelatedProperty().getRoleeName().equals(aData.getPropertyName())) {
								if(vt instanceof IntegerValueTuple) {
									((IntegerValueTuple) vt).setValue(aData.getValue());
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * A method that evaluates OCL constraint on the instance model (in environment)
	 * 
	 * @param tuples
	 * @param constraint
	 * @return result
	 */
	public static String evaluateConstraint(ArrayList<ClassifierTuple> tuples, String constraint) {
		String result = null;
		ClassDiagramTestData data = ClassDiagramTestData.getInstance();
		try {
			data.updateObjectDiagram(tuples, constraint);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		IEvaluatorWrapper ieos = data.getIEOSWrapper();
		try {
			result = ieos.query(constraint);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
