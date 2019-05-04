package app.android.rynz.imoviecatalog.data.model;

import java.util.ArrayList;

import app.android.rynz.imoviecatalog.data.model.results.ResultMovieModel;

public class TopRatedMovieModel extends SearchMovieModel
{
    //UpComingMovieModel has exactly same structure as SearchMovieModel
    public TopRatedMovieModel(int page, int total_results, int total_pages, ArrayList<ResultMovieModel> movieList)
    {
        super(page, total_results, total_pages, movieList);
    }
}
