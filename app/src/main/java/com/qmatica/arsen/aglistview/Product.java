package com.qmatica.arsen.aglistview;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String name;
    private String category;
    private String unitSize;
    private double unitPrice;
    private String caseSize;
    private double casePrice;
    private boolean unitChecked;
    private boolean caseChecked;
    private int type;
    private boolean checked;

    public Product(String name, String category, String unitSize, double unitPrice, String caseSize, double casePrice, int type) {
        this.name = name;
        this.category = category;
        this.unitSize = unitSize;
        this.unitPrice = unitPrice;
        this.caseSize = caseSize;
        this.casePrice = casePrice;
        this.type = type;
        this.checked = false;
    }

    //  for SECTION header
    public Product(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public Product(Parcel parcel) {
        name = parcel.readString();
        category = parcel.readString();
        unitSize = parcel.readString();
        unitPrice = parcel.readDouble();
        caseSize = parcel.readString();
        casePrice = parcel.readDouble();
        unitChecked = (parcel.readInt() == 1);
        caseChecked = (parcel.readInt() == 1);
        type = parcel.readInt();
        checked = (parcel.readInt() == 1);
    }


    // required Parcelable methods
    public int describeContents() {
        return this.hashCode();
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(category);
        parcel.writeString(unitSize);
        parcel.writeDouble(unitPrice);
        parcel.writeString(caseSize);
        parcel.writeDouble(casePrice);
        parcel.writeInt(unitChecked ? 1 : 0);
        parcel.writeInt(caseChecked ? 1 : 0);
        parcel.writeInt(type);
        parcel.writeInt(checked  ? 1 : 0);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Product createFromParcel(Parcel parcel)
        {
            return new Product(parcel);
        }
        public Product[] newArray(int size)
        {
            return new Product[size];
        }
    };

    public void setName(String name)
    {
        this.name = name;
    }
    public String getName()
    {
        return this.name;
    }

    public void setCategory(String category)
    {
        this.category = category;
    }
    public String getCategory()
    {
        return this.category;
    }

    public void setUnitSize(String unitSize)
    {
        this.unitSize = unitSize;
    }
    public String getUnitSize()
    {
        return this.unitSize;
    }

    public void setCaseSize(String caseSize)
    {
        this.caseSize = caseSize;
    }
    public String getCaseSize() {
        return this.caseSize;
    }

    public void unCheck()
    {
        this.checked = false;
        this.unitChecked = false;
        this.caseChecked = false;
    }
    public boolean isChecked()
    {
        return checked;
    }
    public void toggle()
    {
        checked = !checked;
    }

    public void setType(int type)
    {
        this.type = type;
    }
    public int getType()
    {
        return this.type;
    }

    public void setUnitChecked(boolean unitChecked)
    {
        this.unitChecked = unitChecked;
    }
    public void setCaseChecked(boolean caseChecked)
    {
        this.caseChecked = caseChecked;
    }

    public boolean isUnitChecked()
    {
        return this.unitChecked;
    }
    public boolean isCaseChecked()
    {
        return this.caseChecked;
    }

    public boolean isUnitCheckable()
    {
        return this.unitPrice != 0.0;
    }

    public boolean isCaseCheckable()
    {
        return this.casePrice != 0.0;
    }

    public boolean isSection()
    {
        return (this.type == A.TYPE_SECTION);
    }
}
