# Usamos una imagen base de Node.js (Vite requiere versiones recientes)
FROM node:18-alpine

# Establecemos el directorio de trabajo
WORKDIR /app

# Copiamos el package.json y package-lock.json
COPY package*.json ./

# Instalamos las dependencias
RUN npm install

# Copiamos el resto del código fuente del frontend
COPY . .

# Exponemos el puerto 3000 (el que usa Vite por defecto)
EXPOSE 3000

# Comando para iniciar el servidor de desarrollo
# ¡Importante! El '--host 0.0.0.0' es necesario para exponerlo
CMD [ "npm", "run", "dev", "--", "--host", "0.0.0.0" ]
