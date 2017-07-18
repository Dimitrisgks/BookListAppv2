package com.example.hell.booklist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final String Book_Values = "bookListValues";

    //saving the books
    private BookAdapter saveBookAdapter;

    //saving books
    private ListView bListView;

    //create book list where books will be stored
    ArrayList<Book> books = new ArrayList<>();

    //words so you can search the book
    private String sBookWords = "";

    /**
     * Returns true if network is available or about to become available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create Adapter for book list
        if (savedInstanceState != null) {
            books = savedInstanceState.getParcelableArrayList(Book_Values);
        }

        saveBookAdapter = new BookAdapter(this, books);

        // Get a reference to the ListView, and attach this adapter to it.
        bListView = (ListView) findViewById(R.id.book_listview);
        View emptyView = findViewById(R.id.notfound);
        bListView.setEmptyView(emptyView);
        bListView.setAdapter(saveBookAdapter);

        final EditText keywordEditText = (EditText) findViewById(R.id.bookWords);

        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(MainActivity.this)) {
                    sBookWords = keywordEditText.getText().toString();
                    searchBooks();
                } else {
                    Toast.makeText(MainActivity.this, "No internet Connection", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    private void searchBooks() {
        BookGet booklistget = new BookGet(this, this);
        booklistget.execute(sBookWords);
    }

    public void refreshBookList(ArrayList<Book> result) {
        saveBookAdapter.clear();
        for (Book book : result) {
            saveBookAdapter.add(book);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the book list values
        savedInstanceState.putParcelableArrayList(Book_Values, books);
        super.onSaveInstanceState(savedInstanceState);
    }
}