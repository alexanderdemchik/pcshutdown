import { app } from 'electron';
import path from 'path';

export function getRootPath() {
  if (process.env.NODE_ENV === 'development') {
    return __dirname;
  } else {
    return path.dirname(app.getPath('exe'));
  }
}