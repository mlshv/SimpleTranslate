# Простой переводчик

### О приложении
Приложение разработано в качестве тестового задания 
для поступления в Школу мобильной разработки для Android в рамках 
[Мобилизации 2017](https://yandex.ru/mobilization/)

И, конечно же, [Переведено сервисом «Яндекс.Переводчик»](http://translate.yandex.ru/)

### Фукнционал
* Перевод текста с помощью [API Переводчика](https://tech.yandex.ru/translate/)
* Отображение вариантов перевода с помощью [API Словаря](https://tech.yandex.ru/dictionary)
* Сохранение переведённых текстов в истории
* Возможность добавить перевод в избранное
* Возможность обновить список языков
* Кэширование

### Скриншоты
<img src="screenshots/translate.jpg" alt="Перевод" width="250" style="margin=10px;"/>
<img src="screenshots/langs.jpg" alt="Выбор языка" width="250" style="margin=10px;"/>
<img src="screenshots/history.jpg" alt="История" width="250" style="margin=10px;"/>
<img src="screenshots/favorites.jpg" alt="Избранное" width="250" style="margin=10px;"/>
<img src="screenshots/settings.jpg" alt="Настройки" width="250" style="margin=10px;"/>
<img src="screenshots/refresh.jpg" alt="Обновление списка языков" width="250" style="margin=10px;"/>
<img src="screenshots/about.jpg" alt="О приложении" width="250" style="margin=10px;"/>

### Известные баги
* Страница с переводом обнуляется при смене ориентации экрана.
_Почему-то при перевороте у поля с вводом текста вызывается 
setText() с пустой строкой в качестве аргумента. Пока побороть не удалось_