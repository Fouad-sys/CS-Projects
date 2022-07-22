import {
  TextField,
  Grid,
  Typography,
} from "@mui/material";
import React from "react";
import { styled } from "@mui/material/styles";

function PlayTextInput({ text, setText, answer, submitted }) {

  function answerIsCorrect() {
    return answer.toLowerCase() === text.toLowerCase();
  }

  return (
    <div>
      <Grid container direction={"column"} alignItems="center" sx={{ p: 2 }}>
        <Grid item sx={{ pb: 2 }}>
          <Typography variant="p1"> Type Answer here:</Typography>
        </Grid>
        {(() => {
          if (!submitted) {
            return (
              <TextField
                value={text}
                placeholder="Enter Answer"
                label="Answer"
                name="quiz text answer"
                variant="outlined"
                onChange={(t) => {
                  {
                    setText(t.target.value);
                  }
                }}
              />
            );
          }

          if (answerIsCorrect()) {
            return (
              <TextField
                label="Correct"
                required
                variant="outlined"
                defaultValue={text}
                color="success"
                focused
                inputProps={{ readOnly: true }}
                id="validation-outlined-input"
              />
            );
          }

          return (
            <TextField
              error
              id="outlined-error-heper-text"
              label="Incorrect"
              defaultValue={text}
              helperText={`Correct Answer: ${answer}`}
              focused
              inputProps={{ readOnly: true }}
            />
          );
        })()}
      </Grid>
    </div>
  );
}

export default PlayTextInput;
