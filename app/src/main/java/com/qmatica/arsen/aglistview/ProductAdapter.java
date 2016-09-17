package com.qmatica.arsen.aglistview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ProductAdapter extends ArrayAdapter <Product> {
    private Context context;
    private int resource;
    private ArrayList<Product> productList;
    private ArrayList<Product> backupList;

    public ProductAdapter(Context context, int resource, ArrayList<Product> list) {
        super(context, resource, list);
        this.context = context;
        this.resource = resource;
        this.productList = list;
        backupList = new ArrayList<Product>();      // complete original list of Products
        backupList.addAll(list);
    }

//    @Override
//    public int getCount() {
//        return list.size();
//    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isSection()) {
            return A.TYPE_SECTION;
        }
        return A.TYPE_ITEM;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        Product product = productList.getOrder(position);
        Product product = getItem(position);
        int type = product.getType();

        if(convertView == null)
            switch (type) {
                case A.TYPE_SECTION:
                    convertView = new ProductSectionView(getContext());
                    break;
                case A.TYPE_ITEM:
                    convertView = new ProductView(getContext());
                    break;
            }

        switch (type) {
            case A.TYPE_SECTION:
                ProductSectionView productSectionView = (ProductSectionView) convertView;
                productSectionView.setProduct(product);
                break;
            case A.TYPE_ITEM:
                ProductView productView = (ProductView) convertView;
                productView.setProduct(product);
                productView.setChecked(product.isChecked());
                productView.setUnitChecked(product.isUnitChecked());
                productView.setCaseChecked(product.isCaseChecked());
                break;
        }

        return convertView;
    }

    public void setSearchFilter(CharSequence constraint) {

        String s = constraint.toString().toLowerCase();

        productList.clear();

        if (constraint != null && constraint.length() > 0) {
            for (Product product : backupList) {
                String name = product.getName().toLowerCase();
                String category = product.getCategory().toLowerCase();
//                if (name.startsWith(s) || category.contains(s)) {
                if (name.contains(s) || category.contains(s)) {
                    productList.add(product);
                }
            }
            ArrayList<Product> list = sortedList(productList);      // TODO too many lists
            productList.clear();
            productList.addAll(list);
        }
        else {
            productList.addAll(backupList);
        }
        notifyDataSetChanged();
    }

    public void resetSearchFilter() {
        productList.clear();
        productList.addAll(backupList);
    }

    public void setCategoryFilter(String category) {

        productList.clear();
        for(Product product:backupList) {
            if(product.getCategory().equals(category) || product.getName().equals(category)) {
                productList.add(product);
            }
        }
        notifyDataSetChanged();
    }

    public void resetCategoryFilter() {
        productList.clear();
        productList.addAll(backupList);
        notifyDataSetChanged();
    }


    private ArrayList<Product> sortedList (ArrayList<Product> list) {

        Map<String, ArrayList<Product>> map = new HashMap<>();

        ArrayList<Product> cleanList = new ArrayList<>();
        for(Product product : list) {
            if(product.getType() == A.TYPE_ITEM) {
                cleanList.add(product);
            }
        }

        for (Product product : cleanList) {
            String key = product.getCategory();
            if (map.get(key) == null) {
                map.put(key, new ArrayList<Product>());
            }
            map.get(key).add(product);
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

        ArrayList<Product> products = new ArrayList<Product>();
        for (ProductCategory productCategory : categories) {
            String category = productCategory.getCategory();
            Product section = new Product(category, A.TYPE_SECTION);
            products.add(section);
            ArrayList<Product> productSectionList = map.get(category);
            for (Product product : productSectionList) {
                product.setType(A.TYPE_ITEM);
            }
            products.addAll(productSectionList);
        }
        return products;
    }

}

