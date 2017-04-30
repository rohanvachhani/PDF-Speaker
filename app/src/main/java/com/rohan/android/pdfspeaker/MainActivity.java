package com.rohan.android.pdfspeaker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    PDFView pdfView;
    TextToSpeech tts;
    Button speak, stop, next, prev;
    Uri resultUri;
    private final int FILE_SELECT_CODE = 42;
    //public String text = null;
    public File myfile = null;
    PdfReader reader = null;
    public ArrayList<Integer> bookmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Intent rcv = getIntent();
        int page = rcv.getIntExtra("page", 0);
        String path = rcv.getStringExtra("path_rtrn");


        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.US);
                    // Toast.makeText(MainActivity.this, "TTS INITIALIZED", Toast.LENGTH_SHORT).show();
                }
            }
        });
        speak = (Button) findViewById(R.id.speak);
        stop = (Button) findViewById(R.id.stop);
        next = (Button) findViewById(R.id.next_page);
        prev = (Button) findViewById(R.id.prev_page);
        bookmarks = new ArrayList<>();

        pdfView = (PDFView) findViewById(R.id.pdfView);
        //pdfView.fromAsset("s2.pdf").load();
        //pdfView.fromAsset("sample.pdf").load();
        File dir = Environment.getExternalStorageDirectory();

        if (page == 0) {
            myfile = new File(dir, "/sample_small.pdf");
        } else {
            myfile = new File(path);
        }

        pdfView.fromFile(myfile).defaultPage(page - 1).load();
        //int aaa = pdfView.getCurrentPage();

        //Toast.makeText(this, "after LOAD pdf", Toast.LENGTH_SHORT).show();      //FILE SUCCESSFULLY LOADED UPTO HERE.!

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    reader = new PdfReader(String.valueOf(myfile));
                    //Toast.makeText(this, "after reader object" + String.valueOf(myfile), Toast.LENGTH_SHORT).show();
                    int aa = pdfView.getCurrentPage();
                    try {
                        String text = PdfTextExtractor.getTextFromPage(reader, aa + 1).trim(); //Extracting the content from the different pages
                        Toast.makeText(MainActivity.this, "speaking..", Toast.LENGTH_SHORT).show();
                        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts.isSpeaking()) {
                    // Toast.makeText(MainActivity.this,"stoping tts", Toast.LENGTH_SHORT).show();
                    tts.stop();
                } else {
                    Toast.makeText(MainActivity.this, "not speaking at all!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfView.jumpTo(pdfView.getCurrentPage() + 1);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdfView.jumpTo(pdfView.getCurrentPage() - 1);

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflator = getMenuInflater();
        mMenuInflator.inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.home:
                goto_homepage();
                break;
            case R.id.open_file:
                file_chooser();
                break;
            case R.id.bookmark:
                add_bookmark();
                break;
            case R.id.bookmark_list:
                view_bookmark();
                break;
        }
        return true;
    }

    public void goto_homepage() {
        //Toast.makeText(this, "go to Homepage..", Toast.LENGTH_LONG).show();
        pdfView.jumpTo(0);
    }

    public void add_bookmark() {
        //Toast.makeText(this, "add bookmark..", Toast.LENGTH_SHORT).show();
        int pg_no = pdfView.getCurrentPage() + 1;
        bookmarks.add(pg_no);
        Toast.makeText(this, "Bookmark added" + bookmarks, Toast.LENGTH_SHORT).show();
    }

    public void view_bookmark() {
        // Toast.makeText(this, "Bookmarks " + bookmarks, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(MainActivity.this, BookmarkList.class);

        intent.putIntegerArrayListExtra("bookmark list", bookmarks);
        intent.putExtra("path", myfile.toString());
        startActivity(intent);

        // Toast.makeText(this, "Bookmark List..", Toast.LENGTH_SHORT).show();
    }


    public void file_chooser() {
        //Toast.makeText(MainActivity.this, "inside file chooser ", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] ary = {"application/pdf"};
        // intent.putExtra(Intent.EXTRA_MIME_TYPES,ary);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a file"), FILE_SELECT_CODE);
            // Toast.makeText(MainActivity.this, resultUri.getPath(), Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException e) {
        }
    }


    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK && data != null) {
            return;
        } else {
            resultUri = data.getData();

            pdfView.fromUri(resultUri).load();

            String path = resultUri.getLastPathSegment();

            String final_name = resultUri.getLastPathSegment();
            final_name = final_name.replace("primary:", "");
            final_name = "/" + final_name;
            File dir = Environment.getExternalStorageDirectory();
            myfile = new File(dir, final_name);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}

