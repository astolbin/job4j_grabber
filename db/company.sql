-- В одном запросе получить
-- имена всех person, которые не состоят в компании с id = 5
-- название компании для каждого человека
select p.name, c.name from person p
inner join company c on p.company_id = c.id
    and c.id <> 5;


-- Необходимо выбрать название компании с максимальным количеством человек
-- + количество человек в этой компании
select c.name, count(p.id) persons from person p
inner join company c on p.company_id = c.id
group by c.id
order by persons desc
limit 1;
