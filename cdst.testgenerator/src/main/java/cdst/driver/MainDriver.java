package cdst.driver;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.xmi.UnresolvedReferenceException;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Transition;

import cdst.sm.SMReader;
import cdst.sm.SMState;
import cdst.sm.SMTransition;
import cdst.tc.TestCase;
import cdst.tc.TestPath;
import cdst.utils.ModelLoader;

public class MainDriver {

	public static void main(String[] args) {
		ModelLoader smloader = new ModelLoader();
		SMReader smReader = new SMReader();
//		Object objModel = smloader.loadModel("InputFiles/MarioPackage.uml");
//		Object objModel = smloader.loadModel("InputFiles/StateAMachine.uml");
//		Object objModel = smloader.loadModel("InputFiles/Plane.uml");
		Object objModel = smloader.loadModel("InputFiles/ArduPlane.uml");
//		Object objModel = smloader.loadModel("InputFiles/PlaneTurns.uml");
		Model sourceModel;
		EList<PackageableElement> sourcePackagedElements = null;
		if (objModel instanceof Model) {
			sourceModel = (Model) objModel;
			sourcePackagedElements = sourceModel.getPackagedElements();
		} else if (objModel instanceof Package) {
			Package sourcePackage = (Package) objModel;
			sourcePackagedElements = sourcePackage.getPackagedElements();
		}
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
		
		//System.out.println("\nTestPathsGeneration :");
		//for turns
//		ArrayList<String>doneStates = new ArrayList<>();
//		for(SMTransition t:modifiedTransitions) {
//			if(!doneStates.contains(t.getSourceState())) {
//				testCases.generateTestPaths(modifiedTransitions, null,null,new TestPath(), t.getSourceState());
//				doneStates.add(t.getSourceState());
//			}
//		}
		testCases.generateTestPaths(modifiedTransitions, null,null,new TestPath(), "Idle");
//		testCases.generateTestPaths(modifiedTransitions, null,null,new TestPath(), "Straight");
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
}
