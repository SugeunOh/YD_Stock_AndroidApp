package com.example.neps.tabhostgridviewexample;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class StockItemAdapter extends BaseAdapter {
    ArrayList<StockItem> datas;
    LayoutInflater inflater;

    public StockItemAdapter(LayoutInflater inflater, ArrayList<StockItem> datas) {
        // TODO Auto-generated constructor stub
        this.datas= datas;
        this.inflater= inflater;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return datas.size(); //datas의 개수를 리턴
    }


    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return datas.get(position);//datas의 특정 인덱스 위치 객체 리턴.
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if( convertView==null){
            convertView= inflater.inflate(R.layout.program_list_row, null);
        }

        TextView itemName= (TextView)convertView.findViewById(R.id.itemName);
        TextView currentPrice= (TextView)convertView.findViewById(R.id.currentPrice);
        TextView straightPurchaseVolume= (TextView)convertView.findViewById(R.id.straightPurchaseVolume);
        ImageView fluctuationImage= (ImageView)convertView.findViewById(R.id.fluctuationImage);
        TextView fluctuationRate= (TextView)convertView.findViewById(R.id.fluctuationRate);

        itemName.setText(datas.get(position).getName());
        currentPrice.setText(datas.get(position).getCurrentPrice());
        straightPurchaseVolume.setText(datas.get(position).getStraightPurchaseVolume());

        fluctuationRate.setText(datas.get(position).getFluctuationRate());



        if(datas.get(position).getFluctuationImage().equals("2")){
            fluctuationImage.setImageResource(R.drawable.triangle_up);
            fluctuationRate.setTextColor(Color.RED);
        }
        else if(datas.get(position).getFluctuationImage().equals("5")){
            fluctuationImage.setImageResource(R.drawable.triangle_down);
            fluctuationRate.setTextColor(Color.BLUE);
        }
        else{
            fluctuationImage.setImageResource(0);
            fluctuationRate.setTextColor(Color.BLACK);

        }






        return convertView;
    }
}