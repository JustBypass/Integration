--запускать после старта
-- для проверки транзакции
-- alter table scenario
--     add constraint scenario_pk
--         unique (title);
--
-- create table test_table
-- (
--     id integer
--         constraint test_table_pk
--             primary key
--         constraint id references scenario (id)
-- );
-- INSERT INTO test_table VALUES (1),(2),(3);




--пользователи
INSERT INTO authority VALUES ('ROLE_ADMIN'),('ROLE_ANALYST'),
                             ('ROLE_TESTER'),('ROLE_DIRECTOR');
INSERT INTO role VALUES ('Rules everything','admin'),('Creates test cases','analyst'),
                        ('Performs test cases','tester'),('Approves the plan','director');
INSERT INTO role_authorities(authorities_name, role_title) VALUES ('ROLE_ADMIN','admin'),('ROLE_ANALYST','admin'),
                                  ('ROLE_TESTER','admin'),('ROLE_DIRECTOR','admin'),
                                  ('ROLE_ANALYST','analyst'),('ROLE_TESTER','tester'),
                                  ('ROLE_DIRECTOR','director');
-- --пароли формата admin-admin
-- INSERT INTO userr (id, is_active, login, name, surname, patronymic, password, role_title) VALUES
--                          ('2',true,'admin','Иван','Иванов','Иванович','$2a$10$IxwXIodgb8Y5LfJ9LNAhf.3VpW8oVC954E0IBNrS76VKQC7chkxxK','admin'),
--                          ('1',true,'director','Иван','Иванов','Иванович','$2a$10$RRwcXonX5z.GafLQ4H8pz.0KzLxtkMMV1Pbs51hQruLpaODgEjDte','director'),
--                          ('3',true,'analyst','Иван','Иванов','Иванович','$2a$10$RJef5odVkEbE1mFrMIZfmet4f/JrLLEmDtdIPcgkFLwvMuejbSTJO','analyst'),
--                          ('4',true,'tester','Иван','Иванов','Иванович','$2a$10$jiPCz.K8lA8q7XGvp5OaguLKg.QxjlZwcJnwFyMtyNvzSgzehVRci','tester'),
--                          ('5',false,'inactive','Иван','Иванов','Иванович','$2y$10$qpuOza3wgD10NnOGi4jey.FquLv.cK6E8zYh2oVeBW2twb49fRkVm','tester'),
--                          ('6',true,'withoutRole','Иван','Иванов','Иванович','$2a$10$eiiuEujCNpZFukmY4js5z.InuQtYvoRbsxQ8qaZ5yvf3FTIYDWvsO',null);
--
--
--
-- --проекты
-- INSERT INTO project VALUES (1,'Test project');
-- INSERT INTO project VALUES (2,'Test project2');
-- INSERT INTO project VALUES (3,'Test project3');
-- INSERT INTO requirement VALUES (1,'Нужно протестировать ввод','Test project'),
--                                (2,'Нужно протестировать отображение','Test project'),(3,'Нужно протестировать авторизацию','Test project');
-- INSERT INTO test_plan VALUES (true,3,1000,'2024-12-24 12:00:00','2024-5-24 12:00:00','Test project','Тестирование ввода');
-- INSERT INTO test_plan VALUES (true,3,2000,'2024-12-24 12:00:00','2024-5-24 12:00:00','Test project','Тестирование вывода');
-- INSERT INTO scenario VALUES (3,4,1000,'Test project','Тестирование ввода таким-то пользователем');
-- INSERT INTO scenario VALUES (3,4,2000,'Test project','Тестирование ввода сяким-то пользователем');
-- INSERT INTO scenario VALUES (3,4,3000,'Test project','Тестирование ввода тем самым пользователем');
-- INSERT INTO scenario VALUES (3,null,4000,'Test project','Тестирование ввода unassigned1');
-- INSERT INTO scenario VALUES (3,null,5000,'Test project2','Тестирование ввода unassigned1');
-- INSERT INTO test_case VALUES (3,1000,'Ввести по порядку следующие цифры','1 2 3','3 2 1','Test project','Проверка ввода формы1');
-- INSERT INTO test_case VALUES (3,2000,'Ввести по порядку следующие цифры','4 5 6','6 5 4','Test project','Проверка ввода формы2');
-- INSERT INTO test_case VALUES (3,3000,'Ввести по порядку следующие цифры','7 8 9','9 8 7','Test project','Проверка ввода формы3');
-- INSERT INTO test_case VALUES (3,4000,'Ввести по порядку следующие цифры','10 11 12','21 11 01','Test project','Проверка ввода формы4');
-- INSERT INTO test_case VALUES (3,5000,'Ввести по порядку следующие цифры','13 14 15','51 41 31','Test project','Проверка ввода формы5');
--
-- INSERT INTO test_plan_scenarios VALUES (1000,1000);
-- INSERT INTO test_plan_scenarios VALUES (2000,1000);
-- INSERT INTO test_plan_scenarios VALUES (3000,2000);
-- INSERT INTO scenario_case_connection VALUES (false,1000,false,1000,1000,1000,null);
-- INSERT INTO scenario_case_connection VALUES (false,2000,true,1000,2000,1000,null);
-- INSERT INTO scenario_case_connection VALUES (false,3000,true,2000,3000,1000,null);
-- INSERT INTO scenario_case_connection VALUES (false,4000,true,2000,4000,1000,null);
-- INSERT INTO scenario_case_connection VALUES (false,5000,false,3000,5000,2000,null);
