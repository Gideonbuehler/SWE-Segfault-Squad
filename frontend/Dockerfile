FROM --platform=linux/amd64 node:20-alpine


WORKDIR /app

COPY package.json package-lock.json ./

RUN npm install --include=dev

COPY . .

EXPOSE 5173

CMD sh -c "node_modules/.bin/vite --host --mode docker"