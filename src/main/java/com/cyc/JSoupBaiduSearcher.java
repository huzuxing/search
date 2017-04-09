/**
 *
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.cyc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class JSoupBaiduSearcher extends AbstractBaiduSearcher{

    @Override
    public SearchResult search(String keyword) {
        return search(keyword, 1);
    }
    @SuppressWarnings("resource")
	@Override
    public SearchResult search(String keyword, int page) {
        int pageSize = 10;
        //百度搜索结果每页大小为10，pn参数代表的不是页数，而是返回结果的开始数
        //如获取第一页则pn=0，第二页则pn=10，第三页则pn=20，以此类推，抽象出模式：(page-1)*pageSize
        String url = "http://www.baidu.com/s?pn="+(page-1)*pageSize+"&wd="+keyword;
        
        SearchResult searchResult = new SearchResult();
        searchResult.setPage(page);
        List<ResultData> datas = new ArrayList<>();
        try {
        	
        	Connection con = Jsoup.connect(url);
          //浏览器可接受的MIME类型。
//        	con.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");  
//        	con.header("Accept-Encoding", "gzip, deflate");  
//        	con.header("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");  
//        	con.header("Connection", "keep-alive");  
//        	con.header("Host", url);  
        	con.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");  
            Document document = con.get();
            if (!validateHtml(document)) {
            	WebClient client = new WebClient();
            	client.getOptions().setJavaScriptEnabled(true);
            	client.getOptions().setCssEnabled(false);
            	client.getOptions().setThrowExceptionOnScriptError(false);
            	client.getOptions().setTimeout(20000);
            	HtmlPage htmlPage = client.getPage(url);
            	String xmlPage = htmlPage.asXml();
            	document = Jsoup.parse(xmlPage, url);
            }
            //获取搜索结果数目
            int total = getBaiduSearchResultCount(document);
           
            searchResult.setTotal(total);
            int len = 10;
            if (total < 1) {
                return null;
            }
            //如果搜索到的结果不足一页
            if (total < 10) {
                len = total;
            }
            for (int i = 0; i < len; i++) {
                String titleCssQuery = "html body div div div div#content_left div#" + (i + 1 + (page-1)*pageSize) + ".result.c-container h3.t a";
                String summaryCssQuery = "html body div div div div#content_left div#" + (i + 1 + (page-1)*pageSize) + ".result.c-container div.c-abstract";
                Element titleElement = document.select(titleCssQuery).first();
                String href = "";
                String titleText = "";
                if(titleElement != null){
                    titleText = titleElement.text();
                    href = titleElement.attr("href");
                }else{
                    //处理百度百科
                    titleCssQuery = "html body div#out div#in div#wrapper div#container div#content_left div#1.result-op h3.t a";
                    summaryCssQuery = "html body div#out div#in div#wrapper div#container div#content_left div#1.result-op div p";
                    System.out.println("处理百度百科 titleCssQuery:" + titleCssQuery);
                    System.out.println("处理百度百科 summaryCssQuery:" + summaryCssQuery);
                    titleElement = document.select(titleCssQuery).first();
                    if(titleElement != null){
                        titleText = titleElement.text();
                        href = titleElement.attr("href");
                    }
                }
                System.out.println(titleText);
                Element summaryElement = document.select(summaryCssQuery).first();
                //处理百度知道
                if(summaryElement == null){
                    summaryCssQuery = summaryCssQuery.replace("div.c-abstract","font");
                    System.out.println("处理百度知道 summaryCssQuery:" + summaryCssQuery);
                    summaryElement = document.select(summaryCssQuery).first();
                }
                String summaryText = "";
                if(summaryElement != null){
                    summaryText = summaryElement.text(); 
                }
                System.out.println(summaryText);                
                
                if (titleText != null && !"".equals(titleText.trim()) && summaryText != null && !"".equals(summaryText.trim())) {
                    if (href != null) {
                        String content = Tools.getHTMLContent(href);
                        if (null != content && !"".equals(content)) {
                        	String simA = keyword , simB = content;
        					if (keyword.length() < content.length()) {
        						simA = content;
        						simB = keyword;
        						// 求相似度
                            	double rate = Computeclass.SimilarDegree(simA, simB);
                            	if (rate > 0) {
                            		ResultData data = new ResultData();
                            		data.setOrigWord(keyword);
                            		data.setSimWord(Computeclass.longestCommonSubstring(simA, simB));
                            		data.setRate("100%");
                            		datas.add(data);
                            	}
        					}
                        }         
                    } else {
                        System.out.println("页面正确提取失败");
                    }
                } else {
                    System.out.println("获取搜索结果列表项出错:" + titleText + " - " + summaryText);
                }
            }
            
            
        } catch (IOException ex) {
            System.out.println("搜索出错" + ex);
        }
        searchResult.setDatas(datas);
        return searchResult;
    }
    
    private boolean validateHtml(Document document) {
    	String cssQuery = "html body div div div div.nums";
        System.out.println("total cssQuery: " + cssQuery);
        Element totalElement = document.select(cssQuery).first();
        return null == totalElement ? false : true;
    }
    /**
     * 获取百度搜索结果数
     * 获取如下文本并解析数字：
     * 百度为您找到相关结果约13,200个
     * @param document 文档
     * @return 结果数
     */
    private int getBaiduSearchResultCount(Document document){
        String cssQuery = "html body div div div div.nums";
        System.out.println("total cssQuery: " + cssQuery);
        Element totalElement = document.select(cssQuery).first();
        String totalText = totalElement.text(); 
        System.out.println("搜索结果文本：" + totalText);
        
        String regEx="[^0-9]";   
        Pattern pattern = Pattern.compile(regEx);      
        Matcher matcher = pattern.matcher(totalText);
        totalText = matcher.replaceAll("");
        int total = Integer.parseInt(totalText);
        System.out.println("搜索结果数：" + total);
        return total;
    }

    public static void main(String[] args) {        
        Searcher searcher = new JSoupBaiduSearcher();
        SearchResult searchResult = searcher.search("胡祖兴",1);
        List<Webpage> webpages = searchResult.getWebpages();
        if (webpages != null) {
            int i = 1;
            System.out.println("搜索结果 当前第 " + searchResult.getPage() + " 页，页面大小为：" + searchResult.getPageSize() + " 共有结果数：" + searchResult.getTotal());
            for (Webpage webpage : webpages) {
                System.out.println("搜索结果 " + (i++) + " ：");
                System.out.println("标题：" + webpage.getTitle());
                System.out.println("URL：" + webpage.getUrl());
                System.out.println("摘要：" + webpage.getSummary());
                System.out.println("正文：" + webpage.getContent());
                System.out.println("");
            }
        } else {
        	System.out.println("没有搜索到结果");
        }
    }
}