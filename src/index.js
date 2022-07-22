import React, { Component } from "react";
import ReactDOM from "react-dom";
import { mockServer, buildMockLocalContext } from "@graasp/apps-query-client";
import buildDatabase from "./data/db";
import "./index.css";
import { QuizApp } from "./components/QuizApp";

const MOCK_API = process.env.REACT_APP_MOCK_API;

if (MOCK_API) {
  const appContext = buildMockLocalContext(window.appContext);
  // automatically append item id as a query string
  const searchParams = new URLSearchParams(window.location.search);
  if (!searchParams.get("itemId")) {
    searchParams.set("itemId", appContext.itemId);
    window.location.search = searchParams.toString();
  }
  const database = window.Cypress ? window.database : buildDatabase(appContext);
  mockServer({ database, appContext });
}

ReactDOM.render(
  <div id="root">
    <QuizApp />
  </div>,
  document.getElementById("root")
);
