package modeselection.gui.landmarkview;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import modeselection.cluster.ShrinkingImageBSOC;
import modeselection.gui.AdaptedYUYVRenderer;
import modeselection.util.Duple;
import modeselection.util.Util;

public class TopoViewController {
	@FXML
	MenuItem open;
	@FXML
	MenuItem close;
	
	@FXML
	ChoiceBox<Integer> nodeNum;
	
	@FXML
	Button switchTo;
	@FXML
	Button left;
	@FXML
	Button right;
	
	@FXML
	Canvas image;
	
	@FXML
	TextField numSources;
	
	@FXML
	ListView<String> transitions;
	
	FileChooser chooser = new FileChooser();
	ShrinkingImageBSOC map;
	
	@FXML
	void initialize() {
		System.out.println("Initializing");
		nodeNum.selectionModelProperty().addListener((obs, oldVal, newVal) -> switchNode(newVal.getSelectedItem()));
		switchTo.setOnAction(event -> switchNode(nodeNum.getValue()));
		left.setOnAction(event -> switchTo(nodeNum.getValue() - 1));
		right.setOnAction(event -> switchTo(nodeNum.getValue() + 1));
	}
	
	@FXML
	void open() {
		File file = chooser.showOpenDialog(null);
		if (file != null) {
			try {
				map = Util.fileToObject(file, ShrinkingImageBSOC::new);
				nodeNum.getItems().clear();
				for (int node: map.getClusterIds()) {
					nodeNum.getItems().add(node);
				}
				nodeNum.getSelectionModel().select(0);
				switchNode(nodeNum.getValue());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	void switchTo(int target) {
		target = (target + map.size()) % map.size();
		nodeNum.setValue(target);
		switchNode(target);
	}
	
	void switchNode(int updated) {
		AdaptedYUYVRenderer.placeOnCanvas(map.getIdealInputFor(updated), image);
		transitions.getItems().clear();
		ArrayList<Duple<Integer, Integer>> counts = map.transitionCountsFor(updated);
		for (Duple<Integer, Integer> transition: counts) {
			String show = String.format("%d (%d)", transition.getFirst(), transition.getSecond());
			System.out.println(show);
			transitions.getItems().add(show);
		}
		numSources.setText(Integer.toString(map.getNumMergesFor(updated)));
	}
}
