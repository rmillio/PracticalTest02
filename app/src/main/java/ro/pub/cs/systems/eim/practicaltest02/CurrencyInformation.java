package ro.pub.cs.systems.eim.practicaltest02;

import androidx.annotation.NonNull;

public class CurrencyInformation {

   public String usd;
   public String eur;

    public CurrencyInformation(String usd, String eur) {
        this.usd = usd;
        this.eur = eur;
    }

    public CurrencyInformation() {}

    public String getUsd() {
        return usd;
    }

    public void setUsd(String usd) {
        this.usd = usd;
    }

    public String getEur() {
        return eur;
    }

    public void setEur(String eur) {
        this.eur = eur;
    }

    @NonNull
    @Override
    public String toString() {
        return "CurrencyInformation{" +
                "usd='" + usd + '\'' +
                ", eur='" + eur + '\'' +
                '}';
    }

}
