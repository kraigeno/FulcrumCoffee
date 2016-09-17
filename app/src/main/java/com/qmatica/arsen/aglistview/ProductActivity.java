package com.qmatica.arsen.aglistview;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.SubMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private ProductAdapter adapter;
    private ListView listView;
    private SearchView searchView;

    private ArrayList<Product> productList;
    private ArrayList<Product> checkedList;     // pass selected products to OrderActivity
    private ArrayList<Product> cleanedList;     // stripped of category headers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w(" Products OnCreate", "Called");

        Intent intent = this.getIntent();
        if(intent.getBooleanExtra(A.INTENT_FINISH, false))
        {
            finish();
            return;
        }

        setContentView(R.layout.product_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productList = ProductList.products();

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        assert addButton != null;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
// TODO ? is new list necessary
// TODO productList is short when in "searching" mode
                checkedList = new ArrayList<>();
                for (Product product : productList) {
                    if (product.isChecked()) {
                        checkedList.add(product);
                    }
                }

                Intent intent = new Intent(ProductActivity.this, OrderActivity.class);
                intent.putParcelableArrayListExtra(A.PRODUCT_LIST, checkedList);
                startActivity(intent);

            }
        });

        listView = (ListView)findViewById(R.id.product_list_view);
        adapter = new ProductAdapter(this, R.layout.product_item, productList);
        listView.setAdapter(adapter);


// TODO this listener is not used
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {

                Product product = productList.get(position);

//                Log.w(product.getCategory(), " : " + String.valueOf(position));
//                if(product.isCheckable()) {
//                    product.toggle();
//                    adapter.notifyDataSetChanged();
//                }
            }

        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
        Log.w(" Products onNewIntent", "called");
        if(intent.getBooleanExtra(A.INTENT_FINISH, false))
        {
            finish();
            return;
        }

        uncheckList();      // TODO do not uncheck if coming from OrderActivity
        resetSearch();
    }


    @Override
    protected void onSaveInstanceState(Bundle state) {
//        state.putParcelableArrayList(A.NAME_PRODUCTS_STATE, productList);
        super.onSaveInstanceState(state);
    }

    public void checkItem(View view) {
        int position = listView.getPositionForView(view);
        Product product = productList.get(position);
        if(product.isUnitCheckable()) {
            checkUnit(view);
        }
        else {
            checkCase(view);
        }
    }

    private void uncheckList() {
        for(Product product : productList) {
            product.unCheck();
        }
    }
    public void checkUnit(View view) {
        int position = listView.getPositionForView(view);
        Product product = productList.get(position);
        if(product.isUnitCheckable())
        {
            if(product.isCaseChecked()) {
                product.setUnitChecked(true);
                product.setCaseChecked(false);
            }
            else {
                product.toggle();
                product.setUnitChecked(product.isChecked());
            }
            adapter.notifyDataSetChanged();
        }
        else
            Snackbar.make(view, getString(R.string.unit_NA), Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
    public void checkCase(View view) {
        int position = listView.getPositionForView(view);
        Product product = productList.get(position);
        if(product.isCaseCheckable())
        {
            if(product.isUnitChecked()) {
                product.setCaseChecked(true);
                product.setUnitChecked(false);
            }
            else {
                product.toggle();
                product.setCaseChecked(product.isChecked());
            }
            adapter.notifyDataSetChanged();
        }
        else
            Snackbar.make(view, getString(R.string.case_NA), Snackbar.LENGTH_LONG).setAction("Action", null).show();

    }

    private void resetSearch () {
        adapter.resetSearchFilter();
        searchView.setQuery("", false);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.product_menu, menu);

        SubMenu categoryMenu = menu.findItem(R.id.action_categories).getSubMenu();

        int groupID = A.CATEGORY_GROUP_ID;

        categoryMenu.clear();
        categoryMenu.add(groupID, A.CATEGORY_ID, Menu.NONE, getResources().getString(R.string.all_categories));

        for(ProductCategory category : ProductList.categories()) {
            categoryMenu.add(groupID, category.getID(), Menu.NONE, category.getCategory());   // offset category ID from ALL PRODUCTS=0
        }

        MenuItem searchItem = menu.findItem(R.id.action_search);

// class scope var
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_hint));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setBackgroundColor(A.SEARCH_BG_COLOR);
        EditText et = (EditText) searchView.findViewById(R.id.search_src_text);        // TODO hack search widget, use theme instead
        et.setTextColor(A.SEARCH_COLOR);
        et.setHintTextColor(A.SEARCH_HINT_COLOR);
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
//        closeButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));  // TODO ContextCompat.getColor(context, R.color.color_name)
        closeButton.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.setSearchFilter(query.trim());
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        int groupID = item.getGroupId();

        Log.w(" Item "+String.valueOf(id), item.getTitle() + " Group ID "+String.valueOf(item.getGroupId()));
        if(groupID == A.CATEGORY_GROUP_ID) {
            resetSearch();
            filterByCategory(item.getTitle().toString());
        }

//      noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void filterByCategory(String category) {

        if(category.equals(getResources().getString(R.string.all_categories))) {
            adapter.resetCategoryFilter();
        }
        else {
            adapter.setCategoryFilter(category);
        }

        Log.w(" Category", category);

    }

    public void finishProductActivity(MenuItem item) {
        Intent intent = new Intent(ProductActivity.this, ActivityMain.class);
        startActivity(intent);
        finish();
    }

}

//                if(checkedList.isEmpty() && dbManager.getOrderCount() == 0)
//                {
//                    Toast.makeText(ProductActivity.this, "Please select products to add to the first order", Toast.LENGTH_LONG).show(); // TODO
//                }

