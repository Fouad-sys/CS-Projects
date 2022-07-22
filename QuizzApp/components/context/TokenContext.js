import React, { createContext } from "react";
import qs from "qs";
import PropTypes from "prop-types";
import { hooks } from "../../config/queryClient";

const TokenContext = createContext();

const TokenProvider = ({ children }) => {
  const { itemId } = qs.parse(window.location.search, {
    ignoreQueryPrefix: true,
  });
  const { data, isLoading, isError } = hooks.useAuthToken(itemId);

  if (isLoading) {
    return "Loading";
  }

  if (isError) {
    console.log("error");
  }

  const value = data;
  return (
    <TokenContext.Provider value={value}>{children}</TokenContext.Provider>
  );
};

TokenProvider.propTypes = {
  children: PropTypes.node,
};

TokenProvider.defaultProps = {
  children: null,
};

export { TokenContext, TokenProvider };
