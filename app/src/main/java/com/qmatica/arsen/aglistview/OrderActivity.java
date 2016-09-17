package com.qmatica.arsen.aglistview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import android.view.GestureDetector;
import android.view.MotionEvent;
//import android.view.GestureDetector.SimpleOnGestureListener;
//import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    private ArrayList<Order> orderList;     // including sections for adapter
    private ArrayList<Order> cleanList;     // combined list, no sections
    private OrderAdapter     adapter;
    private ListView         listView;
    DBManager                dbManager;

    SwipeGestureListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w( " Orders onCreate()", "called");

        Intent intent = this.getIntent();
        if(intent.getBooleanExtra(A.INTENT_FINISH, false))
        {
            finish();
            return;
        }

        setContentView(R.layout.order_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbManager = new DBManager(this, null, null, 1);
        cleanList = dbManager.getAllOrders();   // if database is empty, list is empty but not null
        recallQuantity(cleanList);                // set current quantities from last order

//      Combine with selected
        ArrayList<Product> productList;
        productList = intent.getParcelableArrayListExtra(A.PRODUCT_LIST);
        addSelectedProducts(cleanList, productList);

        if(cleanList.isEmpty())
            Toast.makeText(this, getResources().getString(R.string.error_no_order), Toast.LENGTH_LONG).show();

        orderList = addCategories(cleanList);

        adapter = new OrderAdapter(this, R.layout.order_item, orderList);
        listView = (ListView)findViewById(R.id.order_list_view);
        assert listView != null;
        listView.setAdapter(adapter);
        gestureListener = new SwipeGestureListener(OrderActivity.this);
        listView.setOnTouchListener(gestureListener);

        FloatingActionButton fab_add_products = (FloatingActionButton) findViewById(R.id.add_products);
        assert fab_add_products != null;
        fab_add_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton fab_debug_order = (FloatingActionButton) findViewById(R.id.debug_order);
        assert fab_debug_order != null;
        fab_debug_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debug_list();
            }
        });

        FloatingActionButton fab_submit = (FloatingActionButton) findViewById(R.id.submit_order);
        assert fab_submit != null;
        fab_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final UserProfile profile = dbManager.readProfile();

                AlertDialog.Builder alertSubmit;
                alertSubmit = new AlertDialog.Builder(OrderActivity.this);

                alertSubmit.setTitle(getResources().getString(R.string.title_submit_confirmation));
                alertSubmit.setMessage(getResources().getString(R.string.message_submit_confirmation) + " " + profile.getSendTo());

                alertSubmit.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(profile.getSendTo().isEmpty()) {
                            Toast.makeText(OrderActivity.this, getResources().getString(R.string.error_no_email_address), Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            resetCleanList();
                            if(cleanList.isEmpty())
                            {
                                Toast.makeText(OrderActivity.this, getResources().getString(R.string.error_empty_order), Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                submitOrder(dbManager.saveHistoryAndDetail(cleanList));
                            }
                        }
                    }
                });

//                alertSubmit.setPositiveButton("COMMENTS", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                    }
//                });

                alertSubmit.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alertSubmit.show();
            }
        });
    }   // end of onCreate()

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w( " Orders onNewIntent()", "called");
        if(intent.getBooleanExtra(A.INTENT_FINISH, false))
        {
            finish();
            return;
        }
        setIntent(intent);
        processIntentData();
    }

//    private boolean hasToFinish(Intent intent) {
//        return intent.getBooleanExtra(A.INTENT_FINISH, false);
//    }


    private void resetCleanList() {
        ArrayList<Order> list = new ArrayList<>();
        list.addAll(orderList);
        removeCategories(list);
        cleanList.clear();
        cleanList.addAll(list);
    }

    private void submitOrder(long orderNo) {

        resetCleanList();
        if(cleanList.isEmpty())
            return;

        UserProfile profile = dbManager.readProfile();

        SendMailMessage smm = new SendMailMessage(profile);
        smm.create(orderNo, orderList);

        int mode = profile.getMode();
        NetworkStatus network = new NetworkStatus(OrderActivity.this);

        if(mode == A.INTERNAL_MODE && network.isOn())
        {
            SendMail sm = new SendMail(this, smm);                          // TODO do not use without setForRollBack
            sm.setForRollBack(dbManager, adapter, orderList, cleanList);    // TODO for strategy pattern later
            sm.execute();
        }
        else if(mode == A.EXTERNAL_MODE || !network.isOn())
        {
            saveOrder();
            sendOrder(smm);
        }
    }

    private void saveOrder() {
        rollQuantity(orderList);
        adapter.notifyDataSetChanged();
        dbManager.deleteAllOrders();
        dbManager.insertAllOrders(cleanList);
    }

    private void sendOrder(SendMailMessage smm) {
        Intent email = new Intent(Intent.ACTION_SEND, Uri.parse("mailto:"));

        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[] {smm.getSendTo()});
        email.putExtra(Intent.EXTRA_CC, new String[] {smm.getCopyTo()});
        email.putExtra(Intent.EXTRA_SUBJECT, smm.getSubject());
        email.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(smm.getMessage()));
        try {
//            Intent intent = new Intent(OrderActivity.this, ActivityMain.class);
//            startActivity(intent);
//            finish();
            startActivity(Intent.createChooser(email, getResources().getString(R.string.choose_email_client)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(OrderActivity.this, getResources().getString(R.string.error_no_email_client), Toast.LENGTH_LONG).show();
        }
    }



    public void processIntentData() {
        Intent intent = getIntent();
        ArrayList<Product> productList = intent.getParcelableArrayListExtra(A.PRODUCT_LIST);

        ArrayList<Order> list;         // TODO get rid of extra list
        if (addSelectedProducts(cleanList, productList)) {
            list = addCategories(cleanList);
            orderList.clear();
            orderList.addAll(list);
            adapter.notifyDataSetChanged();
        }
    }

    private boolean addSelectedProducts(ArrayList<Order> list, ArrayList<Product> productList) {
        boolean result = false;
        if(productList != null) {
            for (Product product : productList) {
                Order order = new Order(product);
                if(!exists(order, list)) {                  // do not add if already in the list
                    list.add(order);
                    result = true;
                }
            }
        }
        return result;
    }

    private boolean exists(Order newOrder, ArrayList<Order> list) {
        for(Order order : list) {
            if(order.getProductName().equals(newOrder.getProductName()) && order.getUnit().equals(newOrder.getUnit())){
                return true;
            }
        }
        return false;
    }

    private ArrayList<Order> addCategories(ArrayList<Order> list) {
//      Group combined list by category
        Map<String, ArrayList<Order>> map = new HashMap<>();

        for (Order order : list) {
            String key = order.getCategory();
            if (map.get(key) == null) {
                map.put(key, new ArrayList<Order>());
            }
            map.get(key).add(order);
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

        Order section;
        ArrayList<Order> orders = new ArrayList<Order>();
        for (ProductCategory productCategory : categories) {
            String category = productCategory.getCategory();
            section = new Order(category, A.TYPE_SECTION);
            orders.add(section);
            ArrayList<Order> orderSectionList = map.get(category);
            for (Order order : orderSectionList) {
                order.setType(A.TYPE_ITEM);
            }
            orders.addAll(orderSectionList);
        }
        section = new Order("", A.TYPE_SECTION);                    // empty placeholder at the end of the list to make room for buttons
        orders.add(section);
        section = new Order("", A.TYPE_SECTION);                    // empty placeholder at the end of the list to make room for buttons
        orders.add(section);
        return orders;
    }


    public void increaseQuantity(View view) {
        int position = listView.getPositionForView(view);
        Order order = orderList.get(position);
        order.increaseQuantity();
        adapter.notifyDataSetChanged();
    }

    public void decreaseQuantity(View view) {
        int position = listView.getPositionForView(view);
        Order order = orderList.get(position);
        order.decreaseQuantity();
        adapter.notifyDataSetChanged();
    }

    public void deleteOrderFromView(View view) {                        // TODO combine method with deleteSwiped
        int position = listView.getPositionForView(view);
        gestureListener.removeOrder(position);
    }


    public void removeCategories(ArrayList<Order> list) {
        Iterator<Order> iterator = list.iterator();
        while(iterator.hasNext()) {
            Order order = iterator.next();
            if(order.getType() == A.TYPE_SECTION) {
                iterator.remove();
            }
        }
    }
    private void rollQuantity(ArrayList<Order> list) {
        for(Order order : list) {
            order.roll();
            order.setID(-1);         // TODO not 0 to change color
        }
    }

    private void recallQuantity(ArrayList<Order> list) {
        for(Order order : list) {
            order.recall();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//      Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.order_menu, menu);
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

    public void finishOrderActivity(MenuItem item) {
        Intent intent;
        intent = new Intent(OrderActivity.this, ProductActivity.class);
        intent.putExtra(A.INTENT_FINISH, true);
        startActivity(intent);
        intent = new Intent(OrderActivity.this, ActivityMain.class);
        startActivity(intent);
        finish();
    }



//  debug methods
    public void debug_list() {
        dbManager.deleteAllOrders();
        dbManager.deleteAllHistoryAndDetail();
    }
    public void debug_categories() {

        Log.w(" Debugging", String.valueOf(ProductList.categories().size()) + " categories...");
        for(ProductCategory c : ProductList.categories()) {
            Log.w("  category", String.valueOf(c.getID() + " " + c.getCategory()));
        }
    }

    public void debug_orderList(ArrayList<Order> l) {
        Log.w(" Debugging", " orderList...");
        if(l != null) {
            for(Order o:l) {
                Log.w("   list", String.valueOf(o.getID()) + " " + o.getProductName() + " " + o.getUnit()+ " " + o.getCategory() + " " +
                        String.valueOf(o.getQuantity(1)) + " " +
                        String.valueOf(o.getQuantity(2)) + " " +
                        String.valueOf(o.getQuantity(3)) + " " +
                        String.valueOf(o.getQuantity(4)));
            }
        }
        else
            Log.w(" orderList is null", "");
    }

    public void debug_productList(ArrayList<Product> pl) {
        Log.w(" Debugging", " productList...");

        ArrayList<Order> l = new ArrayList<>();
        if(pl != null) {
            for (Product p : pl) {
                Order o = new Order(p);
                l.add(o);
            }
        }
        else {
            Log.w(" productList is null", "");
        }
        if(l.size() != 0) {
            for(Order o:l) {
                Log.w("   list", String.valueOf(o.getID()) + " " + o.getProductName() + " " + o.getUnit()+ " " + o.getCategory() + " " +
                        String.valueOf(o.getQuantity(1)) + " " +
                        String.valueOf(o.getQuantity(2)) + " " +
                        String.valueOf(o.getQuantity(3)) + " " +
                        String.valueOf(o.getQuantity(4)));
            }
        }
        else
            Log.w(" orderList is empty", "");
    }

    // end of debugging methods



    public class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

        static final int SWIPE_MIN_DISTANCE         = 120;
        static final int SWIPE_MAX_OFF_PATH         = 250;
        static final int SWIPE_THRESHOLD_VELOCITY   = 200;

        Context context;
        GestureDetector gDetector;
//        ListView listView;

        public SwipeGestureListener() {
            super();
        }

        public SwipeGestureListener(Context context) {
            this.context = context;
//            this.listView = listView;
            if (gDetector == null) {
                gDetector = new GestureDetector(context, this);
            }
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            int position = listView.pointToPosition(Math.round(e1.getX()), Math.round(e1.getY()));
            Order order = (Order) listView.getItemAtPosition(position);
            String name = order.getProductName();

            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH || Math.abs(velocityY) < SWIPE_THRESHOLD_VELOCITY)
                {
                    return false;
                }
//                if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE) {
//                    Toast.makeText(context, "bottomToTop: " + name, Toast.LENGTH_LONG).show();
//                } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE) {
//                    Toast.makeText(context, "topToBottom: " + name, Toast.LENGTH_LONG).show();
//                }
            }
            else
            {
                if (Math.abs(velocityX) < SWIPE_THRESHOLD_VELOCITY) {
                    return false;
                }
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {
//                    Toast.makeText(context, "swipe RightToLeft: " + name, Toast.LENGTH_LONG).show();
                    removeOrder(position);
                }
//                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE)
//                {
////                    Toast.makeText(context, "swipe LeftToright: " + name, Toast.LENGTH_LONG).show();
////                    removeOrder(position);
//                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            return gDetector.onTouchEvent(event);
        }

        public GestureDetector getDetector() {
            return gDetector;
        }

        public void removeOrder(int position) {
            final Order order = orderList.get(position);

            AlertDialog.Builder alertDelete = new AlertDialog.Builder(context);
            alertDelete.setTitle("Remove " + order.getProductName() + " " + order.getUnit() + " from the order list?");

            alertDelete.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    dbManager.deleteOrder(order);
//                    orderList.remove(position);

                    int i = cleanList.indexOf(order);
                    if(i != -1)
                        cleanList.remove(i);
                    ArrayList<Order> list = addCategories(cleanList);
                    orderList.clear();
                    orderList.addAll(list);
                    adapter.notifyDataSetChanged();
                }
            });

            alertDelete.setNegativeButton("CANCEL",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

            alertDelete.show();
        }

    }



}

/*
//        debug_orderList(list);
//        debug_categories();
//        debug_orderList(orderList);
//        debug_productList(productList);



        Log.w(" Username", profile.getUsername());
        Log.w(" Password", profile.getPassword());
        Log.w(" email", email);
        Log.w(" message", message);


*/

//    private void finishProductActivity() {
//        Intent intent = new Intent(OrderActivity.this, ProductActivity.class);
//        intent.putExtra(A.INTENT_FINISH, true);
//        startActivity(intent);
//    }

