import 'source-map-support/register';
import { getRootPath } from './utils';
import { app, Tray, Menu } from 'electron';
import path from 'path';
import i18n, { init as initI18n } from './i18';
import logger from './logger';
import * as server from './server';
import AutoLaunch from 'auto-launch';
import { productName } from '../package.json';

const APP_NAME = productName;

let tray;

const lock = app.requestSingleInstanceLock()

// allow only one instance
if (!lock) {
  logger.debug('Prevent start second instance')
  app.quit();
} else {
  app.on('ready', onReady);
}

async function onReady() {
  process.on('unhandledRejection', function (error) {
    logger.error('%o', error);
    app.quit();
  });

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
  logger.info('check autorun');
  const autoLaunch = new AutoLaunch(
    // fix for linux https://github.com/Teamwork/node-auto-launch/issues/99 TODO: create custom auto-launch package
    process.env.APPIMAGE ? {
      name: APP_NAME,
      path: `${process.env.APPIMAGE}`.replace(/ /g, '\\ '),
    } : {
      name: APP_NAME,
    }
  );

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

/**
 * Add app to tray
 */
async function createTrayMenu() {
  tray = new Tray(path.join(getRootPath(), getAppIconPath()));
  const contextMenu = Menu.buildFromTemplate([
    { label: i18n.t("EXIT"), click() {app.quit()} },
  ]);
  tray.setToolTip(APP_NAME);
  tray.setContextMenu(contextMenu);
}

function getAppIconPath() {
  switch (process.platform) {
    case 'win32':
      return './assets/app_icon.ico';
    case 'darwin':
      return './assets/app_icon.icns';
    case 'linux':
      return './assets/app_icon.png';
  }
}