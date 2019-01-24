package com.example.habilmahendri.kamusproject.activity;


import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;

import com.example.habilmahendri.kamusproject.R;
import com.example.habilmahendri.kamusproject.adapter.KamusAdapter;
import com.example.habilmahendri.kamusproject.db.KamusHelper;
import com.example.habilmahendri.kamusproject.model.KamusModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements
        NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer_layout;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.recyclerview)
    RecyclerView recycler_view;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private ArrayList<KamusModel> dataKamus = new ArrayList<>();
    private KamusHelper kamusHelper;
    private KamusAdapter kamusAdapter;
    private boolean english = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer_layout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        kamusHelper = new KamusHelper(this);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchValue = searchView.getQuery().toString();
                loadData(String.valueOf(searchValue));
            }
        });

        initView();
        loadData();
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initView() {
        kamusAdapter = new KamusAdapter(getApplicationContext());
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        recycler_view.setAdapter(kamusAdapter);
    }

    private void loadData(String search) {
        try {
            kamusHelper.open();
            if (search.isEmpty()) {
                dataKamus = kamusHelper.getAllData(english);
            } else {
                dataKamus = kamusHelper.getDataByName(search, english);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            kamusHelper.close();
        }
        kamusAdapter.change(dataKamus);
    }

    private void loadData() {
        loadData("");
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.english_indonesia) {
            english = true;
            searchView.setQueryHint("find");
            btnSearch.setText("Search");
            loadData();
        }
        if (id == R.id.indonesia_english) {
            english = false;
            searchView.setQueryHint("temukan");
            btnSearch.setText("cari");
            loadData();
        }
        drawer_layout.closeDrawer(GravityCompat.START);
        return true;
    }


}
