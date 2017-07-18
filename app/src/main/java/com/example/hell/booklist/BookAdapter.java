package com.example.hell.booklist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Book book = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.bitems_list, parent, false);
        }

        TextView bookTextView = (TextView) convertView.findViewById(R.id.text);
        bookTextView.setText(book.getTitle());

        TextView translationTextView = (TextView) convertView.findViewById(R.id.author);
        translationTextView.setText(book.getAuthor());

        return convertView;
    }
}
