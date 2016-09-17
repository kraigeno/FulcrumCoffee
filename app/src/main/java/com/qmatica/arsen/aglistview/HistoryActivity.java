package com.qmatica.arsen.aglistview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private History history;         // id and timestamp of the currently displayed order
    private int position;
    private int historyCount;
    private ArrayList<History> historyList;
    private ArrayList<Detail> cleanList;
    private ArrayList<Detail> detailList;

    private HistoryAdapter adapter;
    private ListView listView;
    DBManager dbManager;

    FloatingActionButton buttonNext;
    FloatingActionButton buttonPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w( " History onCreate()", "called");

        Intent intent = this.getIntent();
        if(intent.getBooleanExtra(A.INTENT_FINISH, false))
        {
            finish();
            return;
        }

        setContentView(R.layout.history_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        buttonNext = (FloatingActionButton) findViewById(R.id.history_next);
        assert (buttonNext != null);

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position < historyCount - 1) {
                    position++;
                    displayHistory(position);
                }
            }
        });

        buttonPrev = (FloatingActionButton) findViewById(R.id.history_prev);
        assert (buttonPrev != null);

        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(position > 0) {
                    position--;
                    displayHistory(position);
                }

            }
        });

        FloatingActionButton buttonDebug = (FloatingActionButton) findViewById(R.id.history_debug);
        assert (buttonDebug != null);

        buttonDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debug_historyList();
            }
        });

        dbManager = new DBManager(this, null, null, 1);
        historyList = dbManager.getAllHistory();
        historyCount = dbManager.getHistoryCount();

        detailList = new ArrayList<>();
        adapter = new HistoryAdapter(this, R.layout.detail_item, detailList);
        listView = (ListView)findViewById(R.id.history_list_view);
        assert listView != null;
        listView.setAdapter(adapter);

        if (!historyList.isEmpty()) {
            position = historyCount-1;  // last record
            displayHistory(position);
        }
        else
        {
            Toast.makeText(this, getResources().getString(R.string.error_no_history), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w( " History onNewIntent()", "called");
        if(intent.getBooleanExtra(A.INTENT_FINISH, false))
        {
            finish();
            return;
        }
        setIntent(intent);
    }


    private void displayHistory(int position) {
        history = historyList.get(position);
        setTitle(getResources().getString(R.string.title_history_activity) + "                " + String.format(Locale.US, "%05d", history.getID()) + ".  " + history.getDateTime());
        cleanList = dbManager.getDetail(history.getID());
        ArrayList<Detail> list = addCategories(cleanList);
        detailList.clear();
        detailList.addAll(list);
        adapter.notifyDataSetChanged();
        buttonNext.setVisibility((position == historyCount-1) ? View.INVISIBLE : View.VISIBLE);
        buttonPrev.setVisibility((position == 0) ? View.INVISIBLE : View.VISIBLE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<Detail> addCategories(ArrayList<Detail> list) {
        Map<String, ArrayList<Detail>> map = new HashMap<>();

        for (Detail detail : list) {
            String key = detail.getCategory();
            if (map.get(key) == null) {
                map.put(key, new ArrayList<Detail>());
            }
            map.get(key).add(detail);
        }

        ArrayList<String> keySet = new ArrayList<>(map.keySet());    // sorted alphabetically

        ArrayList<ProductCategory> categories = new ArrayList<>();   // assign ID as in CATEGORIES

        for (String key : keySet) {
            for(ProductCategory productCategory : ProductList.categories()) {
                if(key.equals(productCategory.getCategory())) {
                    categories.add(productCategory);
                }
            }
        }
        Collections.sort(categories);                                // sorted in CATEGORIES order

        Detail section;
        ArrayList<Detail> details = new ArrayList<Detail>();
        for (ProductCategory productCategory : categories) {
            String category = productCategory.getCategory();
            section = new Detail(category, A.TYPE_SECTION);
            details.add(section);
            ArrayList<Detail> detailSectionList = map.get(category);
            for (Detail detail : detailSectionList) {
                detail.setType(A.TYPE_ITEM);
            }
            details.addAll(detailSectionList);
        }
        section = new Detail("", A.TYPE_SECTION);                    // empty placeholder at the end of the list to make room for buttons
        details.add(section);
        section = new Detail("", A.TYPE_SECTION);                    // empty placeholder at the end of the list to make room for buttons
        details.add(section);

        return details;
    }



    public void finishHistoryActivity(MenuItem item) {
     // TODO finish other activities?
        Intent intent = new Intent(HistoryActivity.this, ActivityMain.class);
        startActivity(intent);
        finish();
    }


//  debugging methods
    public void debug_historyList() {

        ArrayList<History> list = dbManager.getAllHistory();

        int size = list.size();
        String str = (size < 2) ? " order" : " orders";
        Toast.makeText(HistoryActivity.this, String.valueOf(size) + str, Toast.LENGTH_LONG).show();

//        Log.w(" Current count", String.valueOf(historyCount));
//        Log.w(" Current record", String.valueOf(history.getID()));
//
//        ArrayList<History> hlist = dbManager.getAllHistory();
//
//        for(History h:hlist) {
//            int id = h.getID();
//            Log.w("   History", String.valueOf(h.getID()) + " " + h.getDateTime());
//            ArrayList<Detail> dlist = dbManager.getDetail(id);
//
//            for(Detail d : dlist) {
//                Log.w("   Order", String.valueOf(d.getOrderID()) + "  Detail "+ String.valueOf(d.getID()) + " " + d.getProductName());
//            }
//
//        }
    }



}
