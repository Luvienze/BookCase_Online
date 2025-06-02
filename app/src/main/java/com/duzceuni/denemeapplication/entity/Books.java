package com.duzceuni.denemeapplication.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Books implements Parcelable {

    private long ISBN;

    private String title;

    private String author;

    private int pages;

    private int copiesAvailable;

    private String description;

    private String image;

    private String rentDate;

    private String returnDate;

    private boolean isReturned;

    private List<Category> categoryList;

    public Books(){}

    public Books(long ISBN, String title, String author, int pages, int copiesAvailable, String image, List<Category> categoryList) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.image = image;
        this.copiesAvailable = copiesAvailable;
        this.categoryList = categoryList;
    }

    public Books(long ISBN, String title, String author, int pages, int copiesAvailable, String description, String image, List<Category> categoryList) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.copiesAvailable = copiesAvailable;
        this.description = description;
        this.image = image;
        this.categoryList = categoryList;
    }

    public Books(long isbn, String rentDate, String returnDate, boolean isReturned){
        this.ISBN = isbn;
        this.rentDate = rentDate;
        this.returnDate = returnDate;
        this.isReturned = isReturned;
    }
    protected Books(Parcel in) {
        ISBN = in.readLong();
        title = in.readString();
        author = in.readString();
        pages = in.readInt();
        copiesAvailable = in.readInt();
        image = in.readString();
        description = in.readString();
        categoryList = new ArrayList<>();
        rentDate = in.readString();
        returnDate = in.readString();
        in.readList(categoryList, Category.class.getClassLoader());
    }

    public static final Creator<Books> CREATOR = new Creator<Books>() {
        @Override
        public Books createFromParcel(Parcel in) {
            return new Books(in);
        }

        @Override
        public Books[] newArray(int size) {
            return new Books[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getISBN() {
        return ISBN;
    }

    public void setISBN(long ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Category> getCategoryList() {
        if (categoryList == null) {
            return new ArrayList<>();
        }
        return categoryList;
    }

    public String getRentDate() {
        return rentDate;
    }

    public void setRentDate(String rentDate) {
        this.rentDate = rentDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Books books = (Books) o;
        return ISBN == books.ISBN;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ISBN);
    }

    @Override
    public String toString() {
        return "Books{" +
                "ISBN=" + ISBN +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", pages=" + pages +
                ", copiesAvailable=" + copiesAvailable +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", rentDate='" + rentDate + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", categoryList=" + categoryList + '\'' +
                ", rentDate = " + rentDate + '\'' +
                ", returnDate = " + rentDate +
                ", isReturned = " + isReturned +
        '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(ISBN);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeInt(pages);
        dest.writeInt(copiesAvailable);
        dest.writeString(image);
        dest.writeString(description);
        dest.writeList(categoryList);
        dest.writeString(rentDate);
        dest.writeString(returnDate);
    }
}
