package com.example.hell.booklist;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {


    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    // Author of the book
    private String bAuthors;

    // Title of the book
    private String bTitle;

    //book object with the parameters that i made above
    public Book(String authors, String title) {
        bAuthors = authors;
        bTitle = title;
    }

    private Book(Parcel in) {
        bAuthors = in.readString();
        bTitle = in.readString();
    }

    // get author
    public String getAuthor() {
        return bAuthors;
    }

    //get title
    public String getTitle() {
        return bTitle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bAuthors);
        dest.writeString(bTitle);
    }

}