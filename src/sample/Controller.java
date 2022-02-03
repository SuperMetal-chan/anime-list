package sample;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller
{

    private ObservableList<AnimeTable> animeData = FXCollections.observableArrayList();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TableColumn<AnimeTable, String> columnYear;

    @FXML
    private Button buttonFirstList;

    @FXML
    private Button buttonSecondList;

    @FXML
    private TableColumn<AnimeTable, String> columnGenres;

    @FXML
    private TableColumn<AnimeTable, String> columnStudio;

    @FXML
    private TableColumn<AnimeTable, String> columnRating;

    @FXML
    private Slider sliderRating;

    @FXML
    private TableColumn<AnimeTable, String> columnID;

    @FXML
    private Button buttonAccept;

    @FXML
    private TableColumn<AnimeTable, String> columnNumberOfEpisodes;

    @FXML
    private TableView<AnimeTable> animeTable;

    @FXML
    private TableColumn<AnimeTable, String> columnName;

    private DatabaseHandler dbHandler;

    private boolean firstListAvailable = true;
    private boolean secondListAvailable = true;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private Date toStartWatching = new Date();

    @FXML
    void initialize() throws SQLException, ClassNotFoundException, IOException, ParseException
    {
        String date = new BufferedReader(new FileReader("src//sample//dateToWatch.txt")).readLine();

        toStartWatching = simpleDateFormat.parse(date);

        dbHandler = new DatabaseHandler(1, 19);

        checkListsAvailability();

        if (!firstListAvailable)
        {
            buttonAccept.setDisable(true);
            sliderRating.setDisable(true);
        }

        buttonFirstList.setVisible(false);

        buttonFirstList.setOnAction(event ->
        {
            try
            {
                checkListsAvailability();
            }
            catch (SQLException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }

            int[] between = dbHandler.getBetween();
            between[0] -= 19;
            between[1] -= 20;
            dbHandler.setBetween(between);
            animeTable.getItems().clear();
            buttonFirstList.setVisible(false);
            buttonSecondList.setVisible(true);

            fillTableOnNewListPage(1);
        });

        buttonSecondList.setOnAction(event ->
        {
            try
            {
                checkListsAvailability();
            }
            catch (SQLException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }

            int[] between = dbHandler.getBetween();
            between[0] += 19;
            between[1] += 20;
            dbHandler.setBetween(between);
            dbHandler = new DatabaseHandler(20, 39);
            animeTable.getItems().clear();
            buttonFirstList.setVisible(true);
            buttonSecondList.setVisible(false);

            fillTableOnNewListPage(2);
        });

        fillTable(dbHandler);

        buttonAccept.setOnAction(event ->
        {
            try
            {
                int score = (int) sliderRating.getValue();
                dbHandler.addRating(score);

                //Getting current date
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, score / 2 + 1);
                toStartWatching = calendar.getTime();

                FileWriter fileWriter = new FileWriter("src//sample//dateToWatch.txt");
                PrintWriter printWriter = new PrintWriter(fileWriter);
                printWriter.println(simpleDateFormat.format(toStartWatching));
                printWriter.close();
                fileWriter.close();

                int newWatchingID = dbHandler.getRandomIDOfUnwatched();

                dbHandler.setWatching(newWatchingID);

                newWatchingID = dbHandler.getRandomIDOfUnwatched();

                if (newWatchingID == 999)
                {
                    dbHandler.addRating(999);
                    dbHandler.toTheNextList();
                }
                else if (newWatchingID == 666)
                {
                    int[] between = dbHandler.getBetween();
                    int[] newBetween = new int[2];
                    newBetween[0] = between[0] + 19;
                    newBetween[1] = between[1] + 20;
                    dbHandler = new DatabaseHandler(newBetween[0], newBetween[1]);
                    newWatchingID = dbHandler.getRandomIDOfUnwatched();
                    dbHandler.setWatching(newWatchingID);
                    dbHandler.setBetween(between);
                    if (between[0] == 1)
                        firstListAvailable = false;
                    else if (between[0] == 1 + 19)
                        secondListAvailable = false;
                }

                buttonAccept.setDisable(true);
                sliderRating.setDisable(true);

                animeTable.getItems().clear();

                fillTable(dbHandler);

                checkListsAvailability();
            }
            catch (SQLException | ClassNotFoundException | IOException e)
            {
                e.printStackTrace();
            }
        });
    }

    private void checkListsAvailability() throws SQLException, ClassNotFoundException
    {
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        int[] betweenOriginal = dbHandler.getBetween();
        int[] betweenFirstList = {1, 19};
        dbHandler.setBetween(betweenFirstList);
        firstListAvailable = dbHandler.getRandomIDOfUnwatched() != 666 && !currentTime.before(toStartWatching);
        secondListAvailable = dbHandler.getRandomIDOfUnwatched() == 666 && !currentTime.before(toStartWatching);
        int[] betweenToSecondList = new int[2];
        betweenToSecondList[0] += 19;
        betweenToSecondList[1] += 20;
        dbHandler.setBetween(betweenToSecondList);
        secondListAvailable = secondListAvailable && dbHandler.getRandomIDOfUnwatched() != 666;
        dbHandler.setBetween(betweenOriginal);
    }

    private void fillTableOnNewListPage(int listNumber)
    {
        try
        {
            fillTable(dbHandler);
        }
        catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        if (listNumber == 1)
        {
            if (!firstListAvailable)
            {
                buttonAccept.setDisable(true);
                sliderRating.setDisable(true);
            }
            else
            {
                buttonAccept.setDisable(false);
                sliderRating.setDisable(false);
            }
        }
        else if (listNumber == 2)
        {
            if (!secondListAvailable)
            {
                buttonAccept.setDisable(true);
                sliderRating.setDisable(true);
            }
            else
            {
                buttonAccept.setDisable(false);
                sliderRating.setDisable(false);
            }
        }

    }

    private void addRow(AnimeTable newRow)
    {
        columnID.setCellValueFactory(new PropertyValueFactory<>("idColumn"));
        columnGenres.setCellValueFactory(new PropertyValueFactory<>("genresColumn"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("nameColumn"));
        columnNumberOfEpisodes.setCellValueFactory(new PropertyValueFactory<>("episodesColumn"));
        columnRating.setCellValueFactory(new PropertyValueFactory<>("ratingColumn"));
        columnStudio.setCellValueFactory(new PropertyValueFactory<>("studioColumn"));
        columnYear.setCellValueFactory(new PropertyValueFactory<>("yearColumn"));

        animeData.add(newRow);

        animeTable.setItems(animeData);
    }

    private void fillTable(DatabaseHandler dbHandler) throws SQLException, ClassNotFoundException
    {
        ResultSet resultSet = dbHandler.getTable();

        int number_of_rows = 1;

        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        //currentTime = simpleDateFormat.parse("09-07-2019");

        while (resultSet.next())
        {
            String id = resultSet.getString(Constant.ANIME_ID);
            String name = resultSet.getString(Constant.ANIME_NAME);
            String year = resultSet.getString(Constant.ANIME_YEAR);
            String number_of_episodes = resultSet.getString(Constant.ANIME_EPISODES);
            String studio = resultSet.getString(Constant.ANIME_STUDIO);
            String genres = dbHandler.getGenreStringByAnimeID(id);
            String rating = resultSet.getString(Constant.ANIME_RATING);

            try
            {
                if (rating.equals("0"))
                {
                    if (!currentTime.before(toStartWatching))
                        rating = "Смотрю";
                    else
                    {
                        //rating = simpleDateFormat.format(toStartWatching);
                        long daysBetween = TimeUnit.DAYS.convert(toStartWatching.getTime() -
                                currentTime.getTime(), TimeUnit.MILLISECONDS);

                        String ending = " дня";
                        if (daysBetween == 1)
                            ending = " день";
                        else if (daysBetween > 4)
                            ending = " дней";

                        rating = "Осталось " + daysBetween + ending;

                        if (daysBetween == 0)
                            rating = "Уже завтра";
                    }
                }
                else if (rating.equals("999"))
                    rating = "В след. список";
            }
            catch (NullPointerException e)
            {
                rating = "";
            }

            AnimeTable newRow = new AnimeTable(id, name, year, number_of_episodes, studio, genres, rating);

            addRow(newRow);

            number_of_rows++;
        }

        animeTable.setPrefHeight(number_of_rows * 25 + 1);

        columnName.setStyle("-fx-font-weight: bold; -fx-table-cell-border-color: black");
        columnGenres.setStyle("-fx-table-cell-border-color: black");
        columnRating.setStyle("-fx-font-weight: bold");

        animeTable.setRowFactory(table ->
                new TableRow<AnimeTable>()
                {
                    @Override
                    public void updateItem(AnimeTable animeTable, boolean empty)
                    {
                        super.updateItem(animeTable, empty);

                        String style = "-fx-background-color: #f3f3f3";

                        try
                        {
                            String rating = animeTable.getRatingColumn();

                            switch (rating)
                            {
                                case "10":
                                    style = "-fx-background-color: #6aa84f";
                                    break;
                                case "9":
                                    style = "-fx-background-color: #93c47d";
                                    break;
                                case "8":
                                    style = "-fx-background-color: #b6d7a8";
                                    break;
                                case "7":
                                    style = "-fx-background-color: #d9ead3";
                                    break;
                                case "6":
                                    style = "-fx-background-color: #fff2cc";
                                    break;
                                case "5":
                                    style = "-fx-background-color: #fce5cd";
                                    break;
                                case "4":
                                    style = "-fx-background-color: #f4cccc";
                                    break;
                                case "3":
                                    style = "-fx-background-color: #ea9999";
                                    break;
                                case "2":
                                    style = "-fx-background-color: #e06666";
                                    break;
                                case "1":
                                    style = "-fx-background-color: #cc0000";
                                    break;
                                case "Смотрю":
                                    style = "-fx-background-color: #3c78d8";
                                    break;
                                case "В след. список":
                                    style = "-fx-background-color: #674ea7";
                                    break;
                            }

//                            try
//                            {
//                                simpleDateFormat.parse(rating);
//                                style = "-fx-background-color: #45818e";
//                            }
//                            catch (ParseException ignore)
//                            {
//
//                            }

                            if (rating.contains("Осталось") || rating.equals("Уже завтра"))
                                style = "-fx-background-color: #45818e";
                        }
                        catch (NullPointerException e)
                        {
                            this.setStyle("-fx-background-color: black");
                        }

                        this.setStyle(style + "; -fx-font-size: 13");
                    }
                });
    }
}
