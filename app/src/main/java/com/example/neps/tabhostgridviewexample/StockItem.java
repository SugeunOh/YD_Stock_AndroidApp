package com.example.neps.tabhostgridviewexample;

public class StockItem {
    String rank;                    // 순위
    String code;                    // 종목코드
    String name;                    // 종목명
    String currentPrice;            // 현재가
    String straightPurchaseVolume;  //순매수 금액 or 수량
    String fluctuationImage;        // 등락기호
    String fluctuationRate;         // 등락율

    public StockItem(String rank, String code, String name, String currentPrice, String straightPurchaseVolume, String fluctuationImage, String fluctuationRate) {
        this.rank = rank;
        this.code = code;
        this.name = name;
        this.currentPrice = currentPrice;
        this.straightPurchaseVolume = straightPurchaseVolume;
        this.fluctuationImage = fluctuationImage;
        this.fluctuationRate = fluctuationRate;
    }

    public void setRank(String rank) {this.rank = rank;}
    public void setCode(String code) {this.code = code;}
    public void setName(String name) {this.name = name;}
    public void setCurrentPrice(String currentPrice) {this.currentPrice = currentPrice;}
    public void setStraightPurchaseVolume(String straightPurchaseVolume){this.straightPurchaseVolume = straightPurchaseVolume;}
    public void setFluctuationImage(String fluctuationImage){this.fluctuationImage = fluctuationImage;}
    public void setFluctuationRate(String fluctuationRate){this.fluctuationRate = fluctuationRate;}

    public String getRank() {return rank;}
    public String getCode() {return code;}
    public String getName() {return name;}
    public String getCurrentPrice() {return currentPrice;}
    public String getStraightPurchaseVolume() {return straightPurchaseVolume;}
    public String getFluctuationImage() {return fluctuationImage;}
    public String getFluctuationRate() {return fluctuationRate;}
}