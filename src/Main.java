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
import java.io.File;
import java.util.List;
import java.util.ArrayList;


public class Main extends Application
{
    private DatabaseDriver dbdriver;
    private Stage window;
    
    public static void main(String[] args)
    {
	launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
	window = stage;
	
	// loading db
	dbdriver = new DatabaseDriver("jdbc:sqlite:../resources/moviedb.db","org.sqlite.JDBC");

	// updating catalog
	updateCatalog("/home/grigoriy/Movies/Movies/");
	
	ResultSet movies = dbdriver.getAllMovies();

	Scene scene = setScene(movies);

	dbdriver.closeConnection();
	
	window.setScene(scene);
	window.show();
    }

    private void updateCatalog(String path) throws Exception
    {
	// load all elements in directory
	List<String> elements = new ArrayList<String>();
	List<String> names = new ArrayList<String>();

	File folder = new File(path);

	for (File file: folder.listFiles())
	    {
		if (file.isDirectory()) continue;
		else
		    {
			elements.add(file.getName());
		    }
	    }

	ResultSet rs = dbdriver.getAllMovies();
	while(rs.next())
	    {
		names.add(rs.getString(1));
	    }
	
	// load elements into db
	for(String element : elements)
	    {
		if (!names.contains(element)){
		    dbdriver.setNewMovie(element, "test", path+element);
		}
	    }
    }

    private void updateBySearchRequest(String searchRequest) throws Exception
    {
	dbdriver = new DatabaseDriver("jdbc:sqlite:../resources/moviedb.db","org.sqlite.JDBC");

	ResultSet movies = dbdriver.getMovies(searchRequest);

	Scene scene = setScene(movies);

	dbdriver.closeConnection();

	window.setScene(scene);
    }

    private void updateByCategory(String categoryName) throws Exception
    {
	dbdriver = new DatabaseDriver("jdbc:sqlite:../resources/moviedb.db","org.sqlite.JDBC");

	ResultSet movies = dbdriver.getCategory(categoryName);

	Scene scene = setScene(movies);

	dbdriver.closeConnection();

	window.setScene(scene);
    }

    private Scene setScene(ResultSet movies) throws Exception
    {
	// defining UI elements
	// search TextField
	TextField search = new TextField("enter the movie name");
	search.setOnAction(e -> {
		try {
		    updateBySearchRequest(search.getText());
		}
		catch(Exception exc){
		    System.out.println("Error in setScene method in search.setOnAction");
		}
	    });

	// search Button -- same behavior as pressing <Enter>
	Button searchButton = new Button("Search");
	searchButton.setOnAction(e -> {
		try {
		    updateBySearchRequest(search.getText());
		}
		catch(Exception exc){
		    System.out.println("Error in setScene method in search.setOnAction");
		}
	    });

	// category Spinner
	Spinner categorySpinner = new Spinner();

	//movie names ListView
	ListView<String> movieList = new ListView<String>();
	ObservableList<String> movieNames = FXCollections.observableArrayList();

	// description Box
	VBox description = new VBox();

	// filling movieNames
	while (movies.next()) {
	    movieNames.add(movies.getString(1));
	}
	
	movieList.setItems(movieNames);
	movieList.setPrefHeight(400);
	movieList.setPrefWidth(350);
	

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
	return(scene);
    }

}
