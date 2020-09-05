package com.example.coursework;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.coursework.api.ApiClient;
import com.example.coursework.api.ApiInterface;
import com.example.coursework.models.Article;
import com.example.coursework.models.News;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The main activity for creating the application.
 * Creates all the components to be used, including the recycler view, the articles to be
 * placed inside of it, the refresh method, the search bar and the categories spinner.
 *
 */

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemSelectedListener {

    public static final String API_KEY = "69ef40c575e442f9b5bd841f1446089f";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static File NEWS_FILE;
    String category = "headlines";

    //The main method creating and setting up the application, including the recycler view,
    //the refresh method, and the spinner.
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Set up the refresh method.
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        //Set up the recycler view.
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        //Set up the spinner and add the strings to them.
        Spinner mySpinner = findViewById(R.id.spinner);
        mySpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> myAdapter = ArrayAdapter.createFromResource(this, R.array.news, android.R.layout.simple_spinner_item);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        onLoadingSwipeRefresh("");

    }

    //Loads the JSON data from the API relevant to what the search categories are.
    public void loadJSON(final String keyword, final String category ){

        swipeRefreshLayout.setRefreshing(true);

        this.category = category;

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String country = Utils.getCountry();

        Call<News> call;

        //Loading the relevant category information
        if (category.equals("")) {
            if (keyword.length() > 0) {
                call = apiInterface.getNewsSearch(keyword, API_KEY);
            } else {
                call = apiInterface.getNews(country, API_KEY);
            }
        }else if(category.equals("headlines")) {
            call = apiInterface.getNews(country, API_KEY);
        }else if(!category.equals("")){
            call = apiInterface.getSpecificSearch(country, category, API_KEY);
        }else{
            this.category = "headlines";
            call = apiInterface.getNews(country, API_KEY);
        }

        //Getting the news articles from the api.
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticle() != null){
                    saveGSON(response.body());
                    if (articles.isEmpty()){
                        articles.clear();
                    }

                    articles = response.body().getArticle();
                    adapter = new Adapter(articles, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initListener();

                    swipeRefreshLayout.setRefreshing(false);

                }else{
                    //If no news articles match the criteria given
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "No news matching criteria found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                News n = loadGSON();
                if (n != null){
                    Toast.makeText(MainActivity.this, "Cached News, No Connection", Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }



    private void saveGSON(News body){
        try{
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(NEWS_FILE, category+".json")), "utf-8"));
            writer.write(new Gson().toJson(body));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private News loadGSON(){
        try{
            NEWS_FILE = getCacheDir();
            Reader reader = new BufferedReader((new InputStreamReader(new FileInputStream(new File(NEWS_FILE, category+".json")), "utf-8")));
            String in = ((BufferedReader) reader).readLine();
            News n = new Gson().fromJson(in, News.class);
            return n;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Finds the item selected in the spinner, to then load the relevant category.
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        String item = parent.getItemAtPosition(pos).toString().toLowerCase();
        loadJSON("", item);
    }

    //If nothing has been selected, just load the JSon as normal.
    public void onNothingSelected(AdapterView<?> parent){
        loadJSON("", "");
    }

    //When the listener is triggered, open the web browser.
    private void initListener(){
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Article article = articles.get(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(article.getUrl()));
                startActivity(browserIntent);
            }
        });
    }

    //Creating the search bar options menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            //When the text has been submitted by entering text into the search bar and then submitting.
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadJSON(query, "");
                return false;
            }

            //Override method needed for when the text changes - nothing happens due to search results not needing to
            //change until the user has stopped typing and hits the submit button on their keyboard.
            @Override
            public boolean onQueryTextChange(String newText){
                return false;
            }

        });

        searchMenuItem.getIcon().setVisible(false, false );

        return true;
    }

    //When the refresh item has been triggered, it reloads the top headlines.
    @Override
    public void onRefresh(){
        loadJSON("", "");
    }
    private void onLoadingSwipeRefresh(final String keyword){
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadJSON(keyword, "");
                    }
                }
        );

    }

}
