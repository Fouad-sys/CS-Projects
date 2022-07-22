export const getDataWithId = (data, id) => {
  return data?.filter((d) => d.id === id)?.first();
};

export const getDataWithType = (data, type) => {
  return data?.filter((d) => d.type === type);
};
