/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

  // @Value("${spring.datasource.url}")
  // private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM Rectangle");

      ArrayList<Rectangle> output = new ArrayList<Rectangle>();
      while (rs.next()) {
        String name = rs.getString("Name");
        Integer id = rs.getInt("ID");
        Float width = rs.getFloat("width");
        Float height = rs.getFloat("height");
        String color = rs.getString("color");
        String borderColor = rs.getString("bordercolor");
        Float borderWidth = rs.getFloat("borderwidth");
        Rectangle rectangle = new Rectangle();
        rectangle.setID(id);
        rectangle.setName(name);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.setColor(color);
        rectangle.setBorderColor(borderColor);
        rectangle.setBorderWidth(borderWidth);
        
        output.add(rectangle);
      }

      model.put("records", output);
      return "index";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @RequestMapping("/addrectangle")
  String getRectangleForm(Map<String, Object> model) {
    Rectangle rectangle = new Rectangle();
    model.put("rectangle", rectangle);
    return "addrectangle";
  }

  @PostMapping(
    path = "/addrectangle",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleBrowserRectangleSubmit(Map<String, Object> model, Rectangle rectangle) throws Exception {
    // Save the person data into the database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Rectangle (ID serial, name varchar(20), width REAL, height REAL, color varchar(20), bordercolor varchar(20), borderwidth REAL)");
      String sql = "INSERT INTO Rectangle (name, width, height, color, bordercolor, borderwidth) VALUES ('" + rectangle.getName() + "'," + rectangle.getWidth() 
      + "," + rectangle.getHeight() + ",'"  + rectangle.getColor() + "','" + rectangle.getBorderColor() + "',"   + rectangle.getBorderWidth() + ")";
      stmt.executeUpdate(sql);
      System.out.println(rectangle.getID() + " " + rectangle.getName()); // print person on console
      return "redirect:/";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }


  @GetMapping("/deleterectangle/{rid}")
  public String deleteSpecificRectangle(Map<String, Object> model, @PathVariable String rid){
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("DELETE FROM Rectangle WHERE ID = " + rid);
    

      return "redirect:/";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
    
  }

  @GetMapping("/deleterectangle/all")
  public String deleteAllRectangle(Map<String, Object> model){
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("TRUNCATE TABLE Rectangle RESTART IDENTITY");
    

      return "redirect:/";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
    
  }


  @GetMapping("/rectangle/{rid}")
  public String getSpecificRectangle(Map<String, Object> model, @PathVariable String rid){
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM Rectangle WHERE ID = " + rid);
      
      Rectangle rectangle = new Rectangle();
      if(rs.next()){
        String name = rs.getString("Name");
        Integer id = rs.getInt("ID");
        Float width = rs.getFloat("width");
        Float height = rs.getFloat("height");
        String color = rs.getString("color");
        String borderColor = rs.getString("bordercolor");
        Float borderWidth = rs.getFloat("borderwidth");
        
        rectangle.setID(id);
        rectangle.setName(name);
        rectangle.setWidth(width);
        rectangle.setHeight(height);
        rectangle.setColor(color);
        rectangle.setBorderColor(borderColor);
        rectangle.setBorderWidth(borderWidth);
      }
    
      model.put("record", rectangle);
      return "rectangle";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
    
  }

  

  

  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @Bean
  public DataSource dataSource() throws URISyntaxException {
    // if (dbUrl == null || dbUrl.isEmpty()) {
    //   return new HikariDataSource();
    // } else {
      HikariConfig config = new HikariConfig();
      URI dbUri = new URI(System.getenv("DATABASE_URL"));

      String username = dbUri.getUserInfo().split(":")[0];
      String password = dbUri.getUserInfo().split(":")[1];
      String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() + "?sslmode=require";
      config.setJdbcUrl(dbUrl);
      config.setUsername(username);
      config.setPassword(password);
      return new HikariDataSource(config);
    // }
  }

}

