package app.android.rynz.imoviecatalog.view.interfaces;

import android.support.annotation.NonNull;
import android.view.View;

import app.android.rynz.imoviecatalog.data.model.results.ResultMovieModel;

public interface ViewMovie
{
    interface OnClickItemListener{
        void onClickItemListener(@NonNull ResultMovieModel movie, @NonNull View v, int position);
    }

    interface OnLongClickItemListener{
        void onLongClickItemListener(@NonNull ResultMovieModel movie, @NonNull View v, int position);
    }
}
