package com.lsz.crawler.ForumCrawer;

import com.alibaba.fastjson.JSON;

import com.lsz.crawler.ForumCrawer.model.OneWholeNote;
import com.lsz.crawler.ForumCrawer.model.Post;
import com.lsz.crawler.ForumCrawer.util.FileUtil;
import com.lsz.crawler.ForumCrawer.util.MailUtil;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.mail.MessagingException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by asus on 2017/4/19.
 */
public class ForumCrawler extends WebCrawler {
    private final static Pattern FILTERS = Pattern
            .compile(".*(\\.(css|js|jpg" + "|png|tiff?|mid|mp2|mp3|mp4" + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    private final static String prefix="http://bbs.tianya.cn";
    private final static Pattern URL_PARAMS_PATTERN=Pattern.compile("/post-");
    public static ConcurrentHashMap<String,OneWholeNote> hasCraoweid= new ConcurrentHashMap<String,OneWholeNote>();
    private final static Pattern ID_PATTERN=Pattern.compile("post-\\w*-(\\d*)-");
    private static volatile int times=0;
    //哪些url爬
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url ){
        String href=url.getURL().toLowerCase();
        if(FILTERS.matcher(href).matches()){
            return false;
        }else if(href.startsWith(prefix)&&URL_PARAMS_PATTERN.matcher(href).find()){
            File file=new File("e:\\url.txt");
            try {
                FileWriter fw=new FileWriter(file,true);
                fw.write(href);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    @Override
    public void visit(Page page){
        File file=new File("e:\\ty2.txt");
        FileWriter fw=null;
        try {
            fw=new FileWriter(file,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String url=page.getWebURL().getURL();
        Matcher match=ID_PATTERN.matcher(url);
        String id="no";
        if(match.find()){
            id=match.group(1);
        }else {
            return;
        }
        if(page.getParseData() instanceof HtmlParseData){
            OneWholeNote oneWholeNote=null;
            if(hasCraoweid.get(id)!=null){
                oneWholeNote=hasCraoweid.get(id);
            }else {
                oneWholeNote=new OneWholeNote();
                hasCraoweid.put(id,oneWholeNote);
            }
            try {
                fw.write("Note id:"+id+"\r\n");
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            HtmlParseData htmlParseData=(HtmlParseData)page.getParseData();
            String html=htmlParseData.getHtml();
            Document doc= Jsoup.parse(html);
            Element a=doc.select("#bd").first();
            Element b=a.select("p.crumbs>em").first();
            Elements sectionEle=b.select("a");
            oneWholeNote.setSection1(sectionEle.get(0).text());
            if(sectionEle.size()>1){
            oneWholeNote.setSection2(sectionEle.get(1).text());}
            Element  head=doc.select("#post_head").first();
            Elements spans=head.select("div.atl-menu").select("div.atl-info").select("span");
            Post main=new Post();
            main.setAuthor(spans.get(0).select("a").first().text());    //楼主名字
            main.setPublish_date(spans.get(1).text());                //发布时间
            oneWholeNote.setClickCount(spans.get(2).text());         //点击量
            oneWholeNote.setReplyCount(spans.get(3).text());        //回帖量
            String title=head.select("h1.atl-title>.s_title").text();
            main.setTitle(title);                      //主贴标题
            try {
                fw.write(title);
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Element atlmain=doc.select(".atl-main").first();
            String content=atlmain.select(".host-item").select(".atl-content").select(".bbs-content").text();
            main.setContent(content);                    //主贴内容
            oneWholeNote.setMain(main);                     //设置主贴对象进去
            Elements replyEle=atlmain.select(".atl-item");
            for(Element e:replyEle){
                if(!e.hasClass("host-item")){
                    Post reply=new Post();
                    reply.setAuthor(e.attr("_host"));
                    reply.setPublish_date(e.attr("js_restime"));
                    String replyContent=e.select(".bbs-content").text();
                    reply.setContent(replyContent);
                    oneWholeNote.getReplys().add(reply);
                }
            }
            /*写入帖子*/
            String str=JSON.toJSONString(oneWholeNote);
            try {
                fw.write(str);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**爬取时间比较长,***/
        if(ForumCrawler.hasCraoweid.size()==50){
            String str= JSON.toJSONString(ForumCrawler.hasCraoweid);
            hasCraoweid.clear();
            File ty=new File("g:\\tianya"+times+".json");

            try {
                BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(ty));
                bufferedWriter.write(str);
                bufferedWriter.close();
                File zipFile= FileUtil.ZipFile2(ty,"e:\\tianya"+times+".zip");
                MailUtil mailUtil=new MailUtil();
                try {
                    mailUtil.sendFileEmail("Crawler tianya notes",(times*50+1)+"-->"+(times+1)*50+"pieces of Data ","530735771@qq.com",zipFile);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            times++;
        }



    }
}
