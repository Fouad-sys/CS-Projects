import {
  TextField,
  Grid,
  Slider,
  Typography,
} from "@mui/material";
import React from "react";

function Slide({
  leftText,
  setLeftText,
  rightText,
  setRightText,
  sliderCorrectValue,
  setSliderCorrectValue,
  setDataChanged,
}) {
  return (
    <div align="center">
      <Grid
        container
        direction={"column"}
        align="left"
        columns={{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12 }}
      >
        <Grid item sx={{ pb: 2 }}>
          <Typography variant="p1">
            Slide the ball to the correct value:
          </Typography>
        </Grid>
        <Grid item sx={{ pb: 2 }}>
          <Slider
            aria-label="Custom marks"
            defaultValue={20}
            valueLabelDisplay="auto"
            value={sliderCorrectValue}
            onChange={(_, val) => {
              setDataChanged(true);
              setSliderCorrectValue(val);
            }}
          />
        </Grid>
        <Grid item sx={{ pb: 2 }}>
          <Grid
            container
            direction={"row"}
            columns={{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12 }}
          >
            <Grid item align="left">
              <TextField
                value={leftText}
                placeholder="Enter Field"
                label="Left Field"
                name="quiz text answer"
                variant="outlined"
                style={{ width: "50%" }}
                onChange={(t) => {
                  setDataChanged(true);
                  setLeftText(t.target.value);
                }}
              />
            </Grid>
            <Grid item align="right">
              <TextField
                value={rightText}
                placeholder="Enter Field"
                label="Right Field"
                name="quiz text answer"
                variant="outlined"
                style={{ width: "50%" }}
                onChange={(t) => {
                  setDataChanged(true);
                  setRightText(t.target.value);
                }}
              />
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </div>
  );
}

export default Slide;
