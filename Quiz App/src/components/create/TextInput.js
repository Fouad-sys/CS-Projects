import {
  TextField,
  Grid,
  Typography,
} from "@mui/material";
import React from "react";

function TextInput({ text, setText, setDataChanged }) {
  return (
    <div align="center">
      <Grid
        container
        direction={"column"}
        align="left"
        columns={{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12 }}
      >
        <Grid item sx={{ pb: 2 }}>
          <Typography variant="p1"> Type Answer here:</Typography>
        </Grid>
        <Grid item sx={{ pb: 2 }}>
          <TextField
            value={text}
            placeholder="Enter Answer"
            label="Answer"
            name="quiz text answer"
            variant="outlined"
            onChange={(t) => {
              {
                setDataChanged(true);
                setText(t.target.value);
              }
            }}
          />
        </Grid>
      </Grid>
    </div>
  );
}

export default TextInput;
