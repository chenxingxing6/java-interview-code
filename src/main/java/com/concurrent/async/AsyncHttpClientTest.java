package com.concurrent.async;

import lombok.SneakyThrows;
import org.asynchttpclient.*;

/**
 * created by lanxinghua@2dfire.com on 2020/8/17
 * 异步处理http请求
 */
public class AsyncHttpClientTest {

    @SneakyThrows
    public static void main(String[] args) {
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        ListenableFuture<Response> execute = asyncHttpClient.prepareGet("http://www.baidu.com").execute();


        RequestBuilder builder = new RequestBuilder();
        builder.setUrl("http://www.baidu.com");
        builder.addQueryParam("name", "test");
        ListenableFuture<Response> future = asyncHttpClient.executeRequest(builder, new AsyncCompletionHandler<Response>() {
            @Override
            public Response onCompleted(Response response) throws Exception {
                System.out.println("ok");
                return response;
            }
        });
        Response response1 = future.get();
        System.out.println(response1.getResponseBody());

    }
}
