import java.lang.StringBuilder;
import java.lang.CharSequence;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.control.*;
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
import java.lang.Runtime;


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
	Scene scene = getAllMovies();
	window.setScene(scene);
	window.show();
    }

    private void updateCatalog(String path, String category) throws Exception
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
		    dbdriver.setNewMovie(element, category, path+"/"+element);
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

    private Scene getAllMovies() throws Exception
    {
	dbdriver = new DatabaseDriver("jdbc:sqlite:../resources/moviedb.db","org.sqlite.JDBC");

	List<String> paths = new ArrayList<String>();
	ResultSet pathsRS = dbdriver.getPaths();
	
	while (pathsRS.next()) {
	    paths.add(pathsRS.getString(1));
	}

	for (String path : paths){
	    ResultSet pathCategory = dbdriver.getPathCategory(path);
	    pathCategory.next();
	    String pathCat = pathCategory.getString(1);
	    updateCatalog(path, pathCat);
	}
	    
	ResultSet movies = dbdriver.getAllMovies();
	Scene scene = setScene(movies);
	
	dbdriver.closeConnection();

	return(scene);
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

	movieList.getSelectionModel().selectedItemProperty().addListener( new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable, String oldValue, String movie) {
			try {
			    dbdriver = new DatabaseDriver("jdbc:sqlite:../resources/moviedb.db","org.sqlite.JDBC");
			    String path = dbdriver.getMoviePath(movie);
			    String category = dbdriver.getMovieCategory(movie);

			    description.getChildren().remove(0,description.getChildren().size());
			    Text p = new Text("Path:");
			    Text t = new Text(path);
			    Text c = new Text("Category: "+category);
			    Button watchBtn = new Button("Watch");
			    watchBtn.setOnAction(e -> {
				    try{
					Runtime.getRuntime().exec("vlc "+path);
				    }
				    catch (Exception exc) {System.out.println("Problem launching file with vlc");}
				});
			    
			    description.getChildren().addAll(p,t,new Text("\n"),c,new Text("\n"),watchBtn);
			    
			    dbdriver.closeConnection();
			}
			catch (Exception exc) {System.out.println(exc);}
		    }});
	

	// setting layout

	GridPane grid = new GridPane();
	grid.setPadding(new Insets(10, 10, 10, 10));
	grid.setVgap(5);
	grid.setHgap(5);

	GridPane.setConstraints(search,0,1);
	GridPane.setConstraints(searchButton,1,1);
	grid.getChildren().addAll(search,searchButton);

	GridPane.setConstraints(categorySpinner,2,1);
	grid.getChildren().add(categorySpinner);

	GridPane.setConstraints(movieList,0,3);
	grid.getChildren().add(movieList);

	GridPane.setConstraints(description,1,3);
	grid.getChildren().add(description);

	Scene scene = new Scene(grid, 1020, 480);

	// setting up MenuBar
	MenuBar mb = createMenu();
	((GridPane)scene.getRoot()).getChildren().addAll(mb);
	
	return(scene);
    }

    private MenuBar createMenu()
    {
	MenuBar mb = new MenuBar();
	
	Menu menuEdit = new Menu("_Edit");
	MenuItem addPath = new MenuItem("Add path");
	addPath.setOnAction(e -> {
		try {
		    AddPath.display();
		    Scene scene = getAllMovies();
		    window.setScene(scene);
		}
		catch (Exception exc) {System.out.println("Problem updating stage after adding new path to DB");}
	    });
	menuEdit.getItems().add(addPath);

	mb.getMenus().add(menuEdit);
	
	return(mb);
    }

}
