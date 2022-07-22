import {
  Grid,
  Button,
} from "@mui/material";
import React, { useEffect } from "react";
import { createTheme, ThemeProvider } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    secondary: {
      main: "#FFFFFF",
      notchedOutline: {
        borderWidth: "5px",
        borderColor: "green !important",
      },
    },
    primary: {
      main: "#555BD9"
    }
  },
});

function PlayMultipleChoice({
  choices,
  answers,
  setAnswers,
  results,
  setResults,
  submitted,
}) {

  // TODO: outlined color once selected
  function computeCorrectness(answer, isCorrect) {
    if (isCorrect !== answer) {
      return "false";
    } else if (isCorrect && answer) {
      return "true";
    } else {
      return "neutral";
    }
  }

  const onSelect = (index) => {
    let newAnswers = [...answers];
    newAnswers[index] = !answers[index];
    setAnswers(newAnswers);
    let newResults = [...results];
    newResults[index] = computeCorrectness(
      newAnswers[index],
      choices[index].isCorrect
    );
    setResults(newResults);
  };

  function selectColor(index) {
    if (submitted) {
      switch (results[index]) {
        case "true":
          return "success";
        case "false":
          return "error";
        default:
          return "secondary";
      }
    } else {
      return answers[index] ? "primary" : "secondary";
    }
  }

  useEffect(() => {
    if (choices) {
      setAnswers(
        Array(choices?.length)
          .fill()
          .map(() => false)
      );
    }
  }, [choices]);

  return (
    <div>
      <Grid container direction={"column"} sx={{ p: 2 }}>
        {choices?.map((choice, index) => (
          <div key={index}>
            <Grid
              container
              direction={"column"}
              sx={{ pb: 3 }}
              align="center"
              columns={{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12 }}
            >
              <Grid item sx={{ py: 3 }}>
                <ThemeProvider theme={theme}>
                  <Button
                    key={index}
                    onClick={() => onSelect(index)}
                    variant="contained"
                    color={selectColor(index)}
                    sx={{ py: 2, borderColor: "black" }}
                    fullWidth
                  >
                    {choice.choice}
                  </Button>
                </ThemeProvider>
              </Grid>
            </Grid>
          </div>
        ))}
      </Grid>
    </div>
  );
}

export default PlayMultipleChoice;
