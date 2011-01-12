package com.mediaportal.remote.activities.media;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.mediaportal.remote.R;
import com.mediaportal.remote.api.DataHandler;
import com.mediaportal.remote.data.Movie;
import com.mediaportal.remote.lists.ILoadingAdapterItem;
import com.mediaportal.remote.lists.LazyLoadingAdapter;
import com.mediaportal.remote.lists.views.MoviePosterViewAdapter;

public class TabMoviesActivity extends Activity {
	private ListView mListView;
	private LazyLoadingAdapter mAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.tabmoviesactivity);

		DataHandler service = DataHandler.getCurrentRemoteInstance();
		List<Movie> movies = service.getAllMovies();

		mAdapter = new LazyLoadingAdapter(this, R.layout.listitem_thumb);

		if (movies != null) {
			for (Movie m : movies) {
				mAdapter.AddItem(new MoviePosterViewAdapter(m));
			}
		}

		mListView = (ListView) findViewById(R.id.ListViewVideos);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Movie selectedMovie = (Movie) ((ILoadingAdapterItem) mListView
						.getItemAtPosition(position)).getItem();

				Intent myIntent = new Intent(v.getContext(),
						TabMovieDetailsActivity.class);
				myIntent.putExtra("movie_id", selectedMovie.getId());
				startActivity(myIntent);

			}
		});
	}
}
