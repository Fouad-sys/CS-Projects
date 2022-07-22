import {
  Fab,
  Grid,
  InputLabel,
  OutlinedInput,
  IconButton,
  InputAdornment,
  FormControl,
  FormControlLabel,
  Checkbox,
  Typography,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import CloseIcon from "@mui/icons-material/Close";
import React from "react";
import { DEFAULT_CHOICE } from "../constants/constants";

function MultipleChoice({ choices, setChoices, setDataChanged }) {
  const handleAnswerCorrectnessChange = (index, e) => {
    setDataChanged(true);
    let newChoices = [...choices];
    newChoices[index] = { ...choices[index], isCorrect: e.target.checked };
    setChoices(newChoices);
  };

  const handleChoiceChange = (index, e) => {
    setDataChanged(true);
    let newChoices = [...choices];
    newChoices[index] = { ...choices[index], choice: e.target.value };
    setChoices(newChoices);
  };

  const addAnswer = () => {
    setDataChanged(true);
    setChoices([...choices, DEFAULT_CHOICE]);
  };

  const removeAnswer = (index) => {
    setDataChanged(true);
    let newChoices = [...choices];
    newChoices.splice(index, 1);
    setChoices(newChoices);
  };

  return (
    <div align="center">
      <Grid
        container
        direction={"column"}
        align="left"
        columns={{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12 }}
      >
        <Grid item>
          <Typography variant="p1"> Answers:</Typography>
        </Grid>
        {choices.map((choice, index) => {
          const readableIndex = index + 1;
          return (
            <div key={index}>
              <Grid
                container
                direction={"column"}
                columns={{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12 }}
                align="left"
              >
                <Grid item sx={{ pt: 2 }}>
                  <Typography> Enter Choice {readableIndex}:</Typography>
                </Grid>
                <Grid container direction={"row"}>
                  <Grid item variant="outlined" sx={{ pt: 2 }}>
                    <FormControl variant="outlined">
                      <InputLabel>Choice {readableIndex}</InputLabel>
                      <OutlinedInput
                        type="text"
                        label={`Choice ${readableIndex}`}
                        value={choice.choice}
                        placeholder={`Enter Choice ${readableIndex}`}
                        onChange={(e) => handleChoiceChange(index, e)}
                        endAdornment={
                          <InputAdornment position="end">
                            <FormControlLabel
                              control={
                                <Checkbox
                                  checked={choices[index].isCorrect}
                                  onChange={(e) =>
                                    handleAnswerCorrectnessChange(index, e)
                                  }
                                  name="answer"
                                  edge="end"
                                />
                              }
                            />
                          </InputAdornment>
                        }
                      />
                    </FormControl>
                  </Grid>
                  <Grid item sx={{ pt: 2.75, pl: 1 }}>
                    {
                      <IconButton
                        type="button"
                        disabled={choices.length <= 2}
                        onClick={
                          choices.length > 2 ? () => removeAnswer(index) : {}
                        }
                      >
                        <CloseIcon />
                      </IconButton>
                    }
                  </Grid>
                </Grid>
              </Grid>
            </div>
          );
        })}

        <Grid item sx={{ pt: 2 }} align="left">
          <Fab color="primary" aria-label="add" onClick={addAnswer}>
            <AddIcon />
          </Fab>
        </Grid>
      </Grid>
    </div>
  );
}

export default MultipleChoice;
