import { app } from 'electron';
import path from 'path';
import logger from './logger';

export function getRootPath() {
  logger.info(process.execPath);
  logger.info(app.getPath('exe'));
 
  if (process.env.NODE_ENV === 'development') {
    return __dirname;
  } else {
    return path.dirname(app.getPath('exe'));
  }
}