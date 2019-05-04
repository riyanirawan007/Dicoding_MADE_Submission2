package app.android.rynz.imoviecatalog.view.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import app.android.rynz.imoviecatalog.BuildConfig;
import app.android.rynz.imoviecatalog.R;
import app.android.rynz.imoviecatalog.data.model.NowPlayingMovieModel;
import app.android.rynz.imoviecatalog.data.model.TopRatedMovieModel;
import app.android.rynz.imoviecatalog.data.model.UpComingMovieModel;
import app.android.rynz.imoviecatalog.data.model.params.NowPlayingParams;
import app.android.rynz.imoviecatalog.data.model.params.TopRatedMovieParams;
import app.android.rynz.imoviecatalog.data.model.params.UpComingMovieParams;
import app.android.rynz.imoviecatalog.data.model.results.ResultMovieModel;
import app.android.rynz.imoviecatalog.util.ExtraKeys;
import app.android.rynz.imoviecatalog.util.lib.FragmentSwitcher;
import app.android.rynz.imoviecatalog.util.lib.SimpleAlertDialog;
import app.android.rynz.imoviecatalog.view.adapter.HomeMovieListAdapter;
import app.android.rynz.imoviecatalog.view.adapter.HomeSliderAdapter;
import app.android.rynz.imoviecatalog.view.interfaces.ViewMovie;
import app.android.rynz.imoviecatalog.viewmodel.NowPlayingMovieViewModel;
import app.android.rynz.imoviecatalog.viewmodel.TopRatedMovieViewModel;
import app.android.rynz.imoviecatalog.viewmodel.UpComingMovieViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener
{
    @BindView(R.id.label_top_rated_more)
    TextView tvTopRatedMore;
    @BindView(R.id.label_now_playing_more)
    TextView tvNowPlayingMore;
    @BindView(R.id.label_up_coming_more)
    TextView tvUpComingMore;
    @BindView(R.id.rv_home_top_rated)
    RecyclerView rvTopRated;
    @BindView(R.id.rv_home_now_playing)
    RecyclerView rvNowPlaying;
    @BindView(R.id.rv_home_up_coming)
    RecyclerView rvUpComing;

    public HomeFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,v);
        tvTopRatedMore.setOnClickListener(this);
        tvNowPlayingMore.setOnClickListener(this);
        tvUpComingMore.setOnClickListener(this);
        if(getActivity()!=null) {
            ((HomeActivity)getActivity()).getSupportActBar().setTitle(R.string.app_title);
            ((HomeActivity)getActivity()).getSupportActBar().setDisplayHomeAsUpEnabled(false);
            ((HomeActivity)getActivity()).getSupportActBar().setDisplayShowHomeEnabled(false);
            ((HomeActivity)getActivity()).enableDrawerNavigationMenu(true);

        }
        getData();
        return v;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);
    }

    private void getData()
    {
        //Top Rated Movie
        TopRatedMovieParams topRatedMovieParams=new TopRatedMovieParams();
        topRatedMovieParams.requiredParams(BuildConfig.TMDBApiKey);
        TopRatedMovieViewModel topRatedMovieViewModel;
        if(getActivity()!=null)
            topRatedMovieViewModel= ViewModelProviders.of(getActivity()).get(TopRatedMovieViewModel.class);
        else
            topRatedMovieViewModel= ViewModelProviders.of(this).get(TopRatedMovieViewModel.class);
        Observer<TopRatedMovieModel> topRatedObserver=new Observer<TopRatedMovieModel>()
        {
            @Override
            public void onChanged(@Nullable TopRatedMovieModel topRatedMovieModel)
            {
                if(topRatedMovieModel==null)
                {
                    SimpleAlertDialog.getInstance()
                            .BuildAlert(getContext(),getString(R.string.dialog_title_information),getString(R.string.dialog_msg_cannot_load_data),false,false)
                            .Basic(getString(R.string.dialog_confirm_ok));
                }
                else
                {
                    renderTopRatedList(topRatedMovieModel);
                }
            }
        };
        topRatedMovieViewModel.topRatedMovieLiveData(topRatedMovieParams).observe(getActivity(),topRatedObserver);

        //Now Playing
        NowPlayingParams nowPlayingParams=new NowPlayingParams();
        nowPlayingParams.requiredParams(BuildConfig.TMDBApiKey);
        NowPlayingMovieViewModel nowPlayingMovieViewModel= ViewModelProviders.of(getActivity()).get(NowPlayingMovieViewModel.class);
        Observer<NowPlayingMovieModel> nowPlayingObserver=new Observer<NowPlayingMovieModel>()
        {
            @Override
            public void onChanged(@Nullable NowPlayingMovieModel nowPlayingMovieModel)
            {
                if(nowPlayingMovieModel==null)
                {
                    SimpleAlertDialog.getInstance()
                            .BuildAlert(getContext(),getString(R.string.dialog_title_information),getString(R.string.dialog_msg_cannot_load_data),false,false)
                            .Basic(getString(R.string.dialog_confirm_ok));
                }
                else
                {
                    renderNowPlayingList(nowPlayingMovieModel);
                }
            }
        };
        nowPlayingMovieViewModel.nowPlayingMovieLiveData(nowPlayingParams).observe(getActivity(),nowPlayingObserver);

        //UpComing
        UpComingMovieParams upComingMovieParams=new UpComingMovieParams();
        upComingMovieParams.requiredParams(BuildConfig.TMDBApiKey);
        UpComingMovieViewModel upComingMovieViewModel=ViewModelProviders.of(getActivity()).get(UpComingMovieViewModel.class);
        Observer<UpComingMovieModel> upComingObserver=new Observer<UpComingMovieModel>()
        {
            @Override
            public void onChanged(@Nullable UpComingMovieModel upComingMovieModel)
            {
                if(upComingMovieModel==null)
                {
                    SimpleAlertDialog.getInstance()
                            .BuildAlert(getContext(),getString(R.string.dialog_title_information),getString(R.string.dialog_msg_cannot_load_data),false,false)
                            .Basic(getString(R.string.dialog_confirm_ok));
                }
                else
                {
                    renderUpComingPlayingList(upComingMovieModel);
                }
            }
        };
        upComingMovieViewModel.upComingMovieLiveData(upComingMovieParams).observe(getActivity(),upComingObserver);


    }

    private void renderTopRatedList(TopRatedMovieModel model)
    {
        if(getContext()!=null)
        {
            LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            if(getActivity()!=null)
            {
                int maxLimit=5;
                ArrayList<ResultMovieModel> movieList=new ArrayList<>();
                if(model.getMovieList().size()>maxLimit)
                {
                    for(int i=0;i<maxLimit;i++)
                    {
                        movieList.add(model.getMovieList().get(i));
                    }
                }
                TopRatedMovieModel topRatedMovieModel=new TopRatedMovieModel(model.getPage(),
                        model.getTotal_results()
                        ,model.getTotal_pages()
                        ,movieList);
                HomeSliderAdapter topRatedAdapter=new HomeSliderAdapter(getActivity().getApplicationContext(),topRatedMovieModel);
                rvTopRated.setLayoutManager(layoutManager);
                rvTopRated.setAdapter(topRatedAdapter);
                final Context context=getActivity().getApplicationContext();
                topRatedAdapter.setOnClickItemListener(new ViewMovie.OnClickItemListener()
                {
                    @Override
                    public void onClickItemListener(@NonNull ResultMovieModel movie, @NonNull View v, int position)
                    {
                        Bundle bundle=new Bundle();
                        bundle.putInt(ExtraKeys.EXTRA_MOVIE_ID,movie.getIdMovie());
                        bundle.putString(ExtraKeys.EXTRA_MOVIE_TITLE,movie.getTitle());
                        FragmentSwitcher.getInstance()
                                .withContext(context)
                                .withContainer(R.id.home_fragment_container)
                                .withFragmentManager(getActivity().getSupportFragmentManager())
                                .withFragment(new DetailMovieFragment())
                                .withExtraBundle(bundle,null)
                                .setToBackStack(true)
                                .commitReplace();
                    }
                });
                topRatedAdapter.notifyDataSetChanged();
            }
        }
    }

    private void renderNowPlayingList(NowPlayingMovieModel model)
    {
        if(getContext()!=null)
        {
            LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            if(getActivity()!=null)
            {
                int maxLimit=6;
                ArrayList<ResultMovieModel> movieList=new ArrayList<>();
                if(model.getMovieList().size()>maxLimit)
                {
                    for(int i=0;i<maxLimit;i++)
                    {
                        movieList.add(model.getMovieList().get(i));
                    }
                }
                NowPlayingMovieModel nowPlayingMovieModel=new NowPlayingMovieModel(model.getPage()
                        ,model.getTotal_results()
                        ,model.getTotal_pages()
                        ,model.getDates()
                        ,movieList);
                HomeMovieListAdapter nowPlayingAdapter=new HomeMovieListAdapter(getActivity().getApplicationContext());
                nowPlayingAdapter.withNowPlayingMovie(nowPlayingMovieModel);
                rvNowPlaying.setLayoutManager(layoutManager);
                rvNowPlaying.setAdapter(nowPlayingAdapter);
                final Context context=getActivity().getApplicationContext();
                nowPlayingAdapter.setOnClickItemListener(new ViewMovie.OnClickItemListener()
                {
                    @Override
                    public void onClickItemListener(@NonNull ResultMovieModel movie, @NonNull View v, int position)
                    {
                        Bundle bundle=new Bundle();
                        bundle.putInt(ExtraKeys.EXTRA_MOVIE_ID,movie.getIdMovie());
                        bundle.putString(ExtraKeys.EXTRA_MOVIE_TITLE,movie.getTitle());
                        FragmentSwitcher.getInstance()
                                .withContext(context)
                                .withContainer(R.id.home_fragment_container)
                                .withFragmentManager(getActivity().getSupportFragmentManager())
                                .withFragment(new DetailMovieFragment())
                                .withExtraBundle(bundle,null)
                                .setToBackStack(true)
                                .commitReplace();
                    }
                });
                nowPlayingAdapter.notifyDataSetChanged();
            }
        }
    }

    private void renderUpComingPlayingList(UpComingMovieModel model)
    {
        if(getContext()!=null)
        {
            //Compare to get real movie upcoming release date from current date
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date currentDate = Calendar.getInstance().getTime();
            Date compareDate = null;
            ArrayList<ResultMovieModel> movieList=new ArrayList<>();

            for(int i=0;i<model.getMovieList().size();i++)
            {
                try
                {
                    compareDate = simpleDateFormat.parse(model.getMovieList().get(i).getRealeseDate());
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }

                if (currentDate != null && compareDate != null)
                {
                    if (currentDate.compareTo(compareDate) <= 0)
                    {
                        movieList.add(model.getMovieList().get(i));
                    }
                }
                compareDate = null;
            }

            Collections.sort(movieList,Collections.reverseOrder(ResultMovieModel.Comparators.RELEASE_DATE));
            model.setMovieList(movieList);
//            movieList.clear();
            if(getActivity()!=null)
            {
                LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
                layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                HomeMovieListAdapter upComingAdapter=new HomeMovieListAdapter(getActivity().getApplicationContext());
                upComingAdapter.withUpComingMovie(model);
                rvUpComing.setLayoutManager(layoutManager);
                rvUpComing.setAdapter(upComingAdapter);
                final Context context=getActivity().getApplicationContext();
                upComingAdapter.setOnClickItemListener(new ViewMovie.OnClickItemListener()
                {
                    @Override
                    public void onClickItemListener(@NonNull ResultMovieModel movie, @NonNull View v, int position)
                    {
                        Bundle bundle=new Bundle();
                        bundle.putInt(ExtraKeys.EXTRA_MOVIE_ID,movie.getIdMovie());
                        bundle.putString(ExtraKeys.EXTRA_MOVIE_TITLE,movie.getTitle());
                        FragmentSwitcher.getInstance()
                                .withContext(context)
                                .withContainer(R.id.home_fragment_container)
                                .withFragmentManager(getActivity().getSupportFragmentManager())
                                .withFragment(new DetailMovieFragment())
                                .withExtraBundle(bundle,null)
                                .setToBackStack(true)
                                .commitReplace();
                    }
                });
                upComingAdapter.notifyDataSetChanged();
            }

        }
    }

    @Override
    public void onClick(View view)
    {
        if(view==tvTopRatedMore)
        {
            if(getActivity()!=null)
            {
                Bundle bundle=new Bundle();
                bundle.putString(ExtraKeys.EXTRA_MOVIE_LIST_STATE,ExtraKeys.MOVIE_LIST_STATE_TOP_RATED);
                FragmentSwitcher.getInstance()
                        .withContext(getActivity().getApplicationContext())
                        .withContainer(R.id.home_fragment_container)
                        .withFragmentManager(getActivity().getSupportFragmentManager())
                        .withFragment(new MovieListFragment())
                        .withExtraBundle(bundle,null)
                        .setToBackStack(true)
                        .commitReplace();
            }

        }
        else if(view==tvNowPlayingMore)
        {
            if(getActivity()!=null)
            {
                Bundle bundle=new Bundle();
                bundle.putString(ExtraKeys.EXTRA_MOVIE_LIST_STATE,ExtraKeys.MOVIE_LIST_STATE_NOW_PLAYING);
                FragmentSwitcher.getInstance()
                        .withContext(getActivity().getApplicationContext())
                        .withContainer(R.id.home_fragment_container)
                        .withFragmentManager(getActivity().getSupportFragmentManager())
                        .withFragment(new MovieListFragment())
                        .withExtraBundle(bundle,null)
                        .setToBackStack(true)
                        .commitReplace();
            }
        }
        else if(view==tvUpComingMore)
        {
            if(getActivity()!=null)
            {
                Bundle bundle=new Bundle();
                bundle.putString(ExtraKeys.EXTRA_MOVIE_LIST_STATE,ExtraKeys.MOVIE_LIST_STATE_UP_COMING);
                FragmentSwitcher.getInstance()
                        .withContext(getActivity().getApplicationContext())
                        .withContainer(R.id.home_fragment_container)
                        .withFragmentManager(getActivity().getSupportFragmentManager())
                        .withFragment(new MovieListFragment())
                        .withExtraBundle(bundle,null)
                        .setToBackStack(true)
                        .commitReplace();
            }
        }
    }
}
