package com.example.neps.tabhostgridviewexample;

//테이블레이아웃 참고 사이트 :
//https://stackoverflow.com/questions/7119231/android-layout-how-to-implement-a-fixed-freezed-header-and-column
//더 이쁜 디자인으로 하고싶다면 사용할 라이브러리 https://github.com/InQBarna/TableFixHeaders

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StockItemDetailActivity extends AppCompatActivity {


    NetWorkService networkservice; // 서비스 객체
    boolean isService = false; // 서비스 중 확인용
    CandleStickChart candleStickChart;
    ArrayList<String> labels = new ArrayList<String>();
    ArrayList<CandleEntry> entries = new ArrayList<>();
    CandleDataSet dataset;
    CandleData data;


    TableRow.LayoutParams wrapWrapTableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
    int[] scrollableColumnWidths = new int[]{30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30};
    int fixedRowHeight = 60; // row 높이


    TableLayout dateColumn;
    TableLayout scrollableColumns;

    private TextView recyclableTextView;
    TableRow row;

    Button buttonWhoisCall;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        candleStickChart = (CandleStickChart) findViewById(R.id.chart);
        YAxis leftAxis = candleStickChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setEnabled(false);
        YAxis rightAxis = candleStickChart.getAxisRight();
        rightAxis.setDrawGridLines(true);


        if(isService == false)
        {
            Intent intent = new Intent(StockItemDetailActivity.this, NetWorkService.class);
            Log.i("StockItemDetailActivity", "Service Create");
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
            Log.i("StockItemDetailActivity", "Service Bind");
            isService = true;
        }

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        final String code = intent.getStringExtra("code");

        setTitle(name + "(" + code + ")");

        dateColumn= (TableLayout) findViewById(R.id.fixed_column);             //첫번째 컬럼
        scrollableColumns = (TableLayout) findViewById(R.id.scrollable_part);   //두번째 이상의 컬럼
        row = new TableRow(this);




        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try
                {
                    Thread.sleep(300);

                    // 차트 가져오기
                    networkservice.TestXmit("opt10081");
                    networkservice.TestXmit(code);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        buttonWhoisCall = (Button)findViewById(R.id.buttonWhoisCall);
        buttonWhoisCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("StockItemDetailActivity", "Message Transmit : 주체별 요청");
                networkservice.TestXmit("opt10059");
                networkservice.TestXmit(code);
            }
        });

        /////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    public TextView makeTableRowWithText(String text, int widthInPercentOfScreenWidth, int fixedHeightInPixels) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        recyclableTextView = new TextView(this);
        recyclableTextView.setText(text);
        recyclableTextView.setTextColor(Color.BLACK);
        recyclableTextView.setTextSize(15);
        recyclableTextView.setWidth(widthInPercentOfScreenWidth * screenWidth / 100);
        recyclableTextView.setHeight(fixedHeightInPixels);
        return recyclableTextView;
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
        public void recvData(JSONObject obj)
        {
            //처리할 일들..
            try
            {
                String name = obj.getString("종목명");
                String amount = obj.getString("거래량");
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
        }

        public void updateProgramList(JSONObject obj)
        {
            // 여기선 사용되지 않음.
        }

        public void updateChartData(JSONObject obj)
        {
            try
            {
                JSONArray ja = obj.getJSONArray("내용");
                for(int i = 0; i < ja.length(); i++)
                {
                    JSONObject test = ja.getJSONObject(ja.length() - 1 -i);
                    String curr_money = test.getString("종가");
                    String start_money = test.getString("시가");
                    String high_money = test.getString("고가");
                    String low_money = test.getString("저가");
                    String date_info = test.getString("일자");

                    //Log.i("StockItemDetailActivity", "일자: " + date_info + " / " + "시가: " + start_money + " / " + "종가: " + curr_money + " / " + "고가: " + high_money + " / " + "저가: " + low_money);

                    entries.add(new CandleEntry(i, Float.parseFloat(high_money), Float.parseFloat(low_money), Float.parseFloat(start_money), Float.parseFloat(curr_money)));
                    labels.add("");
                }

                dataset = new CandleDataSet(entries, "# of Calls");
                dataset.setIncreasingColor(Color.RED);
                dataset.setIncreasingPaintStyle(Paint.Style.FILL);
                dataset.setDecreasingColor(Color.BLUE);
                dataset.setDecreasingPaintStyle(Paint.Style.FILL);
                dataset.setShadowColor(Color.BLACK);
                dataset.setValueTextColor(Color.TRANSPARENT);

                data = new CandleData(dataset);
                candleStickChart.setData(data);

                candleStickChart.setDescription(new Description());
                candleStickChart.getLegend().setEnabled(false);   // Legend를 숨김.

                XAxis xAxis = candleStickChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(true);
                xAxis.setEnabled(false);                        // xAsix를 안보이게 적용.

                candleStickChart.setDrawGridBackground(false);
                candleStickChart.getDescription().setEnabled(false);
                candleStickChart.setBackgroundColor(Color.WHITE);

                candleStickChart.invalidate();



                // 차트는 그려짐, 리스트를 조회하기 위해 다시 요청을 보냄.
                Intent intent = getIntent();
                final String code = intent.getStringExtra("code");

                // 주체별 요청을 보냄.
                networkservice.TestXmit("opt10059");
                networkservice.TestXmit(code);
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
        }

        public void updateWhoisList(JSONObject obj) {
            try
            {

               // selectedDatas.clear();
                JSONArray ja = obj.getJSONArray("내용");


                TextView fixedView = makeTableRowWithText("날짜", scrollableColumnWidths[0], fixedRowHeight);

                fixedView.setBackgroundColor(Color.RED);
                dateColumn.addView(fixedView);
                row = new TableRow(getApplicationContext());
                row.setLayoutParams(wrapWrapTableRowParams);
                row.setGravity(Gravity.CENTER);
                row.setBackgroundColor(Color.RED);
                row.addView(makeTableRowWithText("종가", scrollableColumnWidths[1], fixedRowHeight));
                row.addView(makeTableRowWithText("개인", scrollableColumnWidths[2], fixedRowHeight));
                row.addView(makeTableRowWithText("외국인", scrollableColumnWidths[3], fixedRowHeight));
                row.addView(makeTableRowWithText("기관", scrollableColumnWidths[4], fixedRowHeight));
                row.addView(makeTableRowWithText("금융투자", scrollableColumnWidths[5], fixedRowHeight));
                row.addView(makeTableRowWithText("보험", scrollableColumnWidths[6], fixedRowHeight));
                row.addView(makeTableRowWithText("투신", scrollableColumnWidths[7], fixedRowHeight));
                row.addView(makeTableRowWithText("기타금융", scrollableColumnWidths[8], fixedRowHeight));
                row.addView(makeTableRowWithText("은행", scrollableColumnWidths[9], fixedRowHeight));
                row.addView(makeTableRowWithText("연기금", scrollableColumnWidths[10], fixedRowHeight));
                row.addView(makeTableRowWithText("사모펀드", scrollableColumnWidths[11], fixedRowHeight));
                row.addView(makeTableRowWithText("국가", scrollableColumnWidths[12], fixedRowHeight));
                row.addView(makeTableRowWithText("기타법인", scrollableColumnWidths[13], fixedRowHeight));
                row.addView(makeTableRowWithText("내외국인", scrollableColumnWidths[14], fixedRowHeight));
                scrollableColumns.addView(row);


                for(int i = 0; i < ja.length(); i++)
                {
                    JSONObject oneDaySet = ja.getJSONObject(i);

                    String dateInfo = oneDaySet.getString("일자");
                    String currMoney = oneDaySet.getString("종가");
                    String updownIcon = oneDaySet.getString("대비기호");
                    String indiHolder = oneDaySet.getString("개인투자자");
                    String forgHolder = oneDaySet.getString("외국인투자자");
                    String orgaHolder = oneDaySet.getString("기관계");
                    String financeHolder = oneDaySet.getString("금융투자");
                    String insuranceHolder = oneDaySet.getString("보험");
                    String tusinHolder = oneDaySet.getString("투신");
                    String etcFinanceHolder = oneDaySet.getString("기타금융");
                    String bankHolder = oneDaySet.getString("은행");
                    String yeongiHolder = oneDaySet.getString("연기금등");
                    String samofundHolder = oneDaySet.getString("사모펀드");
                    String counryHolder = oneDaySet.getString("국가");
                    String etcLawHolder = oneDaySet.getString("기타법인");
                    String naeforgHolder = oneDaySet.getString("내외국인");

                    fixedView = makeTableRowWithText(dateInfo, scrollableColumnWidths[0], fixedRowHeight);
                    fixedView.setBackgroundColor(Color.WHITE);
                    dateColumn.addView(fixedView);
                    row = new TableRow(getApplicationContext());
                    row.setLayoutParams(wrapWrapTableRowParams);
                    row.setGravity(Gravity.CENTER);
                    row.setBackgroundColor(Color.WHITE);
                    row.addView(makeTableRowWithText(currMoney, scrollableColumnWidths[1], fixedRowHeight));
                    row.addView(makeTableRowWithText(indiHolder, scrollableColumnWidths[2], fixedRowHeight));
                    row.addView(makeTableRowWithText(forgHolder, scrollableColumnWidths[3], fixedRowHeight));
                    row.addView(makeTableRowWithText(orgaHolder, scrollableColumnWidths[4], fixedRowHeight));
                    row.addView(makeTableRowWithText(financeHolder, scrollableColumnWidths[5], fixedRowHeight));
                    row.addView(makeTableRowWithText(insuranceHolder, scrollableColumnWidths[6], fixedRowHeight));
                    row.addView(makeTableRowWithText(tusinHolder, scrollableColumnWidths[7], fixedRowHeight));
                    row.addView(makeTableRowWithText(etcFinanceHolder, scrollableColumnWidths[8], fixedRowHeight));
                    row.addView(makeTableRowWithText(bankHolder, scrollableColumnWidths[9], fixedRowHeight));
                    row.addView(makeTableRowWithText(yeongiHolder, scrollableColumnWidths[10], fixedRowHeight));
                    row.addView(makeTableRowWithText(samofundHolder, scrollableColumnWidths[11], fixedRowHeight));
                    row.addView(makeTableRowWithText(counryHolder, scrollableColumnWidths[12], fixedRowHeight));
                    row.addView(makeTableRowWithText(etcLawHolder, scrollableColumnWidths[13], fixedRowHeight));
                    row.addView(makeTableRowWithText(naeforgHolder, scrollableColumnWidths[14], fixedRowHeight));
                    scrollableColumns.addView(row);

                }
               // whoisAdapter.notifyDataSetChanged();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
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
}