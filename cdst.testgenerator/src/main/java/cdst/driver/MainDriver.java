package cdst.driver;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.xmi.UnresolvedReferenceException;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;

import cdst.sm.SMFlattener;
import cdst.sm.SMReader;
import cdst.sm.SMState;
import cdst.sm.SMTransition;
import cdst.tc.TestCase;
import cdst.tc.TestPath;
import cdst.utils.ModelLoader;

public class MainDriver {

	public static void main(String[] args) {
		SMReader smReader = new SMReader();
		EList<PackageableElement> smElements = loadStateMachine("InputFiles/ArduPlane1.uml");
		String fsmPath = SMFlattener.flattenStateMachine(smElements);
		
		EList<PackageableElement> sourcePackagedElements = loadStateMachine(fsmPath);
		
		try {
			smReader.processTheModel(sourcePackagedElements);
		} catch (UnresolvedReferenceException e) {
			e.printStackTrace();
		}

		ArrayList<SMState> states;
		ArrayList<SMTransition> transitions = null;
		
		states = smReader.getStates();
		transitions = smReader.getTransitions();
		
		//========================================
		TestCase testCases=new TestCase();
		ArrayList<SMTransition> modifiedTransitions = testCases.getPathReadyTransitions(transitions);
		
		testCases.generateTestPaths(modifiedTransitions, null,null,new TestPath(), SMFlattener.fInitialState);
		testCases.printTestPaths();
		System.out.println("Transition Tree has been generated in \"TransitionTree.txt\"");
		//========================================
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("States.txt");
			for (SMState stateMachineState : states) {
				writer.println(stateMachineState.getStateName());

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
		
		try {
			writer = new PrintWriter("Transitions.txt");
			for (SMTransition stateMachineTransition : modifiedTransitions) {
				writer.println(stateMachineTransition.getTransitionName());
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	/**
	 * A method to load the UML state machine elements from the specified path.
	 * 
	 * @param modelPath
	 * @return state machine elements
	 */
	private static EList<PackageableElement> loadStateMachine(String modelPath) {
		ModelLoader smloader = new ModelLoader();
		Object objModel = smloader.loadModel(modelPath);
		Model sourceModel;
		EList<PackageableElement> sourcePackagedElements = null;
		if (objModel instanceof Model) {
			sourceModel = (Model) objModel;
			sourcePackagedElements = sourceModel.getPackagedElements();
		} else if (objModel instanceof Package) {
			Package sourcePackage = (Package) objModel;
			sourcePackagedElements = sourcePackage.getPackagedElements();
		}
		return sourcePackagedElements;
	}
}

