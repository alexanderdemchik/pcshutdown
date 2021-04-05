import cp from 'child_process';
import os from 'os';
import getMAC from 'getmac';
import { DeviceInfo } from './interfaces/DeviceInfo';

export const shutdown = async () => {
  switch (process.platform) {
    case 'win32':
      return cp.execSync('shutdown /s /t 0');
    case 'darwin':
      return cp.execSync('shutdown -h now');;
    case 'linux':
      return cp.execSync('shutdown -P now');
  }
};

export const sleep = async () => {
  switch (process.platform) {
    case 'win32':
      return cp.execSync('shutdown /h /t 0');
    case 'darwin':
      return cp.execSync('shutdown -s now');;
    case 'linux':
      return cp.execSync('systemctl hibernate');
  }
};

export const restart = async () => {
  switch (process.platform) {
    case 'win32':
      return cp.execSync('shutdown /r /t 0');
    case 'darwin':
      return cp.execSync('shutdown -r now');;
    case 'linux':
      return cp.execSync('shutdown -r now');
  }
};

export const getDeviceInfo = (): DeviceInfo => ({
  hostname: os.hostname(),
  version: os.version(),
  platform: os.platform(),
  arch: os.arch(),
  mac: getMAC()
});
