import { defineConfig } from 'astro/config';
import react from '@astrojs/react';

export default defineConfig({
  integrations: [react()],
  site: 'https://www.capitalcitymech.com',
  server: { port: 4321 },
  vite: {
    server: {
      proxy: {
        // Dev-only: forwards /api calls to Spring Boot so the browser sees same-origin.
        '/api': {
          target: process.env.API_ORIGIN || 'http://localhost:8080',
          changeOrigin: true,
        },
      },
    },
  },
});
