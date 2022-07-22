import { APP_DATA_TYPES, QUESTION_TYPES } from "../components/constants/constants";

const buildDatabase = (appContext) => ({
  appData: [
    {
      data: {
        list: ["id4", "id5", "id6"],
      },
      type: APP_DATA_TYPES.QUESTION_LIST,
    },
    {
      id: "id4",
      data: {
        question: "What is the capital of France?",
        questionType: QUESTION_TYPES.MULTIPLE_CHOICE,
        choices: [
          { choice: "London", isCorrect: false },
          { choice: "Paris", isCorrect: true },
          { choice: "New York", isCorrect: false },
          { choice: "Tokyo", isCorrect: false },
        ],
      },
      type: APP_DATA_TYPES.QUESTION,
    },
    {
      id: "id5",
      data: {
        question: "How happy are you?",
        questionType: QUESTION_TYPES.SLIDER,
        leftText: "Sad",
        rightText: "Happy",
        correctValue: 20,
      },
      type: APP_DATA_TYPES.QUESTION,
    },
    {
      id: "id6",
      data: {
        question: "What is a baby cat called?",
        questionType: QUESTION_TYPES.TEXT_INPUT,
        answer: "kitten",
      },
      type: APP_DATA_TYPES.QUESTION,
    },
  ],
  members: [
    {
      id: appContext.memberId,
      name: "mock-member",
    },
  ],
});

export default buildDatabase;
