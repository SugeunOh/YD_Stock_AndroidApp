package com.example.neps.tabhostgridviewexample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class TrendActivity extends AppCompatActivity {

    GridView gridView;
    GridViewAdapter gridAdapter;
    ArrayList<ImageItem> imageItemList = new ArrayList<>();
    Handler handler = new Handler();

    Button gotoProgramBtn;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);
        setTitle("증시동향");

        gotoProgramBtn = (Button) findViewById(R.id.gotoProgramBtn);

        gotoProgramBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProgramActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, imageItemList);
        gridView.setAdapter(gridAdapter);
        requestImageAndSetImage();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);


                if(item.getTitle().equals("코스피 주체별"))
                {
                    Toast.makeText(getApplicationContext(), "모바일 버전을 지원하지 않는 아이템입니다.", Toast.LENGTH_SHORT).show();
                }
                else if(item.getTitle().equals("코스닥 주체별"))
                {
                    Toast.makeText(getApplicationContext(), "모바일 버전을 지원하지 않는 아이템입니다.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), WebActivity.class);
                    intent.putExtra("selectedItem", item.getTitle());
                    startActivity(intent);
                }
            }
        });
    }

    public void requestImageAndSetImage(){
        Thread WebRequest = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    URL url;
                    InputStream is;
                    Bitmap bm;

                    String urlItems[][] = new String[6][2];

                    urlItems[0][0] = "http://imgfinance.naver.net/chart/main/KOSPI.png";
                    urlItems[0][1] = "코스피";
                    urlItems[1][0] = "http://imgfinance.naver.net/chart/sise/siseMainKOSPI.png?";
                    urlItems[1][1] = "코스피 주체별";
                    urlItems[2][0] = "http://imgfinance.naver.net/chart/main/KOSDAQ.png";
                    urlItems[2][1] = "코스닥";
                    urlItems[3][0] = "http://imgfinance.naver.net/chart/sise/siseMainKOSDAQ.png";
                    urlItems[3][1] = "코스닥 주체별";
                    urlItems[4][0] = "http://imgfinance.naver.net/chart/world/continent/DJI@DJI.png";
                    urlItems[4][1] = "다우산업";
                    urlItems[5][0] = "http://imgfinance.naver.net/chart/world/continent/NAS@IXIC.png";
                    urlItems[5][1] = "나스닥종합";

                    //그리드뷰 이미지 세팅
                    for(int i = 0 ; i < urlItems.length ; i++)
                    {
                        url = new URL(urlItems[i][0]);
                        is = url.openStream();
                        bm = BitmapFactory.decodeStream(is);
                        imageItemList.add(new ImageItem(bm, urlItems[i][1]));
                        is.close();
                    }

                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            gridAdapter.notifyDataSetChanged();
                        }
                    });
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        WebRequest.start();
    }
}
