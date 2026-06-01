# Build stage
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

# Runtime stage
FROM nginx:alpine

# Copiar archivos compilados (Angular 17 output a 'dist' directamente)
# COPY --from=builder /app/dist /usr/share/nginx/html
COPY --from=builder /app/dist/frontend/browser /usr/share/nginx/html
# nginx.conf es una plantilla: el entrypoint hace envsubst con BACKEND_HOST/BACKEND_PORT
COPY nginx.conf /etc/nginx/templates/default.conf.template
ENV BACKEND_HOST=backend \
    BACKEND_PORT=8080 \
    NGINX_ENVSUBST_FILTER=^BACKEND_

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
