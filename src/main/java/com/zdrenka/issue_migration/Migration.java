package com.zdrenka.issue_migration;

import com.zdrenka.model.Comment;
import com.zdrenka.model.Issue;
import com.zdrenka.utils.Utils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


public class Migration {

    private static CloseableHttpClient httpClient;

    public static void main(String args[] ) throws Exception {
//        Utils utils = new Utils();
//        List<Issue> issues = utils.csvToIssue(args[0]);
//        System.out.println("Found " + issues.size() + " issues");
        httpClient = HttpClients.createDefault();
        sendGet();
    }

    private static void sendGet() throws Exception {
        HttpGet request = new HttpGet("https://zdrenka.backlog.com/api/v2/issues?apiKey=KbUGPVXaIuZJkXNRJyWDunbjMpIF5GnYx8ppMUlQepKdroz680HqBdYPIsHnSC7q");
        try (CloseableHttpResponse response = httpClient.execute(request)) {

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            if (entity != null) {
                // return it as a String
                String result = EntityUtils.toString(entity);
                System.out.println(result);
            }

        }

    }

//    private void sendPost() throws Exception {
//
//        HttpPost post = new HttpPost("https://httpbin.org/post");
//
//        // add request parameter, form parameters
//        List<NameValuePair> urlParameters = new ArrayList<>();
//        urlParameters.add(new BasicNameValuePair("username", "abc"));
//        urlParameters.add(new BasicNameValuePair("password", "123"));
//        urlParameters.add(new BasicNameValuePair("custom", "secret"));
//
//        post.setEntity(new UrlEncodedFormEntity(urlParameters));
//
//        try (CloseableHttpClient httpClient = HttpClients.createDefault();
//             CloseableHttpResponse response = httpClient.execute(post)) {
//
//            System.out.println(EntityUtils.toString(response.getEntity()));
//        }
//
//    }


}
