# cloud-storage

20/07/21
TODO server: реализовать перезапись файлов, реализовать скачивание файла с сервера, отрефакторить методы, добавить комментарии

TODO gui: разобраться с кнопкой регистрации (срабатывает со второго раза), сделать автологин, подключить кнопку логин, добавить цвета интерфейсу.

Переработан интерфейс, добавлена навигация, добавлена .jar сборка maven assembly plugin, добавлена Database MySQL, table users, table files,

переработаны методы под базу данных.



13/07/21

На сегодняшний день в проекте присутсвуют три модуля, core, client, server.

Client: На стороне клиента реализованы обработчик файлов, обработчик обновлений, а также netty network, по средством которого 
идет обмен DTO, два класса из модуля core, FileMessage and DeleteFileMessage, 

Interface: релизован выбор файла с компьютера, выбор файла из локального хранилища, удаление 
файла из локкального хранилища, отправка файла на сервер, загрузка выбранного файла с сервера, реализована кнопка refresh.

Server: прием и хранение файлов от клиента, отправка клиенту информации о текущих файлах на сервере, удаление 
файлов с серверa, обработчики DeleteFileHandler, FileHandler, MessageHandler.

Core: два класса для обмена данными FileMessage, DeleteFileMessage.
