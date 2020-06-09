/**
 * 
 */
package cdst.sm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Behavior;
import org.eclipse.uml2.uml.CallEvent;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Event;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Pseudostate;
import org.eclipse.uml2.uml.PseudostateKind;
import org.eclipse.uml2.uml.Region;
import org.eclipse.uml2.uml.SignalEvent;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.StateMachine;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.Trigger;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Vertex;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;
import org.eclipse.uml2.uml.internal.impl.EnumerationImpl;
import org.eclipse.uml2.uml.internal.impl.PrimitiveTypeImpl;
import org.eclipse.uml2.uml.resource.UMLResource;

import cdst.utils.UMLModelFactory;

/**
 * A class that flattens a UML state machine. 
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class SMFlattener {
	private static ArrayList<String> createdClasses = new ArrayList<>(1);
	private static ArrayList<String> doneStates = new ArrayList<>(1);
	private static ArrayList<String> iotLinks = new ArrayList<>(1);
	private static ArrayList<FVertex> allStates = new ArrayList<>(1);
	private static int nameCount=1;
	private static ArrayList<String> linkedStates = new ArrayList<>(1);
	private static ArrayList<String> linkedFinalStates = new ArrayList<>(1);
	public static String fInitialState = null;
	private static ArrayList<Vertex> createdVertics = new ArrayList<>(1);
	
	/**
	 * A method that reads the state machine, flatten it and store in UML format.
	 * 
	 * @param smElements
	 * @return Flattened state machine path
	 */
	public static String flattenStateMachine(EList<PackageableElement> smElements) {
		String fsmDir = "InputFiles";
		String fsmFile = "FSM";
		Package _package = UMLModelFactory.createPackage("FSMPackage");

		//read state machine model
		for (PackageableElement element : smElements){
			//for nested package
			if(element.eClass() == UMLPackage.Literals.PACKAGE){
				org.eclipse.uml2.uml.Package nestedPackage = (org.eclipse.uml2.uml.Package) element;
				EList<PackageableElement> nestedPackagedElements = nestedPackage.getPackagedElements();
				for (PackageableElement nestedElement : nestedPackagedElements){
					readModel(_package, nestedElement);
				}
			}
			else
				readModel(_package, element);
		}

		//save flattened state machine model
		saveModel(_package, URI.createURI(fsmDir).appendSegment(fsmFile).appendFileExtension(UMLResource.FILE_EXTENSION));
		return fsmDir+File.separator+fsmFile+".uml";
	}

	/**
	 * A method that reads the packaged elements and manipulate them accordingly.
	 * 
	 * @param pkg
	 * @param element
	 */
	private static void readModel(Package pkg, PackageableElement element){
		if (element.eClass() == UMLPackage.Literals.CLASS)
		{
			Class clas = (Class) element;
			//System.out.println("Class: "+clas.getName());
			Class newClass = handleClass(pkg, clas);
			if (newClass != null) {
				//state machine as class owned behavior
				EList<Behavior> ownedBehavior = clas.getOwnedBehaviors();
				if (ownedBehavior != null && !ownedBehavior.isEmpty()) {
					handleBehavioralModel(newClass, ownedBehavior);
				} 
			}
		}
	}

	/**
	 * A method that creates the UML classes corresponding to the modeled classes.
	 * 
	 * @param pkg
	 * @param clas
	 * @return newly created UML class
	 */
	private static Class handleClass(Package pkg, Class clas) {
		Class newClass = null;
		if(!createdClasses.contains(clas.getName())) {
			newClass = UMLModelFactory.createClass(pkg, clas.getName(), clas.isAbstract());
			createdClasses.add(clas.getName());
			for(Property prop: clas.getOwnedAttributes()) {
				if(prop.getType() instanceof PrimitiveTypeImpl) {
					PrimitiveTypeImpl primitiveType = (PrimitiveTypeImpl) prop.getType();
					String type = primitiveType.eProxyURI().fragment();
					UMLModelFactory.createProperty(newClass, prop.getName(), UMLModelFactory.createPrimitiveType(pkg, type), 0, 1, 0, 0);
				}
				else if(prop.getType() instanceof EnumerationImpl) {
					EnumerationImpl enumType = (EnumerationImpl) prop.getType();
					UMLModelFactory.createProperty(newClass, prop.getName(), UMLModelFactory.createEnumeration(pkg, enumType.getName()), 0, 1, 0, 0);
					Enumeration enumeration = null;
					for (PackageableElement element : pkg.getPackagedElements()){
						if(element.eClass() == UMLPackage.Literals.ENUMERATION){
							Enumeration enm = (Enumeration) element;
							if(enm.getName().equals(enumType.getName())){
								enumeration = enm;
								break;
							}
						}
					}
					for(EnumerationLiteral el:enumType.getOwnedLiterals()) {
						UMLModelFactory.createEnumerationLiteral(enumeration, el.getName());
					}
				}
				else if(prop.getType() instanceof ClassImpl) {
					ClassImpl classType = (ClassImpl) prop.getType();
					if(!createdClasses.contains(classType.getName()) && classType.getName()!=null) {
						Class linkedClass = UMLModelFactory.createClass(pkg, classType.getName(), classType.isAbstract());
						UMLModelFactory.createAssociation(newClass, true, prop.getAggregation(), prop.getName(), 
								prop.getLower(), prop.getUpper(), linkedClass, false, AggregationKind.NONE_LITERAL, newClass.getName().toLowerCase(), prop.getLower(), prop.getUpper());
						createdClasses.add(linkedClass.getName());
					}
				}
			}
		}
		return newClass;
	}

	/**
	 * A method that reads the state machine and flattens it. 
	 * 
	 * @param newClass
	 * @param ownedBehavior
	 */
	private static void handleBehavioralModel(Class newClass, EList<Behavior> ownedBehavior) {
		for (Behavior behavior: ownedBehavior)
		{
			if(behavior.eClass() == UMLPackage.Literals.STATE_MACHINE)
			{
				StateMachine sm = (StateMachine)behavior;
				if(sm.getSubmachineStates().isEmpty()) { 
					StateMachine newSM = (StateMachine) newClass.createOwnedBehavior(sm.getName(), sm.eClass());
					EList<Region> regions = sm.getRegions();
					Pseudostate initalState = null;
					for(Region region: regions)
					{
						EList<Vertex> vertices = region.getSubvertices();
						for(Vertex vertex: vertices){
							if(vertex.eClass() == UMLPackage.Literals.PSEUDOSTATE){
								Pseudostate pstate = (Pseudostate) vertex;
								if(pstate.getKind().getValue() == PseudostateKind.INITIAL) {
									initalState = pstate;
									if(pstate.getName()!= null && !doneStates.contains(pstate.getName()))
										doneStates.add(pstate.getName());
								}
								else if(pstate.getKind().getValue() == PseudostateKind.FORK) {	
									//TODO: handle forks
								}
							}
							else if(vertex.eClass() == UMLPackage.Literals.STATE){
								State state = (State) vertex;
								if(doneStates.contains(state.getName())) continue;
								//composite but not orthogonal state
								if(!state.getRegions().isEmpty() && state.getRegions().size()<2) {								
									//System.out.println("Composite State: "+state.getName());
									ArrayList<FVertex> compositeStates = handleCompositeState(state);
									allStates.addAll(compositeStates);
								}
								//composite and orthogonal state
								else if(!state.getRegions().isEmpty() && state.getRegions().size()>1) {								
									//System.out.println("Composite & Orth State: "+state.getName());
									ArrayList<FVertex> orthogonalStates = handleOrthogonalState(state);
									allStates.addAll(orthogonalStates);
								}
								//simple state
								else {
									//System.out.println("Simple State: "+state.getName());
									FVertex simpleState = new FVertex(state.getName(), state.getIncomings(), state.getOutgoings());
									allStates.add(simpleState);
									if(state.getName()!= null && !doneStates.contains(state.getName()))
										doneStates.add(state.getName());
								}
							}
							else if(vertex.eClass() == UMLPackage.Literals.FINAL_STATE){
								State fstate = (State) vertex;
								FVertex finalState = new FVertex(fstate.getName(), fstate.getIncomings(), null);
								allStates.add(finalState);
								if(fstate.getName()!= null && !doneStates.contains(fstate.getName()))
									doneStates.add(fstate.getName());
							}
						}
					}

					//create state machine states and links
					createStatesLinks(newSM, initalState);
					//System.out.println(doneStates);
				}
			}
		}
	}

	/**
	 * A method that creates initiate the link creation from the initial state. 
	 * 
	 * @param newSM
	 * @param initalState
	 */
	private static void createStatesLinks(StateMachine newSM, Pseudostate initalState) {
		Region newSMRegion = newSM.createRegion(newSM.getName()+"Region1");
		Vertex initalVertex = newSMRegion.createSubvertex(initalState.getName(), initalState.eClass());
		for(Transition initOut: initalState.getOutgoings()) {
			Vertex itVertex = initOut.getTarget();
			if(itVertex.eClass() == UMLPackage.Literals.STATE){
				linkStates(newSMRegion, itVertex, initalVertex, initOut);
			}
		}
	}

	/**
	 * A method that recursively creates incoming and outgoing links between the flattened states.
	 * 
	 * @param newSMRegion
	 * @param itVertex
	 * @param sVertex
	 * @param stTransition
	 */
	private static void linkStates(Region newSMRegion, Vertex itVertex, Vertex sVertex, Transition stTransition) {
		State iTarget = (State) itVertex;
		ArrayList<FVertex> tarStates = findStates(iTarget.getName());
		for(FVertex tState : tarStates) {	
			if(fInitialState == null)
				fInitialState = tState.getName();
			if(!linkedStates.contains(tState.getName())) {
				Vertex targetVertex = newSMRegion.createSubvertex(tState.getName(), iTarget.eClass());
				createdVertics.add(targetVertex);
				createStateTransition(newSMRegion, sVertex, targetVertex, stTransition);
				linkedStates.add(tState.getName());
				for(Transition tranOut: tState.getOutgoings()) {
					Vertex itNVertex = tranOut.getTarget();
					if(itNVertex.eClass() == UMLPackage.Literals.STATE){
						State inewTarget = (State) itNVertex;
						linkStates(newSMRegion, inewTarget, targetVertex, tranOut);
					}
					else if(itNVertex.eClass() == UMLPackage.Literals.FINAL_STATE){
						State finalState = (State) itNVertex;
						if(!linkedFinalStates.contains(targetVertex.getName())) {
							Vertex fVertex = newSMRegion.createSubvertex(finalState.getName(), finalState.eClass());
							createdVertics.add(fVertex);
							createStateTransition(newSMRegion, targetVertex, fVertex, tranOut);
							linkedFinalStates.add(targetVertex.getName());
						}
					}
				}
				for(Transition tranInc: tState.getIncomings()) {
					Vertex itNVertex = tranInc.getSource();
					if(itNVertex.eClass() == UMLPackage.Literals.STATE){
						State inewTarget = (State) itNVertex;
						Vertex crINVertex = null;
						for(Vertex crVer : createdVertics) {
							if(crVer.getName() != null) {
								if(crVer.getName().equals(inewTarget.getName()) || crVer.getName().contains(inewTarget.getName())) {
									crINVertex = crVer;
									//System.out.println("Found: "+crVer.getName());
									break;
								}
							}
						}
						if(crINVertex == null) {
							crINVertex = newSMRegion.createSubvertex(inewTarget.getName(), inewTarget.eClass());
						}
						createStateTransition(newSMRegion, targetVertex, crINVertex, tranInc);
					}
				}
			}
		}
	}

	/**
	 * A method that creates a new transition between the source and target vertex (flattened states).
	 * 
	 * @param newSMRegion
	 * @param sourceVertex
	 * @param targetVertex
	 * @param sourceTran
	 */
	private static void createStateTransition(Region newSMRegion, Vertex sourceVertex, Vertex targetVertex, Transition sourceTran) {
		if (!iotLinks.contains(sourceVertex.getName()+"-"+targetVertex.getName())) {
			Transition newTransition = newSMRegion.createTransition("T" + nameCount);
			newTransition.setSource(sourceVertex);
			newTransition.setTarget(targetVertex);
			newTransition.setGuard(sourceTran.getGuard());
			newTransition.setEffect(sourceTran.getEffect());
			for (Trigger trigger : sourceTran.getTriggers()) {
				Event preEvent = trigger.getEvent();
				if (preEvent.eClass() == UMLPackage.Literals.CALL_EVENT) {
					CallEvent callEvent = (CallEvent) preEvent;
					Trigger newTrigger = newTransition.createTrigger(callEvent.getOperation().getName());
					newTrigger.setEvent(trigger.getEvent());
				} else if (preEvent.eClass() == UMLPackage.Literals.SIGNAL_EVENT) {
					SignalEvent signalEvent = (SignalEvent) preEvent;
					Trigger newTrigger = newTransition.createTrigger(signalEvent.getSignal().getName());
					newTrigger.setEvent(trigger.getEvent());
				}
			}
			iotLinks.add(sourceVertex.getName()+"-"+targetVertex.getName());
			nameCount++;
		}
	}

	/**
	 * A method that finds the list of states corresponding to the input state name.
	 * 
	 * @param name
	 * @return list of states containing the name
	 */
	private static ArrayList<FVertex> findStates(String name) {
		ArrayList<FVertex> fStates = new ArrayList<>(1);
		for(FVertex state : allStates) {
			if(state.getName() != null)
				if(state.getName().equals(name) || state.getName().contains(name))
					fStates.add(state);
		}
		return fStates;
	}

	/**
	 * A method that flattens the composite states. 
	 * 
	 * @param state
	 * @return a list of flattened composite states
	 */
	private static ArrayList<FVertex> handleCompositeState(State state) {
		ArrayList<FVertex> compositeStates = new ArrayList<>(1);
		ArrayList<FVertex> subSMStates = null;
		Region nestedRegion = state.getRegions().get(0);
		EList<Vertex> nestedVertices = nestedRegion.getSubvertices();
		for(Vertex nvertex: nestedVertices){
			FVertex compositeState = null;
			if(nvertex.eClass() == UMLPackage.Literals.STATE){
				State nestedState = (State) nvertex;
				//handle sub state machine
				if(nestedState.getSubmachine() != null) {
					subSMStates = handleSubStatemachine(state.getName(), nestedState.getSubmachine());
				}else {
					String newState = state.getName()+":"+nestedState.getName();
					//System.out.println("- newState: "+newState);

					EList<Transition> incomings = state.getIncomings();
					EList<Transition> outgoings = state.getOutgoings();
					incomings.addAll(nestedState.getIncomings());
					outgoings.addAll(nestedState.getOutgoings());
					//					EList<Transition> outgoings = filterOutTransitions(state.getOutgoings());
					//					outgoings.addAll(filterOutTransitions(nestedState.getOutgoings()));
					compositeState = new FVertex(newState, incomings, outgoings);

					if(!doneStates.contains(state.getName()))
						doneStates.add(state.getName());
					if(!doneStates.contains(nestedState.getName()))
						doneStates.add(nestedState.getName());
					if(!doneStates.contains(newState))
						doneStates.add(newState);
				}
			}
			if(subSMStates != null && compositeState == null) {
				for(FVertex subState : subSMStates) {
					String newState = state.getName()+":";
					newState += subState.getName();
					EList<Transition> incomings = state.getIncomings();
					EList<Transition> outgoings = state.getOutgoings();
					incomings.addAll(subState.getIncomings());
					outgoings.addAll(subState.getOutgoings());
					//					EList<Transition> outgoings = filterOutTransitions(state.getOutgoings());
					//					outgoings.addAll(filterOutTransitions(subState.getOutgoings()));
					FVertex nCompState = new FVertex(newState, incomings, outgoings);
					compositeStates.add(nCompState);
					if(!doneStates.contains(newState))
						doneStates.add(newState);
				}
			}
			else if(subSMStates == null && compositeState != null) {
				compositeStates.add(compositeState);
			}
		}
		return compositeStates;
	}

	/*private static EList<Transition> filterOutTransitions(EList<Transition> outgoings) {
		EList<Transition> newOutgoings = new BasicEList<Transition>();
		for(Transition tranOut: outgoings) {
			Vertex itNVertex = tranOut.getTarget();
			if(!isFinal && itNVertex.eClass() == UMLPackage.Literals.FINAL_STATE) {
				newOutgoings.add(tranOut);
				isFinal=true;
			}
			else if(isFinal && itNVertex.eClass() != UMLPackage.Literals.FINAL_STATE){
				newOutgoings.add(tranOut);
			}
			//			newOutgoings.add(tranOut);
		}
		return newOutgoings;
	}*/

	/**
	 * A method that flattens the orthogonal states.
	 * 
	 * @param state
	 * @return a list of flattened states
	 */
	private static ArrayList<FVertex> handleOrthogonalState(State state) {
		ArrayList<FVertex> orthogonalStates = new ArrayList<>(1);
		for(int i=0;i<state.getRegions().size();i++) {
			Region sourceRegion = state.getRegions().get(i);
			for(int j=i+1;j<state.getRegions().size();j++) {
				Region targetRegion = state.getRegions().get(j);
				for(Vertex srcVertex : sourceRegion.getSubvertices()) {
					if(srcVertex.eClass() == UMLPackage.Literals.STATE) {
						State srcState = (State) srcVertex;
						ArrayList<FVertex> srcCompStates = new ArrayList<>(1);
						if(!srcState.getRegions().isEmpty() && srcState.getRegions().size()<2) {								
							//System.out.println("Src Composite State: "+srcState.getName());
							srcCompStates.addAll(handleCompositeState(srcState));
						}else {
							FVertex nsState = new FVertex(srcState.getName(), srcState.getIncomings(), srcState.getOutgoings());
							srcCompStates.add(nsState);
						}
						for(Vertex tarVertex : targetRegion.getSubvertices()) {
							if(tarVertex.eClass() == UMLPackage.Literals.STATE) {
								State tarState = (State) tarVertex;
								ArrayList<FVertex> tarCompStates = null;
								if(!tarState.getRegions().isEmpty() && tarState.getRegions().size()<2) {								
									//System.out.println("Tar Composite State: "+tarState.getName());
									tarCompStates = handleCompositeState(tarState);
								}

								if(tarCompStates != null) {
									for(FVertex scState : srcCompStates) {
										for(FVertex tcState : tarCompStates) {
											String newState = scState.getName()+":"+tcState.getName();
											//System.out.println("- tsnewState: "+newState);
											EList<Transition> incomings = scState.getIncomings();
											EList<Transition> outgoings = scState.getOutgoings();
											incomings.addAll(tcState.getIncomings());
											outgoings.addAll(tcState.getOutgoings());
											//											EList<Transition> outgoings = filterOutTransitions(scState.getOutgoings());
											//											outgoings.addAll(filterOutTransitions(tcState.getOutgoings()));
											FVertex compositeState = new FVertex(newState, incomings, outgoings);
											allStates.add(compositeState);
										}
									}
								}
								else {
									for(FVertex scState : srcCompStates) {
										String newState = scState.getName()+":"+tarState.getName();
										//System.out.println("- tsnewState: "+newState);
										EList<Transition> incomings = scState.getIncomings();
										EList<Transition> outgoings = scState.getOutgoings();
										incomings.addAll(tarState.getIncomings());
										outgoings.addAll(tarState.getOutgoings());
										//										EList<Transition> outgoings = filterOutTransitions(scState.getOutgoings());
										//										outgoings.addAll(filterOutTransitions(tarState.getOutgoings()));
										FVertex compositeState = new FVertex(newState, incomings, outgoings);
										allStates.add(compositeState);
									}
								}
							}
						}
					}
				}
			}
		}
		return orthogonalStates;
	}

	/**
	 * A method that flattens the sub state machine. 
	 * 
	 * @param rootState
	 * @param subSM
	 * @return a list of flattened states
	 */
	private static ArrayList<FVertex> handleSubStatemachine(String rootState, StateMachine subSM) {
		ArrayList<FVertex> subSMStates = new ArrayList<>(1);
		EList<Region> regions = subSM.getRegions();
		for(Region region: regions)
		{
			EList<Vertex> vertices = region.getSubvertices();
			for(Vertex vertex: vertices)
			{
				if(vertex.eClass() == UMLPackage.Literals.STATE)
				{
					State state = (State)vertex;
					if(doneStates.contains(state.getName())) {
						for(FVertex subSMState:allStates) {
							if(subSMState.getName() != null) {
								if(subSMState.getName().equals(state.getName()) || subSMState.getName().contains(state.getName())){
									subSMStates.add(subSMState);
								}
							}
						}
					}
					else {
						if(state.isComposite() && state.getRegions().size()==1) {
							Region nRegion = state.getRegions().get(0);
							EList<Vertex> nVertices = nRegion.getSubvertices();
							for(Vertex nv: nVertices)
							{
								if(nv.eClass() == UMLPackage.Literals.STATE)
								{
									State nState = (State)nv;
									String newState = state.getName()+":"+nState.getName();
									//System.out.println("- newState: "+newState);
									EList<Transition> incomings = state.getIncomings();
									EList<Transition> outgoings = state.getOutgoings();
									incomings.addAll(nState.getIncomings());
									outgoings.addAll(nState.getOutgoings());
									//									EList<Transition> outgoings = filterOutTransitions(state.getOutgoings());
									//									outgoings.addAll(filterOutTransitions(nState.getOutgoings()));
									FVertex compositeState = new FVertex(newState, incomings, outgoings);
									//									allStates.add(compositeState);
									subSMStates.add(compositeState);
									if(!doneStates.contains(state.getName()))
										doneStates.add(state.getName());
									if(!doneStates.contains(nState.getName()))
										doneStates.add(nState.getName());
									if(!doneStates.contains(newState))
										doneStates.add(newState);
								}
							}
						}
					}
				}
			}
		}
		return subSMStates;
	}

	/**
	 * A method that exports UML model to file
	 * 
	 * @param _package
	 * @param uri
	 */
	@SuppressWarnings("unchecked")
	private static void saveModel(org.eclipse.uml2.uml.Package _package, URI uri) {
		Resource resource = new ResourceSetImpl().createResource(uri);
		resource.getContents().add(_package);
		try {
			resource.save(null);
			System.out.println("Flattened state machine is saved. Path - ["+uri+"]");
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
}
