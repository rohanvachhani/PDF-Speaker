package com.rohan.android.pdfspeaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class BookmarkList extends AppCompatActivity {


    ListView listView;
    String path_1;
    ArrayList<Integer> received_list;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_list);
        listView = (ListView) findViewById(R.id.bookmark_text_view);
        Intent i = this.getIntent();
        if (getIntent().getIntegerArrayListExtra("bookmark list") != null) {
           // Toast.makeText(this, "in if", Toast.LENGTH_SHORT).show();

            received_list = i.getIntegerArrayListExtra("bookmark list");
            path_1 = i.getStringExtra("path");
            //Toast.makeText(this, "receive path" + path_1, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "NULL", Toast.LENGTH_SHORT).show();
        }

        Integer ary[] = new Integer[received_list.size()];
        for (int ii = 0; ii < received_list.size(); ii++) {
            ary[ii] = received_list.get(ii);
            //   Toast.makeText(BookmarkList.this, "ary : " + ary[ii], Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<Integer> itemsAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ary);

        listView.setAdapter(itemsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                View view1 = listView.getChildAt(position);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);


                int page = Integer.parseInt(tv.getText().toString());
                Intent intent_return = new Intent(BookmarkList.this, MainActivity.class);
                intent_return.putExtra("page", page);
                //Toast.makeText(BookmarkList.this, "return path:" + path_1, Toast.LENGTH_SHORT).show();
                intent_return.putExtra("path_rtrn", path_1);
                startActivity(intent_return);

                //Toast.makeText(BookmarkList.this, "PAGE:"+tv.getText(), Toast.LENGTH_SHORT).show();

            }
        });

    }

}


