import { getRootPath } from './utils';
import { app, Tray, Menu } from 'electron';
import path from 'path';
import i18n, { init as initI18n } from './i18';
import logger from './logger';
import * as server from './server';
import AutoLaunch from 'auto-launch';

const APP_NAME = "PC Shutdown";

let tray;

const lock = app.requestSingleInstanceLock()

// allow only one instance
if (!lock) {
  logger.debug('Prevent start second instance')
  app.quit();
}

app.on('ready', onReady);

async function onReady() {
  process.on('uncaughtException', function (error) {
    logger.error('%o', error);
    app.quit();
  });

  if (process.env.NODE_ENV === 'production') await checkAutorun();
  
  await initI18n(app.getLocale());
  await server.start();

  createTrayMenu();
}

/**
 * Try to add app to autorun
 */
async function checkAutorun() {
  const autoLaunch = new AutoLaunch({
    name: APP_NAME,
    path: app.getPath('exe'),
  });

  try {
    const isAutorunEnabled = await autoLaunch.isEnabled()
    if (!isAutorunEnabled) {
      autoLaunch.enable();
      logger.info("Added app to autorun");
    } else {
      logger.info("Autorun enabled");
    }
  } catch (e) {
    logger.error('Error to add app to autorun %o', e);
  }
}

async function createTrayMenu() {
  tray = new Tray(path.join(getRootPath(), './assets/app_icon.ico'));
  const contextMenu = Menu.buildFromTemplate([
    { label: i18n.t("EXIT"), click() {app.quit()} },
  ]);
  tray.setToolTip(APP_NAME);
  tray.setContextMenu(contextMenu);
}
