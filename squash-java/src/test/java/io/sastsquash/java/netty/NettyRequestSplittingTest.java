/*
 * Copyright 2023 LightShield.
 * <p>
 * All Rights Reserved.
 */
package io.sastsquash.java.netty;

import org.junit.jupiter.api.Test;
import org.openrewrite.DocumentExample;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class NettyRequestSplittingTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec
          .recipe(new NettyRequestOrResponseSplitting())
          .parser(JavaParser.fromJavaVersion().classpath(
            "netty-buffer",
            "netty-codec-http",
            "netty-codec"
          ));
    }

    @Test
    @DocumentExample
    void nettyRequestSplitting() {
        rewriteRun(
          java(
            """
              import io.netty.buffer.ByteBuf;
              import io.netty.handler.codec.http.DefaultFullHttpResponse;
              import io.netty.handler.codec.http.DefaultFullHttpRequest;
              import io.netty.handler.codec.http.DefaultHttpRequest;
              import io.netty.handler.codec.http.DefaultHttpResponse;
              import io.netty.handler.codec.http.DefaultHttpHeaders;
              import io.netty.handler.codec.http.HttpMethod;
              import io.netty.handler.codec.http.HttpResponseStatus;
              import io.netty.handler.codec.http.HttpVersion;
                          
              class NettyRequestSplitting {
                          
                  void requestSplitting(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content) {
                      // BAD: Disables the internal request splitting verification
                      DefaultHttpHeaders badHeaders = new DefaultHttpHeaders(false);
                  
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultHttpHeaders goodHeaders = new DefaultHttpHeaders();
                  
                      // BAD: Disables the internal request splitting verification
                      DefaultHttpRequest badRequest = new DefaultHttpRequest(httpVersion, method, uri, false);
                  
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultHttpRequest goodResponse = new DefaultHttpRequest(httpVersion, method, uri);
                      
                      // BAD: Disables the internal request splitting verification
                      DefaultFullHttpRequest badRequest2 = new DefaultFullHttpRequest(httpVersion, method, uri, content, false);
                      
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultFullHttpRequest goodRequest2 = new DefaultFullHttpRequest(httpVersion, method, uri, content);
                  }
                  
                  void responseSplitting(HttpVersion httpVersion, HttpResponseStatus status, ByteBuf content) {
                      // BAD: Disables the internal response splitting verification
                      DefaultHttpHeaders badHeaders = new DefaultHttpHeaders(false);
                  
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultHttpHeaders goodHeaders = new DefaultHttpHeaders();
                  
                      // BAD: Disables the internal response splitting verification
                      DefaultHttpResponse badResponse = new DefaultHttpResponse(httpVersion, status, false);
                  
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultHttpResponse goodResponse = new DefaultHttpResponse(httpVersion, status);
                  
                      // BAD: Disables the internal response splitting verification
                      DefaultFullHttpResponse badResponse = new DefaultFullHttpResponse(httpVersion, status, false);
                      
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultFullHttpResponse goodResponse = new DefaultFullHttpResponse(httpVersion, status);
                      
                      // BAD: Disables the internal response splitting verification
                      DefaultFullHttpResponse badResponse2 = new DefaultFullHttpResponse(httpVersion, status, false);
                      
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultFullHttpResponse goodResponse2 = new DefaultFullHttpResponse(httpVersion, status);
                      
                      // BAD: Disables the internal response splitting verification
                      DefaultFullHttpResponse badResponse3 = new DefaultFullHttpResponse(httpVersion, status, content, false);
                      
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultFullHttpResponse goodResponse3 = new DefaultFullHttpResponse(httpVersion, status, content);
                  }
              }
              """,
            """
              import io.netty.buffer.ByteBuf;
              import io.netty.handler.codec.http.DefaultFullHttpResponse;
              import io.netty.handler.codec.http.DefaultFullHttpRequest;
              import io.netty.handler.codec.http.DefaultHttpRequest;
              import io.netty.handler.codec.http.DefaultHttpResponse;
              import io.netty.handler.codec.http.DefaultHttpHeaders;
              import io.netty.handler.codec.http.HttpMethod;
              import io.netty.handler.codec.http.HttpResponseStatus;
              import io.netty.handler.codec.http.HttpVersion;
                          
              class NettyRequestSplitting {
                          
                  void requestSplitting(HttpVersion httpVersion, HttpMethod method, String uri, ByteBuf content) {
                      // BAD: Disables the internal request splitting verification
                      DefaultHttpHeaders badHeaders = new DefaultHttpHeaders();
                  
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultHttpHeaders goodHeaders = new DefaultHttpHeaders();
                  
                      // BAD: Disables the internal request splitting verification
                      DefaultHttpRequest badRequest = new DefaultHttpRequest(httpVersion, method, uri);
                  
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultHttpRequest goodResponse = new DefaultHttpRequest(httpVersion, method, uri);
                      
                      // BAD: Disables the internal request splitting verification
                      DefaultFullHttpRequest badRequest2 = new DefaultFullHttpRequest(httpVersion, method, uri, content);
                      
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultFullHttpRequest goodRequest2 = new DefaultFullHttpRequest(httpVersion, method, uri, content);
                  }
                  
                  void responseSplitting(HttpVersion httpVersion, HttpResponseStatus status, ByteBuf content) {
                      // BAD: Disables the internal response splitting verification
                      DefaultHttpHeaders badHeaders = new DefaultHttpHeaders();
                  
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultHttpHeaders goodHeaders = new DefaultHttpHeaders();
                  
                      // BAD: Disables the internal response splitting verification
                      DefaultHttpResponse badResponse = new DefaultHttpResponse(httpVersion, status);
                  
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultHttpResponse goodResponse = new DefaultHttpResponse(httpVersion, status);
                  
                      // BAD: Disables the internal response splitting verification
                      DefaultFullHttpResponse badResponse = new DefaultFullHttpResponse(httpVersion, status);
                      
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultFullHttpResponse goodResponse = new DefaultFullHttpResponse(httpVersion, status);
                      
                      // BAD: Disables the internal response splitting verification
                      DefaultFullHttpResponse badResponse2 = new DefaultFullHttpResponse(httpVersion, status);
                      
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultFullHttpResponse goodResponse2 = new DefaultFullHttpResponse(httpVersion, status);
                      
                      // BAD: Disables the internal response splitting verification
                      DefaultFullHttpResponse badResponse3 = new DefaultFullHttpResponse(httpVersion, status, content);
                      
                      // GOOD: Verifies headers passed don't contain CRLF characters
                      DefaultFullHttpResponse goodResponse3 = new DefaultFullHttpResponse(httpVersion, status, content);
                  }
              }
              """
          )
        );
    }

    @Test
    void lessPopularApi() {
        rewriteRun(
          java(
            """
              import io.netty.handler.codec.http.CombinedHttpHeaders;
                          
              class Test {
                  void test() {
                      CombinedHttpHeaders headers = new CombinedHttpHeaders(false);
                  }
              }
              """,
            """
              import io.netty.handler.codec.http.CombinedHttpHeaders;
                          
              class Test {
                  void test() {
                      CombinedHttpHeaders headers = new CombinedHttpHeaders();
                  }
              }
              """
          )
        );
    }
}
