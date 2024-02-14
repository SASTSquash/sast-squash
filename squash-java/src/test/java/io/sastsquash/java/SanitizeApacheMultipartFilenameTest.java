/*
 * Copyright 2023 LightShield.
 * <p>
 * All Rights Reserved.
 */
package io.sastsquash.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.java.Assertions.java;

public class SanitizeApacheMultipartFilenameTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new SanitizeApacheMultipartFilename());
    }

    @Test
    void apacheCommons() {
        rewriteRun(
          spec -> spec.parser(JavaParser.fromJavaVersion().classpath("commons-fileupload", "javax.servlet-api")),
          java(
            """                
              import org.apache.commons.fileupload.FileItem;
              import org.apache.commons.fileupload.FileUploadException;
              import org.apache.commons.fileupload.disk.DiskFileItemFactory;
              import org.apache.commons.fileupload.servlet.ServletFileUpload;
                  
              import javax.servlet.http.HttpServletRequest;
              import java.io.File;
              import java.io.FileNotFoundException;
              import java.io.FileOutputStream;
              import java.util.List;
                  
              public class ApacheMultipartFilename {
                  public void handleCommonsFileUpload(HttpServletRequest request) throws FileUploadException, FileNotFoundException {
                      DiskFileItemFactory factory = new DiskFileItemFactory();
                      ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
                      List<FileItem> items = servletFileUpload.parseRequest(request);
                      for (FileItem item : items) {
                          File f = new File("uploadDir", item.getName());
                          FileOutputStream os = new FileOutputStream(f);
                      }
                  }
              }
              """,
            """                
              import org.apache.commons.fileupload.FileItem;
              import org.apache.commons.fileupload.FileUploadException;
              import org.apache.commons.fileupload.disk.DiskFileItemFactory;
              import org.apache.commons.fileupload.servlet.ServletFileUpload;
                  
              import javax.servlet.http.HttpServletRequest;
              import java.io.File;
              import java.io.FileNotFoundException;
              import java.io.FileOutputStream;
              import java.util.List;
                  
              public class ApacheMultipartFilename {
                  public void handleCommonsFileUpload(HttpServletRequest request) throws FileUploadException, FileNotFoundException {
                      DiskFileItemFactory factory = new DiskFileItemFactory();
                      ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
                      List<FileItem> items = servletFileUpload.parseRequest(request);
                      for (FileItem item : items) {
                          File f = new File("uploadDir", item.getName());
                          if (!f.toPath().normalize().startsWith("uploadDir")) {
                              throw new RuntimeException("Bad uploaded file name");
                          }
                          FileOutputStream os = new FileOutputStream(f);
                      }
                  }
              }
              """
          )
        );
    }

    @Test
    void apacheCommonsJakarta() {
        rewriteRun(
          spec -> spec.parser(JavaParser.fromJavaVersion().classpath(
            "commons-fileupload2-jakarta-servlet6",
            "commons-fileupload2-core",
            "commons-io",
            "jakarta.servlet-api"
          )),
          java(
            """
              import org.apache.commons.fileupload2.core.DiskFileItem;
              import org.apache.commons.fileupload2.core.DiskFileItemFactory;
              import org.apache.commons.fileupload2.core.FileUploadException;
              import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;

              import java.io.File;
              import java.io.FileNotFoundException;
              import java.io.FileOutputStream;
              import java.util.List;

              public class ApacheMultipartFilenameJakarta {

                  public void handleCommonsFileUploadJakarta(jakarta.servlet.http.HttpServletRequest request) throws FileUploadException, FileNotFoundException {
                      DiskFileItemFactory factory = DiskFileItemFactory.builder().get();
                      JakartaServletFileUpload<DiskFileItem, DiskFileItemFactory> servletFileUpload = new JakartaServletFileUpload<>(factory);
                      List<DiskFileItem> items = servletFileUpload.parseRequest(request);
                      for (DiskFileItem item : items) {
                          File f = new File("uploadDir", item.getName());
                          if (!f.toPath().normalize().startsWith("uploadDir")) {
                              throw new RuntimeException("Bad uploaded file name");
                          }
                          FileOutputStream os = new FileOutputStream(f);
                      }
                  }
              }
              """
          )
        );
    }
}
