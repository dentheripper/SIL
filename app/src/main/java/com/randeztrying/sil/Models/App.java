package com.randeztrying.sil.Models;

public class App implements Comparable<App>{

    private String packageName;
    private String appName;
    private boolean isChecked;

    public App() {this.isChecked = false;}

    @Override
    public int compareTo(App other) {
        int compareInt = this.appName.compareTo(other.appName);
        return Integer.compare(compareInt, 0);
    }

    public String getPackageName() {return packageName;}
    public void setPackageName(String packageName) {this.packageName = packageName;}
    public String getAppName() {return appName;}
    public void setAppName(String appName) {this.appName = appName;}
    public boolean isChecked() {return isChecked;}
    public void setChecked(boolean checked) {isChecked = checked;}
}