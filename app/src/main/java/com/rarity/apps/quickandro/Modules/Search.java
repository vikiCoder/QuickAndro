package com.rarity.apps.quickandro.Modules;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.rarity.apps.quickandro.R;

public class Search {

    private Context context;

    public Search(Context context){
        this.context = context;
    }

    public String googleSearch(String query){
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(SearchManager.QUERY, query);
        context.startActivity(intent);
        return context.getString(R.string.searching)+context.getString(R.string.google) + query;
    }

    public String wikiSearch(String query){
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.wikiLink) + query.replaceAll(" ", "_")) );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return context.getString(R.string.searching)+context.getString(R.string.wiki) + query;
    }

    public String dictionarySearch(String query){
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.dictionaryLink) + query) );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return context.getString(R.string.searching)+context.getString(R.string.dictionary) + query;
    }

    public String youtubeSearch(String query){
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.youtubeLink) + query.replaceAll(" ", "+")) );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return context.getString(R.string.searching)+context.getString(R.string.youtube) + query;
    }
}
