package cdst.application;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import cdst.modelutils.ClassInfo;
import cdst.modelutils.ModelReader;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * @author Hassan Sartaj
 * @version 1.1
 */
public class OCLGenMain extends Application {
	private ArrayList<ClassInfo> classesInfo;
	@Override
	public void start(Stage primaryStage) {
		try {
			CodeArea codeArea = new CodeArea();

			// add line numbers to the left of area
			codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

			// recompute the syntax highlighting 500 ms after user stops editing area
			/*Subscription cleanupWhenNoLongerNeedIt = codeArea

					// plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
					// multi plain changes = save computation by not rerunning the code multiple times
					//   when making multiple changes (e.g. renaming a method at multiple parts in file)
					.multiPlainChanges()

					// do not emit an event until 500 ms have passed since the last emission of previous stream
					.successionEnds(Duration.ofMillis(500))

					// run the following code block when previous stream emits an event
					.subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));*/
			
			// recompute the syntax highlighting 500 ms after user stops editing area
			codeArea.multiPlainChanges().successionEnds(Duration.ofMillis(500))
			.subscribe(ignore -> codeArea.setStyleSpans(0, OCLGenController.computeHighlighting(codeArea.getText())));
			
			final FileChooser fileChooser = new FileChooser();
			final DirectoryChooser dirChooser = new DirectoryChooser();
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("OCLGen.fxml"));
			ObservableList<Node> children = root.getChildren();
			StackPane sp = null;
			for(Node c: children) {
				if(c instanceof StackPane) {
					sp = (StackPane) c;
					sp.getChildren().add(new VirtualizedScrollPane<>(codeArea));
					break;
				}
			}
			
			for(Node c: children) {
				if(c instanceof ComboBox) {
					if(c.idProperty().getValue().equals("propCB")) {
						ComboBox<?> propCB = (ComboBox<?>) c;
						propCB.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								if (classesInfo != null)
									OCLGenController.handleDataType(classesInfo, propCB.getValue().toString(), children);
							}
						});
					}
					else if(c.idProperty().getValue().equals("enumCB")) {
						ComboBox<?> enumCB = (ComboBox<?>) c;
						enumCB.setDisable(true);
					}
				}
				else if(c instanceof Button) {
					if(c.idProperty().getValue().equals("browseBtn")) {
						Button browseBtn = (Button) c;
						browseBtn.setGraphic(new ImageView("icons/icons8-search-folder-24.png"));
						browseBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								OCLGenController.configureFileChooser(fileChooser);
								File file = fileChooser.showOpenDialog(primaryStage);
								for(Node cn: children) {
									if(cn instanceof TextField) {
										if(cn.idProperty().getValue().equals("mpathTF")) {
											TextField pathTF = (TextField) cn;
											try {
												pathTF.setText(file.getPath());
											} catch (Exception e) {
												Alert alert = new Alert(AlertType.ERROR);
												alert.setTitle("File Error");
												alert.setHeaderText("No File Choosen");
												alert.setContentText("Expand to see the full detail!");
												StringWriter sw = new StringWriter();
												PrintWriter pw = new PrintWriter(sw);
												e.printStackTrace(pw);
												alert.getDialogPane().setExpandableContent(OCLGenController.createExceptionArea(sw.toString()));
												alert.showAndWait();
											}
											break;
										}
									}
								}
							}
						});
					}
					else if(c.idProperty().getValue().equals("loadBtn")) {
						Button loadBtn = (Button) c;
						loadBtn.setGraphic(new ImageView("icons/icons8-load-24.png"));
						loadBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								//TODO:add extract child method
								for(Node cn: children) {
									if(cn instanceof TextField) {
										if(cn.idProperty().getValue().equals("mpathTF")) {
											TextField pathTF = (TextField) cn;
											String path = pathTF.getText();
											if(!path.isEmpty())
												classesInfo = ModelReader.getClassDetails(path);
											else {
												Alert alert = new Alert(AlertType.ERROR);
												alert.setTitle("Path Error");
												alert.setHeaderText("No model file path");
												alert.setContentText("Please select UML model file to load!");
												alert.showAndWait();
											}
											break;
										}
									}
								}
								if (classesInfo != null) {
									for (Node cn : children) {
										if(cn instanceof ComboBox) {
											if(cn.idProperty().getValue().equals("classCB")) {
												@SuppressWarnings("unchecked")
												ComboBox<String> classCB = (ComboBox<String>) cn;
												//TODO:iterate classesInfo
												classCB.setValue(classesInfo.get(0).getClassName());
											}
											else if(cn.idProperty().getValue().equals("propCB")) {
												@SuppressWarnings("unchecked")
												ComboBox<String> propCB = (ComboBox<String>) cn;
												Set<String> attrs = classesInfo.get(0).getAttrDtMap().keySet();
												ObservableList<String> attrList = FXCollections.observableArrayList();
												attrList.addAll(attrs);
												propCB.setPromptText(attrList.get(0));
												propCB.setItems(attrList);
												OCLGenController.handleDataType(classesInfo, attrList.get(0), children);
											}
											else if(cn.idProperty().getValue().equals("stateCB")) {
												@SuppressWarnings("unchecked")
												ComboBox<String> stateCB = (ComboBox<String>) cn;
												ObservableList<String> statesList = FXCollections.observableArrayList();
												statesList.addAll(classesInfo.get(0).getStates());
												stateCB.setPromptText(statesList.get(0));
												stateCB.setItems(statesList);
											}
											else if(cn.idProperty().getValue().equals("logOpCB")) {
												@SuppressWarnings("unchecked")
												ComboBox<String> lopsCB = (ComboBox<String>) cn;
												ObservableList<String> lopsList = FXCollections.observableArrayList();
												lopsList.add("AND");
												lopsList.add("OR");
												lopsList.add("IMPLIES");
												lopsList.add("XOR");
												lopsCB.setPromptText(lopsList.get(0));
												lopsCB.setItems(lopsList);
											}
											else if(cn.idProperty().getValue().equals("relOpCB")) {
												@SuppressWarnings("unchecked")
												ComboBox<String> ropsCB = (ComboBox<String>) cn;
												ObservableList<String> ropsList = FXCollections.observableArrayList();
												ropsList.add(">");
												ropsList.add(">=");
												ropsList.add("<");
												ropsList.add("<=");
												ropsList.add("==");
												ropsList.add("<>");
												ropsCB.setPromptText(ropsList.get(0));
												ropsCB.setItems(ropsList);
											}
										}
									} 
								}
							}
						});
					}
					else if(c.idProperty().getValue().equals("newBtn")) {
						Button newBtn = (Button) c;
						newBtn.setGraphic(new ImageView("icons/icons8-new-copy-26.png"));
						newBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								String className=null, state=null;
								for(Node cn: children) {
									if(cn instanceof ComboBox) {
										if(cn.idProperty().getValue().equals("classCB")) {
											ComboBox<?> classCB = (ComboBox<?>) cn;
											if(classCB.getValue() == null) {
												if(!classCB.getItems().isEmpty()){
													className = classCB.getItems().get(0).toString();
												}else {
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Build Error");
													alert.setHeaderText("No class is available");
													alert.setContentText("Please load UML model!");
													alert.showAndWait();
												}
											}
											else
												className = classCB.getValue().toString();
										}
										else if(cn.idProperty().getValue().equals("stateCB")) {
											ComboBox<?> stateCB = (ComboBox<?>) cn;
											if(stateCB.getValue() == null) {
												if(!stateCB.getItems().isEmpty()){
													state = stateCB.getItems().get(0).toString();
												}else {
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Build Error");
													alert.setHeaderText("No state is available");
													alert.setContentText("Please load UML model!");
													alert.showAndWait();
												}
											}
											else
												state = stateCB.getValue().toString();
										}
									}
								}
								String contextPart = "context "+ className+" inv: self.oclIsInState("+state+") ";
								codeArea.appendText("\n"+contextPart);
							}
						});
					}
					else if(c.idProperty().getValue().equals("exportBtn")) {
						Button exportBtn = (Button) c;
						exportBtn.setGraphic(new ImageView("icons/icons8-export-16.png"));
						exportBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								OCLGenController.configureFolderChooser(dirChooser);
								File file = dirChooser.showDialog(primaryStage);
								try {
									String filePath = file.getPath()+"/constraints.ocl";
									if(!codeArea.getText().isEmpty()) {
										String constraints = codeArea.getText();
										OCLGenController.createOCLFile(constraints, filePath);
									}else {
										Alert alert = new Alert(AlertType.ERROR);
										alert.setTitle("Export Error");
										alert.setHeaderText("Nothing to export");
										alert.setContentText("No OCL constraints written to export!");
										alert.showAndWait();
									}
								} catch (Exception e) {
									Alert alert = new Alert(AlertType.ERROR);
									alert.setTitle("Folder Error");
									alert.setHeaderText("No Folder is selected to export");
									alert.setContentText("Expand to see the full detail!");
									StringWriter sw = new StringWriter();
									PrintWriter pw = new PrintWriter(sw);
									e.printStackTrace(pw);
									alert.getDialogPane().setExpandableContent(OCLGenController.createExceptionArea(sw.toString()));
									alert.showAndWait();
								}
							}
						});
					}
//					else if(c.idProperty().getValue().equals("validateBtn")) {
//						Button exportBtn = (Button) c;
//						exportBtn.setOnAction(new EventHandler<ActionEvent>() {
//							@Override
//							public void handle(ActionEvent arg0) {
//								if(!codeArea.getText().isEmpty()) {
//									String valRes = "";
//									
//									String[] allConstraints = codeArea.getText().split("\\r?\\n");
//									for (String constraint : allConstraints) {
//										String result = OCLGenController.validateOCLConstraint(constraint);
//										if(!result.equals("ok")) {
//											valRes += "C: "+constraint+"\n Error: "+result+"\n";
//										}
//									}
//									Alert alert = new Alert(AlertType.ERROR);
//									alert.setTitle("Syntax Error");
//									alert.setHeaderText("OCL constraint with syntax error");
//									alert.setContentText(valRes);
//									alert.showAndWait();
//								}
//								else {
//									Alert alert = new Alert(AlertType.ERROR);
//									alert.setTitle("Error");
//									alert.setHeaderText("Empty Constraints Area");
//									alert.setContentText("No OCL constraint to check syntax!");
//									alert.showAndWait();
//								}
//							}
//
//						});
//					}
					else if(c.idProperty().getValue().equals("undoBtn")) {
						Button undoBtn = (Button) c;
						undoBtn.setText("");
						undoBtn.setGraphic(new ImageView("icons/icons8-undo-30.png"));
						undoBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								if(codeArea.getUndoManager().isUndoAvailable()) {
									codeArea.getUndoManager().undo();
								}
								else {
									Alert alert = new Alert(AlertType.ERROR);
									alert.setTitle("Undo Error");
									alert.setHeaderText("Nothing to undo");
									alert.setContentText("Please build the constraint!");
									alert.showAndWait();
								}
							}
						});
					}
					else if(c.idProperty().getValue().equals("redoBtn")) {
						Button redoBtn = (Button) c;
						redoBtn.setText("");
						redoBtn.setGraphic(new ImageView("icons/icons8-redo-30.png"));
						redoBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								if(codeArea.getUndoManager().isRedoAvailable()) {
									codeArea.getUndoManager().redo();
								}
								else {
									Alert alert = new Alert(AlertType.ERROR);
									alert.setTitle("Redo Error");
									alert.setHeaderText("Nothing to redo");
									alert.setContentText("Please continue building constraints!");
									alert.showAndWait();
								}
							}
						});
					}
					else if(c.idProperty().getValue().equals("buildBtn")) {
						Button buildBtn = (Button) c;
						buildBtn.setGraphic(new ImageView("icons/icons8-add-row-30.png"));
						buildBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								String className=null, attribute=null, state=null, rop=null, lop=null, value=null; 
								boolean isError=false;
								for(Node cn: children) {
									if(cn instanceof ComboBox) {
										if(cn.idProperty().getValue().equals("classCB")) {
											ComboBox<?> classCB = (ComboBox<?>) cn;
											if(classCB.getValue() == null) {
												if(!classCB.getItems().isEmpty()){
													className = classCB.getItems().get(0).toString();
												}else {
													isError=true;
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Build Error");
													alert.setHeaderText("No class is available");
													alert.setContentText("Please load UML model!");
													alert.showAndWait();
												}
											}
											else
												className = classCB.getValue().toString();
										}
										else if(cn.idProperty().getValue().equals("propCB")) {
											ComboBox<?> propCB = (ComboBox<?>) cn;
											if(propCB.getValue() == null) {
												if(!propCB.getItems().isEmpty()){
													attribute = propCB.getItems().get(0).toString();
												}else {
													isError=true;
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Build Error");
													alert.setHeaderText("No property is available");
													alert.setContentText("Please load UML model!");
													alert.showAndWait();
												}
											}
											else {
												attribute = propCB.getValue().toString();
											}
										}
										else if(cn.idProperty().getValue().equals("stateCB")) {
											ComboBox<?> stateCB = (ComboBox<?>) cn;
											if(stateCB.getValue() == null) {
												if(!stateCB.getItems().isEmpty()){
													state = stateCB.getItems().get(0).toString();
												}else {
													isError=true;
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Build Error");
													alert.setHeaderText("No state is available");
													alert.setContentText("Please load UML model!");
													alert.showAndWait();
												}
											}
											else
												state = stateCB.getValue().toString();
										}
										else if(cn.idProperty().getValue().equals("logOpCB")) {
											ComboBox<?> lopsCB = (ComboBox<?>) cn;
											if(lopsCB.getValue() == null) {
												if(!lopsCB.getItems().isEmpty()){
													lop = lopsCB.getItems().get(0).toString().toLowerCase();
												}else {
													isError=true;
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Build Error");
													alert.setHeaderText("No logical operator is available");
													alert.setContentText("Please load UML model!");
													alert.showAndWait();
												}
											}
											else
												lop = lopsCB.getValue().toString().toLowerCase();
										}
										else if(cn.idProperty().getValue().equals("relOpCB")) {
											ComboBox<?> ropsCB = (ComboBox<?>) cn;
											if(ropsCB.getValue() == null) {
												if(!ropsCB.getItems().isEmpty()){
													rop = ropsCB.getItems().get(0).toString();
												}else {
													isError=true;
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Build Error");
													alert.setHeaderText("No relational operator is available");
													alert.setContentText("Please load UML model!");
													alert.showAndWait();
												}
											}
											else
												rop = ropsCB.getValue().toString();
										}
										else if (cn.idProperty().getValue().equals("enumCB")) {
											ComboBox<?> enumCB = (ComboBox<?>) cn;
											if(!enumCB.isDisabled()) {
												if(enumCB.getValue() == null) {
													if(!enumCB.getItems().isEmpty()){
														value = enumCB.getItems().get(0).toString();
													}else {
														isError=true;
														Alert alert = new Alert(AlertType.ERROR);
														alert.setTitle("Build Error");
														alert.setHeaderText("No enum literal is available");
														alert.setContentText("Please load UML model!");
														alert.showAndWait();
													}
												}
												else
													value = enumCB.getValue().toString();
											}
										} 
									}
									else if(cn instanceof TextField) {
										if(cn.idProperty().getValue().equals("valueTF")) {
											TextField valueTF = (TextField) cn;
											if(!valueTF.isDisabled() && valueTF.isEditable()) {
												if(!valueTF.getText().isEmpty())
													value = valueTF.getText();
												else {
													isError=true;
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Build Error");
													alert.setHeaderText("Empty text field");
													alert.setContentText("Please enter integer/real/string value in text field!");
													alert.showAndWait();
												}
											}
										}
									}
									else if(cn instanceof RadioButton) {
										if(cn.idProperty().getValue().equals("trueRB")) {
											RadioButton trueRB = (RadioButton) cn;
											if(trueRB.isSelected())
												value = "true";
										}
										else if(cn.idProperty().getValue().equals("falseRB")) {
											RadioButton falseRB = (RadioButton) cn;
											falseRB.setDisable(true);
											if(falseRB.isSelected())
												value = "false";
										}
									}
								}
								
								if (!isError) {
									String contextPart = "context " + className + " inv: self.oclIsInState(" + state
											+ ") ";
									String clausePart = lop + " self." + attribute + " " + rop + " " + value + " ";
									if (codeArea.getText().isEmpty()) {
										codeArea.appendText(contextPart + clausePart);
									} else {
										codeArea.appendText(clausePart);
									} 
								}
							}
						});
					}
				}
				else if(c instanceof RadioButton) {
					if(c.idProperty().getValue().equals("trueRB")) {
						RadioButton trueRB = (RadioButton) c;
						trueRB.setDisable(true);
					}
					else if(c.idProperty().getValue().equals("falseRB")) {
						RadioButton falseRB = (RadioButton) c;
						falseRB.setDisable(true);
					}
				}
				else if(c instanceof TextField) {
					if(c.idProperty().getValue().equals("mpathTF")) {
						TextField pathTF = (TextField) c;
						pathTF.setText("model/Plane.uml");
					}
					else if(c.idProperty().getValue().equals("valueTF")) {
						TextField valueTF = (TextField) c;
						valueTF.setDisable(true);
					}
				}
			}

			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Constraints Specifier");
			primaryStage.setResizable(false);
			primaryStage.setFullScreen(false);
			primaryStage.getIcons().add(new Image("icons/model1.png"));
			primaryStage.show();

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent arg0) {
					if(!codeArea.getText().isEmpty()) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Unsaved Changes");
						alert.setHeaderText("Unsaved Changes");
						alert.setContentText("Do you want to save constraints!");
						Optional<ButtonType> result = alert.showAndWait();
						if(result.get() == ButtonType.OK) {
							OCLGenController.configureFolderChooser(dirChooser);
							File file = dirChooser.showDialog(primaryStage);
							String filePath = file.getPath()+"/constraints.ocl";
							String constraints = codeArea.getText();
							OCLGenController.createOCLFile(constraints, filePath);
						}
					}
						
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Exception stack trace");
			alert.setHeaderText("Exception");
			alert.setContentText("Hey there is an exception!");
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			alert.getDialogPane().setExpandableContent(OCLGenController.createExceptionArea(sw.toString()));
			alert.showAndWait();
		}
	}
	
	
	public static void main(String[] args) {
		launch(args);		
	}
	
}
