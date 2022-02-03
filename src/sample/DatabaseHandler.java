package sample;

import java.sql.*;
import java.util.*;

class DatabaseHandler extends Config
{
    private int[] between = new int[2];

    public int[] getBetween()
    {
        return between;
    }

    public void setBetween(int[] between)
    {
        this.between = between;
    }

    DatabaseHandler(int from, int to)
    {
        between[0] = from;
        between[1] = to;
    }

    private Connection getDbConnection() throws ClassNotFoundException, SQLException
    {
        String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName +
                "?autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&" +
                "useLegacyDatetimeCode=false&serverTimezone=UTC&maxReconnects=11";

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(connectionString, dbUser, dbPass);
    }

    void toTheNextList() throws SQLException, ClassNotFoundException
    {
        String select = "SELECT * FROM " + Constant.ANIME_TABLE + " WHERE ID BETWEEN " + between[0] + " AND " +
                between[1] + " AND " + Constant.ANIME_RATING + "=999";
        PreparedStatement prepStat = getDbConnection().prepareStatement(select);
        ResultSet toTheNextListAnime = prepStat.executeQuery();

        String selectForMaxID = "SELECT * FROM " + Constant.ANIME_TABLE + " ORDER BY " + Constant.ANIME_ID +
                " DESC LIMIT 1";
        prepStat = getDbConnection().prepareStatement(selectForMaxID);
        ResultSet maxIDResultSet = prepStat.executeQuery();

        String insert = "INSERT INTO " + Constant.ANIME_TABLE + " (" + Constant.ANIME_ID + ", "
                + Constant.ANIME_NAME + ", " + Constant.ANIME_YEAR + ", " + Constant.ANIME_EPISODES + ", "
                + Constant.ANIME_STUDIO + ") VALUES (?, ?, ?, ?, ?)";
        prepStat = getDbConnection().prepareStatement(insert);

        toTheNextListAnime.next();
        maxIDResultSet.next();
        int maxID = maxIDResultSet.getInt(Constant.ANIME_ID) + 1;

        for (int i = 1; i < 6; i++)
        {
            if (i == 1)
                prepStat.setInt(i, maxID);
            else
                prepStat.setString(i, toTheNextListAnime.getString(i));
        }

        prepStat.executeUpdate();

        select = "SELECT * FROM " + Constant.HAVE + " WHERE " + Constant.HAVE_ANIME + "=?";
        prepStat = getDbConnection().prepareStatement(select);

        prepStat.setInt(1, maxID);

        ResultSet resultSetOfIDs = prepStat.executeQuery();

        Vector<Integer> idsOfGenres = new Vector<>();

        while (resultSetOfIDs.next())
            idsOfGenres.add(resultSetOfIDs.getInt(Constant.HAVE_GENRES));

        for (int id : idsOfGenres)
        {
            prepStat = getDbConnection().prepareStatement("INSERT INTO " + Constant.HAVE + "(" +
                    Constant.HAVE_ANIME + ", " + Constant.HAVE_GENRES + ") VALUES (?,?)");

            prepStat.setInt(1, maxID);
            prepStat.setInt(2, id);

            prepStat.executeUpdate();
        }
    }

    void addRating(double rating) throws SQLException, ClassNotFoundException
    {
        String ending;
        boolean toTheNextList = rating == 999;
        if (toTheNextList)
            ending = " IS NULL";
        else
            ending = "=0";
        String select = "UPDATE " + Constant.ANIME_TABLE + " SET " + Constant.ANIME_RATING + "=?"
                + " WHERE " + Constant.ANIME_RATING + ending + " AND ID BETWEEN " + between[0] + " AND " + between[1];

        PreparedStatement prepStat = getDbConnection().prepareStatement(select);

        prepStat.setDouble(1, rating);
        prepStat.executeUpdate();
    }

    void setWatching(int id) throws SQLException, ClassNotFoundException
    {
        String update = "UPDATE " + Constant.ANIME_TABLE + " SET " + Constant.ANIME_RATING + "=0"
                + " WHERE " + Constant.ANIME_ID + "=?";

        PreparedStatement prepStat = getDbConnection().prepareStatement(update);

        prepStat.setInt(1, id);
        prepStat.executeUpdate();
    }

    ResultSet getTable() throws SQLException, ClassNotFoundException
    {
        String select = "SELECT * FROM " + Constant.ANIME_TABLE + " WHERE ID BETWEEN " + between[0] + " AND " +
                between[1];

        PreparedStatement prepStat = getDbConnection().prepareStatement(select);

        return prepStat.executeQuery();
    }

    int getRandomIDOfUnwatched() throws SQLException, ClassNotFoundException
    {
        String select = "SELECT * FROM " + Constant.ANIME_TABLE + " WHERE Rating IS NULL AND ID BETWEEN " +
                between[0] + " AND " + between[1];


        PreparedStatement prepStat = getDbConnection().prepareStatement(select);

        ResultSet resultSet = prepStat.executeQuery();

        ArrayList<Integer> unwatchedAnime = new ArrayList<>();

        while (resultSet.next())
            unwatchedAnime.add(resultSet.getInt(Constant.ANIME_ID));

        Random randomAnime = new Random();

        resultSet.last();

        if (resultSet.getRow() == 1)
            return 999;
        else if (resultSet.getRow() == 0)
            return 666;
        return unwatchedAnime.get(randomAnime.nextInt(resultSet.getRow()));
    }

    String getGenreStringByAnimeID(String id) throws SQLException, ClassNotFoundException
    {
        String select = "SELECT g.* FROM " + Constant.GENRES_TABLE + " g JOIN " + Constant.HAVE + " ag ON g." +
                Constant.GENRES_ID + " = ag." + Constant.HAVE_GENRES + " WHERE ag." + Constant.HAVE_ANIME + " =?";

        PreparedStatement prepStat = getDbConnection().prepareStatement(select);

        prepStat.setString(1, id);

        ResultSet resultSet = prepStat.executeQuery();

        StringBuilder returnString = new StringBuilder();

        while (resultSet.next())
        {
            if (returnString.toString().equals(""))
                returnString.append(resultSet.getString(Constant.GENRES_NAME));
            else
                returnString.append(", ").append(resultSet.getString(Constant.GENRES_NAME));
        }

        return returnString.toString();
    }
}
