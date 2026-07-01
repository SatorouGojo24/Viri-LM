# ETAPA 1: Construcción (Build)
FROM node:24-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

# Le asignamos 4GB de RAM a Node
ENV NODE_OPTIONS="--max-old-space-size=4096"
RUN npm run build --configuration=production

# ETAPA 2: Ejecución (Run)
FROM nginx:alpine

# REGLA ESTRICTA: Destruimos la página de bienvenida por defecto de Nginx
RUN rm -rf /usr/share/nginx/html/*

# Copiamos nuestros archivos al directorio vacío
COPY --from=builder /app/dist/frontend/browser/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80