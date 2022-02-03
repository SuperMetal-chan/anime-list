package sample;

public class HaveInfo
{
    private int ID_Anime;
    private int ID_Genre;

    public HaveInfo(int ID_Anime, int ID_Genre)
    {
        this.ID_Anime = ID_Anime;
        this.ID_Genre = ID_Genre;
    }

    public int getID_Anime()
    {
        return ID_Anime;
    }

    public void setID_Anime(int ID_Anime)
    {
        this.ID_Anime = ID_Anime;
    }

    public int getID_Genre()
    {
        return ID_Genre;
    }

    public void setID_Genre(int ID_Genre)
    {
        this.ID_Genre = ID_Genre;
    }
}
