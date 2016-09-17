package com.qmatica.arsen.aglistview;

import android.os.Parcel;
import android.os.Parcelable;

public class Order implements Parcelable {

    private static final int MAX_INDEX = 5;
    private int id = 0; // new order from selected products (not from db)
    private String productName;
    private String category;
    private String unit;
    private int[] quantity = new int[] {1, 0, 0, 0, 0};     // quantity[0] stores currently selectable quantity
    private int type;

    // required Parcelable methods
    public int describeContents() {
        return this.hashCode();
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(productName);
        parcel.writeString(category);
        parcel.writeString(unit);
        parcel.writeInt(quantity[0]);
        parcel.writeInt(quantity[1]);
        parcel.writeInt(quantity[2]);
        parcel.writeInt(quantity[3]);
        parcel.writeInt(quantity[4]);
        parcel.writeInt(type);
    }

    public Order(Parcel parcel) {
        productName = parcel.readString();
        category = parcel.readString();
        unit = parcel.readString();
        quantity[0] = parcel.readInt();
        quantity[1] = parcel.readInt();
        quantity[2] = parcel.readInt();
        quantity[3] = parcel.readInt();
        quantity[4] = parcel.readInt();
        type = parcel.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Order createFromParcel(Parcel parcel)
        {
            return new Order(parcel);
        }
        public Order[] newArray(int size)
        {
            return new Order[size];
        }
    };

//  for DB
    public Order () {
        this.type = A.TYPE_ITEM;
    }
    public Order(String productName, String category, String unit, int quantity1, int quantity2, int quantity3, int quantity4) {
        this.productName = productName;
        this.category = category;
        this.unit = unit;
        this.quantity[1] = quantity1;
        this.quantity[2] = quantity2;
        this.quantity[3] = quantity3;
        this.quantity[4] = quantity4;
        this.quantity[0] = quantity4;   // copy from the last order
    }

    public Order(Product product) {
        super();
        this.productName = product.getName();
        this.category = product.getCategory();
        this.unit = "";
        if(product.isUnitChecked()) {
            this.unit = product.getUnitSize();
        }
        else if(product.isCaseChecked()) {
            this.unit = product.getUnitSize() + " [" + product.getCaseSize() + "]";
        }
        this.type = A.TYPE_ITEM;
    }

//  for SECTION header
    public Order(String productName, int type) {
        this.productName = productName;
        this.category = productName;
        this.type = type;
        this.quantity[0] = 0;   // set specifically to avoid rolling quantities in the section header row
    }

    public void setID(int id)
    {
        this.id = id;
    }
    public int getID()
    {
        return this.id;
    }
    public void setProductName(String productName)
    {
        this.productName = productName;
    }
    public String getProductName()
    {
        return this.productName;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }
    public String getCategory()
    {
        return this.category;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }
    public String getUnit()
    {
        return this.unit;
    }

    public void setQuantity(int quantity)
    {
        this.quantity[0] = quantity;
    }
    public int getQuantity()
    {
        return this.quantity[0];
    }

    public void setQuantity(int i, int quantity)
    {
        this.quantity[i] = quantity;
    }
    public int getQuantity(int i)
    {
        return (i<MAX_INDEX) ? quantity[i] : 0;
    }

    public void setType(int type)
    {
        this.type = type;
    }
    public int getType()
    {
        return this.type;
    }

    public boolean isSection()
    {
        return (type == A.TYPE_SECTION);
    }

    public void increaseQuantity() {
        this.quantity[0] += 1;
    }
    public void increaseQuantity(int i) {
        this.quantity[i] += 1;
    }

    public void decreaseQuantity() {
        if (this.quantity[0] > 0) {
            this.quantity[0] -= 1;
        }
    }
    public void decreaseQuantity(int i) {
        if (this.quantity[i] > 0) {
            this.quantity[i] -= 1;
        }
    }

    public void roll() {
        for(int i=1; i<MAX_INDEX-1; i++) {
            quantity[i] = quantity[i+1];
        }
        quantity[4] = quantity[0];  // quantity[0] goes to the most recent order
    }
    public void recall() {
        quantity[0] = quantity[4];
    }

}
