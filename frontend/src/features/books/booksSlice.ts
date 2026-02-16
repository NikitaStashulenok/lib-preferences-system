import { createSlice, type PayloadAction } from '@reduxjs/toolkit';

type CatalogFilterState = {
  genre: string;
  author: string;
};

const initialState: CatalogFilterState = {
  genre: '',
  author: '',
};

const booksSlice = createSlice({
  name: 'booksFilters',
  initialState,
  reducers: {
    setFilters(_state, action: PayloadAction<CatalogFilterState>) {
      return action.payload;
    },
    clearFilters() {
      return initialState;
    },
  },
});

export const { setFilters, clearFilters } = booksSlice.actions;
export default booksSlice.reducer;
