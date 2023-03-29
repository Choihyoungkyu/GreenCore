import { configureStore, ThunkAction, Action, combineReducers } from '@reduxjs/toolkit';
import { persistReducer, persistStore, FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER } from 'redux-persist';
import storage from 'redux-persist/lib/storage';

import counterReduer from './temp/counter/counterSlice';
import postReducer from './temp/post/postSlice';

import commonReducer from './common/commonSlice';
import userReducer from './user/userSlice';
import diaryReducer from './diary/diarySlice';
import alertReducer from './alert/alertSlice';
import feedReducer from './feed/feedSlice';
import plantReducer from './plant/plantSlice';

const persistConfig = { key: 'root', version: 1, storage, whitelist: ['common'] };

const rootReducer = combineReducers({
  counter: counterReduer,
  post: postReducer,

  common: commonReducer,
  user: userReducer,
  diary: diaryReducer,
  alert: alertReducer,
  feed: feedReducer,
  plant: plantReducer,
});

const persistedReducer = persistReducer(persistConfig, rootReducer);

export function makeStore() {
  return configureStore({
    reducer: persistedReducer,
    middleware: (getDefaultMiddleware) =>
      // getDefaultMiddleware({ serializableCheck: { ignoreActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER] } }),
      getDefaultMiddleware({ serializableCheck: false }),
  });
}

const store = makeStore();

export type AppState = ReturnType<typeof store.getState>;

export type AppDispatch = typeof store.dispatch;

export type AppThunk<ReturnType = void> = ThunkAction<ReturnType, AppState, unknown, Action<string>>;

export const persistor = persistStore(store);
export default store;
