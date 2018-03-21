package com.fourth.blips;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.MenuItemHoverListener;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LookUp extends AppCompatActivity {

    private ServerInterface server;
    private ListView attractionsTypeList;
    private Map<String,String> selectedTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_up);

        selectedTypes = new HashMap<>();

        setSupportActionBar((Toolbar)findViewById(R.id.mapToolbar));
        attractionsTypeList = (ListView) findViewById(R.id.attractionTypes);
        attractionsTypeList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        attractionsTypeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView selected = (CheckedTextView) view.findViewById(R.id.name);
                selected.toggle();

                if (selected.isChecked()) {
                    selected.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                    HashMap<String,String> item = (HashMap<String,String>) parent.getItemAtPosition(position);
//                    selectedTypes.put("Name",item.get("Name"));
                    selectedTypes.putAll(item);
                }
                else {
                    selected.setCheckMarkDrawable(null);
                }

                System.out.println(selectedTypes);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        server = ServerInterface.getInstance(getApplicationContext());
        BlipRequest request = new BlipRequest(server);
        request.requestAttractionList(new ServerInterface.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {

                new LoadAttractionList().execute(response);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_actions,menu);

        menu.findItem(R.id.toolbar_edit_action).setEnabled(false);
        menu.findItem(R.id.toolbar_edit_action).setVisible(false);

        menu.findItem(R.id.toolbar_done_action).setEnabled(true);
        menu.findItem(R.id.toolbar_done_action).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(
                        this,
                        NavUtils.getParentActivityIntent(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;

            case R.id.toolbar_done_action:
                System.out.println(selectedTypes);

                Toast toast = Toast.makeText(getApplicationContext(), selectedTypes.get("ProperName"), Toast.LENGTH_SHORT);
                toast.show();
//                getCheckedAttractionTypes();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<Map<String,String>> getCheckedAttractionTypes() {
        SparseBooleanArray checkedIds = attractionsTypeList.getCheckedItemPositions();
//        android.R.drawable.;

        for (int i = 0; i < attractionsTypeList.getAdapter().getCount(); i++){
            if (checkedIds.get(i)) {
                return null;
            }
            else {
//                for (long item : checkedIds) {
//                    ArrayList<Map<String,String>> attraction = (ArrayList<Map<String,String>>)attractionsTypeList.getAdapter().getItem((int)item);
                    System.out.println(checkedIds);
//                }
                return null;
            }
        }

        return null;
    }

    private class LoadAttractionList extends AsyncTask<JSONObject, Void, ArrayList<Map<String,String>>>{
//        @Override
//        protected void onPreExecute(){
//
//        }

        @Override
        protected ArrayList<Map<String,String>> doInBackground(JSONObject... attractionListObj){

            ArrayList<Map<String,String>> attractionTypes = new ArrayList<>();

            //parse attraction types list
            try {
                JSONArray attractionList = attractionListObj[0].getJSONArray("attraction_types");

                for(int i = 0; i<attractionList.length(); i++){
                    HashMap<String,String> attraction = new HashMap<>();
                    attraction.put(
                            "Name",
                            attractionList.getJSONObject(i).getString("Name"));
                    attraction.put(
                            "ProperName",
                            attractionList.getJSONObject(i).getString("ProperName"));

                    attractionTypes.add(attraction);
                }

//                        System.out.println(attractionTypes);


            } catch (final JSONException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return attractionTypes;

        }

        @Override
        protected void onPostExecute(ArrayList<Map<String,String>> result){
            super.onPostExecute(result);

            ListAdapter adapter = new SimpleAdapter(
                    LookUp.this,
                    result,
                    R.layout.attraction_type_list_item,
                    new String[]{"ProperName"},
                    new int[]{R.id.name});

            attractionsTypeList.setAdapter(adapter);
        }


    }
}
