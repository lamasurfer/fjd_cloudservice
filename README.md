# Дипломная работа “Облачное хранилище”

Простой REST-сервис, который позволяет пользователям загружать и отображать файлы в своем облачном хранилище, 
написан на `Java` с использованием `Spring Boot` для интеграции с заранее подготовленным 
[веб-приложением](https://github.com/netology-code/jd-homeworks/tree/master/diploma/netology-diplom-frontend) 
в соответствии со [спецификацией](https://github.com/netology-code/jd-homeworks/blob/master/diploma/CloudServiceSpecification.yaml).

Для автоматического запуска с уже собранным веб-приложением перейдите в репозиторий 
https://github.com/lamasurfer/fjd_cloudservice_auto

Для тестовых запусков вручную понадобится установить `nodejs` (версия не ниже 14.15.0) следуя инструкции: https://nodejs.org/ru/download/

1. Склонируйте (или просто [скачайте](https://github.com/lamasurfer/fjd_cloudservice/archive/refs/heads/master.zip)) репозиторий:
```bash
git clone https://github.com/lamasurfer/fjd_cloudservice.git
```

В корневом каталоге репозитория запустите:
```bash
docker-compose up -d
```

2. Далее необходимо склонировать (или опять же просто [скачать](https://github.com/lamasurfer/fjd_cloudservice_frontend/archive/refs/heads/master.zip)) 
`front`: 
```bash
git clone https://github.com/lamasurfer/fjd_cloudservice_frontend.git
```

В корневом каталоге в командной строке запустите:
```bash
npm install

npm run serve
```

Сервис будет доступен по адресу: http://localhost:8080

Для авторизации используйте данные одного из зарегистрированных пользователей:

|логин|пароль|
|---|---|
|john|john|
|ivan|ivan|

Для завершения работы:
```
docker-compose down
```

Ссылка на репозиторий с исходным кодом: https://github.com/lamasurfer/fjd_cloudservice

Ссылка на задание: https://github.com/netology-code/jd-homeworks/blob/master/diploma/cloudservice.md