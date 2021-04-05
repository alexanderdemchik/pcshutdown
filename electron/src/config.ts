import fs from 'fs';
import path from 'path';
import { getRootPath } from './utils';

const config = JSON.parse(fs.readFileSync(path.join(getRootPath(), 'config.json'), 'utf-8'));

export default config;