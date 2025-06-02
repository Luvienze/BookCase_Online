package com.duzceuni.denemeapplication.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class Category implements Parcelable {
    private long id;
    private String name;

    public Category() {}

    public Category(long id, String name) {
        this.id = id;
        this.name = name;
    }
    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
    }
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    protected Category(Parcel in) {
        id = in.readLong();
        name = in.readString();
    }
}
