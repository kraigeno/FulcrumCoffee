package com.qmatica.arsen.aglistview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ProductFile {

    static int idx_name = 0;
    static int idx_unit = 1;
    static int idx_unitPrice = 2;
    static int idx_case = 3;
    static int idx_casePrice = 4;
    static int idx_type = 5;

    InputStream inputStream;

    ArrayList<Product> productList = new ArrayList<>();
    ArrayList<ProductCategory> categoryList = new ArrayList<>();

    public ProductFile(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    public void read(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            String category = "";
            int i = A.CATEGORY_ID_OFFSET;   // create IDs for ProductCategory. Start with offset 1

            productList.clear();
            categoryList.clear();

            while ((line = reader.readLine()) != null) {
                String[] item = line.split("\t");
                int type = Integer.parseInt(item[idx_type]);
                if(type == A.TYPE_SECTION) {
                    category = item[idx_name];
                    ProductCategory productCategory = new ProductCategory(i++, category);   // i++ for hashmap sorting
                    categoryList.add(productCategory);
                }
                Product product = new Product(item[idx_name], category, item[idx_unit], Double.parseDouble(item[idx_unitPrice]), item[idx_case], Double.parseDouble(item[idx_casePrice]), Integer.parseInt(item[idx_type]));
                productList.add(product);
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Error in reading CSV file: " + e);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
    }
    public ArrayList<Product> getProductList() {
        return productList;
    }

    public ArrayList<ProductCategory> getCategoryList() {
        return categoryList;
    }
}
