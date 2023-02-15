# Alive-Corner (en)

## General description of the project

**"Alive Corner "** is an app for managing a smart feeding system so that your pet won't go hungry when you're not at home, and you can always monitor that it is alive, healthy and well - fed.

![image](https://drive.google.com/uc?export=view&id=1-wiik8oKs-GqMuFQq3fX2pniC7HtOEuk)

Most of similar applications available on the market suffer from the lack of optimization or due to limitations in the flexibility of feeding schedule settings. In my application I tried to take into account these shortcomings. 

The main advantages of the application are:
- Flexibility in configuring the feeding schedule, with the ability to choose the time, days of the week and portion size for each element of the schedule;
  
- For the peace of mind of pet owners, all news in the feed is accompanied by a photo from the scene;
  
- One app can be responsible for multiple auto-feeders at once;
  
- There is an option to "feed now" through the app;
  
- There are two languages - English and Russian. The initial choice of language depends on the settings in Android
  
- There is an opportunity to select the time zone. The initial value also depends on the settings in the system
  
- All the communication with the server is encrypted with the AES-128 algorithm.

## Technical mistakes
Looking back, we would like to note the following problems with the project:
- The *Hardcode*. How to fix it: move all of the constants into a separate static class.
  
- *Long classes* that take on more than one role. How to fix it: split classes into separate logical blocks
  
- *Inappropriate names* of some variables and classes. How to fix: rename them, heh)

I'm writing all this out, since my level of code design has increased, and I don't have time to refactor old projects. 


## Useful links
- [Google disk with presentation, apk and more technical description of the project](https://drive.google.com/drive/folders/1E7BK6E-fxWHqnMY-ePl0PLqZBao8J9X1?usp=sharing)
  
- [YouTube video](https://www.youtube.com/watch?v=K4ss20Cq2QI)

---

# Alive-Corner (rus)

## Общее описание проекта

**«Alive Corner»** – приложение для управления умной системой кормления, чтоб ваш питомец не голодал в ваше отсутствие, и вы всегда могли отслеживать, что он жив, здоров и сыт.

![image](https://drive.google.com/uc?export=view&id=1-wiik8oKs-GqMuFQq3fX2pniC7HtOEuk)

Большинство аналогичных приложений, представленных на рынке страдают от отсутствия русского языка, оптимизации или из-за ограничений в гибкости настройки графика кормления. В своем приложении я постарался учесть данные недочеты. 

Основные преимущества приложения:
- гибкая настройка графика кормления, с возможностью выбора времени, дней недели и размера порции под каждый элемент расписания;
  
- Для спокойствия владельцев питомцев ко всем новостям в ленте прилагается фотография с места событий;
  
- Одно приложение может отвечать сразу за несколько автокормушек;
  
- Присутствует возможность «покормить сейчас» через приложение;
  
- Есть два языка - английский и русский. Первоначальный выбор языка зависит от настроек в Android
  
- Есть возможность выбрать часовой пояс. Начальное значение также зависит от настроек в системе
  
- Все общение с сервером зашифровано при помощи алгоритма AES-128.

## Технические огрехи
Оглядываясь назад, хочется подметить следующие проблемы проекта:
- *Хардкод*. Как исправить: вынести все константы в отдельный статический класс
  
- *Длинные классы*, берущие на себя более одной роли. Как исправить: раздробить классы на отдельные логические блоки
  
- *Неудачные названия* некоторых переменных и классов. Как исправить: переименовать их, хех)

Все это я расписываю, т.к мой уровень проектирования кода вырос, а времени на рефакторинг старых проектов нет. 


## Полезные ссылки
- [Google диск с презентацией, apk и более подробным техническм описанием проекта](https://drive.google.com/drive/folders/1E7BK6E-fxWHqnMY-ePl0PLqZBao8J9X1?usp=sharing)
  
- [Видео на YouTube](https://www.youtube.com/watch?v=K4ss20Cq2QI)