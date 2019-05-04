package app.android.rynz.imoviecatalog.view.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
import app.android.rynz.imoviecatalog.data.model.SearchMovieModel;
import app.android.rynz.imoviecatalog.data.model.TopRatedMovieModel;
import app.android.rynz.imoviecatalog.data.model.UpComingMovieModel;
import app.android.rynz.imoviecatalog.data.model.params.NowPlayingParams;
import app.android.rynz.imoviecatalog.data.model.params.SearchMovieParams;
import app.android.rynz.imoviecatalog.data.model.params.TopRatedMovieParams;
import app.android.rynz.imoviecatalog.data.model.params.UpComingMovieParams;
import app.android.rynz.imoviecatalog.data.model.results.ResultMovieModel;
import app.android.rynz.imoviecatalog.util.ExtraKeys;
import app.android.rynz.imoviecatalog.util.lib.FragmentSwitcher;
import app.android.rynz.imoviecatalog.util.lib.SimpleAlertDialog;
import app.android.rynz.imoviecatalog.view.adapter.MovieListAdapter;
import app.android.rynz.imoviecatalog.view.interfaces.ViewMovie;
import app.android.rynz.imoviecatalog.viewmodel.NowPlayingMovieViewModel;
import app.android.rynz.imoviecatalog.viewmodel.SearchMoviesViewModel;
import app.android.rynz.imoviecatalog.viewmodel.TopRatedMovieViewModel;
import app.android.rynz.imoviecatalog.viewmodel.UpComingMovieViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment
{
    @BindView(R.id.rv_movie_list)
    RecyclerView rvMovieList;
    @BindView(R.id.srl_movie_list)
    SwipeRefreshLayout refreshLayout;


    private MovieListAdapter searchAdapter;
    private MovieListAdapter nowPlayingAdapter;
    private MovieListAdapter topRatedAdapter;
    private MovieListAdapter upComingAdapter;

    private SearchMoviesViewModel searchMoviesViewModel;
    private SearchMovieParams searchMovieParams=new SearchMovieParams();

    private TopRatedMovieViewModel topRatedMovieViewModel;
    private TopRatedMovieParams topRatedMovieParams=new TopRatedMovieParams();

    private NowPlayingMovieViewModel nowPlayingMovieViewModel;
    private NowPlayingParams nowPlayingParams=new NowPlayingParams();

    private UpComingMovieViewModel upComingMovieViewModel;
    private UpComingMovieParams upComingMovieParams=new UpComingMovieParams();

    private String movieListState;
    private String keywords;
    private Bundle savedState=null;

    public MovieListFragment()
    {
        // Required empty public constructor
    }


    private Bundle saveState()
    {
        Bundle bundle=new Bundle();
        bundle.putString(ExtraKeys.EXTRA_MOVIE_LIST_STATE,movieListState);
        if(movieListState!=null)
        {
            if(movieListState.equals(ExtraKeys.MOVIE_LIST_STATE_SEARCH))
            {
                bundle.putString(ExtraKeys.EXTRA_SEARCH_KEYWORDS,keywords);
            }
        }
        return bundle;
    }

    private void loadState(@Nullable Bundle savedInstanceState)
    {
        if(savedInstanceState!=null)
        {
            //LoadState after change
            savedState=savedInstanceState.getBundle(ExtraKeys.FRAGMENT_MOVIE_LIST_BUNDLE);
        }

        //Load State Arguments
        if(savedState!=null)
        {
            movieListState=savedState.getString(ExtraKeys.EXTRA_MOVIE_LIST_STATE);
            if(movieListState!=null)
            {
                if(movieListState.equals(ExtraKeys.MOVIE_LIST_STATE_SEARCH))
                {
                    keywords=savedState.getString(ExtraKeys.EXTRA_SEARCH_KEYWORDS);
                }
            }
        }
        else
        {
            if(getArguments()!=null)
            {
                movieListState=getArguments().getString(ExtraKeys.EXTRA_MOVIE_LIST_STATE);
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
        outState.putBundle(ExtraKeys.FRAGMENT_MOVIE_LIST_BUNDLE,savedState!=null?savedState:saveState());
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
        View v=inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this,v);
        loadState(savedInstanceState);
        setHasOptionsMenu(true);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorPrimaryDark));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                if(movieListState!=null)
                {
                    switch (movieListState)
                    {
                        case ExtraKeys.MOVIE_LIST_STATE_SEARCH:{
                            searchMovieParams.requiredParams(BuildConfig.TMDBApiKey,keywords);
                            searchMoviesViewModel.searchMoviesLiveData(searchMovieParams);
                            break;
                        }
                        case ExtraKeys.MOVIE_LIST_STATE_TOP_RATED:{
                            topRatedMovieViewModel.topRatedMovieLiveData(topRatedMovieParams);
                            getTopRatedMovie();
                            break;
                        }
                        case ExtraKeys.MOVIE_LIST_STATE_NOW_PLAYING:{
                            nowPlayingMovieViewModel.nowPlayingMovieLiveData(nowPlayingParams);
                            getNowPlayingMovie();
                            break;
                        }
                        case ExtraKeys.MOVIE_LIST_STATE_UP_COMING:{
                            upComingMovieViewModel.upComingMovieLiveData(upComingMovieParams);
                            getUpComingMovie();
                            break;
                        }
                    }
                }
            }
        });
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        menu.clear();
        if(movieListState!=null)
        {
            switch (movieListState)
            {
                case ExtraKeys.MOVIE_LIST_STATE_SEARCH:{
                    inflater.inflate(R.menu.movie_list_menu,menu);
                    break;
                }
                case ExtraKeys.MOVIE_LIST_STATE_TOP_RATED:{
                    if(getActivity()!=null) {
                        ((HomeActivity)getActivity()).enableDrawerNavigationMenu(false);
                        ((HomeActivity)getActivity()).getSupportActBar().setTitle(R.string.label_top_rated);
                        ((HomeActivity)getActivity()).getSupportActBar().setDisplayHomeAsUpEnabled(true);
                        ((HomeActivity)getActivity()).getSupportActBar().setDisplayShowHomeEnabled(true);
                    }
                    break;
                }
                case ExtraKeys.MOVIE_LIST_STATE_NOW_PLAYING:{
                    if(getActivity()!=null) {
                        ((HomeActivity)getActivity()).enableDrawerNavigationMenu(false);
                        ((HomeActivity)getActivity()).getSupportActBar().setTitle(R.string.label_now_playing);
                        ((HomeActivity)getActivity()).getSupportActBar().setDisplayHomeAsUpEnabled(true);
                        ((HomeActivity)getActivity()).getSupportActBar().setDisplayShowHomeEnabled(true);
                    }
                    break;
                }
                case ExtraKeys.MOVIE_LIST_STATE_UP_COMING:{
                    if(getActivity()!=null) {
                        ((HomeActivity)getActivity()).enableDrawerNavigationMenu(false);
                        ((HomeActivity)getActivity()).getSupportActBar().setTitle(R.string.label_up_coming);
                        ((HomeActivity)getActivity()).getSupportActBar().setDisplayHomeAsUpEnabled(true);
                        ((HomeActivity)getActivity()).getSupportActBar().setDisplayShowHomeEnabled(true);
                    }
                    break;
                }
            }
        }
        else
        {
            if(getActivity()!=null) getActivity().onBackPressed();
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        switch (movieListState)
        {
            case ExtraKeys.MOVIE_LIST_STATE_SEARCH:{
                searchMovieParams.requiredParams(BuildConfig.TMDBApiKey,"");
                getSearchMovie();
                MenuItem searchMenuItem=menu.findItem(R.id.action_search_movie);
                final SearchView searchView=(SearchView) searchMenuItem.getActionView();
                searchView.setQueryHint(getResources().getString(R.string.label_search_movie));
                searchMenuItem.expandActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
                {
                    @Override
                    public boolean onQueryTextSubmit(String s)
                    {
                        keywords=s;
                        if(!TextUtils.isEmpty(keywords))
                        {
                            searchMovieParams.requiredParams(BuildConfig.TMDBApiKey,keywords);
                            searchMoviesViewModel.searchMoviesLiveData(searchMovieParams);
                            searchView.clearFocus();
                        }else
                        {
                            SimpleAlertDialog.getInstance()
                                    .BuildAlert(getContext(),getString(R.string.dialog_title_information)
                                            ,getString(R.string.search_tell_cant_empty_keywords),false,false)
                                    .Basic(getString(R.string.dialog_confirm_ok));
                        }
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s)
                    {
                        return false;
                    }
                });
                searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
                {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem)
                    {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem)
                    {
                        if(getActivity()!=null) getActivity().onBackPressed();
                        return true;
                    }
                });

                if(keywords!=null)
                {
                    searchView.setQuery(keywords,true);
                }
                break;
            }
            case ExtraKeys.MOVIE_LIST_STATE_TOP_RATED:
            {
                topRatedMovieParams.requiredParams(BuildConfig.TMDBApiKey);
                getTopRatedMovie();
                break;
            }
            case ExtraKeys.MOVIE_LIST_STATE_NOW_PLAYING:
            {
                nowPlayingParams.requiredParams(BuildConfig.TMDBApiKey);
                getNowPlayingMovie();
                break;
            }
            case ExtraKeys.MOVIE_LIST_STATE_UP_COMING:
            {
                upComingMovieParams.requiredParams(BuildConfig.TMDBApiKey);
                getUpComingMovie();
                break;
            }
        }
    }


    private void getSearchMovie()
    {
        if(getActivity()!=null)
        {
            final Context context=getActivity().getApplicationContext();
            searchMoviesViewModel= ViewModelProviders.of(getActivity()).get(SearchMoviesViewModel.class);
            Observer<SearchMovieModel> observer=new Observer<SearchMovieModel>()
            {
                @Override
                public void onChanged(@Nullable SearchMovieModel searchMovieModel)
                {
                    if(refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
                    if(searchMovieModel!=null)
                    {
                        if(keywords!=null)
                        {
                            LinearLayoutManager layoutManager=new LinearLayoutManager(context);
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            searchAdapter=new MovieListAdapter(context).withSearchMovie(searchMovieModel);
                            rvMovieList.setLayoutManager(layoutManager);
                            rvMovieList.setAdapter(searchAdapter);
                            searchAdapter.setOnClickItemListener(new ViewMovie.OnClickItemListener()
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

                            searchAdapter.notifyDataSetChanged();
                        }
                    }
                }
            };
            if(getActivity()!=null)
                searchMoviesViewModel.searchMoviesLiveData(searchMovieParams).observe(getActivity(),observer);
        }
    }

    private void getTopRatedMovie()
    {
        if(getActivity()!=null)
        {
            final Context context=getActivity().getApplicationContext();
            topRatedMovieViewModel = ViewModelProviders.of(getActivity()).get(TopRatedMovieViewModel.class);
            Observer<TopRatedMovieModel> observer=new Observer<TopRatedMovieModel>()
            {
                @Override
                public void onChanged(@Nullable TopRatedMovieModel topRatedMovieModel)
                {
                    if(refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
                    if(topRatedMovieModel!=null)
                    {
                        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        topRatedAdapter=new MovieListAdapter(context).withTopRatedMovie(topRatedMovieModel);
                        rvMovieList.setLayoutManager(layoutManager);
                        rvMovieList.setAdapter(topRatedAdapter);
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
            };
            if(getActivity()!=null)
                topRatedMovieViewModel.topRatedMovieLiveData(topRatedMovieParams).observe(getActivity(),observer);
        }
    }

    private void getNowPlayingMovie()
    {
        if(getActivity()!=null)
        {
            final Context context=getActivity().getApplicationContext();
            nowPlayingMovieViewModel = ViewModelProviders.of(getActivity()).get(NowPlayingMovieViewModel.class);
            Observer<NowPlayingMovieModel> observer=new Observer<NowPlayingMovieModel>()
            {
                @Override
                public void onChanged(@Nullable NowPlayingMovieModel nowPlayingMovieModel)
                {
                    if(refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
                    if(nowPlayingMovieModel!=null)
                    {
                        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        nowPlayingAdapter=new MovieListAdapter(context).withNowPlayingMovie(nowPlayingMovieModel);
                        rvMovieList.setLayoutManager(layoutManager);
                        rvMovieList.setAdapter(nowPlayingAdapter);
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
            };
            if(getActivity()!=null)
                nowPlayingMovieViewModel.nowPlayingMovieLiveData(nowPlayingParams).observe(getActivity(),observer);
        }
    }

    private void getUpComingMovie()
    {
        if(getActivity()!=null)
        {
            final Context context=getActivity().getApplicationContext();
            upComingMovieViewModel = ViewModelProviders.of(getActivity()).get(UpComingMovieViewModel.class);
            Observer<UpComingMovieModel> observer=new Observer<UpComingMovieModel>()
            {
                @Override
                public void onChanged(@Nullable UpComingMovieModel upComingMovieModel)
                {
                    if(refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
                    if(upComingMovieModel!=null)
                    {
                        //Compare to get real movie upcoming release date from current date
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date currentDate = Calendar.getInstance().getTime();
                        Date compareDate = null;
                        ArrayList<ResultMovieModel> movieList=new ArrayList<>();

                        for(int i=0;i<upComingMovieModel.getMovieList().size();i++)
                        {
                            try
                            {
                                compareDate = simpleDateFormat.parse(upComingMovieModel.getMovieList().get(i).getRealeseDate());
                            } catch (ParseException e)
                            {
                                e.printStackTrace();
                            }

                            if (currentDate != null && compareDate != null)
                            {
                                if (currentDate.compareTo(compareDate) <= 0)
                                {
                                    movieList.add(upComingMovieModel.getMovieList().get(i));
                                }
                            }
                            compareDate = null;
                        }

                        Collections.sort(movieList,Collections.reverseOrder(ResultMovieModel.Comparators.RELEASE_DATE));
                        upComingMovieModel.setMovieList(movieList);


                        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        upComingAdapter=new MovieListAdapter(context).withUpComingMovie(upComingMovieModel);
                        rvMovieList.setLayoutManager(layoutManager);
                        rvMovieList.setAdapter(upComingAdapter);
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
            };
            if(getActivity()!=null)
                upComingMovieViewModel.upComingMovieLiveData(upComingMovieParams).observe(getActivity(),observer);
        }
    }

}
