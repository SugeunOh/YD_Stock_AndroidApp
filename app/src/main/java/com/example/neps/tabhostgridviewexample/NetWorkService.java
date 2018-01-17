package com.example.neps.tabhostgridviewexample;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class NetWorkService extends Service
{
    private final IBinder mBinder = new LocalBinder();

    //  TCP연결 관련
    private Socket clientSocket;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private int port = 9000;
    private final String ip = "165.132.221.240"; //172.26.36.95
    private InteractionHandler handler;
    private TCPClientThread ClientThread;
    private ICallback mCallback;

    class LocalBinder extends Binder
    {
        NetWorkService getService()
        {
            return NetWorkService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try
        {
            clientSocket = new Socket(ip, port);
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            socketOut = new PrintWriter(clientSocket.getOutputStream(), true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        handler = new InteractionHandler();
        ClientThread = new TCPClientThread();
        ClientThread.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.i("NetWorkService", "onDestroy call ");
        ClientThread.setRunningState(false);
        ClientThread.interrupt();
        try
        {
            clientSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    class TCPClientThread extends Thread
    {
        boolean isRunning = true;
        @Override
        public void run()
        {
            while (isRunning)
            {
                try
                {

                    String data;

                    data = socketIn.readLine();
                    //Log.i("TCPClientThread","DataReceived: " + data);

                    //JSONArray ja = new JSONArray(new JSONObject(data));



                    JSONObject json = new JSONObject(data);
                    // Message 객체를 생성, 핸들러에 정보를 보낼 땐 이 메세지 객체를 이용
                    Message msg = handler.obtainMessage();
                    msg.obj = json;
                    handler.sendMessage(msg);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        public void setRunningState(boolean state)
        {
            isRunning = state;
        }
    }

    class InteractionHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg)
        {
            JSONObject obj = (JSONObject) msg.obj;

            // 테스트용 JSONObject 타입 판단(이를 통해 호출할 CallBackMethod 제어) 및 JSONArray 파싱 부분.
            // 현재는 거의 다 구현이 되어있어서 로그부분은 지워도 무방할듯 함. 테스트시에만 사용하자.
            try
            {
                String type = obj.getString("타입");

                if(type.equals("주식요청"))
                {
                    String name = obj.getString("종목명");
                    String volume = obj.getString("거래량");

                    Log.i("InterationHandler","종목명: " + name);
                    Log.i("InterationHandler", "거래량: " + volume);
                    mCallback.recvData(obj);
                }
                else if(type.equals("순위요청"))
                {


                    //디버깅을 위한 출력 코드라서 응용되진 않음
                    /*
                    JSONArray ja = obj.getJSONArray("내용");
                    for(int i = 0; i < ja.length(); i++)
                    {
                        JSONObject test = ja.getJSONObject(i);

                        String rank = test.getString("순위");
                        String name = test.getString("종목명");
                        String code = test.getString("종목코드");
                        String program_by_volume = test.getString("프로그램순매수금액");
                        String curr_money = test.getString("현재가");
                        String updown_icon = test.getString("등락기호");
                        String updown_rate = test.getString("등락율");


                        Log.i("InterationHandler", "순위: " + rank + " / " + "종목명: " + name + " / " + "종목코드: " + code + " / " + "현재가: " + curr_money);
                        Log.i("InterationHandler", "전일대비: " + program_by_volume + " / " + "등락기호: " + updown_icon + " / " + "등락율: " + updown_rate);
                        Log.i("InterationHandler", "----------------------------------------------------------------------------------------------------------");
                    }
                    */
                    mCallback.updateProgramList(obj);
                }
                else if(type.equals("차트정보"))
                {
                    /*
                    JSONArray ja = obj.getJSONArray("내용");
                    for(int i = 0; i < ja.length(); i++)
                    {
                        JSONObject test = ja.getJSONObject(i);
                        String curr_money = test.getString("종가");
                        String start_money = test.getString("시가");
                        String high_money = test.getString("고가");
                        String low_money = test.getString("저가");
                        String date_info = test.getString("일자");

                        Log.i("InterationHandler", "일자: " + date_info + " / " + "시가: " + start_money + " / " + "종가: " + curr_money + " / " + "고가: " + high_money + " / " + "저가: " + low_money);
                    }
                    */
                    mCallback.updateChartData(obj);
                }
                else if(type.equals("종목별투자기관정보"))
                {
                    /*
                    JSONArray ja = obj.getJSONArray("내용");
                    for(int i = 0; i < ja.length(); i++)
                    {
                        JSONObject test = ja.getJSONObject(i);
                        String dateInfo = test.getString("일자");
                        String currMoney = test.getString("종가");
                        String updownIcon = test.getString("대비기호");
                        String indiHolder = test.getString("개인투자자");
                        String forgHolder = test.getString("외국인투자자");
                        String orgaHolder = test.getString("기관계");
                        String financeHolder = test.getString("금융투자");
                        String insuranceHolder = test.getString("보험");
                        String tusinHolder = test.getString("투신");
                        String etcFinanceHolder = test.getString("기타금융");
                        String bankHolder = test.getString("은행");
                        String yeongiHolder = test.getString("연기금등");
                        String samofundHolder = test.getString("사모펀드");
                        String counryHolder = test.getString("국가");
                        String etcLawHolder = test.getString("기타법인");
                        String naeforgHolder = test.getString("내외국인");
                        Log.i("InterationHandler", "일자: " +dateInfo+ ' '+
                                "종가: "+currMoney+' '+
                                "대비기호: "+updownIcon+' '+
                                "개인투자자: "+indiHolder+' '+
                                "외국인투자자: "+forgHolder+' '+
                                "기관계: "+orgaHolder+' '+
                                "금융투자: "+financeHolder+' '+
                                "보험: "+insuranceHolder+' '+
                                "투신: "+tusinHolder+' '+
                                "기타금융: "+etcFinanceHolder+' '+
                                "은행: "+bankHolder+' '+
                                "연기금등: "+yeongiHolder+' '+
                                "사모펀드: "+samofundHolder+' '+
                                "국가: "+counryHolder+' '+
                                "기타법인: "+etcLawHolder+' '+
                                "내외국인: "+naeforgHolder);
                    }
                    */
                    mCallback.updateWhoisList(obj);
                }
                else
                {
                    Log.e("InterationHandler", "HandleMessage : 응 Else에 걸렷어~");
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    //콜백 인터페이스 선언
    public interface ICallback
    {
        public void recvData(JSONObject obj);           // 주식 정보 수신, UI 수정.
        public void updateProgramList(JSONObject obj);  // KOSPI, KOSDAQ 리스트 갱신용.
        public void updateChartData(JSONObject obj);    // 차트 정보 갱신용.
        public void updateWhoisList(JSONObject obj);    // 주체별 매수 정보 갱신용.
    }

    //액티비티에서 콜백 함수를 등록하기 위함.
    public void registerCallback(ICallback cb)
    {
        mCallback = cb;
    }

    //액티비티에서 서비스 함수를 호출하기 위한 함수 생성
    public void TestXmit(String str)
    {
        socketOut.println(str);
    }
}
