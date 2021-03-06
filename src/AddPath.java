import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.DirectoryChooser;
import java.io.File;

public class AddPath
{
    private static Stage window;
    private static DatabaseDriver dbdriver;
    
    public static void display()
    {
	window = new Stage();

	window.initModality(Modality.APPLICATION_MODAL);
	
	Scene scene = setScene();

	window.setScene(scene);
	window.showAndWait();
    }

    private static Scene setScene()
    {	
	GridPane grid = new GridPane();
	grid.setPadding(new Insets(10,10,10,10));
	grid.setVgap(5);
	grid.setHgap(5);

	TextField path = new TextField("path");
	TextField category = new TextField("category");

	Button chooseDir = new Button("Choose");
	chooseDir.setOnAction(e -> {
		DirectoryChooser dc = new DirectoryChooser();
		File selectedDir = dc.showDialog(window);

		if (!(selectedDir==null)) path.setText(selectedDir.getAbsolutePath());
	    });
	
	Button submit = new Button("Submit");
	submit.setOnAction(e -> {
		updateDB(path.getText(),category.getText());
		window.close();
	    });

	GridPane.setConstraints(path,0,1);
	GridPane.setConstraints(chooseDir,1,1);
	GridPane.setConstraints(category,0,2);
	GridPane.setConstraints(submit,1,3);

	grid.getChildren().add(path);
	grid.getChildren().add(category);
	grid.getChildren().add(chooseDir);
	grid.getChildren().add(submit);

	Scene scene = new Scene(grid,320,240);

	return(scene);
    }

    private static void updateDB(String path, String category)
    {
	try{
	    dbdriver = new DatabaseDriver("jdbc:sqlite:../resources/moviedb.db","org.sqlite.JDBC");
	    dbdriver.setNewPath(path,category);
	    
	    dbdriver.closeConnection();
	}
	catch (Exception exc) {System.out.println("Problem accesing database");}
    }
}
