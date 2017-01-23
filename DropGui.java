
import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class DropGui extends Application{
	
	Stage window;
	Scene startScene;
	Scene udScene;
	
	
		
	public static void main(String[] args){
		launch(args);
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("The Large Drop Collider's Digital Pal");
		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 800,465);

		
////////////////// Start Construction of GridPane ////////////////////////////		
		
		GridPane gridpane = new GridPane();
//		gridpane.setAlignment(Pos.CENTER_LEFT);
		gridpane.setPadding(new Insets(5));
		gridpane.setHgap(5);
		gridpane.setVgap(5);
		ColumnConstraints column1 = new ColumnConstraints(126);
		ColumnConstraints column2 = new ColumnConstraints(50,150,655);
		column1.setHgrow(Priority.ALWAYS);
		column2.setHgrow(Priority.ALWAYS);
		gridpane.getColumnConstraints().addAll(column1, column2);
		
		
		Label runNameLabel = new Label("Run Name: ");
		Label pathToVideoLabel = new Label("Path to Video: ");
		Label gap1Label = new Label(" ");
		Label timeInstructionsLabel = new Label("TIME FORMAT: 00.000");
		Label startTimeLabel = new Label("Start Time: ");
		Label endTimeLabel = new Label("End Time: ");
		Label outputPathLabel = new Label("Output Path: ");
		Label pathToScaleImageLabel = new Label("Path to Scale Image: ");
		Label leftPlatformYPointLabel = new Label ("Left Platform Y Value: ");
		Label rightPlatformYPointLabel = new Label ("Right Platform Y Value: ");
		Label intensityCutoffLabel = new Label ("Line Threshold (1-155): ");
		Label leftPlatformXPointLabel = new Label ("Left Platform X Value: ");
		Label rightPlatformXPointLabel = new Label ("Right Platform X Value: ");
		
		TextField runNameTF = new TextField();
		TextField pathToVideoTF = new TextField();
		Label gap3Label = new Label(" ");
		Label gap4Label = new Label(" ");
		TextField startTimeTF = new TextField();
		TextField endTimeTF = new TextField();
		TextField outputPathTF = new TextField();
		TextField pathToScaleImageTF = new TextField();
		TextField leftPlatformYPointTF = new TextField();
		TextField rightPlatformYPointTF = new TextField();
		TextField intensityCutoffTF = new TextField();
		TextField leftPlatformXPointTF = new TextField();
		TextField rightPlatformXPointTF = new TextField();
		
		
		
		gridpane.add(runNameLabel, 0, 0);
		gridpane.add(runNameTF, 1, 0);
		
		gridpane.add(pathToVideoLabel, 0, 1);
		gridpane.add(pathToVideoTF, 1, 1);
		
		gridpane.add(outputPathLabel, 0, 2);
		gridpane.add(outputPathTF, 1, 2);
		
		gridpane.add(pathToScaleImageLabel, 0, 3);
		gridpane.add(pathToScaleImageTF, 1, 3);
		
		gridpane.add(gap1Label,0,4);
		gridpane.add(gap3Label, 1, 4);
		
		gridpane.add(timeInstructionsLabel, 0, 5);
		gridpane.add(gap4Label, 1, 5);
		
		gridpane.add(startTimeLabel, 0, 6);
		gridpane.add(startTimeTF, 1, 6);
		
		gridpane.add(endTimeLabel, 0, 7);
		gridpane.add(endTimeTF, 1, 7);
		
		gridpane.add(leftPlatformXPointLabel, 0, 8);
		gridpane.add(leftPlatformXPointTF, 1, 8);
		
		gridpane.add(leftPlatformYPointLabel, 0, 9);
		gridpane.add(leftPlatformYPointTF, 1, 9);

		gridpane.add(rightPlatformXPointLabel, 0, 10);
		gridpane.add(rightPlatformXPointTF, 1, 10);
		
		gridpane.add(rightPlatformYPointLabel, 0, 11);
		gridpane.add(rightPlatformYPointTF, 1, 11);
		
		gridpane.add(intensityCutoffLabel, 0, 12);
		gridpane.add(intensityCutoffTF, 1, 12);
		
/////////////////////// End Construction of GridPane ////////////////////
		
////////////////////// Begin Construction of RadioGroup ////////////////
		
		ToggleGroup group = new ToggleGroup();
		
		RadioButton sideTopRB = new RadioButton("Side View (On Top)");
		RadioButton topRB = new RadioButton("Top View");
		RadioButton sideBotRB = new RadioButton("Side View (On Bottom)");
		
		sideTopRB.setToggleGroup(group);
		topRB.setToggleGroup(group);
		sideBotRB.setToggleGroup(group);
		
		VBox viewTypeRB = new VBox(5);
		viewTypeRB.getChildren().addAll(sideTopRB, sideBotRB, topRB);
		
////////////////////// End Construction of RadioGroup //////////////////	
		
		Button startButton = new Button("Start");
		startButton.setOnAction(e -> {
			try {
				try {
					startButtonClicked(runNameTF.getText(), pathToVideoTF.getText(), outputPathTF.getText(), pathToScaleImageTF.getText(),
							startTimeTF.getText(), endTimeTF.getText(), sideTopRB.isSelected(), sideBotRB.isSelected(), rightPlatformYPointTF.getText(), leftPlatformYPointTF.getText(),
							intensityCutoffTF.getText(), rightPlatformXPointTF.getText(), leftPlatformXPointTF.getText());
				} catch (org.bytedeco.javacv.FrameGrabber.Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException e1) {
			}
		});
		
		
		root.setTop(viewTypeRB);
		root.setCenter(gridpane);
		root.setBottom(startButton);
		primaryStage.setScene(scene);
		primaryStage.show();

	}
	
	private void startButtonClicked(String runName, String vidPath, String outPath, String scalePath, String startTime, String endTime, boolean sideView,
			boolean sideViewBot, String rightPlatformY, String leftPlatformY, String intensityCutoff, String rightPlatformX, String leftPlatformX) throws org.bytedeco.javacv.FrameGrabber.Exception, IOException{
		//if none of the inputs are null run dropsprogram
		DropsProgram.runProgram(runName, vidPath, outPath, scalePath, startTime, endTime, sideView, sideViewBot, rightPlatformY, leftPlatformY, intensityCutoff, rightPlatformX, leftPlatformX);
	}

}
