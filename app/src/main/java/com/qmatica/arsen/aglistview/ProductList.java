package com.qmatica.arsen.aglistview;

import android.content.Context;

import java.io.InputStream;
import java.util.ArrayList;


public class ProductList {
    private static ArrayList<Product> productList;
    private static ArrayList<ProductCategory> categoryList;

    private ProductList() {}        // singleton

    static
    {
        // do stuff at compile
    }

    public static ArrayList<Product> products() {
        ArrayList<Product> list = new ArrayList<>();
        list.addAll(productList);
        return list;
    }
    public static ArrayList<ProductCategory> categories() {
//        ArrayList<ProductCategory> list = new ArrayList<>();
//        list.addAll(categoryList);
        return categoryList;
    }

    public static void create(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.products);
        ProductFile file = new ProductFile(inputStream);
        file.read();
        productList = file.getProductList();
        categoryList = file.getCategoryList();
    }
}
