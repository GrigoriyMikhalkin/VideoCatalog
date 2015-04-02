import java.lang.StringBuilder;
import java.lang.CharSequence;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Spinner;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Orientation;
import javafx.geometry.Insets;
import java.sql.ResultSet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;


public class Main extends Application
{
    private DatabaseDriver dbdriver;
    
    public static void main(String[] args)
    {
	launch(args);
    }

    @Override
    public void start(Stage window) throws Exception
    { 	
	// defining UI elements
	// search TextField
	TextField search = new TextField("enter the movie name");
	search.setOnAction(e -> System.out.println(search.getText()));

	// search Button -- same behavior as pressing <Enter>
	Button searchButton = new Button("Search");
	searchButton.setOnAction(e -> System.out.println(search.getText()));

	// category Spinner
	Spinner categorySpinner = new Spinner();

	//movie names ListView
	ListView<String> movieList = new ListView<String>();
	ObservableList<String> movieNames = FXCollections.observableArrayList();

	// description Box
	VBox description = new VBox();

	// loading db
	dbdriver = new DatabaseDriver("jdbc:sqlite:moviedb.db","org.sqlite.JDBC");
	ResultSet movies = dbdriver.getAllMovies();
	while (movies.next()) {
	    movieNames.add(movies.getString(1));
	}
	dbdriver.closeConnection();

	movieList.setItems(movieNames);
	movieList.setPrefHeight(180);

	// setting layout

	GridPane grid = new GridPane();
	grid.setPadding(new Insets(10, 10, 10, 10));
	grid.setVgap(5);
	grid.setHgap(5);

	GridPane.setConstraints(search,0,0);
	GridPane.setConstraints(searchButton,1,0);
	grid.getChildren().addAll(search,searchButton);

	GridPane.setConstraints(categorySpinner,2,0);
	grid.getChildren().add(categorySpinner);

	GridPane.setConstraints(movieList,0,2);
	grid.getChildren().add(movieList);

	GridPane.setConstraints(description,0,3);
	grid.getChildren().add(description);

	Scene scene = new Scene(grid, 640, 480);
	window.setScene(scene);
	window.show();
    }
}
