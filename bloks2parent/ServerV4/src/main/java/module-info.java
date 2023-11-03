module ServerV4 {
  requires java.sql;
//  requires javax.annotation.api;
//  requires java.ws.rs;
  requires java.net.http;
  requires com.google.gson;
//  requires javax.servlet.api; kann weg pm
  requires javax.websocket.api;
  requires com.fasterxml.jackson.databind;
  requires tyrus.client;
  requires tyrus.server;
//  requires jetty.servlet; kann weg pm
//  requires jetty.server; TODO kann weg pm
  requires javafx.graphics;
  requires javafx.fxml;
  requires javafx.controls;
  //test
  requires javafx.base;



  requires com.fasterxml.jackson.annotation;
  // requires RestServer;
  exports ai;
  exports boards;
  exports gamelogic;
  exports datatypes to
      com.fasterxml.jackson.databind;

  opens gui;
  opens websockets;

  exports model;

  opens ai;
  opens gamelogic to
      com.fasterxml.jackson.databind;
  opens boards to
      com.fasterxml.jackson.databind;
  opens gametiles to
      com.fasterxml.jackson.databind;
  opens model to
      com.fasterxml.jackson.databind;
}
