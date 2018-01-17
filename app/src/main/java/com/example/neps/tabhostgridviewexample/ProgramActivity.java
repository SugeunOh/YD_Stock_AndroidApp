package com.example.neps.tabhostgridviewexample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ProgramActivity extends AppCompatActivity {
    NetWorkService networkservice; // 서비스 객체
    boolean isService = false; // 서비스 중 확인용

    Button gotoTrendBtn;

    ArrayList<StockItem> kospiDatas= new ArrayList<StockItem>();
    ArrayList<StockItem> kosdaqDatas= new ArrayList<StockItem>();
    ArrayList<StockItem> interestDatas= new ArrayList<StockItem>();


    StockItemAdapter kospiAdapter;
    StockItemAdapter kosdaqAdapter;
    StockItemAdapter interestAdapter;

    ListView listViewKospi;
    ListView listViewKosdaq;
    ListView listViewInterest;

    SQLiteDatabase sqliteDB ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("ProgramActivity", "Create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);
        setTitle("프로그램");

        if(isService == false) // Auto Bind
        {
            Intent intent = new Intent(ProgramActivity.this, NetWorkService.class);
            Log.i("ProgramActivity", "Service Create");
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
            Log.i("ProgramActivity", "Service Bind");
            isService = true;
        }

        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1) ;
        tabHost1.setup() ;

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1") ;
        ts1.setIndicator("코스피") ;
        ts1.setContent(R.id.content1) ;
        tabHost1.addTab(ts1) ;

        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2") ;
        ts2.setIndicator("코스닥") ;
        ts2.setContent(R.id.content2) ;
        tabHost1.addTab(ts2) ;

        // 세 번째 Tab. (탭 표시 텍스트:"TAB 3"), (페이지 뷰:"content3")

        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tab Spec 3") ;
        ts3.setIndicator("관심종목") ;
        ts3.setContent(R.id.content3) ;
        tabHost1.addTab(ts3) ;

        listViewKospi= (ListView)findViewById(R.id.listViewKospi);
        listViewKospi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), StockItemDetailActivity.class);
                intent.putExtra("name", kospiDatas.get(position).getName());
                intent.putExtra("code", kospiDatas.get(position).getCode());
                startActivity(intent);
            }
        });
        kospiAdapter = new StockItemAdapter( getLayoutInflater() , kospiDatas);
        listViewKospi.setAdapter(kospiAdapter);


        listViewKosdaq = (ListView)findViewById(R.id.listViewKosdaq);
        listViewKosdaq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), StockItemDetailActivity.class);
                intent.putExtra("name", kosdaqDatas.get(position).getName());
                intent.putExtra("code", kosdaqDatas.get(position).getCode());
                startActivity(intent);
            }

        });
        kosdaqAdapter = new StockItemAdapter( getLayoutInflater() , kosdaqDatas);
        listViewKosdaq.setAdapter(kosdaqAdapter);




        listViewInterest = (ListView) findViewById(R.id.listViewInterest);
        interestAdapter = new StockItemAdapter( getLayoutInflater() , interestDatas);
        listViewInterest.setAdapter(interestAdapter);

        listViewKospi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                String rank = kospiDatas.get(position).getRank();
                String code = kospiDatas.get(position).getCode();
                String name = kospiDatas.get(position).getName();
                String currentPrice = kospiDatas.get(position).getCurrentPrice();
                String straightPurchaseVolume = kospiDatas.get(position).getStraightPurchaseVolume();
                String fluctuationImage = kospiDatas.get(position).getFluctuationImage();
                String fluctuationRate = kospiDatas.get(position).getFluctuationRate();

                boolean isExist = false;
                for (int i =0 ; i < interestDatas.size() ; i++)
                {
                    if(interestDatas.get(i).getName().equals(name))
                    {
                        isExist = true;
                        break;
                    }
                }

                if(isExist)
                    Toast.makeText(getApplicationContext(), "관심종목에 이미 존재하여 추가할 수 없음",Toast.LENGTH_SHORT).show();
                else
                {
                    interestDatas.add(new StockItem(rank, code, name, currentPrice, straightPurchaseVolume, fluctuationImage, fluctuationRate));
                    interestAdapter.notifyDataSetChanged();
                    add_item(rank, code, name, currentPrice, straightPurchaseVolume, fluctuationImage, fluctuationRate);

                    Toast.makeText(getApplicationContext(), name+"이(가) 관심종목에 추가됨",Toast.LENGTH_SHORT).show();
                }

                return true; //true를 반환해주면 다음 이벤트를 받지 않고 무시함
            }
        });

        listViewKosdaq.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                String rank = kosdaqDatas.get(position).getRank();
                String code = kosdaqDatas.get(position).getCode();
                String name = kosdaqDatas.get(position).getName();
                String currentPrice = kosdaqDatas.get(position).getCurrentPrice();
                String straightPurchaseVolume = kosdaqDatas.get(position).getStraightPurchaseVolume();
                String fluctuationImage = kosdaqDatas.get(position).getFluctuationImage();
                String fluctuationRate = kosdaqDatas.get(position).getFluctuationRate();

                boolean isExist = false;
                for (int i =0 ; i < interestDatas.size() ; i++)
                {
                    if(interestDatas.get(i).getName().equals(name))
                    {
                        isExist = true;
                        break;
                    }
                }

                if(isExist)
                    Toast.makeText(getApplicationContext(), "관심종목에 이미 존재하여 추가할 수 없음",Toast.LENGTH_SHORT).show();
                else
                {
                    interestDatas.add(new StockItem(rank, code, name, currentPrice, straightPurchaseVolume, fluctuationImage, fluctuationRate));
                    interestAdapter.notifyDataSetChanged();
                    add_item(rank, code, name, currentPrice, straightPurchaseVolume, fluctuationImage, fluctuationRate);
                    Toast.makeText(getApplicationContext(), name+"이(가) 관심종목에 추가됨",Toast.LENGTH_SHORT).show();
                }


                return true; //true를 반환해주면 다음 이벤트를 받지 않고 무시함
            }
        });

        listViewInterest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getApplicationContext(), StockItemDetailActivity.class);
                intent.putExtra("name", interestDatas.get(position).getName());
                intent.putExtra("code", interestDatas.get(position).getCode());
                startActivity(intent);
            }
        });

        listViewInterest.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                String rank = interestDatas.get(position).getRank();
                String code = interestDatas.get(position).getCode();
                String name = interestDatas.get(position).getName();
                String currentPrice = interestDatas.get(position).getCurrentPrice();
                String straightPurchaseVolume = interestDatas.get(position).getStraightPurchaseVolume();
                String fluctuationImage = interestDatas.get(position).getFluctuationImage();
                String fluctuationRate = interestDatas.get(position).getFluctuationRate();

                interestDatas.remove(position);
                interestAdapter.notifyDataSetChanged();
                delete_values(name);
                Toast.makeText(getApplicationContext(), name+"이(가) 관심종목에서 삭제됨",Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////
        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    Thread.sleep(1000);
                    Log.i("ProgramActivity", "Message Transmit : 코스피 순위 상위 15위까지 요청");
                    networkservice.TestXmit("opt90003");
                    networkservice.TestXmit("P00101"); // 코스피
                    Thread.sleep(1000);
                    Log.i("ProgramActivity", "Message Transmit : 코스닥 순위 상위 15위까지 요청");
                    networkservice.TestXmit("opt90003");
                    networkservice.TestXmit("P10102"); // 코스닥
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

        gotoTrendBtn = (Button) findViewById(R.id.gotoTrendBtn);

        gotoTrendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TrendActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    ServiceConnection conn = new ServiceConnection()
    {
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            NetWorkService.LocalBinder mBinder = (NetWorkService.LocalBinder) service;
            networkservice = mBinder.getService();
            networkservice.registerCallback(mCallback); //콜백 등록
            isService = true;
        }

        public void onServiceDisconnected(ComponentName name)
        {
            isService = false;
        }
    };

    private NetWorkService.ICallback mCallback = new NetWorkService.ICallback()
    {
        public void updateChartData(JSONObject obj)
        {
        }

        @Override
        public void updateWhoisList(JSONObject obj)
        {
        }

        public void recvData(JSONObject obj)
        {
            // 사용하지 않음.
        }

        public void updateProgramList(JSONObject obj)
        {
            try
            {
                ArrayList<StockItem> selectedDatas = null;
                StockItemAdapter adapter = null;

                if(obj.getString("시장구분").equals("코스피"))
                {
                    selectedDatas = kospiDatas;
                    adapter = kospiAdapter;
                }
                else if(obj.getString("시장구분").equals("코스닥"))
                {
                    selectedDatas= kosdaqDatas;
                    adapter = kosdaqAdapter;
                }
                else
                {
                  // Exception Handling...   
                }
                selectedDatas.clear(); // 기존 데이터를 지워서 중복으로 쓰이는 것을 막음.
                JSONArray ja = obj.getJSONArray("내용");

                for(int i = 0; i < ja.length(); i++)
                {
                    JSONObject oneDaySet = ja.getJSONObject(i);

                    String rank = oneDaySet.getString("순위");
                    String code = oneDaySet.getString("종목코드");
                    String name = oneDaySet.getString("종목명");

                    String currentPrice;
                    currentPrice = oneDaySet.getString("현재가").replace('+', ' ');
                    currentPrice = currentPrice.replace('-', ' ');
                    currentPrice = currentPrice.trim(); // 좌우 공백 제거.

                    if(!currentPrice.equals("")){
                        int temp = Integer.parseInt(currentPrice);
                        currentPrice = String.format("%,d", temp); // 천 단위로 쉼표를 찍음
                    }



                    String program_buy_volume = oneDaySet.getString("프로그램순매수금액");
                    program_buy_volume = program_buy_volume.trim();

                    if(!program_buy_volume.equals("")) {
                        int temp2 = Integer.parseInt(program_buy_volume);
                        program_buy_volume = String.format("%,d", temp2);
                    }
                    String fluctuationImage = oneDaySet.getString("등락기호");
                    String fluctuationRate = oneDaySet.getString("등락율") + '%';

                    selectedDatas.add(new StockItem(rank, code, name, currentPrice, program_buy_volume, fluctuationImage, fluctuationRate));

                }
                adapter.notifyDataSetChanged();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        /*
        public void updateWhoIsList(JSONObject obj)
        {
            try {
                ArrayList<StockItem> selectedDatas = null;
                StockItemAdapter adapter = null;


            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        */

    };

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(isService)
        {
            unbindService(conn); // 서비스 종료
            isService = false;
        }
    }

    private SQLiteDatabase init_database() {

        SQLiteDatabase db = null ;
        // File file = getDatabasePath("contact.db") ;
        File file = new File(getFilesDir(), "interestItem.db") ;

        System.out.println("PATH : " + file.toString()) ;
        try {
            db = SQLiteDatabase.openOrCreateDatabase(file, null) ;
        } catch (SQLiteException e) {
            e.printStackTrace() ;
        }

        if (db == null) {
            System.out.println("DB creation failed. " + file.getAbsolutePath()) ;
        }

        return db ;
    }

    private void init_tables()
    {
        if (sqliteDB != null) {
            String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS INTEREST ("    +
                    "RANK " + "TEXT," +
                    "CODE " + "TEXT," +
                    "NAME " + "TEXT," +
                    "CURRENTPRICE " + "TEXT," +
                    "STRAIGHTPURCHASEVOLUME " + "TEXT," +
                    "FLUCTUATIONIMAGE " + "TEXT," +
                    "FLUCTUATIONRATE " + "TEXT" +
                    ")";
            System.out.println(sqlCreateTbl);
            sqliteDB.execSQL(sqlCreateTbl);
        }
    }

    private void load_values() {
        if (sqliteDB != null)
        {
            String sqlQueryTbl = "SELECT * FROM INTEREST";
            Cursor cursor = null; // 쿼리 실행
            cursor = sqliteDB.rawQuery(sqlQueryTbl, null);

            String temp ="";

            while(cursor.moveToNext()){
                String rank = cursor.getString(0);
                String code = cursor.getString(1);
                String name = cursor.getString(2);
                String currentPrice = cursor.getString(3);
                String straightPurchaseVolume = cursor.getString(4);
                String fluctuationImage = cursor.getString(5);
                String fluctuationRate = cursor.getString(6);

                interestDatas.add(new StockItem(rank, code, name, currentPrice, straightPurchaseVolume, fluctuationImage, fluctuationRate));
            }
        }
    }

    private void delete_values(String name)
    {
        if (sqliteDB != null)
        {

            name.trim();

            String sqlDelete = "DELETE FROM INTEREST WHERE NAME = '" +  name + "'";
            System.out.println(sqlDelete) ;
            sqliteDB.execSQL(sqlDelete) ;

            ////////////////////////////////////////////////////////////
            String sqlQueryTbl = "SELECT * FROM INTEREST";
            Cursor cursor = null; // 쿼리 실행
            cursor = sqliteDB.rawQuery(sqlQueryTbl, null);

            int count = 0;
            while(cursor.moveToNext())
            {
                count++;
                String rank = cursor.getString(0);
                String code = cursor.getString(1);
                String name1 = cursor.getString(2);
                String currentPrice = cursor.getString(3);
                String straightPurchaseVolume = cursor.getString(4);
                String fluctuationImage = cursor.getString(5);
                String fluctuationRate = cursor.getString(6);

                System.out.println(rank +'\n'+ code +'\n'+ name1 +'\n'+ currentPrice +'\n'+ straightPurchaseVolume +'\n'+ fluctuationImage +'\n'+ fluctuationRate);
            }

            System.out.println("테이블 원소개수!!!: "+count);
            ////////////////////////////////////////////////////////////

        }
    }

    private void add_item(String rate, String code, String name, String currentPrice, String straightPurchaseVolume, String fluctuationImage, String fluctuationRate)
    {
        if (sqliteDB != null) {
            String sqlInsert = "INSERT INTO INTEREST " +
                    "(RANK, CODE, NAME, CURRENTPRICE, STRAIGHTPURCHASEVOLUME, FLUCTUATIONIMAGE, FLUCTUATIONRATE) VALUES (" +
                    rate + "," +
                    "'" + code + "'," +
                    "'" + name + "'," +
                    "'" + currentPrice + "'," +
                    "'" + straightPurchaseVolume + "'," +
                    "'" + fluctuationImage + "'," +
                    "'" + fluctuationRate +
                    "')" ;

            System.out.println(sqlInsert) ;

            sqliteDB.execSQL(sqlInsert) ;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus == true)
        {
            if(isService == true)
            {
                if (mCallback != null)
                {
                    Thread thread = new Thread()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                Thread.sleep(500);
                                networkservice.registerCallback(mCallback); //콜백 재등록

                                //0. 관심종목 리스트 초기화
                                interestDatas.clear();

                                //1.DB연결
                                sqliteDB = init_database() ;

                                //2. DB테이블이 없다면 생성
                                init_tables() ;

                                //3. DB에 있는거 복원
                                load_values();

                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    };

                    thread.start();

                }
                // StockItemDetailActivity에서 Back 버튼으로 해당 액티비티로 돌아올 때, Focus가 mCallback을 재등록해줘야한다.
                // 단, 서비스가 종료되있는 상태에서는 에러가 발생하므로(NullPointerException) 이를 핸들링하기 위해 isService == true일때만 작동한다.
            }
        }
        else
        {
            // Empty Statement
        }
    }








}
