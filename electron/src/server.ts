import Koa from 'koa';
import Router from '@koa/router';
import logger from './logger';
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

export default server;