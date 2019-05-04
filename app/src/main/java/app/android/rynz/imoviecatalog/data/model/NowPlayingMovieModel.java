package app.android.rynz.imoviecatalog.data.model;

import java.util.ArrayList;

import app.android.rynz.imoviecatalog.data.model.results.ResultDateModel;
import app.android.rynz.imoviecatalog.data.model.results.ResultMovieModel;

public class NowPlayingMovieModel
{
    public static final String KEY_PAGE = "page";
    public static final String KEY_TOTAL_RESULT = "total_results";
    public static final String KEY_TOTAL_PAGES = "total_pages";
    public static final String KEY_UPCOMING_RESULT = "results";
    public static final String KEY_UPCOMING_DATE = "dates";

    private int page;
    private int total_results;
    private int total_pages;
    private ResultDateModel dates;
    private ArrayList<ResultMovieModel> movieList;

    public NowPlayingMovieModel(int page, int total_results, int total_pages, ResultDateModel dates, ArrayList<ResultMovieModel> movieList)
    {
        this.page = page;
        this.total_results = total_results;
        this.total_pages = total_pages;
        this.dates=dates;
        this.movieList = movieList;
    }

    public int getPage()
    {
        return page;
    }

    public int getTotal_results()
    {
        return total_results;
    }

    public int getTotal_pages()
    {
        return total_pages;
    }

    public ResultDateModel getDates()
    {
        return dates;
    }

    public ArrayList<ResultMovieModel> getMovieList()
    {
        return movieList;
    }

    public void setMovieList(ArrayList<ResultMovieModel> movieList)
    {
        this.movieList = movieList;
    }
}
