package com.qmatica.arsen.aglistview;


public class ProductCategory implements Comparable<ProductCategory> {
    private int id;
    private String category;

    ProductCategory(int id, String category) {
        this.id = id;
        this.category = category;
    }

    public int getID()
    {
        return id;
    }
    public String getCategory()
    {
        return category;
    }

    @Override
    public int compareTo(ProductCategory productCategory) {
        return (this.id - productCategory.getID());
    }
}
