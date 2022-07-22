import React, { createContext, useEffect } from "react";
import PropTypes from "prop-types";
import qs from "qs";
import { hooks } from "../../config/queryClient";

const Context = createContext();

const ContextProvider = ({ children }) => {
  const { itemId } = qs.parse(window.location.search, {
    ignoreQueryPrefix: true,
  });
  const {
    data: context,
    isLoading,
    isError,
  } = hooks.useGetLocalContext(itemId);

  if (isLoading) {
    return "Loading";
  }

  if (isError) {
    console.log("error");
  }

  const value = context ?? {};

  return <Context.Provider value={value}>{children}</Context.Provider>;
};

ContextProvider.propTypes = {
  children: PropTypes.node,
};

ContextProvider.defaultProps = {
  children: null,
};

export { Context, ContextProvider };
