Описание проектов репозитория LearnQA_Java_API
Общее пояснение: - 
**RestAssured**: Библиотека для тестирования REST API.
- **Response**: Класс, представляющий ответ от HTTP-запроса.
- **Test**: Аннотация из библиотеки JUnit 4, указывающая на то, что метод является тестовым.
=====================

Project1: Выводит на консоль приветствие с именем автора теста
-----------------------------------

Project2: Выводит содержимое текста в ответе на Get-запрос
-----------------------------------

Project3: Содержит следующие тесты:
-----------------------------------

- Ex5Test выводит текст второго сообщения в ответе JSON
- Ex6Test:Этот код выполняет GET-запрос к API по указанному URL и не следует за редиректами. Вместо этого он получает ответ от сервера и извлекает значение заголовка Location, если сервер возвращает редирект. Это полезно для тестирования поведения API, когда нужно проверить, какой URL будет указан в заголовке Location при редиректе, не переходя по нему автоматически.
- Ex7Test:Этот код осуществляет последовательные HTTP-запросы к указанному URL и автоматически обрабатывает редиректы. Он продолжает запрашивать новые URL, пока не получит успешный ответ с кодом 200. Это полезно для тестирования API, которые могут перенаправлять запросы на другие адреса.
- Ex8Test: Этот код тестирует создание долгосрочной задачи через API, проверяет ее статус до готовности и выводит финальный статус задачи. 
- Ex9Test: Код читает файл pass.txt, разбивает каждую строку на пароли, и для каждого пароля выполняет попытку авторизации. Для этого код разбивает строку на массив passwords, и для каждого пароля выполняется запрос на авторизацию;Если авторизация успешна, выводится сообщение об успешной авторизации, и программа завершает работу с помощью return.

Project4: Содержит следующие тесты:
-----------------------------------
	
- Ex10Test
	Тест в классе проверяет длину переменной типа String с помощью метода assertTrue/assertFalse. 
	В классе два теста: первый тест проверяет, что длина строки больше 15 символов, а второй тест проверяет, что длина строки не больше или равна 20 символам.

- Ex11Test
 		Тест в классе возвращает какую-то cookie с каким-то значением. Необходимо понять что за 			cookie и с каким значением, и зафиксировать это поведение с помощью assert.

- Ex12Test
	Тест в классе возвращает headers с каким-то значением. Необходимо понять что за headers и с каким значением, и зафиксировать это поведение с помощью assert.
	
- Ex13Test
	Тест в классе тест берет из файла user_agents.txt  ожидаемые значения, делает GET запрос с User Agent и убеждаться, что результат работы нашего метода правильный - т.е. в ответе ожидаемое значение всех трех полей.Если метод не может определить какой-то из параметров, он выставляет значение Unknown.
