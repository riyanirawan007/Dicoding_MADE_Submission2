package app.android.rynz.imoviecatalog.view.interfaces;

import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import app.android.rynz.imoviecatalog.data.model.DetailMovieModel;
import app.android.rynz.imoviecatalog.data.model.NowPlayingMovieModel;
import app.android.rynz.imoviecatalog.data.model.SearchMovieModel;
import app.android.rynz.imoviecatalog.data.model.TopRatedMovieModel;
import app.android.rynz.imoviecatalog.data.model.UpComingMovieModel;
import app.android.rynz.imoviecatalog.data.model.params.DetailMovieParams;
import app.android.rynz.imoviecatalog.data.model.params.NowPlayingParams;
import app.android.rynz.imoviecatalog.data.model.params.SearchMovieParams;
import app.android.rynz.imoviecatalog.data.model.params.TopRatedMovieParams;
import app.android.rynz.imoviecatalog.data.model.params.UpComingMovieParams;

public interface TMDBService
{
    interface SearchMovies
    {
        void searchMovies(@NonNull SearchMovieParams params, boolean isAsyncProcess, @NonNull final SearchListener listener);

        interface SearchListener
        {
            void onCompleted(@Nullable SearchMovieModel searchResult);

            void onFailed(@Nullable String strError, @Nullable String apiRespons);
        }

        interface SearchMovieLiveData
        {
            MutableLiveData<SearchMovieModel> searchMoviesLiveData(@NonNull SearchMovieParams params);
        }
    }

    interface DetailMovie
    {
        void detailMovie(@NonNull DetailMovieParams params, boolean isAsyncProcess, @NonNull final DetailMovieListener listener);

        interface DetailMovieListener
        {
            void onCompleted(@Nullable DetailMovieModel movieDetail);

            void onFailed(@Nullable String strError, @Nullable String apiRespons);
        }

        interface DetailMovieLiveData
        {
            MutableLiveData<DetailMovieModel> detailMovieLiveData(@NonNull DetailMovieParams params);
        }
    }

    interface NowPlayingMovies
    {
        void nowPlayingMovies(@NonNull NowPlayingParams params, boolean isAsyncProcess, @NonNull final NowPlayingListener listener);

        interface NowPlayingListener
        {
            void onCompleted(@NonNull NowPlayingMovieModel nowPlayingMovies);

            void onFailed(@Nullable String strError, @Nullable String apiRespons);
        }

        interface NowPlayingMovieLiveData
        {
            MutableLiveData<NowPlayingMovieModel> nowPlayingMovieLiveData(@NonNull NowPlayingParams params);
        }
    }

    interface UpComingMovies
    {
        void upComingMovies(@NonNull UpComingMovieParams params, boolean isAsyncProcess, @NonNull final UpComingListener listener);

        interface UpComingListener
        {
            void onCompleted(@Nullable UpComingMovieModel upComingMovies);

            void onFailed(@Nullable String strError, @Nullable String apiRespons);
        }

        interface UpComingMovieLiveData
        {
            MutableLiveData<UpComingMovieModel> upComingMovieLiveData(@NonNull UpComingMovieParams params);
        }
    }

    interface TopRatedMovies
    {
        void topRatedMovies(@NonNull TopRatedMovieParams params, boolean isAsyncProcess, @NonNull final TopRatedListener listener);

        interface TopRatedListener
        {
            void onCompleted(@NonNull TopRatedMovieModel topRatedMovies);
            void onFailed(@Nullable String strError,@Nullable String apiRespons);
        }

        interface TopRatedMovieLiveData
        {
            MutableLiveData<TopRatedMovieModel> topRatedMovieLiveData(@NonNull TopRatedMovieParams params);
        }
    }

}
