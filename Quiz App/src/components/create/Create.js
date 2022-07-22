import {
  TextField,
  Grid,
  Box,
  InputLabel,
  MenuItem,
  FormControl,
  Button,
  Typography,
  Select,
} from "@mui/material";
import React, { useState, useEffect } from "react";
import {
  DEFAULT_TEXT,
  DEFAULT_CHOICES,
  APP_DATA_TYPES,
  QUESTION_TYPES,
  SCREEN_TYPES,
} from "../constants/constants";
import { useAppData } from "../context/hooks";
import { getDataWithId, getDataWithType } from "../context/utilities";
import { MUTATION_KEYS, useMutation } from "../../config/queryClient";
import MultipleChoice from "./MultipleChoice";
import TextInput from "./TextInput";
import Slider from "./Slide";
import DeleteIcon from "@mui/icons-material/Delete";
import QuestionTopBar from "../quiz_navigation/QuestionTopBar";

/**
 * Component displaying the quiz creation screen elements.
 * 
 * @returns {React.ReactElement} The quiz creation screen.
 */
function Create() {
  // The data of the database
  const { data } = useAppData();
  const { mutateAsync: postAppDataAsync } = useMutation(
    MUTATION_KEYS.POST_APP_DATA
  );
  const { mutate: postAppData } = useMutation(MUTATION_KEYS.POST_APP_DATA);
  const { mutate: patchAppData } = useMutation(MUTATION_KEYS.PATCH_APP_DATA);
  const { mutate: deleteAppData } = useMutation(MUTATION_KEYS.DELETE_APP_DATA);

  const [questionList, setQuestionList] = useState([]);
  const [currentQuestionIndex, setCurrentQuestionIndex] = React.useState(0);
  const [currentQuestionId, setCurrentQuestionId] = useState(null);

  const screenType = SCREEN_TYPES.CREATE;
  const [question, setQuestion] = useState(DEFAULT_TEXT);
  const [type, setType] = useState(QUESTION_TYPES.MULTIPLE_CHOICE);
  // Flag to indicate whether any of the create screen's current data has changed, if set to false the save button will be disabled
  const [dataChanged, setDataChanged] = useState(true);
  // Flag to indicate whether the data has been initially fetched from the database.
  const [fetched, setFetched] = useState(0);

  const [choices, setChoices] = useState(DEFAULT_CHOICES);
  const [text, setText] = useState(DEFAULT_TEXT);
  const [sliderLeftText, setSliderLeftText] = useState(DEFAULT_TEXT);
  const [sliderRightText, setSliderRightText] = useState(DEFAULT_TEXT);
  const [sliderCorrectValue, setSliderCorrectValue] = useState(0);

  /** Initializes all of the screen's values to their default ones. */
  const initializeValues = (type) => {
    setQuestion(DEFAULT_TEXT);
    setType(type ?? QUESTION_TYPES.MULTIPLE_CHOICE);
    setChoices(DEFAULT_CHOICES);
    setText(DEFAULT_TEXT);
    setSliderLeftText(DEFAULT_TEXT);
    setSliderRightText(DEFAULT_TEXT);
    setSliderCorrectValue(0);
  };

  /** Fetches the question list's and current question's data from the database and initializes their respective local values.  */
  const fetchData = (qList) => {
    if (data) {
      let newQuestionList = qList ?? questionList;
      // Fetches the database's question list only when the data is loaded for the first time.
      if (fetched < 2) {
        newQuestionList = getDataWithType(
          data,
          APP_DATA_TYPES.QUESTION_LIST
        )?.first()?.data?.list;
        setQuestionList(newQuestionList);
      }
      let newCurrentQuestionId = newQuestionList[currentQuestionIndex];
      setCurrentQuestionId(newCurrentQuestionId);
      // The database data structure of the current question.
      let newCurrentQuestionData = getDataWithId(data, newCurrentQuestionId);
      if (newCurrentQuestionData) {
        setQuestion(newCurrentQuestionData?.data?.question ?? DEFAULT_TEXT);
        let newType =
          newCurrentQuestionData?.data?.questionType ??
          QUESTION_TYPES.MULTIPLE_CHOICE;
        setType(newType);
        switch (newType) {
          case QUESTION_TYPES.MULTIPLE_CHOICE: {
            setChoices(
              newCurrentQuestionData?.data?.choices ?? DEFAULT_CHOICES
            );
            break;
          }
          case QUESTION_TYPES.TEXT_INPUT: {
            setText(newCurrentQuestionData?.data?.answer ?? DEFAULT_TEXT);
            break;
          }
          case QUESTION_TYPES.SLIDER: {
            setSliderLeftText(
              newCurrentQuestionData?.data?.leftText ?? DEFAULT_TEXT
            );
            setSliderRightText(
              newCurrentQuestionData?.data?.rightText ?? DEFAULT_TEXT
            );
            setSliderCorrectValue(
              newCurrentQuestionData?.data?.correctValue ?? 0
            );
            break;
          }
        }
      } else {
        initializeValues();
      }
    }
  };

  /**
   * Sets the type parameter to the dropdown menu's selected question type.
   *
   * @param {object} event The question type menu change event.
   */
  const handleTypeSelect = (event) => {
    setDataChanged(true);
    initializeValues(event.target.value);
  };

  useEffect(() => {
    setDataChanged(true);
    fetchData();
  }, [currentQuestionIndex]);

  useEffect(() => {
    if (fetched < 2) {
      fetchData();
      setFetched(fetched + 1);
    }
  }, [data]);

  /**
   * Creates a question and adds it to the database, then runs the provided callback function.
   * 
   * @param {(number) => ()} callback callback function to be called with the id of the newly created question.
   */
  const createQuestion = async (callback) => {
    const { id: newAppDataId } = await postAppDataAsync({
      data: {
        question: DEFAULT_TEXT,
        questionType: QUESTION_TYPES.MULTIPLE_CHOICE,
        choices: DEFAULT_CHOICES,
      },
      type: APP_DATA_TYPES.QUESTION,
    });
    setDataChanged(true);
    callback(newAppDataId);
  };

  /**
   * Creates a new question and navigates to it.
   */
  const onAddQuestion = () => {
    setDataChanged(true);
    createQuestion((id) => {
      let newQuestionList = [...questionList];
      const newQuestionIndex = currentQuestionIndex + 1;
      // Adds the newly created question's ID right after the current question's one in the question list.
      newQuestionList.splice(newQuestionIndex, 0, id);
      setQuestionList(newQuestionList);
      // Save the new question list.
      handleSave(newQuestionList);
    });
    let newQuestionList = [...questionList];
    const newQuestionIndex = currentQuestionIndex + 1;
    // Adds a temporary empty ID right after the current question's one in the question list, to be changed once the actual ID is fetched at the end of the createQuestion asynchronous function. This temporary ID prevents blocking the screen until retrieveing the new ID for the purpose of improving the user experience.
    newQuestionList.splice(newQuestionIndex, 0, "");
    setQuestionList(newQuestionList);
    handleNext(newQuestionList);
  };

  /**
   * Deletes the current question and navigates to the previous one, or the next one if there are none.
   */
  const deleteQuestion = () => {
    setDataChanged(true);
    // We do not allow the deletion of all questions of the quiz, there should be at least one visible, but maybe we could add a screen for when no quiz questions have been created.
    if (questionList.length > 1) {
      deleteAppData({ currentQuestionId });
      let newQuestionList = [...questionList];
      // Deletes current question
      newQuestionList.splice(currentQuestionIndex, 1);
      setQuestionList(newQuestionList);
      handlePrevious(newQuestionList);
    }
  };

  /**
   * Saves the current quiz state to the database and navigates to the next question.
   * 
   * @param {number[]} newQuestionList the current question ID list.
   */
  const handleNext = (newQuestionList) => {
    handleSave(newQuestionList);
    const questionListLength = newQuestionList?.length ?? questionList.length;
    if (currentQuestionIndex + 1 < questionListLength) {
      setCurrentQuestionIndex(currentQuestionIndex + 1);
    }
  };

  /**
   * Saves the current quiz state to the database and navigates to the previous question.
   * 
   * @param {number[]} newQuestionList the current question ID list.
   */
  const handlePrevious = (newQuestionList) => {
    handleSave(newQuestionList);
    if (currentQuestionIndex > 0) {
      setCurrentQuestionIndex(currentQuestionIndex - 1);
    }
    // To handle the case of the current index not changing (ie. is 0) and no call to useEffect
    fetchData(newQuestionList);
  };

  /**
   * Saves the question list and question data to the database if the data is stale.
   * 
   * @param {number[]} newQuestionList the current question ID list.
   */
  const handleSave = (qList) => {
    // Only save the data if it has changed
    if (dataChanged) {
      let questionListDBId = getDataWithType(
        data,
        APP_DATA_TYPES.QUESTION_LIST
      )?.first()?.id;
      if (questionListDBId) {
        patchAppData({
          id: questionListDBId,
          data: {
            list: qList ?? questionList,
          },
          type: APP_DATA_TYPES.QUESTION_LIST,
        });
      }
      switch (type) {
        case QUESTION_TYPES.MULTIPLE_CHOICE: {
          if (!questionList.length) {
            postAppData({
              data: {
                question: question,
                questionType: QUESTION_TYPES.MULTIPLE_CHOICE,
                choices: choices,
              },
              type: APP_DATA_TYPES.QUESTION,
            });
          } else {
            patchAppData({
              id: currentQuestionId,
              data: {
                question: question,
                questionType: QUESTION_TYPES.MULTIPLE_CHOICE,
                choices: choices,
              },
              type: APP_DATA_TYPES.QUESTION,
            });
          }
          break;
        }
        case QUESTION_TYPES.TEXT_INPUT: {
          if (!questionList.length) {
            postAppData({
              data: {
                question: question,
                questionType: QUESTION_TYPES.TEXT_INPUT,
                answer: text,
              },
              type: APP_DATA_TYPES.QUESTION,
            });
          } else {
            if (currentQuestionId) {
              patchAppData({
                id: currentQuestionId,
                data: {
                  question: question,
                  questionType: QUESTION_TYPES.TEXT_INPUT,
                  answer: text,
                },
                type: APP_DATA_TYPES.QUESTION,
              });
            }
          }
          break;
        }
        case QUESTION_TYPES.SLIDER: {
          if (!questionList.length) {
            postAppData({
              data: {
                question: question,
                questionType: QUESTION_TYPES.SLIDER,
                leftText: sliderLeftText,
                rightText: sliderRightText,
                correctValue: sliderCorrectValue,
              },
              type: APP_DATA_TYPES.QUESTION,
            });
          } else {
            if (currentQuestionId) {
              patchAppData({
                id: currentQuestionId,
                data: {
                  question: question,
                  questionType: QUESTION_TYPES.SLIDER,
                  leftText: sliderLeftText,
                  rightText: sliderRightText,
                  correctValue: sliderCorrectValue,
                },
                type: APP_DATA_TYPES.QUESTION,
              });
            }
          }
          break;
        }
      }
      setDataChanged(false);
    }
  };

  return (
    <div align="center">
      <Grid
        container
        direction={"row"}
        alignItems="center"
        justifyContent="center"
        sx={{ p: 2 }}
      >
        <Grid item>
          <QuestionTopBar
            screenType={screenType}
            currentQuestionIndex={currentQuestionIndex}
            setCurrentQuestionIndex={setCurrentQuestionIndex}
            questionList={questionList}
            onAddQuestion={onAddQuestion}
          />
        </Grid>
      </Grid>
      <Typography variant="h1" fontSize={40} sx={{ pb: 4 }}>
        Create your quiz
      </Typography>
      <Grid
        container
        direction={"column"}
        align="left"
        columns={{ xs: 12, sm: 12, md: 12, lg: 12, xl: 12 }}
      >
        <Grid item sx={{ pb: 2 }}>
          <Box sx={{ minWidth: 120 }}>
            <FormControl fullWidth>
              <InputLabel id="demo-simple-select-label">Type</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={type}
                label="Type"
                onChange={handleTypeSelect}
              >
                <MenuItem value={QUESTION_TYPES.MULTIPLE_CHOICE}>
                  Multiple Choice
                </MenuItem>
                <MenuItem value={QUESTION_TYPES.TEXT_INPUT}>
                  Text Input
                </MenuItem>
                <MenuItem value={QUESTION_TYPES.SLIDER}>Slider</MenuItem>
              </Select>
            </FormControl>
          </Box>
        </Grid>
        <Grid item sx={{ pb: 2 }}>
          <Typography variant="p1"> Question:</Typography>
        </Grid>
        <Grid item sx={{ pb: 2 }}>
          <TextField
            value={question}
            placeholder="Enter Question"
            label="Question"
            name="quizQuestion"
            variant="outlined"
            onChange={(t) => {
              setDataChanged(true);
              setQuestion(t.target.value);
            }}
          />
        </Grid>
        {(() => {
          switch (type) {
            case QUESTION_TYPES.MULTIPLE_CHOICE: {
              return (
                <MultipleChoice
                  choices={choices}
                  setChoices={setChoices}
                  setDataChanged={setDataChanged}
                />
              );
            }
            case QUESTION_TYPES.TEXT_INPUT: {
              return (
                <TextInput
                  text={text}
                  setText={setText}
                  currentQuestion={currentQuestionIndex}
                  setDataChanged={setDataChanged}
                />
              );
            }
            case QUESTION_TYPES.SLIDER: {
              return (
                <Slider
                  leftText={sliderLeftText}
                  setLeftText={setSliderLeftText}
                  rightText={sliderRightText}
                  setRightText={setSliderRightText}
                  sliderCorrectValue={sliderCorrectValue}
                  setSliderCorrectValue={setSliderCorrectValue}
                  currentQuestion={currentQuestionIndex}
                  setDataChanged={setDataChanged}
                />
              );
            }
            default:
              return (
                <MultipleChoice
                  choices={choices}
                  setChoices={setChoices}
                  setDataChanged={setDataChanged}
                />
              );
          }
        })()}
        <Grid
          container
          direction={"row"}
          spacing={4}
          sx={{ py: 2 }}
          align="center"
        >
          <Grid item>
            <Button
              variant="contained"
              color="primary"
              onClick={() => handlePrevious()}
              disabled={currentQuestionIndex === 0}
            >
              Prev
            </Button>
          </Grid>
          <Grid item>
            <Button
              color="error"
              variant="contained"
              startIcon={<DeleteIcon />}
              onClick={deleteQuestion}
              disabled={questionList.length <= 1}
            >
              Delete
            </Button>
          </Grid>
          <Grid item>
            <Button
              onClick={() => handleSave()}
              variant="contained"
              color="success"
              disabled={!dataChanged}
            >
              Save
            </Button>
          </Grid>
          <Grid item>
            <Button
              variant="contained"
              color="primary"
              onClick={() => handleNext()}
              disabled={currentQuestionIndex === questionList.length - 1}
            >
              Next
            </Button>
          </Grid>
        </Grid>
      </Grid>
    </div>
  );
}

export default Create;
