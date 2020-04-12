package com.dungdemo.shopnow.utils;

public class MoneyType {
    public static String toMoney(int n){
        String s=Integer.toString(n);
        String m="";
        int count=0;
        for(int i=s.length()-1;i>=0;i--){
            count++;
                m=m+s.charAt(i);
                if(count==3 &&i!=0){
                    m=m+".";
                    count=0;
                }
        }
        String m1="";
        for(int i=m.length()-1;i>=0;i--){
            m1=m1+m.charAt(i);
        }
        return m1;
    }
    public static String toMoney(long n){
        String s=Long.toString(n);
        String m="";
        int count=0;
        for(int i=s.length()-1;i>=0;i--){
            count++;
            m=m+s.charAt(i);
            if(count==3 &&i!=0){
                m=m+".";
                count=0;
            }
        }
        String m1="";
        for(int i=m.length()-1;i>=0;i--){
            m1=m1+m.charAt(i);
        }
        return m1;
    }
}
