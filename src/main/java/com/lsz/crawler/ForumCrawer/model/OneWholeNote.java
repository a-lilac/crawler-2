package com.lsz.crawler.ForumCrawer.model;

import java.util.ArrayList;

/**
 * Created by asus on 2017/4/19.
 * 每个帖子的全部信息
 */
public class OneWholeNote {
    private String section1;  //归属1
    private String section2;   //归属2
    private String clickCount;  //点击量
    private String replyCount;   //回复量
    private Post main;     //主贴信息
    private ArrayList<Post> replys=new ArrayList<Post>(50);   //后面的回复包括楼主后面加上去的
    public String getSection1() {
        return section1;
    }

    public void setSection1(String section1) {
        this.section1 = section1;
    }

    public String getSection2() {
        return section2;
    }

    public void setSection2(String section2) {
        this.section2 = section2;
    }

    public String getClickCount() {
        return clickCount;
    }

    public void setClickCount(String clickCount) {
        this.clickCount = clickCount;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }

    public Post getMain() {
        return main;
    }

    public void setMain(Post main) {
        this.main = main;
    }

    public ArrayList<Post> getReplys() {
        return replys;
    }

    public void setReplys(ArrayList<Post> replys) {
        this.replys = replys;
    }

}
