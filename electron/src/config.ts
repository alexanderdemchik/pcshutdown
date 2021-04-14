import fs from 'fs';
import path from 'path';
import { getRootPath } from './utils';

const config = JSON.parse(fs.readFileSync(path.join(getRootPath(), 'config.json'), 'utf-8'));

config.set = (key: string, value: string) => {
  const configToSave = {...config};
  delete config.set;
  configToSave[key] = value;

  fs.writeFileSync(path.join(getRootPath(), 'config.json'), JSON.stringify(configToSave));
}

export default config;