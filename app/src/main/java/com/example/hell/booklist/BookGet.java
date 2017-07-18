package com.example.hell.booklist;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class BookGet extends AsyncTask<String, Void, ArrayList<Book>> {

    public MainActivity mMainActivity;

    private final String LOG_TAG = BookGet.class.getSimpleName();

    private final Context mContext;

    public BookGet(Context context, MainActivity mainActivity) {
        mContext = context;
        mMainActivity = mainActivity;
    }

    //represents book list in JSON Format getting  data and constructing the Strings for the book list.
    //constructor makes JSON string to an Object

    private ArrayList<Book> getBookDataFromJson(String booksJsonStr)
            throws JSONException {

        // The bookList to fill with results
        ArrayList<Book> bookList = new ArrayList();

        // The key to pass on the JSON Object
        final String BOOK_ITEMS = "items";
        final String BOOK_VOLUME_INFO = "volumeInfo";
        final String BOOK_TITLE = "title";
        final String BOOK_AUTHOR = "authors";

        try {
            JSONObject booksJson = new JSONObject(booksJsonStr);
            JSONArray itemsArray = booksJson.getJSONArray(BOOK_ITEMS);

            for (int i = 0; i < itemsArray.length(); i++) {

                //values that will be stored
                String title = "";
                String author = "";

                //Json Object becoming a book
                JSONObject bookInfo = itemsArray.getJSONObject(i);
                JSONObject volumeInfoJson = bookInfo.getJSONObject(BOOK_VOLUME_INFO);

                title = volumeInfoJson.getString(BOOK_TITLE);
                if (volumeInfoJson.has(BOOK_AUTHOR)) {
                    // parse the authors field
                    JSONArray authorArray = volumeInfoJson.getJSONArray(BOOK_AUTHOR);
                    for (int j = 0; j < authorArray.length(); j++) {
                        if (j == 0) {
                            author += authorArray.getString(j);
                        } else {
                            author += ", " + authorArray.getString(j);
                        }
                    }
                } else {
                    // Authors placeholder text (e.g. "Author N/A")

                }



                // add a entry in the book list if there is none
                Book book = new Book(author, title);
                boolean isBookInList = false;
                for (Book b : bookList) {
                    if (b.getTitle().equals(title)) {
                        isBookInList = true;
                    }
                }
                if (!isBookInList) {
                    bookList.add(book);
                }
            }

            Log.d(LOG_TAG, "Getting books finished");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return bookList;
    }

    @Override
    protected ArrayList<Book> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        String BookWordQ = params[0];


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // new JSON as a string.
        String booksJsonStr = null;

        int numMaxResults = 20;
        String order = "newest";

        try {
            // URL for  Books API
            // https://developers.google.com/books/docs/v1/getting_started#intro
            final String GOOGLE_BOOKS_BASE_URL =
                    "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q";
            final String MAX_PARAM = "maxResults";
            final String ORDER_PARAM = "orderBy";

            Uri builtUri = Uri.parse(GOOGLE_BOOKS_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(MAX_PARAM, Integer.toString(numMaxResults))
                    .appendQueryParameter(ORDER_PARAM, order)
                    .build();

            URL url = new URL(builtUri.toString());

            // request and connect to google api
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                //empty stream
                return null;
            }
            booksJsonStr = buffer.toString();
            return getBookDataFromJson(booksJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // unsuccessful  data don't  parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        //if error found :
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Book> result) {
        if (result != null) {
            mMainActivity.refreshBookList(result);
        }
    }
}