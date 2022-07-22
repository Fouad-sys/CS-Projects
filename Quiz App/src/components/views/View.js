import React, { useContext } from "react";
import { Context } from "../context/ContextContext";
import { PERMISSION_LEVELS } from "../config/settings";
import Create from "../create/Create";
import Play from "../play/Play";

const View = () => {
  const context = useContext(Context);
  switch (context.get("permission")) {
    case PERMISSION_LEVELS.ADMIN:
    case PERMISSION_LEVELS.WRITE:
      return <Create />;

    case PERMISSION_LEVELS.READ:
    default:
      return <Play />;
  }
};

export default View;
