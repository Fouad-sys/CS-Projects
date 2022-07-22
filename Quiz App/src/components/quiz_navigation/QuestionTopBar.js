import * as React from "react";
import {
  Fab,
  Grid,
  Stepper,
  Step,
  StepLabel,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import StepButton from "@mui/material/StepButton";
import { SCREEN_TYPES } from "../constants/constants";

const buttonStyle = {
  maxHeight: "23px",
  minHeight: "23px",
  minWidth: "23px",
  maxWidth: "23px",
};

const addStyle = {
  maxHeight: "20px",
  minHeight: "20px",
  minWidth: "20px",
  maxWidth: "20px",
};

export default function QuestionTopBar({
  screenType,
  currentQuestionIndex,
  setCurrentQuestionIndex,
  questionList,
  onAddQuestion,
  completedQuestions,
  viewedQuestions,
}) {

  /**
   * Navigates to the given step in the question list top bar. In the Play screen, this is only possible if the step's question has been already viewed by the player.
   * 
   * @param {number} step The question top bar step to switch to
   * @returns 
   */
  const handleStep = (step) => () => {
    console.log(typeof step)
    if (
      screenType === SCREEN_TYPES.CREATE ||
      (screenType === SCREEN_TYPES.PLAY && viewedQuestions[step])
    ) {
      setCurrentQuestionIndex(step);
    }
  };

  return (
    <div>
      <Grid container direction={"row"}>
        <Grid item>
          <Stepper nonLinear alternativeLabel activeStep={currentQuestionIndex}>
            {questionList?.map((label, index) => (
              <Step
                key={label}
                completed={
                  completedQuestions ? completedQuestions[index] : false
                }
              >
                <StepButton color="inherit" onClick={handleStep(index)}>
                  {`Question ${index + 1}`}
                </StepButton>
              </Step>
            ))}
            {screenType === SCREEN_TYPES.CREATE ? (
              <Step key="plus">
                <StepLabel
                  icon={
                      <Fab
                        color="primary"
                        aria-label="add"
                        justify-content="center"
                        style={buttonStyle}
                        onClick={onAddQuestion}
                        align="center"
                      >
                        <AddIcon style={addStyle} />
                      </Fab>
                  }
                ></StepLabel>
              </Step>
            ) : null}
          </Stepper>
        </Grid>
      </Grid>
    </div>
  );
}
