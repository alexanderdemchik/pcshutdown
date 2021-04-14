import i18next, { BackendModule, TFunction } from 'i18next';
import path from 'path';
import fs from 'fs';
import { getRootPath } from './utils';
import logger from './logger';

/**
 * {@link BackendModule} for i18next
 * allows read translation files from file system
 */
const backend: BackendModule<{loadPath: string}> = {
  type: 'backend',
  init: function(services, backendOptions, i18nextOptions) {
    this.loadPath = backendOptions.loadPath;
    this.services = services;
  },
  read: function(language, namespace, callback) {
    const filename = this.services.interpolator.interpolate(this.loadPath, { lng: language, ns: namespace });
    fs.readFile(filename, 'utf8', (err, data) => {
      if (err) {
        callback(err, null);
      } else {
        callback(null, JSON.parse(data));
      }
    });
  }
}

/**
 * i18n initialization
 * @param locale initial locale
 * @returns translate function
 */
export async function init(locale: string): Promise<TFunction> {
  logger.info('Init i18n');
  return i18next.use(backend).init({
    lng: locale,
    whitelist: ['en', 'ru'],
    debug: false,
    fallbackLng: 'en',
    load: 'languageOnly',
    backend: {
      loadPath: path.join(getRootPath(), '/locales/{{lng}}.json'),
    }
  });   
}

export default i18next;