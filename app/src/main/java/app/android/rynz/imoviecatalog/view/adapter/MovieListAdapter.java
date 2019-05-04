package app.android.rynz.imoviecatalog.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

import app.android.rynz.imoviecatalog.R;
import app.android.rynz.imoviecatalog.data.model.NowPlayingMovieModel;
import app.android.rynz.imoviecatalog.data.model.SearchMovieModel;
import app.android.rynz.imoviecatalog.data.model.TopRatedMovieModel;
import app.android.rynz.imoviecatalog.data.model.UpComingMovieModel;
import app.android.rynz.imoviecatalog.data.model.results.ResultMovieModel;
import app.android.rynz.imoviecatalog.data.repository.TMDBApiReference;
import app.android.rynz.imoviecatalog.util.lib.DateFormatConverter;
import app.android.rynz.imoviecatalog.view.interfaces.ViewMovie;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder>
{
    private Context context;
    private SearchMovieModel searchMovieModel;
    private TopRatedMovieModel topRatedMovieModel;
    private NowPlayingMovieModel nowPlayingMovieModel;
    private UpComingMovieModel upComingMovieModel;
    private ResultMovieModel movie;
    
    private ViewMovie.OnClickItemListener onClickItemListener;

    public MovieListAdapter(@NonNull Context context)
    {
        this.context = context;
    }

    public MovieListAdapter withSearchMovie(@NonNull SearchMovieModel searchMovieModel)
    {
        this.searchMovieModel = searchMovieModel;
        return this;
    }

    public MovieListAdapter withTopRatedMovie(@NonNull TopRatedMovieModel topRatedMovieModel)
    {
        this.topRatedMovieModel = topRatedMovieModel;
        return this;
    }

    public MovieListAdapter withNowPlayingMovie(@NonNull NowPlayingMovieModel nowPlayingMovieModel)
    {
        this.nowPlayingMovieModel=nowPlayingMovieModel;
        return this;
    }

    public MovieListAdapter withUpComingMovie(@NonNull UpComingMovieModel upComingMovieModel)
    {
        this.upComingMovieModel=upComingMovieModel;
        return this;
    }

    @NonNull
    @Override
    public MovieListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.rv_movie_list_item,viewGroup,false);
        return new MovieListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieListViewHolder holder, int i)
    {
        int position = holder.getAdapterPosition();
        if(searchMovieModel!=null)
        {
            movie=searchMovieModel.getMovieList().get(position);
        }
        else if(nowPlayingMovieModel!=null)
        {
            movie=nowPlayingMovieModel.getMovieList().get(position);
        }
        else if(topRatedMovieModel!=null)
        {
            movie=topRatedMovieModel.getMovieList().get(position);
        }
        else if(upComingMovieModel!=null)
        {
            movie=upComingMovieModel.getMovieList().get(position);
        }

        Glide.with(context)
                .load(TMDBApiReference.TMDB_POSTER_342px + movie.getPosterPath())
                .thumbnail(0.9f)
                .apply(new RequestOptions().placeholder(R.drawable.no_images)
                .error(R.drawable.no_images))
                .into(holder.poster);
        String titleSnipset = movie.getTitle();
        if (titleSnipset.length() > 30)
        {
            titleSnipset = titleSnipset.substring(0, 30) + "...";
        }
        holder.title.setText(titleSnipset);

        String overviewSnipset = movie.getOverview();
        if (overviewSnipset.length() > 120)
        {
            overviewSnipset = overviewSnipset.substring(0, 120) + "...";
        }
        holder.desc.setText(overviewSnipset);
        holder.releaseDate.setText(
                new DateFormatConverter()
                        .withDate(movie.getRealeseDate())
                        .withPatternConvert(DateFormatConverter.PATTERN_DATE_SQL, DateFormatConverter.PATTERN_DATE_SPELL_COMMON, Locale.getDefault())
                        .doConvert()
        );
        holder.share.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(searchMovieModel!=null)
                {
                    movie=searchMovieModel.getMovieList().get(holder.getAdapterPosition());
                }
                else if(nowPlayingMovieModel!=null)
                {
                    movie=nowPlayingMovieModel.getMovieList().get(holder.getAdapterPosition());
                }
                else if(topRatedMovieModel!=null)
                {
                    movie=topRatedMovieModel.getMovieList().get(holder.getAdapterPosition());
                }
                else if(upComingMovieModel!=null)
                {
                    movie=upComingMovieModel.getMovieList().get(holder.getAdapterPosition());
                }
                Intent shareIntent=new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,context.getString(R.string.share_caption_watch)+" "+movie.getTitle()+"\n"+context.getResources().getString(R.string.label_release_date)
                        +" "+new DateFormatConverter()
                        .withDate(movie.getRealeseDate())
                        .withPatternConvert(DateFormatConverter.PATTERN_DATE_SQL, DateFormatConverter.PATTERN_DATE_SPELL_COMMON, Locale.getDefault())
                        .doConvert()+"\n"+context.getResources().getString(R.string.label_overview)+" : "+movie.getOverview());
                shareIntent.setType("text/plain");
                context.startActivity(shareIntent);
            }
        });
        holder.getView().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(searchMovieModel!=null)
                {
                    movie=searchMovieModel.getMovieList().get(holder.getAdapterPosition());
                }
                else if(nowPlayingMovieModel!=null)
                {
                    movie=nowPlayingMovieModel.getMovieList().get(holder.getAdapterPosition());
                }
                else if(topRatedMovieModel!=null)
                {
                    movie=topRatedMovieModel.getMovieList().get(holder.getAdapterPosition());
                }
                else if(upComingMovieModel!=null)
                {
                    movie=upComingMovieModel.getMovieList().get(holder.getAdapterPosition());
                }
                if(onClickItemListener!=null) onClickItemListener.onClickItemListener(movie,holder.getView(),holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        int size;
        if(searchMovieModel!=null)
        {
            size=searchMovieModel.getMovieList().size();
        }
        else if(nowPlayingMovieModel!=null)
        {
            size=nowPlayingMovieModel.getMovieList().size();
        }
        else if(topRatedMovieModel!=null)
        {
            size=topRatedMovieModel.getMovieList().size();
        }
        else if(upComingMovieModel!=null)
        {
            size=upComingMovieModel.getMovieList().size();
        }
        else
        {
            size=0;
        }
        return size;
    }

    class MovieListViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.lv_item_poster)
        ImageView poster;
        @BindView(R.id.lv_item_title)
        TextView title;
        @BindView(R.id.lv_item_desc)
        TextView desc;
        @BindView(R.id.lv_item_release_date)
        TextView releaseDate;
        @BindView(R.id.iv_item_share)
        ImageView share;
        private View v;

        MovieListViewHolder(@NonNull View v)
        {
            super(v);
            ButterKnife.bind(this, v);
            this.v=v;
        }

        public View getView()
        {
            return v;
        }
    }


    public void setOnClickItemListener(ViewMovie.OnClickItemListener onClickItemListener)
    {
        this.onClickItemListener = onClickItemListener;
    }
}
