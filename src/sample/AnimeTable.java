package sample;

public class AnimeTable
{
    private String idColumn;
    private String nameColumn;
    private String yearColumn;
    private String episodesColumn;
    private String studioColumn;
    private String genresColumn;
    private String ratingColumn;

    public String getIdColumn()
    {
        return idColumn;
    }

    public void setIdColumn(String idColumn)
    {
        this.idColumn = idColumn;
    }

    public String getNameColumn()
    {
        return nameColumn;
    }

    public void setNameColumn(String nameColumn)
    {
        this.nameColumn = nameColumn;
    }

    public String getYearColumn()
    {
        return yearColumn;
    }

    public void setYearColumn(String yearColumn)
    {
        this.yearColumn = yearColumn;
    }

    public String getEpisodesColumn()
    {
        return episodesColumn;
    }

    public void setEpisodesColumn(String episodesColumn)
    {
        this.episodesColumn = episodesColumn;
    }

    public String getStudioColumn()
    {
        return studioColumn;
    }

    public void setStudioColumn(String studioColumn)
    {
        this.studioColumn = studioColumn;
    }

    public String getGenresColumn()
    {
        return genresColumn;
    }

    public void setGenresColumn(String genresColumn)
    {
        this.genresColumn = genresColumn;
    }

    public String getRatingColumn()
    {
        return ratingColumn;
    }

    public void setRatingColumn(String ratingColumn)
    {
        this.ratingColumn = ratingColumn;
    }

    public AnimeTable(String idColumn, String nameColumn, String yearColumn, String episodesColumn, String studioColumn,
                      String genresColumn, String rating)
    {
        this.idColumn = idColumn;
        this.nameColumn = nameColumn;
        this.yearColumn = yearColumn;
        this.episodesColumn = episodesColumn;
        this.studioColumn = studioColumn;
        this.genresColumn = genresColumn;
        this.ratingColumn = rating;
    }
}
