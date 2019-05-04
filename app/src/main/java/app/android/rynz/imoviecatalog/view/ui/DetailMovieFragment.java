package app.android.rynz.imoviecatalog.view.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

import app.android.rynz.imoviecatalog.BuildConfig;
import app.android.rynz.imoviecatalog.R;
import app.android.rynz.imoviecatalog.data.model.DetailMovieModel;
import app.android.rynz.imoviecatalog.data.model.params.DetailMovieParams;
import app.android.rynz.imoviecatalog.data.repository.TMDBApiReference;
import app.android.rynz.imoviecatalog.util.ExtraKeys;
import app.android.rynz.imoviecatalog.util.lib.DateFormatConverter;
import app.android.rynz.imoviecatalog.viewmodel.DetailMovieViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailMovieFragment extends Fragment
{

    @BindView(R.id.srl_detail)
    SwipeRefreshLayout srlDetail;
    @BindView(R.id.tv_detail_load_info)
    TextView tvLoadInfo;
    @BindView(R.id.rl_detail_container)
    RelativeLayout rlDetailContainer;

    @BindView(R.id.Iv_detail_poster)
    ImageView ivPoster;
    @BindView(R.id.tv_detail_title)
    TextView tvTitle;
    @BindView(R.id.tv_detail_release_date)
    TextView tvReleaseDate;
    @BindView(R.id.tv_detail_score)
    TextView tvScore;
    @BindView(R.id.tv_detail_runtime)
    TextView tvRuntime;
    @BindView(R.id.tv_detail_status)
    TextView tvStatus;
    @BindView(R.id.tv_detail_budget)
    TextView tvBudget;
    @BindView(R.id.tv_detail_genres)
    TextView tvGenres;
    @BindView(R.id.tv_detail_companies)
    TextView tvCompanies;
    @BindView(R.id.tv_detail_countries)
    TextView tvCountries;
    @BindView(R.id.tv_detail_languages)
    TextView tvLanguages;
    @BindView(R.id.tv_detail_overview)
    TextView tvOverview;

    private DetailMovieParams params = new DetailMovieParams();
    private DetailMovieViewModel detailViewModel;

    private int movieID = 0;
    private String movieTitle="Movie";
    private Bundle savedState;

    public DetailMovieFragment()
    {
        // Required empty public constructor
    }

    private Bundle saveState()
    {
        Bundle bundle=new Bundle();
        bundle.putInt(ExtraKeys.EXTRA_MOVIE_ID,movieID);
        bundle.putString(ExtraKeys.EXTRA_MOVIE_TITLE,movieTitle);
        return bundle;
    }

    private void loadState(@Nullable Bundle savedInstanceState)
    {
        if(savedInstanceState!=null)
        {
            savedState=savedInstanceState.getBundle(ExtraKeys.FRAGMENT_MOVIE_DETAIL_BUNDLE);
        }
        if(savedState!=null)
        {
            movieID=savedState.getInt(ExtraKeys.EXTRA_MOVIE_ID);
            movieTitle=savedState.getString(ExtraKeys.EXTRA_MOVIE_TITLE);
        }
        else
        {
            if(getArguments()!=null)
            {
                if(getArguments().containsKey(ExtraKeys.EXTRA_MOVIE_ID))
                {
                    movieID=getArguments().getInt(ExtraKeys.EXTRA_MOVIE_ID);
                }
                if(getArguments().containsKey(ExtraKeys.EXTRA_MOVIE_TITLE))
                {
                    movieTitle=getArguments().getString(ExtraKeys.EXTRA_MOVIE_TITLE);
                }
            }
        }

        savedState=null;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        savedState=saveState();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBundle(ExtraKeys.FRAGMENT_MOVIE_DETAIL_BUNDLE,savedState!=null?savedState:saveState());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        loadState(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_detail_movie, container, false);
        ButterKnife.bind(this,v);
        loadState(savedInstanceState);
        setHasOptionsMenu(true);

        srlDetail.setRefreshing(true);
        srlDetail.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorAccent));
        rlDetailContainer.setVisibility(View.GONE);
        tvLoadInfo.setVisibility(View.VISIBLE);
        tvLoadInfo.setText(R.string.loading_movie_detail);

        srlDetail.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                rlDetailContainer.setVisibility(View.GONE);
                tvLoadInfo.setVisibility(View.VISIBLE);
                tvLoadInfo.setText(R.string.loading_movie_detail);
                params.requiredParams(BuildConfig.TMDBApiKey, movieID);
                if (detailViewModel != null)
                {
                    detailViewModel.detailMovieLiveData(params);
                }
            }
        });
        params.requiredParams(BuildConfig.TMDBApiKey, movieID);
        setUpDetailMovieDataObserver();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        if(getActivity()!=null) {
            ((HomeActivity)getActivity()).enableDrawerNavigationMenu(false);
            ((HomeActivity)getActivity()).getSupportActBar().setTitle(movieTitle);
            ((HomeActivity)getActivity()).getSupportActBar().setDisplayHomeAsUpEnabled(true);
            ((HomeActivity)getActivity()).getSupportActBar().setDisplayShowHomeEnabled(true);
        }
    }
    private void setUpDetailMovieDataObserver()
    {
        if(getActivity()!=null)
        {
            detailViewModel = ViewModelProviders.of(this).get(DetailMovieViewModel.class);
            Observer<DetailMovieModel> observer = new Observer<DetailMovieModel>()
            {
                @Override
                public void onChanged(@Nullable DetailMovieModel detailMovieModel)
                {
                    displayDetailMovie(detailMovieModel);
                }
            };
            detailViewModel.detailMovieLiveData(params).observe(this, observer);
        }

    }

    private void displayDetailMovie(DetailMovieModel model)
    {
        if (srlDetail.isRefreshing())
        {
            srlDetail.setRefreshing(false);
        }
        if (model != null)
        {
            rlDetailContainer.setVisibility(View.VISIBLE);
            tvLoadInfo.setVisibility(View.GONE);
            if(getActivity()!=null)
            {
                Glide.with(getActivity().getApplicationContext())
                        .load(TMDBApiReference.TMDB_POSTER_500px + model.getPosterPath())
                        .apply(new RequestOptions().placeholder(R.drawable.no_images)
                                .error(R.drawable.no_images))
                        .thumbnail(0.9f)
                        .into(ivPoster);
            }

            tvTitle.setText(model.getTitle());
            tvReleaseDate.setText(
                    new DateFormatConverter().withDate(model.getReleaseDate())
                            .withPatternConvert(DateFormatConverter.PATTERN_DATE_SQL, DateFormatConverter.PATTERN_DATE_SPELL_COMMON, Locale.getDefault())
                            .doConvert());
            String score = model.getVoteAverage() + "/10 (" + model.getVoteCount() + " votes)";
            tvScore.setText(score);
            String runtime = getString(R.string.undefined_content);
            if (model.getRuntime() != 0)
            {
                runtime = model.getRuntime() + " minutes";
            }
            tvRuntime.setText(runtime);
            tvStatus.setText(model.getStatus());
            String budget = model.getBuget() + " USD";
            tvBudget.setText(budget);
            String genres = "-";
            if (model.getGenres().size() > 0)
            {
                genres = "";
                for (int i = 0; i < model.getGenres().size(); i++)
                {
                    if (i != 0)
                    {
                        genres = genres.concat(", ");
                    }
                    genres = genres.concat(model.getGenres().get(i).getName());

                }
            }
            tvGenres.setText(genres);

            String companies = "-";
            if (model.getProductionCompanies().size() > 0)
            {
                companies = "";
                for (int i = 0; i < model.getProductionCompanies().size(); i++)
                {
                    if (i != 0)
                    {
                        companies = companies.concat(", ");
                    }
                    companies = companies.concat(model.getProductionCompanies().get(i).getName());

                }
            }
            tvCompanies.setText(companies);

            String countries = "-";
            if (model.getProductionCountries().size() > 0)
            {
                countries = "";
                for (int i = 0; i < model.getProductionCountries().size(); i++)
                {
                    if (i != 0)
                    {
                        countries = companies.concat(", ");
                    }
                    countries = countries.concat(model.getProductionCountries().get(i).getName());

                }
            }
            tvCountries.setText(countries);

            String languages = "-";
            if (model.getSpokenLanguages().size() > 0)
            {
                languages = "";
                for (int i = 0; i < model.getSpokenLanguages().size(); i++)
                {
                    if (i != 0)
                    {
                        languages = companies.concat(", ");
                    }
                    languages = languages.concat(model.getSpokenLanguages().get(i).getName());

                }
            }
            tvLanguages.setText(languages);

            tvOverview.setText(model.getOverview());
        } else
        {
            rlDetailContainer.setVisibility(View.GONE);
            tvLoadInfo.setVisibility(View.VISIBLE);
            tvLoadInfo.setText(R.string.loading_detail_failed);
        }
    }
}
