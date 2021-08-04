package com.example.digitallibrary_admin.ui.notes;

public class notemodel {

    String author,bookurl,name,size;

    public notemodel() {
    }

    public notemodel(String author, String bookurl, String name, String size) {
        this.author = author;
        this.bookurl = bookurl;
        this.name = name;
        this.size = size;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookurl() {
        return bookurl;
    }

    public void setBookurl(String bookurl) {
        this.bookurl = bookurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
