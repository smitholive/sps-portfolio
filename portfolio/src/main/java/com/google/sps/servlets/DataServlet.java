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
import java.util.ArrayList;
import java.util.List;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
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

  public DataServlet(){
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
     Query query = new Query("Entry").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> messages = new ArrayList<String>();
    ArrayList<Date> timestamps = new ArrayList<Date>();

    for (Entity entity : results.asIterable()) {
      String name = (String) entity.getProperty("name");
      String message = (String) entity.getProperty("message");
      Date timestamp = new Date((long) entity.getProperty("timestamp"));
      names.add(name);
      messages.add(message);
      timestamps.add(timestamp);
    }
    response.setContentType("text/html;");
    for(int i = 0; i < messages.size(); i++){
        response.getWriter().printf("%s at %s said: ", names.get(i), timestamps.get(i).toString());
        response.getWriter().println(messages.get(i));
    }
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

    // note: this URL will have to change once site is deployed
    response.sendRedirect("https://8080-dot-12685145-dot-devshell.appspot.com/contact.html");
  }
}
