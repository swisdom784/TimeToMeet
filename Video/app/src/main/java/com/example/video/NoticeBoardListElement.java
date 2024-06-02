package com.example.video;

public class NoticeBoardListElement {
    private String memo;
    private String writer;
    public NoticeBoardListElement(String s,String w){this.memo = s; this.writer = w;}
    public String getWriter() {return writer;}
    public void setWriter(String writer) {this.writer = writer;}
    public String getMemo() {return memo;}
    public void setMemo(String s) {this.memo = s;}
}
