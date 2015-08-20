package com.mstr.letschat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mstr.letschat.model.UserProfile;
import com.mstr.letschat.tasks.Response.Listener;
import com.mstr.letschat.tasks.SearchUserTask;

public class SearchUserActivity extends Activity implements OnQueryTextListener, Listener<UserProfile>, OnClickListener {
	private SearchUserTask task;
	
	private LinearLayout hintWrapper;
	private TextView hintText;
	private SearchView searchView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_add_contact);
		
		hintWrapper = (LinearLayout)findViewById(R.id.ll_hint_wrapper);
		hintText = (TextView)findViewById(R.id.tv_hint);
		hintText.setOnClickListener(this);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_contact_menu, menu);
		
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchItem.expandActionView();
		searchView = (SearchView)searchItem.getActionView();
		searchView.setOnQueryTextListener(this);
		searchView.setQueryHint(getResources().getText(R.string.cell_phone_number));
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onQueryTextSubmit(String query) {
		executeSearchTask(query);
		
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (newText != null && newText.trim().length() > 0) {
			if (hintWrapper.getVisibility() != View.VISIBLE) {
				hintWrapper.setVisibility(View.VISIBLE);
			}
			hintText.setText(getResources().getString(R.string.search) + newText);
		} else {
			if (hintWrapper.getVisibility() != View.INVISIBLE) {
				hintWrapper.setVisibility(View.INVISIBLE);
			}
		}
		
		return true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		if (task != null) {
			task.cancel(false);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == hintText) {
			executeSearchTask(searchView.getQuery().toString());
		}
	}
	
	private void executeSearchTask(String query) {
		task = new SearchUserTask(this, this, query);
		task.execute();
	}

	@Override
	public void onResponse(UserProfile result) {
		if (result != null) {
			Intent intent = new Intent(this, UserProfileActivity.class);
			intent.putExtra(UserProfileActivity.EXTRA_DATA_NAME_USER_PROFILE, result);
			startActivity(intent);
		} else {
			Toast.makeText(this, R.string.search_contact_no_result, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onErrorResponse(SmackInvocationException exception) {
		Toast.makeText(this, R.string.search_user_error, Toast.LENGTH_SHORT).show();
	}
}