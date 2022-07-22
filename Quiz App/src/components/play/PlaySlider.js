import {
  Grid,
  Typography,
  Slider,
} from "@mui/material";
import React from "react";
import { useAppData } from "../context/hooks";
import { getDataWithId } from "../context/utilities";

function PlaySlider({
  currentQuestionId,
  sliderValue,
  setSliderValue,
  sliderCorrectValue,
  submitted,
}) {
  const { data } = useAppData();
  const leftLabel = getDataWithId(data, currentQuestionId)?.data?.leftText;
  const rightLabel = getDataWithId(data, currentQuestionId)?.data?.rightText;
  const marks = [
    { value: 0, label: leftLabel },
    {
      value: 100,
      label: rightLabel,
    },
  ];

  function answerIsCorrect() {
    return sliderValue === sliderCorrectValue;
  }

  return (
    <div>
      <Grid container direction={"column"} sx={{ p: 2 }}>
        <Grid item sx={{ pb: 2 }}>
          <Slider
            aria-label="Custom marks"
            defaultValue={50}
            valueLabelDisplay="on"
            value={submitted ? [sliderValue, sliderCorrectValue] : sliderValue}
            onChange={(e, val) => {
              setSliderValue(val);
            }}
            marks={marks}
          />
        </Grid>
        {(() => {
          if (submitted) {
            if (answerIsCorrect()) {
              return (
                <Typography variant="p1" color="success.main">
                  Correct!
                </Typography>
              );
            } else {
              return (
                <div>
                  <Typography variant="subtitle1" color="error">
                    Incorrect!
                  </Typography>
                  <Typography variant="subtitle2">
                    Correct value was: {sliderCorrectValue}
                  </Typography>
                </div>
              );
            }
          }
        })()}
      </Grid>
    </div>
  );
}

export default PlaySlider;
