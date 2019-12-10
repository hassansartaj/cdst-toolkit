package cdst.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.helper.OCLHelper;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import cdst.modelutils.ClassInfo;
import cdst.validation.InstanceGeneratorUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

public class OCLGenController {
	private static final String[] KEYWORDS = new String[] {
			"context", "inv", "pre", "post", "init",
			"def", "derive", "and", "or", "implies",
			"xor", "else", "for",  "if", "self"
	};

	//TODO:change pattern
	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
	private static final String PAREN_PATTERN = "\\(|\\)";
	private static final String BRACE_PATTERN = "\\{|\\}";
	private static final String BRACKET_PATTERN = "\\[|\\]";
	private static final String SEMICOLON_PATTERN = "\\;";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

	private static final Pattern PATTERN = Pattern.compile(
			"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
					+ "|(?<PAREN>" + PAREN_PATTERN + ")"
					+ "|(?<BRACE>" + BRACE_PATTERN + ")"
					+ "|(?<BRACKET>" + BRACKET_PATTERN + ")"
					+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
					+ "|(?<STRING>" + STRING_PATTERN + ")"
					+ "|(?<COMMENT>" + COMMENT_PATTERN + ")"
			);

	public static void configureFileChooser(FileChooser fileChooser) {
		fileChooser.setTitle("Select UML/XMI Model File");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("UML", "*.uml"),
				new FileChooser.ExtensionFilter("XMI", "*.xmi"));
	}
	public static void configureFolderChooser(DirectoryChooser dirChooser) {
		dirChooser.setTitle("Select directory to export OCL constraints");
		dirChooser.setInitialDirectory(new File(System.getProperty("user.home")));
	}
	public static StyleSpans<Collection<String>> computeHighlighting(String text) {
		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder
		= new StyleSpansBuilder<>();
		while(matcher.find()) {
			String styleClass =
					matcher.group("KEYWORD") != null ? "keyword" :
						matcher.group("PAREN") != null ? "paren" :
							matcher.group("BRACE") != null ? "brace" :
								matcher.group("BRACKET") != null ? "bracket" :
									matcher.group("SEMICOLON") != null ? "semicolon" :
										matcher.group("STRING") != null ? "string" :
											matcher.group("COMMENT") != null ? "comment" :
												null; /* never happens */ assert styleClass != null;
												spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
												spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
												lastKwEnd = matcher.end();
		}
		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		return spansBuilder.create();
	}
	
	public static GridPane createExceptionArea(String expMsg) {
		Label label = new Label("The exception stacktrace is:");

		TextArea textArea = new TextArea(expMsg);
		textArea.setEditable(false);
		textArea.setWrapText(false);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);
		return expContent;
	}
	
	public static void handleDataType(ArrayList<ClassInfo> classesInfo, String propCB, ObservableList<Node> children) {
		String dtype = classesInfo.get(0).getAttrDtMap().get(propCB);
		if(dtype.equals("Integer"))
		{
			for(Node cn: children) {
				if(cn instanceof TextField) {
					if(cn.idProperty().getValue().equals("valueTF")) {
						TextField pathTF = (TextField) cn;
						pathTF.setDisable(false);
					}
				}
				else if(cn instanceof RadioButton) {
					if(cn.idProperty().getValue().equals("trueRB")) {
						RadioButton trueRB = (RadioButton) cn;
						trueRB.setDisable(true);
					}
					else if(cn.idProperty().getValue().equals("falseRB")) {
						RadioButton falseRB = (RadioButton) cn;
						falseRB.setDisable(true);
					}
				}
				else if(cn instanceof ComboBox) {
					if (cn.idProperty().getValue().equals("enumCB")) {
						@SuppressWarnings("unchecked")
						ComboBox<String> enumCB = (ComboBox<String>) cn;
						enumCB.setDisable(true);
					}
				}
			}
		}
		else if(dtype.equals("Boolean"))
		{
			for(Node cn: children) {
				if(cn instanceof RadioButton) {
					if(cn.idProperty().getValue().equals("trueRB")) {
						RadioButton trueRB = (RadioButton) cn;
						trueRB.setDisable(false);
						trueRB.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								for(Node cn1: children) {
									if(cn1 instanceof RadioButton) {
										if(cn1.idProperty().getValue().equals("falseRB")) {
											RadioButton falseRB = (RadioButton) cn1;
											falseRB.setSelected(false);
											break;
										}
									}
								}
							}
						});
					}
					else if(cn.idProperty().getValue().equals("falseRB")) {
						RadioButton falseRB = (RadioButton) cn;
						falseRB.setDisable(false);
						falseRB.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent arg0) {
								for(Node cn1: children) {
									if(cn1 instanceof RadioButton) {
										if(cn1.idProperty().getValue().equals("trueRB")) {
											RadioButton trueRB = (RadioButton) cn1;
											trueRB.setSelected(false);
											break;
										}
									}
								}
							}
						});
					}
				}
				else if(cn instanceof TextField) {
					if(cn.idProperty().getValue().equals("valueTF")) {
						TextField pathTF = (TextField) cn;
						pathTF.setDisable(true);
					}
				}
				else if(cn instanceof ComboBox) {
					if (cn.idProperty().getValue().equals("enumCB")) {
						@SuppressWarnings("unchecked")
						ComboBox<String> enumCB = (ComboBox<String>) cn;
						enumCB.setDisable(true);
					}
				}
			}
		}
		else //if(dtype.equals("Enum"))
		{
			for(Node cn: children) {
				if(cn instanceof ComboBox){
					if (cn.idProperty().getValue().equals("enumCB")) {
						@SuppressWarnings("unchecked")
						ComboBox<String> enumCB = (ComboBox<String>) cn;
						enumCB.setDisable(false);
						ObservableList<String> elsList = FXCollections.observableArrayList();
						for(String l: classesInfo.get(0).getEnumLiteralsMap().get(dtype)) {
							elsList.add(dtype+"::"+l);
						}
						enumCB.setPromptText(elsList.get(0));
						enumCB.setItems(elsList);
						break;
					} 
				}
				else if(cn instanceof TextField) {
					if(cn.idProperty().getValue().equals("valueTF")) {
						TextField pathTF = (TextField) cn;
						pathTF.setDisable(true);
					}
				}
				else if(cn instanceof RadioButton) {
					if(cn.idProperty().getValue().equals("trueRB")) {
						RadioButton trueRB = (RadioButton) cn;
						trueRB.setDisable(true);
					}
					else if(cn.idProperty().getValue().equals("falseRB")) {
						RadioButton falseRB = (RadioButton) cn;
						falseRB.setDisable(true);
					}
				}
			}
		}
	}
	
	public static void createOCLFile(String constraints, String filePath) {
		BufferedWriter bw=null;
		try {
			bw = new BufferedWriter(new FileWriter(filePath));
			bw.write(constraints);
			bw.flush();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Failed to create OCL file");
			alert.setHeaderText(e.getMessage());
			alert.setContentText(e.getStackTrace().toString());
			alert.showAndWait();
		}finally {
			try {
				bw.close();
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("OCL File Generated");
				alert.setContentText("All OCL constraints are written in constraints.ocl file!");
				alert.showAndWait();
			} catch (IOException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Failed to close OCL file");
				alert.setHeaderText(e.getMessage());
				alert.setContentText(e.getStackTrace().toString());
				alert.showAndWait();
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static String validateOCLConstraint(String constraint) {
		InstanceGeneratorUtil igUtil = new InstanceGeneratorUtil();
		igUtil.setUp();
//		context Airplane inv: self.oclIsInState(TurningLeft) or self.heading > 1 
//		context Airplane inv: self.oclIsInState(TurningLeft) 
		OCLHelper oclHelper = igUtil.getHelper();
		try {
//			if(constraint.contains("inv")) {
//				int beginIndex=constraint.indexOf(":");
//				String newQuery=constraint.substring(beginIndex+1, constraint.length());
//				oclHelper.createConstraint(ConstraintKind.INVARIANT, newQuery.trim());
////				oclHelper.getOCL().validate(oclHelper.createConstraint(ConstraintKind.INVARIANT, newQuery.trim()));
//			}else {
//				oclHelper.createQuery(constraint);
//			}
			oclHelper.createQuery(constraint);
		} catch (ParserException e) {
//			e.printStackTrace();
			return e.getMessage()==null?e.getCause().toString():e.getMessage();
		}
		return "ok";
	}
}
