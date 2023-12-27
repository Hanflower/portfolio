package com.example.aram;

import android.widget.RelativeLayout;

public class PostModel {
    private String userid;
    private String username;
    private String postimage;
    private String origin;
    private String origin_writer;
    private String description;
    private String postdate;

    private int postID;
    private int num_of_bookmark;
    private int bookmarked;
    private int emoticoned;
    private int love, surprise, angry, sad;
    private int mine;
    private RelativeLayout userinfo;


    //    public PostModel(String userid, String username, String postimage, String origin, String origin_writer, String description, String postdate){
//        this.userid = userid;
//        this.username = username;
//        this.postimage = postimage;
//        this.origin = origin;
//        this.origin_writer = origin_writer;
//        this.description = description;
//        this.postdate = postdate;
//    }
    public String getUserid(){
        return userid;
    }
    public void setUserid(String userid){
        this.userid = userid;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){this.username = username;}
    public String getPostimage(){
        return postimage;
    }
    public void setPostimage(String postimage){
        this.postimage = postimage;
    }
    public String getOrigin(){
        return origin;
    }
    public void setOrigin(String origin){
        this.origin = origin;
    }
    public String getOrigin_writer(){
        return origin_writer;
    }
    public void setOrigin_writer(String origin_writer){
        this.origin_writer =  origin_writer;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public String getPostdate(){
        return postdate;
    }
    public void setPostdate(String postdate){
        this.postdate = postdate;
    }

    public int getPostID() {return postID;}
    public void setPostID(int postID) {this.postID = postID;}

    public int getNum_of_bookmark() {return num_of_bookmark;}
    public void setNum_of_bookmark(int num_of_bookmark) {this.num_of_bookmark = num_of_bookmark;}

    public int getBookmarked() {return bookmarked;}
    public void setBookmarked(int bookmarked) {this.bookmarked = bookmarked;}

    public int getEmoticoned() {return emoticoned;}
    public void setEmoticoned(int emoticoned) {this.emoticoned = emoticoned;}

    public int getLove() {return love;}
    public void setLove(int love) {this.love = love;}

    public int getSurprise() {return surprise;}
    public void setSurprise(int surprise) {this.surprise = surprise;}

    public int getAngry() {return angry;}
    public void setAngry(int angry) {this.angry = angry;}

    public int getSad() {return sad;}
    public void setSad(int sad) {this.sad = sad;}

    public int getMine() {return mine;}
    public void setMine(int mine) {this.mine = mine;}

//    public RelativeLayout getUserinfo(){return userinfo;}
//    public void setUserinfo(android.widget.RelativeLayout userinfo) {
//        this.userinfo = userinfo;
//    }
}
