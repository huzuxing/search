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
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleAjaxSearcher implements GoogleSearcher{

    @Override
    public SearchResult search(String keyword) {
        return search(keyword, 1);
    }
    @Override
    public SearchResult search(String keyword, int page) {
        int pageSize = 8;
        //谷歌搜索结果每页大小为8，start参数代表的是返回结果的开始数
        //如获取第一页则start=0，第二页则start=10，第三页则start=20，以此类推，抽象出模式：(page-1)*pageSize
        String url = "http://ajax.googleapis.com/ajax/services/search/web?start="+(page-1)*pageSize+"&rsz=large&v=1.0&q=" + keyword;
        
        SearchResult searchResult = new SearchResult();
        searchResult.setPage(page);
        List<Webpage> webpages = new ArrayList<>();
        try {
            HttpClient httpClient = new HttpClient();
            GetMethod getMethod = new GetMethod(url);

            httpClient.executeMethod(getMethod);
            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler());

            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            InputStream in = getMethod.getResponseBodyAsStream();
            byte[] responseBody = Tools.readAll(in);
            String response = new String(responseBody, "UTF-8");
            JSONObject json = new JSONObject(response);
            String totalResult = json.getJSONObject("responseData").getJSONObject("cursor").getString("estimatedResultCount");
            int totalResultCount = Integer.parseInt(totalResult);
            System.out.println("搜索返回记录数： " + totalResultCount);
            searchResult.setTotal(totalResultCount);

            JSONArray results = json.getJSONObject("responseData").getJSONArray("results");

            System.out.println("搜索结果:");
            for (int i = 0; i < results.length(); i++) {
                Webpage webpage = new Webpage();
                JSONObject result = results.getJSONObject(i);
                //提取标题
                String title = result.getString("titleNoFormatting");
                System.out.println("标题：" + title);
                webpage.setTitle(title);
                //提取摘要
                String summary = result.get("content").toString();
                summary = summary.replaceAll("<b>", "");
                summary = summary.replaceAll("</b>", "");
                summary = summary.replaceAll("\\.\\.\\.", "");
                System.out.println("摘要：" + summary);
                webpage.setSummary(summary);
                //从URL中提取正文
                String _url = result.get("url").toString();
                webpage.setUrl(_url);
                String content = Tools.getHTMLContent(_url);
                System.out.println("正文：" + content);
                webpage.setContent(content);
                webpages.add(webpage);
            }
        } catch (IOException | JSONException | NumberFormatException e) {
            System.out.println("执行搜索失败：" + e);
        }
        searchResult.setWebpages(webpages);
        return searchResult;
    }

    public static void main(String args[]) {
        String keyword = "杨尚川";
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e);
        }
        
        Searcher searcher = new GoogleAjaxSearcher();
        SearchResult searchResult = searcher.search(keyword, 1);
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
            }
        } else {
            System.out.println("没有搜索到结果");
        }
    }
}