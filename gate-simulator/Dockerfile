
FROM node:14

# определение переменных среды для линукс
ENV HOME=/app
ENV NODE_ENV=production
ENV NODE_PORT=9999

# Устанавливаем рабочую директорию
WORKDIR /usr/src/app

# Копируем package.json и package-lock.json
COPY package*.json ./

# Устанавливаем зависимости для запуска приложения
RUN npm install

# Копируем остальные файлы
COPY . .

# Открываем порт 9999
EXPOSE $NODE_PORT

# Запускаем приложение
CMD ["npm", "start"]
