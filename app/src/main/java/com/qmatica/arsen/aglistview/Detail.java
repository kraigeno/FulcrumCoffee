package com.qmatica.arsen.aglistview;

public class Detail {
    private long ID;
    private long orderID;
    private String productName;
    private String category;
    private String unit;
    private int quantity;
    private int type;

    public Detail ()
    {
        this.type = A.TYPE_ITEM;
    }

    public Detail(long ID, long orderID, String productName, String category, String unit, int quantity) {
        this.ID = ID;
        this.orderID = orderID;
        this.productName = productName;
        this.category = category;
        this.unit = unit;
        this.quantity = quantity;
        this.type = A.TYPE_ITEM;
    }

//  for SECTION header
    public Detail(String productName, int type) {
        this.productName = productName;
        this.category = productName;
        this.type = type;
    }


    public long getID()
    {
        return this.ID;
    }
    public long getOrderID()
    {
        return this.orderID;
    }
    public String getProductName()
    {
        return this.productName;
    }
    public String getCategory()
    {
        return this.category;
    }
    public String getUnit()
    {
        return this.unit;
    }
    public int getQuantity()
    {
        return this.quantity;
    }
    public int getType() {
        return this.type;
    }
    public boolean isSection()
    {
        return (this.type == A.TYPE_SECTION);
    }
    public void setType(int type) {
        this.type = type;
    }
}
