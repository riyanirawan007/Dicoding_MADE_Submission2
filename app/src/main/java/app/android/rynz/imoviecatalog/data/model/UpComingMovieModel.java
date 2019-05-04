package app.android.rynz.imoviecatalog.data.model;

import java.util.ArrayList;

import app.android.rynz.imoviecatalog.data.model.results.ResultDateModel;
import app.android.rynz.imoviecatalog.data.model.results.ResultMovieModel;

public class UpComingMovieModel extends NowPlayingMovieModel
{
    //UpComingMovieModel has exactly same structure as NowPlayingMovieModel
    public UpComingMovieModel(int page, int total_results, int total_pages, ResultDateModel dates, ArrayList<ResultMovieModel> movieList)
    {
        super(page, total_results, total_pages, dates, movieList);
    }
}
