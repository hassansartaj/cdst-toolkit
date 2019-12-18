package cdst.application;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import cdst.camrecorder.CameraRecorder;
import cdst.screenrecorder.ScreenshotRecorder;
import cdst.utils.EditingCell;
import cdst.utils.ModelReader;
import cdst.utils.StateModel;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
/**
 * A class to start image capturer application UI
 * 
 * @author Hassan Sartaj
 * @version 1.0
 */
public class RecorderMain extends Application {
	private int totalTime=0, captureTime=0;
	private ArrayList<String> states = null;
	private HashMap<String, Integer> stateDurationMap=new HashMap<>();
//	private TableColumn tbcDuration=null;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
	            @Override
	            public TableCell call(TableColumn p) {
	                return new EditingCell();
	            }
	        };
			
			final DirectoryChooser dirChooser = new DirectoryChooser();
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("CDRecorder.fxml"));
			ObservableList<Node> children = root.getChildren();
			for(Node c: children) {
				if(c instanceof ComboBox) {
					if(c.idProperty().getValue().equals("totalTimeCB")) {
						ComboBox<String> propCB = (ComboBox<String>) c;
						String[] attrs = {"Seconds", "Minutes", "Hours"};
						ObservableList<String> attrList = FXCollections.observableArrayList();
						attrList.addAll(attrs);
						propCB.setPromptText(attrList.get(0));
						propCB.setItems(attrList);
					}
					else if(c.idProperty().getValue().equals("captureTimeCB")) {
						ComboBox<String> propCB = (ComboBox<String>) c;
						String[] attrs = {"Seconds", "Minutes", "Hours"};
						ObservableList<String> attrList = FXCollections.observableArrayList();
						attrList.addAll(attrs);
						propCB.setPromptText(attrList.get(0));
						propCB.setItems(attrList);
					}
				}
				else if(c instanceof Button) {
					if(c.idProperty().getValue().equals("browseBtn")) {
						Button browseBtn = (Button) c;
						browseBtn.setGraphic(new ImageView("icons/icons8-search-folder-24.png"));
						browseBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								AppController.configureFolderChooser(dirChooser);;
								File file = dirChooser.showDialog(primaryStage);
								for(Node cn: children) {
									if(cn instanceof TextField) {
										if(cn.idProperty().getValue().equals("browseTF")) {
											TextField pathTF = (TextField) cn;
											try {
												pathTF.setText(file.getPath());
											} catch (Exception e) {
												Alert alert = new Alert(AlertType.ERROR);
												alert.setTitle("Folder Error");
												alert.setHeaderText("No Folder is selected to save images");
												alert.setContentText("Expand to see the full detail!");
												StringWriter sw = new StringWriter();
												PrintWriter pw = new PrintWriter(sw);
												e.printStackTrace(pw);
												alert.getDialogPane().setExpandableContent(AppController.createExceptionArea(sw.toString()));
												alert.showAndWait();
											}
											break;
										}
									}
								}
							}
						});
					}
					else if(c.idProperty().getValue().equals("captureBtn")) {
						Button captureBtn = (Button) c;
						captureBtn.setGraphic(new ImageView("icons/icons8-take-screenshot-16.png"));
						captureBtn.setOnAction(new EventHandler<ActionEvent>() {
							@SuppressWarnings("deprecation")
							@Override
							public void handle(ActionEvent arg0) {
								totalTime=0; captureTime=0;
								String imagesDir=null;
								for(Node cn: children) {
									if(cn instanceof TextField) {
										if(cn.idProperty().getValue().equals("browseTF")) {
											TextField pathTF = (TextField) cn;
											imagesDir=pathTF.getText();
											if(imagesDir==null) {
												Alert alert = new Alert(AlertType.ERROR);
												alert.setTitle("Path Error");
												alert.setHeaderText("No destination path specified");
												alert.setContentText("Please choose destination folder to store images!");
												alert.showAndWait();
											}
										}
										else if(cn.idProperty().getValue().equals("totalDurationTF")) {
											TextField totalTF = (TextField) cn;
											String totalDuration = totalTF.getText();
											if(totalDuration!=null) {
												try {
													totalTime = Integer.parseInt(totalDuration);
												} catch (NumberFormatException e) {
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Invalid Duration");
													alert.setHeaderText("Duration must be a number");
													alert.setContentText("Expand to see the full detail!");
													StringWriter sw = new StringWriter();
													PrintWriter pw = new PrintWriter(sw);
													e.printStackTrace(pw);
													alert.getDialogPane().setExpandableContent(AppController.createExceptionArea(sw.toString()));
													alert.showAndWait();
												}
											}else {
												Alert alert = new Alert(AlertType.ERROR);
												alert.setTitle("Duration Error");
												alert.setHeaderText("Total duration is not specified");
												alert.setContentText("Please specify the total capture duration!");
												alert.showAndWait();
											}
										}
										else if(cn.idProperty().getValue().equals("captureDurationTF")) {
											TextField captureTF = (TextField) cn;
											String captureDuration = captureTF.getText();
											if(captureDuration!=null) {
												try {
													captureTime = Integer.parseInt(captureDuration);
												} catch (NumberFormatException e) {
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Invalid Duration");
													alert.setHeaderText("Duration must be a number");
													alert.setContentText("Expand to see the full detail!");
													StringWriter sw = new StringWriter();
													PrintWriter pw = new PrintWriter(sw);
													e.printStackTrace(pw);
													alert.getDialogPane().setExpandableContent(AppController.createExceptionArea(sw.toString()));
													alert.showAndWait();
												}
											}else {
												Alert alert = new Alert(AlertType.ERROR);
												alert.setTitle("Capture Error");
												alert.setHeaderText("Capture duration is not specified");
												alert.setContentText("Please specify the screenshot capture duration!");
												alert.showAndWait();
											}
										}
									}
								}
								if(imagesDir!=null) {
									for(Node cn: children) {
										if(cn instanceof ComboBox) {
											if(cn.idProperty().getValue().equals("totalTimeCB")) {
												ComboBox<?> totalTimeCB = (ComboBox<?>) cn;
												String tUnit = AppController.getTimeUnit(totalTimeCB);
												if(!tUnit.equals("Seconds"))
													totalTime = AppController.convertToSeconds(tUnit, totalTime);
											}
											else if(cn.idProperty().getValue().equals("captureTimeCB")) {
												ComboBox<?> captureTimeCB = (ComboBox<?>) cn;
												String cUnit = AppController.getTimeUnit(captureTimeCB);
												if(!cUnit.equals("Seconds"))
													captureTime = AppController.convertToSeconds(cUnit, captureTime);
											}
										}
										else if(cn instanceof TableView) {
											if(cn.idProperty().getValue().equals("statesTable")) {
												TableView<StateModel> statesTable = (TableView<StateModel>) cn;
												if(!stateDurationMap.isEmpty()) stateDurationMap.clear();
												for(StateModel sm:statesTable.getItems()) {
													stateDurationMap.put(sm.getName(), sm.getDuration());
												}
											}
										}
									}
									primaryStage.hide();
									ScreenshotRecorder imgCap = new ScreenshotRecorder(imagesDir, captureTime, stateDurationMap);
									imgCap.start();
									try {
										imgCap.join(totalTime*1000);
									} catch (InterruptedException e) {
										Alert alert = new Alert(AlertType.ERROR);
										alert.setTitle("Capture Error");
										alert.setHeaderText("Problem in capturing screenshot");
										alert.setContentText("Expand to see the full detail!");
										StringWriter sw = new StringWriter();
										PrintWriter pw = new PrintWriter(sw);
										e.printStackTrace(pw);
										alert.getDialogPane().setExpandableContent(AppController.createExceptionArea(sw.toString()));
										alert.showAndWait();
									}
									imgCap.stop();
									primaryStage.show();
									Alert alert = new Alert(AlertType.INFORMATION);
									alert.setTitle("Done Recording");
									alert.setHeaderText("All images are recorded");
									alert.showAndWait();
								}
							}
						});
					}
					else if(c.idProperty().getValue().equals("camRecBtn")) {
						Button captureBtn = (Button) c;
						captureBtn.setGraphic(new ImageView("icons/icons8-camera-16.png"));
						captureBtn.setOnAction(new EventHandler<ActionEvent>() {
							@SuppressWarnings("deprecation")
							@Override
							public void handle(ActionEvent arg0) {
								totalTime=0; captureTime=0;
								String imagesDir=null;
								for(Node cn: children) {
									if(cn instanceof TextField) {
										if(cn.idProperty().getValue().equals("browseTF")) {
											TextField pathTF = (TextField) cn;
											imagesDir=pathTF.getText();
											if(imagesDir==null) {
												Alert alert = new Alert(AlertType.ERROR);
												alert.setTitle("Path Error");
												alert.setHeaderText("No destination path specified");
												alert.setContentText("Please choose destination folder to store images!");
												alert.showAndWait();
											}
										}
										else if(cn.idProperty().getValue().equals("totalDurationTF")) {
											TextField totalTF = (TextField) cn;
											String totalDuration = totalTF.getText();
											if(totalDuration!=null) {
												try {
													totalTime = Integer.parseInt(totalDuration);
												} catch (NumberFormatException e) {
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Invalid Duration");
													alert.setHeaderText("Duration must be a number");
													alert.setContentText("Expand to see the full detail!");
													StringWriter sw = new StringWriter();
													PrintWriter pw = new PrintWriter(sw);
													e.printStackTrace(pw);
													alert.getDialogPane().setExpandableContent(AppController.createExceptionArea(sw.toString()));
													alert.showAndWait();
												}
											}else {
												Alert alert = new Alert(AlertType.ERROR);
												alert.setTitle("Duration Error");
												alert.setHeaderText("Total duration is not specified");
												alert.setContentText("Please specify the total capture duration!");
												alert.showAndWait();
											}
										}
										else if(cn.idProperty().getValue().equals("captureDurationTF")) {
											TextField captureTF = (TextField) cn;
											String captureDuration = captureTF.getText();
											if(captureDuration!=null) {
												try {
													captureTime = Integer.parseInt(captureDuration);
												} catch (NumberFormatException e) {
													Alert alert = new Alert(AlertType.ERROR);
													alert.setTitle("Invalid Duration");
													alert.setHeaderText("Duration must be a number");
													alert.setContentText("Expand to see the full detail!");
													StringWriter sw = new StringWriter();
													PrintWriter pw = new PrintWriter(sw);
													e.printStackTrace(pw);
													alert.getDialogPane().setExpandableContent(AppController.createExceptionArea(sw.toString()));
													alert.showAndWait();
												}
											}else {
												Alert alert = new Alert(AlertType.ERROR);
												alert.setTitle("Capture Error");
												alert.setHeaderText("Capture duration is not specified");
												alert.setContentText("Please specify the screenshot capture duration!");
												alert.showAndWait();
											}
										}
									}
								}
								if(imagesDir!=null) {
									for(Node cn: children) {
										if(cn instanceof ComboBox) {
											if(cn.idProperty().getValue().equals("totalTimeCB")) {
												ComboBox<?> totalTimeCB = (ComboBox<?>) cn;
												String tUnit = AppController.getTimeUnit(totalTimeCB);
												if(!tUnit.equals("Seconds"))
													totalTime = AppController.convertToSeconds(tUnit, totalTime);
											}
											else if(cn.idProperty().getValue().equals("captureTimeCB")) {
												ComboBox<?> captureTimeCB = (ComboBox<?>) cn;
												String cUnit = AppController.getTimeUnit(captureTimeCB);
												if(!cUnit.equals("Seconds"))
													captureTime = AppController.convertToSeconds(cUnit, captureTime);
											}
										}
										else if(cn instanceof TableView) {
											if(cn.idProperty().getValue().equals("statesTable")) {
												TableView<StateModel> statesTable = (TableView<StateModel>) cn;
												if(!stateDurationMap.isEmpty()) stateDurationMap.clear();
												for(StateModel sm:statesTable.getItems()) {
													stateDurationMap.put(sm.getName(), sm.getDuration());
												}
											}
										}
									}
									primaryStage.hide();
									CameraRecorder camRecorder = new CameraRecorder(imagesDir, captureTime, stateDurationMap);
									camRecorder.start();
									try {
										camRecorder.join(totalTime*1000);
									} catch (InterruptedException e) {
										Alert alert = new Alert(AlertType.ERROR);
										alert.setTitle("Capture Error");
										alert.setHeaderText("Problem in capturing screenshot");
										alert.setContentText("Expand to see the full detail!");
										StringWriter sw = new StringWriter();
										PrintWriter pw = new PrintWriter(sw);
										e.printStackTrace(pw);
										alert.getDialogPane().setExpandableContent(AppController.createExceptionArea(sw.toString()));
										alert.showAndWait();
									}
									camRecorder.stop();
									primaryStage.show();
									Alert alert = new Alert(AlertType.INFORMATION);
									alert.setTitle("Done Recording");
									alert.setHeaderText("All images are recorded");
									alert.showAndWait();
								}
							}
						});
					}
					else if(c.idProperty().getValue().equals("browseModelBtn")) {
						Button browseBtn = (Button) c;
						browseBtn.setGraphic(new ImageView("icons/icons8-search-folder-24.png"));
					}
					else if(c.idProperty().getValue().equals("loadBtn")) {
						Button loadBtn = (Button) c;
						loadBtn.setGraphic(new ImageView("icons/icons8-load-24.png"));
						loadBtn.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								for(Node cn: children) {
									if(cn instanceof TextField) {
										if(cn.idProperty().getValue().equals("mpathTF")) {
											TextField pathTF = (TextField) cn;
											String path = pathTF.getText();
											if(!path.isEmpty()) {
												states = ModelReader.getStates(path);
												for(Node cn1:children) {
													if (cn1 instanceof TableView) {
														if(cn1.idProperty().getValue().equals("statesTable")) {
															TableView<StateModel> statesTable = (TableView<StateModel>) cn1;
															statesTable.setEditable(true);
															for(TableColumn tbc: statesTable.getColumns()) {
																if(tbc.idProperty().getValue().equals("name")) {
																	tbc.setCellValueFactory(new PropertyValueFactory<>("name"));
																}
																else if(tbc.idProperty().getValue().equals("duration")) {
																	tbc.setCellValueFactory(new PropertyValueFactory<>("duration"));
//																	tbc.setCellFactory(TextFieldTableCell.forTableColumn());
//																	tbc.setOnEditCommit(new EventHandler<CellEditEvent<StateModel, Integer>>() {
//																		@Override
//																		public void handle(CellEditEvent<StateModel, Integer> t) {
//																			((StateModel) t.getTableView().getItems().get(t.getTablePosition().getRow()))
//																					.setDuration(t.getNewValue());
//																		}
//																	});
																}
																else if(tbc.idProperty().getValue().equals("unit")) {
																	tbc.setCellValueFactory(new PropertyValueFactory<>("unit"));
																}
															}
															
															if (states != null) {
																ObservableList<StateModel> statesData = FXCollections.observableArrayList();
																for (String state : states) {
																	StateModel sm = new StateModel(state, 0, "Seconds");
																	statesData.add(sm);
																}
																statesTable.setItems(statesData);
															}
														}
													}
												}
											}
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
							}
						});
					}
				}
				else if(c instanceof TextField) {
					if(c.idProperty().getValue().equals("mpathTF")) {
						TextField pathTF = (TextField) c;
						pathTF.setText("model/Plane.uml");
					}
				}
				else if (c instanceof TableView) {
					if(c.idProperty().getValue().equals("statesTable")) {
						TableView<StateModel> statesTable = (TableView<StateModel>) c;
						for(TableColumn tbc: statesTable.getColumns()) {
							if(tbc.idProperty().getValue().equals("duration")) {
								tbc.setCellValueFactory(new PropertyValueFactory<>("duration"));
								tbc.setCellFactory(cellFactory);
								tbc.setOnEditCommit(new EventHandler<CellEditEvent<StateModel, Integer>>() {
									@Override
									public void handle(CellEditEvent<StateModel, Integer> t) {
										((StateModel) t.getTableView().getItems().get(t.getTablePosition().getRow()))
										.setDuration(t.getNewValue());
									}
								});
							}
						}
//						String umlFilePath=null;
//						for(Node cn: children) {
//							if(cn instanceof TextField) {
//								if(cn.idProperty().getValue().equals("mpathTF")) {
//									TextField pathTF = (TextField) cn;
//									umlFilePath = pathTF.getText();
//									break;
//								}
//							}
//						}
						
//						ObservableList<StateModel> statesData = FXCollections.observableArrayList(
//								new StateModel("Standing", 12, "Seconds"),
//								new StateModel("Taxiing", 18, "Seconds"),
//								new StateModel("TakeOff", 30, "Seconds"),
//								new StateModel("Climb", 100, "Seconds"),
//								new StateModel("Cruise", 90, "Seconds"),
//								new StateModel("Descent", 50, "Seconds"),
//								new StateModel("StraightAndLevel", 40, "Seconds"),
//								new StateModel("Approach", 20, "Seconds"),
//								new StateModel("Landing", 10, "Seconds"));
						
//						if (states != null) {
//							ObservableList<StateModel> statesData = FXCollections.observableArrayList();
//							for (String state : states) {
//								StateModel sm = new StateModel(state, 0, "Seconds");
//								statesData.add(sm);
//							}
//							statesTable.setItems(statesData);
//						}
					}
				}
			}
			
			//display scene
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("CDS Recorder");
			primaryStage.setResizable(false);
			primaryStage.setFullScreen(false);
			primaryStage.getIcons().add(new Image("icons/icons8-video-record-60.png"));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

}
