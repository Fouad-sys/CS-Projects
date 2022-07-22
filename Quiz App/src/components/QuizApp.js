import React from "react";
import { TokenProvider } from "./context/TokenContext";
import { ContextProvider } from "./context/ContextContext";
import { ThemeProvider } from "@mui/material/styles";

import Create from "./create/Create";
import Play from "./play/Play";
import {
  queryClient,
  QueryClientProvider,
  ReactQueryDevtools,
} from "../config/queryClient";
import View from "./views/View";
import graaspTheme from "../layout/theme"


export const QuizApp = () => {
  const quiz = (
    <div className="container">
      <div className="title">
        <View />
      </div>
    </div>
  );

  const app = (
    <QueryClientProvider client={queryClient}>
      <ContextProvider>
        <TokenProvider> 
          <ThemeProvider theme={graaspTheme}>
              {quiz}
          </ThemeProvider>
        </TokenProvider>

      </ContextProvider>
      {process.env.NODE_ENV === "development" && <ReactQueryDevtools />}
    </QueryClientProvider>
  );
  return app;
};
