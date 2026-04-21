import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/users': { target: 'http://localhost:8001', changeOrigin: true },
      '/products': { target: 'http://localhost:8002', changeOrigin: true },
      '/categories': { target: 'http://localhost:8002', changeOrigin: true },
      '/cart': { target: 'http://localhost:8004', changeOrigin: true },
      '/orders': { target: 'http://localhost:8005', changeOrigin: true },
    },
  },
})