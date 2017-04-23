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
<img src="screenshots/translate.jpg" alt="Перевод" style="width: 250px;"/>
<p></p>
<img src="screenshots/langs.jpg" alt="Выбор языка" style="width: 250px;"/>
<p></p>
<img src="screenshots/history.jpg" alt="История" style="width: 250px;"/>
<p></p>
<img src="screenshots/favorites.jpg" alt="Избранное" style="width: 250px;"/>
<p></p>
<img src="screenshots/settings.jpg" alt="Настройки" style="width: 250px;"/>
<p></p>
<img src="screenshots/refresh.jpg" alt="Обновление списка языков" style="width: 250px;"/>
<p></p>
<img src="screenshots/about.jpg" alt="О приложении" style="width: 250px;"/>

### Известные баги
* Страница с переводом обнуляется при смене ориентации экрана.
_Почему-то при перевороте у поля с вводом текста вызывается 
setText() с пустой строкой в качестве аргумента. Пока побороть не удалось_