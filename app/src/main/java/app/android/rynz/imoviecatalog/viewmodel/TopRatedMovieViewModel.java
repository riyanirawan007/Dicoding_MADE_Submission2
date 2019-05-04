package app.android.rynz.imoviecatalog.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import app.android.rynz.imoviecatalog.data.model.TopRatedMovieModel;
import app.android.rynz.imoviecatalog.data.model.params.TopRatedMovieParams;
import app.android.rynz.imoviecatalog.data.repository.TMDBRepository;
import app.android.rynz.imoviecatalog.view.interfaces.TMDBService;

public class TopRatedMovieViewModel extends AndroidViewModel implements TMDBService.TopRatedMovies.TopRatedMovieLiveData
{
    TMDBRepository tmdbRepository;
    MutableLiveData<TopRatedMovieModel> mutableLiveData=new MutableLiveData<>();

    public TopRatedMovieViewModel(@NonNull Application application)
    {
        super(application);
        tmdbRepository=new TMDBRepository();
    }


    @Override
    public MutableLiveData<TopRatedMovieModel> topRatedMovieLiveData(@NonNull TopRatedMovieParams params)
    {
        tmdbRepository.topRatedMovies(params, true, new TMDBService.TopRatedMovies.TopRatedListener()
        {
            @Override
            public void onCompleted(@NonNull TopRatedMovieModel topRatedMovies)
            {
                mutableLiveData.setValue(topRatedMovies);
            }

            @Override
            public void onFailed(@Nullable String strError, @Nullable String apiRespons)
            {

            }
        });
        return mutableLiveData;
    }
}
