package app.android.rynz.imoviecatalog.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import app.android.rynz.imoviecatalog.data.model.UpComingMovieModel;
import app.android.rynz.imoviecatalog.data.model.params.UpComingMovieParams;
import app.android.rynz.imoviecatalog.data.repository.TMDBRepository;
import app.android.rynz.imoviecatalog.view.interfaces.TMDBService;

public class UpComingMovieViewModel extends AndroidViewModel implements TMDBService.UpComingMovies.UpComingMovieLiveData
{
    private TMDBRepository tmdbRepository;
    private MutableLiveData<UpComingMovieModel> mutableLiveData=new MutableLiveData<>();

    public UpComingMovieViewModel(@NonNull Application application)
    {
        super(application);
        tmdbRepository=new TMDBRepository();
    }

    @Override
    public MutableLiveData<UpComingMovieModel> upComingMovieLiveData(@NonNull UpComingMovieParams params)
    {
        tmdbRepository.upComingMovies(params, true, new TMDBService.UpComingMovies.UpComingListener()
        {
            @Override
            public void onCompleted(@Nullable UpComingMovieModel upComingMovies)
            {
                mutableLiveData.setValue(upComingMovies);
            }

            @Override
            public void onFailed(@Nullable String strError, @Nullable String apiRespons)
            {

            }
        });
        return mutableLiveData;
    }
}
