import {
  configureQueryClient,
  buildMockLocalContext,
  buildMockParentWindow,
} from "@graasp/apps-query-client";

const {
  queryClient,
  QueryClientProvider,
  hooks,
  useMutation,
  ReactQueryDevtools,
  API_ROUTES,
  MUTATION_KEYS,
  HOOK_KEYS,
} = configureQueryClient({
  notifier: (data) => {
    console.log("notifier: ", data);
  },
  enableWebsocket: true,
  keepPreviousData: true,
  // avoid refetching when same data are closely fetched
  staleTime: 1000, // ms
  GRAASP_APP_ID: "id",
  targetWindow: process.env.REACT_APP_MOCK_API
    ? // build mock parent window given cypress context or mock data
      buildMockParentWindow(buildMockLocalContext(window.appContext))
    : window.parent,
});

export {
  queryClient,
  QueryClientProvider,
  hooks,
  useMutation,
  ReactQueryDevtools,
  API_ROUTES,
  MUTATION_KEYS,
  HOOK_KEYS,
};
