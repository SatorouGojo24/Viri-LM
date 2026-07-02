import {
  AngularNodeAppEngine,
  createNodeRequestHandler,
  isMainModule,
  writeResponseToNodeResponse,
} from '@angular/ssr/node';
import express from 'express';
import { join, resolve } from 'node:path';

// Usamos resolve para asegurar que siempre encuentre la carpeta correcta
const browserDistFolder = resolve(process.cwd(), 'dist/frontend/browser');
const app = express();

// FUNDAMENTAL PARA RENDER: Confiar en el proxy
app.set('trust proxy', true);

// Seguridad SSR de Angular (con as any para evitar errores de TypeScript locales)
const angularApp = new AngularNodeAppEngine({
  trustProxyHeaders: true, 
  allowedHosts: ['*'] 
} as any);

app.use(
  express.static(browserDistFolder, {
    maxAge: '1y',
    index: false,
    redirect: false,
  }),
);

app.use((req, res, next) => {
  angularApp
    .handle(req)
    .then((response) =>
      response ? writeResponseToNodeResponse(response, res) : next(),
    )
    .catch((err) => {
      console.error('SSR Error:', err);
      next(err);
    });
});

if (isMainModule(import.meta.url) || process.env['pm_id']) {
  const port = process.env['PORT'] || 4000;
  app.listen(port, () => {
    console.log(`Node Express server listening on http://localhost:${port}`);
  });
}

export const reqHandler = createNodeRequestHandler(app);