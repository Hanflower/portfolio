package com.example.aram;

public class UserFollowModel {
    private String followid;
    private String followname;

    private int followed;
    private int num_of_following;
    private int num_of_follower;

    public String getFollowid(){return followid;}
    public void setFollowid(String followid){this.followid = followid;}
    public String getFollowname(){return followname;}
    public void setFollowname(String followname){this.followname=followname;}

    public int getFollowed(){return followed;}
    public void setFollowed(int followed){this.followed = followed;}

    public int getNum_of_follower(){return num_of_follower;}
    public void setNum_of_follower(int num_of_follower){this.num_of_follower = num_of_follower;}

    public int getNum_of_following(){return num_of_following;}
    public void setNum_of_following(int num_of_following){this.num_of_following = num_of_follower;}
}
