// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import java.util.Date; 
import java.util.List;
import java.util.ArrayList;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  public DataServlet() {}

  class Comment {
      public String name;
      public String message;
      public String timestamp;

      public Comment(String name, String message, String timestamp) {
          this.name = name;
          this.message = message;
          this.timestamp = timestamp;
      }
  }
  
  private String toGson(ArrayList<Comment> comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Entry").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // iterate through data / entities and pull pertinent info from them
    // encapsulate data in comment class
    // then send respond with comment in JSON string format

    response.setContentType("application/json;");
    ArrayList<Comment> jsonArray = new ArrayList<Comment>();

    for (Entity entity : results.asIterable()) {

      String name = (String) entity.getProperty("name");
      String message = (String) entity.getProperty("message");
      Date timestamp = new Date((long) entity.getProperty("timestamp"));

      Comment comment = new Comment(name, message, timestamp.toString());
      jsonArray.add(comment);
    }
    response.getWriter().println(toGson(jsonArray));
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    String message = request.getParameter("message-input");
    String name = request.getParameter("name-input");
    // get date & time of post request
    Date date = new Date(); 

    Entity entryEntity = new Entity("Entry");
    entryEntity.setProperty("timestamp", date.getTime());
    entryEntity.setProperty("message", message);
    entryEntity.setProperty("name", name);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(entryEntity);

    response.sendRedirect("/contact.html");
  }
}
