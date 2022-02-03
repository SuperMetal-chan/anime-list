package sample;

public class AnimeInfo
{
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public int getEpisodes()
    {
        return episodes;
    }

    public void setEpisodes(int episodes)
    {
        this.episodes = episodes;
    }

    public int getStudio()
    {
        return studio;
    }

    public void setStudio(int studio)
    {
        this.studio = studio;
    }

    public int getRating()
    {
        return rating;
    }

    public void setRating(int rating)
    {
        this.rating = rating;
    }

    private int id;
    private String name;
    private int year;
    private int episodes;
    private int studio;
    private int rating;

    public AnimeInfo(int id, String name, int year, int episodes, int studio)
    {
        this.id = id;
        this.name = name;
        this.year = year;
        this.episodes = episodes;
        this.studio = studio;
    }
}
