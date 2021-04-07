import Koa from 'koa';
import Router from '@koa/router';
import logger from './logger';
import config from './config';
import * as commands from './commands';

const server = new Koa();
const router = new Router();

server.use(async (ctx, next) => {
  const started = Date.now();
  await next();
  // once all middleware below completes, this continues
  const ellapsed = (Date.now() - started) + 'ms';
  logger.debug(`${ctx.url} - ${ellapsed}`);
});

server.use(router.routes());

router.get('/', async (ctx) => {
  ctx.body = commands.getDeviceInfo();
  ctx.status = 200;
});

router.post('/shutdown', async (ctx) => {
  await commands.shutdown();
  ctx.status = 200;
});

router.post('/restart', async (ctx) => {
  await commands.restart();
  ctx.status = 200;
});

router.post('/sleep', async (ctx) => {
  await commands.sleep();
  ctx.status = 200;
});

server.on('error', (err) => {
  logger.error('%o', err);
});

let tries = 0;
const ports = config.ports;

/**
 * start server on passed port argument or select port from config
 * if port in use tries start on ports that are listed in config key "ports"
 * @param port server start port 
 */
export const start = async (port?: number) => {
  return new Promise((resolve, reject) => {
    const p = port ? port : config.port;
    server.listen(p, () => {
      logger.info(`Started server on port ${p}`)
      
      if (p != config.port) {
        config.set('port', p);
      }

      resolve(undefined);
    }).on('error', (error) => {
      // @ts-ignore
      if (error.code === 'EADDRINUSE') {
        // @ts-ignore
        logger.debug(`port in use ${error.port}`)
    
        if (tries < ports.length) {
          start(ports[tries]);
        } else {
          logger.error('Unable to start server, ports in use');
          reject(error);
        }
      } else {
        logger.error('%o', error);
        reject(error);
      }
    });
  });
}
